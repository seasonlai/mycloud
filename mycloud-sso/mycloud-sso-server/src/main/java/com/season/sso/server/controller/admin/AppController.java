package com.season.sso.server.controller.admin;

import com.season.common.base.BaseResult;
import com.season.common.model.ResultCode;
import com.season.common.validator.Validator;
import com.season.common.validator.annotation.ValidateParam;
import com.season.sso.server.controller.BaseController;
import com.season.sso.server.entity.App;
import com.season.sso.server.service.AppService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created by Administrator on 2018/7/20.
 */
@Api("应用管理")
@RestController
@RequestMapping("/admin/app")
public class AppController extends BaseController {

    @Autowired
    private AppService appService;

    @GetMapping
    public ModelAndView index() {
        return new ModelAndView("/admin/app");
    }

    @ApiOperation(value = "获取应用列表", httpMethod = "POST")
    @PostMapping("/list")
    public BaseResult list(
            @RequestParam(value = "pageNum", defaultValue = "0")
            @ValidateParam(Validator.INT) String pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10")
            @ValidateParam(Validator.INT) String pageSize) {

        Page<App> page = appService.findByPage(Integer.valueOf(pageNum)
                , Integer.valueOf(pageSize));
        return BaseResult.successData(page);
    }

    @ApiOperation(value = "获取应用列表", httpMethod = "POST")
    @PostMapping("/listAll")
    public BaseResult list() {
        List<App> apps = appService.findAll();
        return BaseResult.successData(apps);
    }


    @ApiOperation(value = "应用编辑", httpMethod = "POST")
    @PostMapping(value = "/edit")
    public BaseResult edit(App app,
                           @RequestParam("option") @ValidateParam(Validator.INT) Integer option) {
        appService.edit(app, option);
        return BaseResult.success("操作成功");
    }

    @ApiOperation(value = "应用删除", httpMethod = "POST")
    @PostMapping(value = "/delete")
    public BaseResult delete(@RequestBody List<App> apps) {
        if (CollectionUtils.isEmpty(apps)) {
            return new BaseResult(ResultCode.VALIDATE_ERROR, "没有待删除项");
        }
        appService.delete(apps);
        return BaseResult.success("操作成功");
    }

}
