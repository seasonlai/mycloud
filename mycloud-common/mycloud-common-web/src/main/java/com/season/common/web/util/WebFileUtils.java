package com.season.common.web.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/8/17.
 */
public class WebFileUtils {


    static Logger logger = LoggerFactory.getLogger(WebFileUtils.class);
    
    public static File getTmpFileDir(HttpServletRequest request) {
        //根目录下新建文件夹upload，存放上传图片
        String uploadPath = String.valueOf(request.getServletContext()
                .getAttribute("_diskTmpPath"));
        if (StringUtils.isEmpty(uploadPath)) {
            logger.warn("保存文件临时路径没初始化");
            return null;
        }
        return getDirectory(uploadPath.trim());
    }

    public static File getImgFileDir(HttpServletRequest request) {
        //根目录下新建文件夹upload，存放上传图片
        String uploadPath = String.valueOf(request.getServletContext()
                .getAttribute("_diskImgPath"));
        if (StringUtils.isEmpty(uploadPath)) {
            logger.warn("保存图片文件路径没初始化");
            return null;
        }
        return getDirectory(uploadPath.trim());

    }
    public static File getVideoFileDir(HttpServletRequest request) {
        //根目录下新建文件夹upload，存放上传图片
        String uploadPath = String.valueOf(request.getServletContext()
                .getAttribute("_diskVideoPath"));
        if (StringUtils.isEmpty(uploadPath)) {
            logger.warn("保存视频文件路径没初始化");
            return null;
        }
        return getDirectory(uploadPath.trim());

    }

    public static File getDirectory(String filePath){
        File file = new File(filePath);
        if (!file.exists() && !file.mkdirs()) {
            logger.warn("创建目录失败:{}", filePath);
            return null;
        }
        if (!file.isDirectory()) {
            logger.warn("{}不是目录", filePath);
            return null;
        }
        return file;
    }

    /**
     * 获取上传文件的名称,新文件名为原文件名加上时间戳
     *
     * @return 文件名
     */
    public static String getFileNameWithTimestamp(String filename) {
        int suffixIndex = filename.lastIndexOf(".");
        String fileName = filename.substring(0, suffixIndex);
        String suffix = filename.substring(suffixIndex);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timeStr = sdf.format(new Date());
        return fileName + "_" + timeStr + suffix;
    }
}
