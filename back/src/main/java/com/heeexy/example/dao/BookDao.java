package com.heeexy.example.dao;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * @author: hxy
 * @description: 文章Dao层
 * @date: 2017/10/24 16:06
 */
public interface BookDao {

    List<JSONObject> listBook(JSONObject jsonObject);
    
	int countBook(JSONObject jsonObject);

	int addBook(JSONObject jsonObject);

	int updateBook(JSONObject jsonObject);

	int deleteBook(JSONObject jsonObject);

	int borrowBook(JSONObject jsonObject);

	List<JSONObject> listBorrowInfo(JSONObject jsonObject);

	int updateBorrowInfo(JSONObject jsonObject);
}
