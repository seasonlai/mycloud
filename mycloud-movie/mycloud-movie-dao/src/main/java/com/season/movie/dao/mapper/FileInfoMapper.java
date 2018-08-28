package com.season.movie.dao.mapper;

import com.season.movie.dao.MyMapper;
import com.season.movie.dao.entity.FileInfo;
import org.apache.ibatis.annotations.Param;

public interface FileInfoMapper extends MyMapper<FileInfo> {


    int updatePointer(@Param("name") String code, @Param("count") int count);

}