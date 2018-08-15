package com.season.common.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2018/7/14.
 */
@Component
public class AppConfig {
    @Value("${app.name:season}")
    private String name;

    @Value("${app.workerId:1}")
    private Long workerId;

    @Value("${app.dataCenterId:1}")
    private Long dataCenterId;

    @Value("${app.company:season}")
    private String company;

    @Value("${app.email:season@email}")
    private String email;

    @Value("${app.code:null}")
    private String appCode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Long workerId) {
        this.workerId = workerId;
    }

    public Long getDataCenterId() {
        return dataCenterId;
    }

    public void setDataCenterId(Long dataCenterId) {
        this.dataCenterId = dataCenterId;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }
}
