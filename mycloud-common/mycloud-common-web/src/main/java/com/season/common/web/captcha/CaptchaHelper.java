package com.season.common.web.captcha;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 验证码缓存工具类
 */
public class CaptchaHelper {

    public static final String CACHE_CAPTCHA = "_captcha";

    private static final Logger LOGGER = LoggerFactory.getLogger(CaptchaHelper.class);

    public static void setInCache(final HttpServletRequest request, HttpServletResponse response) throws IOException {
        BufferedImage image = new Captcha() {
            protected void setInCache(String captcha) {
                request.getSession().setAttribute(CACHE_CAPTCHA, captcha);
                if(LOGGER.isDebugEnabled()){
                    LOGGER.debug("验证码生成：{}",captcha);
                }
            }
        }.generate();

        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "jpg", out);
        try {
            out.flush();
        } finally {
            out.close();
        }
    }

    public static boolean validate(HttpServletRequest request, String captcha) {
        HttpSession session = request.getSession();
        if (session == null) {
            return false;
        }
        String sessionCaptcha = String.valueOf(session.getAttribute(CaptchaHelper.CACHE_CAPTCHA));
        if (sessionCaptcha != null && sessionCaptcha.equalsIgnoreCase(captcha)) {
            session.removeAttribute(CaptchaHelper.CACHE_CAPTCHA);
            return true;
        }
        return false;
    }
}