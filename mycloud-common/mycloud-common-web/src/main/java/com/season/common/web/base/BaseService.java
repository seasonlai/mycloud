package com.season.common.web.base;

import com.season.common.base.BaseException;
import com.season.common.model.ResultCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Administrator on 2018/7/30.
 */
public class BaseService {

    protected boolean hasNullObject(Object... objects) {
        if (objects != null && objects.length > 0) {
            for (Object object : objects) {
                if (Objects.isNull(object))
                    return true;
            }
        }
        return false;
    }

    protected void throwExceptionIfExistNull(Object... objects) {
        if (hasNullObject(objects))
            throw new BaseException(ResultCode.VALIDATE_ERROR, "参数为空");
    }

    protected List<Integer> stringToIntList(String[] strings) {
        return stringToIntList(strings, "数字格式不对?");
    }

    protected List<Integer> stringToIntList(String[] strings, String errMsg) {
        List<Integer> result = new ArrayList<>();
        if (Objects.isNull(strings)) {
            return result;
        }
        try {
            for (String string : strings) {
                result.add(Integer.parseInt(string));
            }
            return result;
        } catch (Throwable t) {
            throw new BaseException(ResultCode.VALIDATE_ERROR, errMsg, t);
        }
    }

}
