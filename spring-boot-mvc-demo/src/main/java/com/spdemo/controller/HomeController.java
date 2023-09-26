package com.spdemo.controller;

import com.spdemo.db.dao.UserInfoRepository;
import com.spdemo.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;



@Controller
public class HomeController {

	@RequestMapping("/home")
	public @ResponseBody String greeting() {
		return "Hello, World";
	}

}