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
package org.shredzone.commons.captcha;

import java.awt.image.BufferedImage;

/**
 * A generator for captcha images.
 *
 * @author Richard "Shred" Körber
 */
public interface CaptchaGenerator {

    /**
     * Gets the captcha image width. This method does not create an actual captcha image,
     * and thus can be used e.g. for writign img width attributes.
     */
    int getWidth();

    /**
     * Gets the captcha image height. This method does not create an actual captcha image,
     * and thus can be used e.g. for writign img width attributes.
     */
    int getHeight();

    /**
     * Creates a captcha image of the given text.
     *
     * @param text
     *            the text to be shown in the captcha image
     * @return {@link BufferedImage} with the generated captcha
     */
    BufferedImage createCaptcha(char[] text);

}
