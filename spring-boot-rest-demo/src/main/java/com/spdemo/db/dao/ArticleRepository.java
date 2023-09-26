package com.spdemo.db.dao;

import com.spdemo.db.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer>  {

    @Query("SELECT s FROM Article s  ")
    List<Article> getAllArticles();
    @Query("SELECT s FROM Article s WHERE s.articleId = :articleId")
    Article getArticleById(@Param("articleId") int articleId);



    @Transactional
    @Modifying
    @Query("DELETE FROM Article a WHERE a.articleId = :articleId")
    void deleteArticle(@Param("articleId") int articleId);

    @Query("select new java.lang.Boolean( count(a) > 0) from Article a where a.title = :title and a.category = :category")
    Boolean articleExists(@Param("title") String title, @Param("category") String category);


}
