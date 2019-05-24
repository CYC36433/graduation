package com.heeexy.example.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.heeexy.example.dao.BookDao;
import com.heeexy.example.service.BookService;
import com.heeexy.example.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author: hxy
 * @date: 2017/10/24 16:07
 */
@Service
public class BookServiceImpl implements BookService {

	@Autowired
	private BookDao bookDao;

	@Override
	public JSONObject listBook(JSONObject jsonObject) {
		CommonUtil.fillPageParam(jsonObject);
		int count = bookDao.countBook(jsonObject);
		List<JSONObject> list = bookDao.listBook(jsonObject);
		return CommonUtil.successPage(jsonObject, list, count);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject addBook(JSONObject jsonObject){
		bookDao.addBook(jsonObject);
		return CommonUtil.successJson();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject updateBook(JSONObject jsonObject){
		bookDao.updateBook(jsonObject);
		return CommonUtil.successJson();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject deleteBook(JSONObject jsonObject){
		bookDao.deleteBook(jsonObject);
		return CommonUtil.successJson();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject borrowBook(JSONObject jsonObject){
		bookDao.borrowBook(jsonObject);
		return CommonUtil.successJson();
	}

	@Override
	public JSONObject listBorrowInfo(JSONObject jsonObject){
		List<JSONObject> list = bookDao.listBorrowInfo(jsonObject);
		return CommonUtil.successPage(list);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject updateBorrowInfo(JSONObject jsonObject){
		bookDao.updateBorrowInfo(jsonObject);
		return CommonUtil.successJson();
	}
}
