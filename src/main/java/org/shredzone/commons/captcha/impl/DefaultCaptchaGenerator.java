/*
 * Shredzone Commons
 *
 * Copyright (C) 2012 Richard "Shred" Körber
 *   http://commons.shredzone.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Library General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.shredzone.commons.captcha.impl;

import static java.lang.Math.PI;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.shredzone.commons.captcha.CaptchaGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Default implementation of {@link CaptchaGenerator}.
 *
 * @author Richard "Shred" Körber
 */
@Component("captchaGenerator")
public class DefaultCaptchaGenerator implements CaptchaGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultCaptchaGenerator.class);

    private final Random rnd = new Random();

    @Value("${captcha.width}")
    private int width;

    @Value("${captcha.height}")
    private int height;

    @Value("${captcha.fontPath}")
    private String fontPath;

    @Value("${captcha.fontSize}")
    private float fontSize;

    @Value("${captcha.grid}")
    private boolean showGrid;

    private int gridSize = 11;
    private int rotationAmplitude = 10;
    private int scaleAmplitude = 20;

    private Font font;

    @Override
    public int getWidth() { return width; }

    @Override
    public int getHeight() { return height; }

    /**
     * Sets up the captcha generator.
     */
    @PostConstruct
    public void setup() {
        if (width <= 0 || height <= 0) {
            throw new IllegalStateException("Captcha size is not set");
        }

        if (fontPath == null) {
            throw new IllegalStateException("Font is not set");
        }

        try {
            InputStream fontStream = DefaultCaptchaGenerator.class.getResourceAsStream(fontPath);
            font = Font.createFont(Font.TRUETYPE_FONT, fontStream);
        } catch (Exception ex) {
            LOG.error("Could not open font " + fontPath, ex);
            throw new IllegalStateException();
        }
    }

    @Override
    public BufferedImage createCaptcha(char[] text) {
        if (text == null || text.length == 0) {
            throw new IllegalArgumentException("No captcha text given");
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setBackground(Color.WHITE);
        g2d.setColor(Color.BLACK);

        clearCanvas(g2d);

        if (showGrid) {
            drawGrid(g2d);
        }

        int charMaxWidth = width / text.length;
        int xPos = 0;
        for (char ch : text) {
            drawCharacter(g2d, ch, xPos, charMaxWidth);
            xPos += charMaxWidth;
        }

        g2d.dispose();
        return image;
    }

    /**
     * Clears the canvas.
     */
    private void clearCanvas(Graphics2D g2d) {
        g2d.clearRect(0, 0, width, height);
    }

    /**
     * Draws the background grid.
     */
    private void drawGrid(Graphics2D g2d) {
        for (int y = 2; y < height; y += gridSize) {
            g2d.drawLine(0, y, width - 1, y);
        }

        for (int x = 2; x < width; x += gridSize) {
            g2d.drawLine(x, 0, x, height -1);
        }
    }

    /**
     * Draws a single character.
     *
     * @param g2d
     *            {@link Graphics2D} context
     * @param ch
     *            character to draw
     * @param x
     *            left x position of the character
     * @param boxWidth
     *            width of the box
     */
    private void drawCharacter(Graphics2D g2d, char ch, int x, int boxWidth) {
        double degree = (rnd.nextDouble() * rotationAmplitude * 2) - rotationAmplitude;
        double scale = 1 - (rnd.nextDouble() * scaleAmplitude / 100);

        Graphics2D cg2d = (Graphics2D) g2d.create();
        cg2d.setFont(font.deriveFont(fontSize));

        cg2d.translate(x + (boxWidth / 2), height / 2);
        cg2d.rotate(degree * PI / 90);
        cg2d.scale(scale, scale);

        FontMetrics fm = cg2d.getFontMetrics();
        int charWidth = fm.charWidth(ch);
        int charHeight = fm.getAscent() + fm.getDescent();

        cg2d.drawString(String.valueOf(ch), -(charWidth / 2), fm.getAscent() - (charHeight / 2));

        cg2d.dispose();
    }

}
