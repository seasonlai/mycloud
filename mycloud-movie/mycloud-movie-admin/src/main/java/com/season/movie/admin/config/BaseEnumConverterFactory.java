package com.season.movie.admin.config;

import com.season.movie.dao.base.BaseEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by Administrator on 2018/8/23.
 */
public class BaseEnumConverterFactory implements ConverterFactory<String, BaseEnum> {
    private static final Map<Class, Converter> converterMap = new WeakHashMap<>();

    @Override
    public <T extends BaseEnum> Converter<String, T> getConverter(Class<T> targetType) {
        //如果converterMap里存在targetType的值就直接返回，否则插入新建的IntegerStrToEnum，并返回
        return converterMap.computeIfAbsent(targetType, IntegerStrToEnum::new);
    }

    class IntegerStrToEnum<T extends BaseEnum> implements Converter<String, T> {

        private final Class<T> enumType;

        private Map<String, T> enumMap = new HashMap<>();

        IntegerStrToEnum(Class<T> enumType) {
            this.enumType = enumType;
            T[] enums = enumType.getEnumConstants();
            for (T e : enums) {
                enumMap.put(e.getCode() + "", e);
            }
        }

        @Nullable
        @Override
        public T convert(String source) {
            T result = enumMap.get(source);
            if (result == null) {
                throw new IllegalArgumentException("没有枚举类型为： " + source);
            }
            return result;
        }
    }
}

