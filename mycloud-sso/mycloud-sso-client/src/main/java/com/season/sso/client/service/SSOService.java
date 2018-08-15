package com.season.sso.client.service;

import com.season.common.base.BaseResult;
import com.season.sso.client.model.LoginRole;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Administrator on 2018/8/9.
 */
@Service
public class SSOService implements InitializingBean {


    @Autowired
    RestTemplate restTemplate;

    @Value("${mycloud.sso.serviceUrl:http://mycloud-sso/}")
    private String ssoUrl;

    public BaseResult validateAppLogin(ServletRequest request, String token) {
        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>(3);
        multiValueMap.add("token", token);
        return restTemplate.postForObject(ssoUrl + "sso-service/token", multiValueMap
                , BaseResult.class);
    }


    public Map<String, String> selectAllChainDefinition(String appCode) {
        HashMap<String, String> map = new HashMap<>(3);
        map.put("appCode", appCode);
        BaseResult result = restTemplate.postForObject(
                ssoUrl + "sso-service/appChainDefinition?appCode=" + appCode,
                map, BaseResult.class);
        if (!Objects.isNull(result)
                && result.getCode() == 0
                && !Objects.isNull(result.getData())) {
            return (Map<String, String>) result.getData();
        }
        return null;

    }

    public List<LoginRole> selectRoleByUsername(String name) {
        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>(3);
        multiValueMap.add("username", name);
        BaseResult baseResult = restTemplate.postForObject(ssoUrl + "sso-service/roles", multiValueMap, BaseResult.class);
        if (Objects.equals(baseResult.getCode(), 0) && !Objects.isNull(baseResult.getData())) {
            if (baseResult.getData() instanceof List) {
                return (List<LoginRole>) baseResult.getData();
            }
        }
        return null;
    }

    private String handlerUrl(String url) {
        if (StringUtils.isEmpty(url))
            return "/";
        if (!url.endsWith("/")) {
            url += "/";
        }
        return url;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ssoUrl = handlerUrl(ssoUrl);
    }
}
