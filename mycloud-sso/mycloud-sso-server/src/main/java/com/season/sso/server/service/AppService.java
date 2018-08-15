package com.season.sso.server.service;

import com.season.common.base.BaseException;
import com.season.common.model.ResultCode;
import com.season.sso.client.util.ShiroUtil;
import com.season.common.web.base.BaseService;
import com.season.sso.server.entity.App;
import com.season.sso.server.jms.SSOServerJMS;
import com.season.sso.server.repository.AppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Administrator on 2018/7/20.
 */
@Service
public class AppService extends BaseService {

    @Autowired
    private AppRepository appRepository;
    @Autowired
    private SSOServerJMS ssoServerJMS;

    public Page<App> findByPage(int pageNum, int pageSize) {
        return appRepository.findAll(PageRequest.of(pageNum, pageSize));
    }

    public void edit(App app, Integer option) {
        if (Objects.isNull(app)
                || StringUtils.isEmpty(app.getAppName())
                || StringUtils.isEmpty(app.getAppCode())
                || StringUtils.isEmpty(app.getEnable())
                || Objects.isNull(app.getSort())) {
            throw new BaseException(ResultCode.VALIDATE_ERROR, "个别参数为空");
        }
        switch (option) {
            case 1:
                appRepository.save(app);
                break;

            case 2:
                if (Objects.isNull(app.getAppId()))
                    throw new BaseException(ResultCode.VALIDATE_ERROR, "应用ID为空");
                appRepository.updateByAppId(app);
                break;

            case 3:
                if (Objects.isNull(app.getAppId()))
                    throw new BaseException(ResultCode.VALIDATE_ERROR, "应用ID为空");
                appRepository.delete(app);
                break;
            default:
                throw new BaseException(ResultCode.VALIDATE_ERROR, "不合法操作类型");
        }
        //更新权限
        ssoServerJMS.updateApp(app.getAppCode());
    }


    public boolean isEnable(String appCode) {
        if (StringUtils.isEmpty(appCode))
            return false;
        App app = appRepository.findByAppCode(appCode);
        return app != null && app.getEnable() == 1;
    }

    public List<App> findAll() {
        return appRepository.findAll();
    }

    public void delete(List<App> app) {
        appRepository.deleteInBatch(app);

        //更新权限
        for (App a : app) {
            ssoServerJMS.updateApp(a.getAppCode());
        }
    }
}
