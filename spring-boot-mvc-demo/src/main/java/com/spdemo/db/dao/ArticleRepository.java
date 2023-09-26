package com.spdemo.db.dao;

import com.spdemo.db.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer>  {
    @Transactional
    @Modifying
    @Query("DELETE FROM Article a WHERE a.articleId = :articleId")
    void deleteByArticleId(@Param("articleId") Integer articleId);

}