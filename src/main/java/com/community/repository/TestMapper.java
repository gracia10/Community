package com.community.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.community.model.domain.User;

@Mapper
public interface TestMapper {
	
	@Select("select * from user where id = #{id}")
	User findAll(@Param("id")String id);
}
