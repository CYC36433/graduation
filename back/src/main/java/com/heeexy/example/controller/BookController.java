package com.heeexy.example.controller;

import com.alibaba.fastjson.JSONObject;
import com.heeexy.example.service.BookService;
import com.heeexy.example.util.CommonUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;

import javax.management.RuntimeErrorException;
import javax.servlet.http.HttpServletRequest;

/**
 * @author: hxy
 * @description: 图书相关Controller
 * @date: 2017/10/24 16:04
 */
@RestController
@RequestMapping("/book")
public class BookController {

	@Autowired
	private BookService bookService;

	/**
	 * 查询图书列表
	 */
	@RequiresPermissions("article:list")
	@GetMapping("/listBook")
	public JSONObject listBook(HttpServletRequest request) {
		return bookService.listBook(CommonUtil.request2Json(request));
	}

	@PostMapping("/addBook")
	public JSONObject addBook(@RequestBody JSONObject requestJson){
		return bookService.addBook(requestJson);
	}

	@PostMapping("/updateBook")
	public JSONObject updateBook(@RequestBody JSONObject requestJson){
		return bookService.updateBook(requestJson);
	}

	@PostMapping("/deleteBook")
	public JSONObject deleteBook(@RequestBody JSONObject requestJson){
		return bookService.deleteBook(requestJson);
	}

	@PostMapping("/borrowBook")
	public JSONObject borrowBook(@RequestBody JSONObject requestJson){
		return bookService.borrowBook(requestJson);
	}

	@PostMapping("/listBorrowInfo")
	public JSONObject listBorrowInfo(@RequestBody JSONObject requestJson){
		return bookService.listBorrowInfo(requestJson);
	}

	@PostMapping("/updateBorrowInfo")
	public JSONObject updateBorrowInfo(@RequestBody JSONObject requestJson){
		return bookService.updateBorrowInfo(requestJson);
	}

	@PostMapping("/upload")
	public String upload(@RequestParam(value = "file", required = false) MultipartFile image,HttpServletRequest request){
		String fileName = image.getOriginalFilename();
		//String realPath=request.getSession().getServletContext().getRealPath("/");
		String realPath = "D:\\Desktop\\webpack\\graduation-project\\SpringBoot-Shiro-Vue\\vue\\src\\assets\\book_images/";
		// 自定义的文件名称
		//String trueFileName=String.valueOf(System.currentTimeMillis())+fileName;
		// 设置存放图片文件的路径
		String path=realPath+/*System.getProperty("file.separator")+*/fileName;
		// System.out.println(image);
		// System.out.println("--------------------------------------------------------------");
		// System.out.println("存放图片文件的路径:"+path);
		// 转存文件到指定的路径
		try {
			image.transferTo(new File(path));
		} catch (Exception e) {
			throw new RuntimeException("");
			//TODO: handle exception
		}
		// System.out.println("文件成功上传到指定目录下");
		return "success";
	}
}
