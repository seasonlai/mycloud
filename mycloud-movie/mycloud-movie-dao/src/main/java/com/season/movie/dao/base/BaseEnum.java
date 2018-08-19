package com.season.movie.dao.base;

public interface BaseEnum<E extends Enum<?>> {
    public Integer getCode();

    public String getValue();
}