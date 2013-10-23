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

import java.awt.image.BufferedImage;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.shredzone.commons.captcha.CaptchaGenerator;
import org.shredzone.commons.captcha.CaptchaService;
import org.springframework.stereotype.Component;

/**
 * Default implementation of {@link CaptchaService}.
 *
 * @author Richard "Shred" Körber
 */
@Component("captchaService")
public class DefaultCaptchaService implements CaptchaService {

    private static final String CHARSET = "ABCDEFGHJLMNOPQRSTUWZ";
    private static final int NUMBER_OF_CHARS = 5;
    private static final String CAPTCHA_NAME = "captcha.position";
    private static final String LASTCLICK_NAME = "captcha.lastclick";

    private final Random rnd = new Random();

    @Resource
    private CaptchaGenerator captchaGenerator;

    @Override
    public BufferedImage createCaptcha(HttpSession session) {
        int captchaPos = computeCaptchaPosition(session);
        return captchaGenerator.createCaptcha(computeChars(captchaPos));
    }

    @Override
    public boolean isValidCaptcha(HttpSession session, int x, int y) {
        Integer pos = getCaptchaPosition(session);

        if (pos == null) {
            // There was no captcha generated yet, so the answer is always false.
            return false;
        }

        int cw = captchaGenerator.getWidth();
        int ch = captchaGenerator.getHeight();

        if (x < 0 || y < 0 || x >= cw || y >= ch) {
            // The click was outside of the captcha, so the answer is always false.
            return false;
        }

        if (x == 0 && y == 0) {
            // Ignore the simplest possible coordinate. No human being would click
            // there... ;-)
            return false;
        }

        int boxWidth = cw / NUMBER_OF_CHARS;
        int answer = x / boxWidth;

        setLastclickPosition(session, answer);

        return answer == pos;
    }

    /**
     * Compute a random set of characters, with exactly one 'X' at the given position.
     *
     * @param pos
     *            position of the 'X'
     * @return captcha text
     */
    private char[] computeChars(int pos) {
        char[] chars = new char[NUMBER_OF_CHARS];

        for (int ix = 0; ix < NUMBER_OF_CHARS; ix++) {
            if (ix == pos) {
                chars[ix] = 'X';
            } else {
                chars[ix] = CHARSET.charAt(rnd.nextInt(CHARSET.length()));
            }
        }

        return chars;
    }

    /**
     * Computes the position of the correct captcha answer.
     * <p>
     * Makes sure the new correct answer is never at the same position as the previous
     * click. This will keep spammers from just clicking at the same position until they
     * gave the right answer by lucky chance.
     *
     * @param session
     *            {@link HttpSession} with captcha data
     * @return position of the correct answer
     */
    private int computeCaptchaPosition(HttpSession session) {
        int newPos;

        Integer oldPos = getLastclickPosition(session);
        if (oldPos != null) {
            // Make sure newPos is always != oldPos
            newPos = rnd.nextInt(NUMBER_OF_CHARS - 1);
            if (newPos >= oldPos) newPos++;
        } else {
            newPos = rnd.nextInt(NUMBER_OF_CHARS);
        }

        setCaptchaPosition(session, newPos);
        return newPos;
    }

    /**
     * Gets the last captcha position from the session.
     *
     * @param session
     *            {@link HttpSession}
     * @return the last captcha position, or {@code null} if there is none yet
     */
    private Integer getCaptchaPosition(HttpSession session) {
        return (Integer) session.getAttribute(CAPTCHA_NAME);
    }

    /**
     * Sets the captcha position.
     *
     * @param session
     *            {@link HttpSession}
     * @param pos
     *            captcha position to store
     */
    private void setCaptchaPosition(HttpSession session, int pos) {
        session.setAttribute(CAPTCHA_NAME, pos);
    }

    /**
     * Gets the position of the last click.
     *
     * @param session
     *            {@link HttpSession}
     * @return position of the last click, or {@code null} if the user did not click yet
     */
    private Integer getLastclickPosition(HttpSession session) {
        return (Integer) session.getAttribute(LASTCLICK_NAME);
    }

    /**
     * Sets the position of the last click.
     *
     * @param session
     *            {@link HttpSession}
     * @param pos
     *            position of the last click
     */
    private void setLastclickPosition(HttpSession session, int pos) {
        session.setAttribute(LASTCLICK_NAME, pos);
    }

}
