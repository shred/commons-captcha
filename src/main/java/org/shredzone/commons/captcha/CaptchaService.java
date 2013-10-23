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

import javax.servlet.http.HttpSession;

/**
 * Service for handling click based captchas.
 * <p>
 * In a first step, the captcha is created, and the correct answer is stored in the http
 * session.
 * <p>
 * In a second step, the coordinates of the user's click within the captcha is evaluated.
 *
 * @author Richard "Shred" Körber
 */
public interface CaptchaService {

    /**
     * Creates a random captcha. The correct answer is stored in the session.
     *
     * @param session
     *            {@link HttpSession} to store the answer in
     * @return {@link BufferedImage} generated captcha
     */
    BufferedImage createCaptcha(HttpSession session);

    /**
     * Checks if the captcha answer is valid.
     *
     * @param session
     *            {@link HttpSession} that contains the correct answer
     * @param x
     *            x position of the mouse click
     * @param y
     *            y position of the mouse click
     * @return {@code true} if the captcha has been correctly answered
     */
    boolean isValidCaptcha(HttpSession session, int x, int y);

}
