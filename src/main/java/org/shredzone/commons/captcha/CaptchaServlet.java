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

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.FrameworkServlet;

/**
 * This servlet creates a random captcha image, stores the correct answer in the http
 * session, and streams the generated captcha image as PNG.
 *
 * @author Richard "Shred" Körber
 */
public class CaptchaServlet extends FrameworkServlet {
    private static final long serialVersionUID = 3241024444677649962L;

    @Override
    protected void doService(HttpServletRequest request, HttpServletResponse response)
    throws Exception {
        if (!request.getMethod().equals("GET")) {
            response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED,
                            request.getMethod() + " is not accepted");
        }

        // Prepare header
        response.setDateHeader("Date", System.currentTimeMillis());
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/png");

        // Write captcha image
        CaptchaService cs = getWebApplicationContext().getBean("captchaService", CaptchaService.class);
        BufferedImage challenge = cs.createCaptcha(request.getSession());
        ImageIO.write(challenge, "png", response.getOutputStream());
    }

}
