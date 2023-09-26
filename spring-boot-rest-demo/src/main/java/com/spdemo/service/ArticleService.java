package com.spdemo.service;

import com.spdemo.db.dao.ArticleRepository;
import com.spdemo.db.entity.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleService   {


	@Autowired
	private ArticleRepository articleRepository;

	@Secured({"ROLE_ADMIN", "ROLE_USER"})
	public Article getArticleById(int articleId) {
		Article obj = articleRepository.getArticleById(articleId);
		return obj;
	}

	@Secured({"ROLE_ADMIN", "ROLE_USER"})
	public List<Article> getAllArticles(){
		return articleRepository.getAllArticles();
	}

	@Secured ({"ROLE_ADMIN"})
	public synchronized boolean addArticle(Article article){
       if (articleRepository.articleExists(article.getTitle(), article.getCategory())) {
    	   return false;
       } else {
		   article.setArticleId(null);
		   articleRepository.save(article);
    	   return true;
       }
	}

	@Secured ({"ROLE_ADMIN"})
	public void updateArticle(Article article) {
		articleRepository.save(article);
	}

	@Secured ({"ROLE_ADMIN"})
	public void deleteArticle(int articleId) {
		articleRepository.deleteArticle(articleId);
	}
}
