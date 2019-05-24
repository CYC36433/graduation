package com.heeexy.example.dao;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Param;

/**
 * @author: hxy
 * @description: 登录相关dao
 * @date: 2017/10/24 11:02
 */
public interface LoginDao {
	/**
	 * 根据用户名和密码查询对应的用户
	 */
	JSONObject getUser(@Param("username") String username, @Param("password") String password);

	/**
	 * 注册新用户
	 */
	int register(JSONObject jsonObject);

	/**
	 * 校验用户是否为注销状态
	 */
	int isDeleted(JSONObject jsonObject);

	/**
	 * 用户重注册
	 */
	int _register(JSONObject jsonObject);
}
