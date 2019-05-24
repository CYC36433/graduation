package com.heeexy.example.service;

import com.alibaba.fastjson.JSONObject;

/**
 * @author: hxy
 * @date: 2017/10/24 16:06
 */
public interface BookService {

	/**
	 * 图书列表
	 */
	JSONObject listBook(JSONObject jsonObject);

	/**
	 * 新增图书
	 */
	JSONObject addBook(JSONObject jsonObject);

	/**
	 * 修改图书信息
	 */
	JSONObject updateBook(JSONObject jsonObject);

	/**
	 * 删除图书
	 */
	JSONObject deleteBook(JSONObject jsonObject);

	/**
	 *图书借阅
	 */
	JSONObject borrowBook(JSONObject jsonObject);

	/**
	 * 借阅信息
	 */
	JSONObject listBorrowInfo(JSONObject jsonObject);

	/**
	 * 更新借阅信息
	 */
	JSONObject updateBorrowInfo(JSONObject jsonObject);

}
