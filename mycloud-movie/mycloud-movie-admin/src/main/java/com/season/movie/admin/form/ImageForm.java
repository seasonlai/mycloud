package com.season.movie.admin.form;

import javax.validation.constraints.NotNull;

/**
 * Created by Administrator on 2018/8/25.
 */
public class ImageForm {
    @NotNull
    String imgData;
    String imgData2;
    @NotNull
    String fileName;

    public String getImgData() {
        return imgData;
    }

    public void setImgData(String imgData) {
        this.imgData = imgData;
    }

    public String getImgData2() {
        return imgData2;
    }

    public void setImgData2(String imgData2) {
        this.imgData2 = imgData2;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
