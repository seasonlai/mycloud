package com.season.movie.dao.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import com.season.movie.dao.base.BaseEnum;
import com.season.movie.dao.enums.TaskStatus;
import com.season.movie.dao.enums.VideoStatus;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MappedTypes({VideoStatus.class, TaskStatus.class})
public class BaseEnumTypeHandler<E extends Enum<E> & BaseEnum> extends BaseTypeHandler<E> {
    static Logger logger = LoggerFactory.getLogger(BaseEnumTypeHandler.class);
    private Class<E> type;

    public BaseEnumTypeHandler() {
    }

    public BaseEnumTypeHandler(Class<E> type) {
        logger.info("调用BaseEnumTypeHandler构造方法 -  参数：{}", type.getSimpleName());
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        } else {
            this.type = type;
        }
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        ps.setObject(i, parameter.getCode());
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int code = rs.getInt(columnName);
        return rs.wasNull() ? null : this.codeOf(code);
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int code = rs.getInt(columnIndex);
        return rs.wasNull() ? null : this.codeOf(code);
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int code = cs.getInt(columnIndex);
        return cs.wasNull() ? null : this.codeOf(code);
    }

    private E codeOf(int code) {
        try {
            return this.codeOf(this.type, code);
        } catch (Exception var3) {
            throw new IllegalArgumentException("Cannot convert " + code + " to "
                    + this.type.getSimpleName() + " by code value.", var3);
        }
    }

    private <E extends BaseEnum> E codeOf(Class<E> enumClass, int code) {
        E[] enumConstants = enumClass.getEnumConstants();
        for (E e : enumConstants) {
            if (Objects.equals(e.getCode(), code)) {
                return e;
            }
        }

        return null;
    }
}