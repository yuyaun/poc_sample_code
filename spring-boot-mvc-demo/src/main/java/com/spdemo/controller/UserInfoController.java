package com.spdemo.controller;

import com.spdemo.db.dao.UserInfoRepository;
import com.spdemo.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("app")
public class UserInfoController {
    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private ArticleService articleService;

    @GetMapping("login")
    public String login() {
        return "custom-login";
    }

    @GetMapping("secure/article-details")
    public String getAllUserArticles(Model model) {
        model.addAttribute("userArticles", articleService.getAllUserArticles());

        return "articles";
    }

    @GetMapping("error")
    public String error(Model model) {
        String errorMessage = "You are not authorized for the requested data.";
        model.addAttribute("errorMsg", errorMessage);

        return "403";
    }
} 