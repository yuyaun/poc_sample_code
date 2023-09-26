package com.spdemo.service;

import com.spdemo.db.dao.ArticleRepository;
import com.spdemo.db.entity.Article;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ArticleService {

    @Autowired
    ArticleRepository articleRepository;

    @Cacheable(key = "allUserArticles")
    @Secured({"ROLE_ADMIN"})
    public List<Article> getAllUserArticles(){
        log.info("getAllUserArticles time: " + new Date().toString());
        return articleRepository.findAll();
    }
}
