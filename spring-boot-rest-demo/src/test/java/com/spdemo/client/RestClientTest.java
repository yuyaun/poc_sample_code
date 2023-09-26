package com.spdemo.client;

import com.spdemo.MyRestApplication;
import com.spdemo.db.entity.Article;

import lombok.extern.slf4j.Slf4j;

import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Date;

@SpringBootTest(classes = MyRestApplication.class) 
@Slf4j
public class RestClientTest {
    private HttpHeaders getHeaders() {
    	String credential="admin:123";
    	//String credential="user:123";
    	String encodedCredential = new String(Base64.encodeBase64(credential.getBytes()));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
     	headers.add("Authorization", "Basic " + encodedCredential);
    	return headers;
    }

//    @Test
//    public void getArticleByIdDemo() {if(true)throw new RuntimeException("xxx");
//    	HttpHeaders headers = getHeaders();  
//        RestTemplate restTemplate = new RestTemplate();
//	    String url = "http://localhost:8080/app/article/{id}";
//        HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
//        ResponseEntity<Article> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Article.class, 1);
//        Article article = responseEntity.getBody();
//        log.info("Id:"+article.getArticleId()+", Title:"+article.getTitle()
//                 +", Category:"+article.getCategory());      
//    }
//
//    @Test
//	public void getAllArticlesDemo() {
//    	HttpHeaders headers = getHeaders();  
//        RestTemplate restTemplate = new RestTemplate();
//	    String url = "http://localhost:8080/user/articles";
//        HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
//        ResponseEntity<Article[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Article[].class);
//        Article[] articles = responseEntity.getBody();
//        for(Article article : articles) {
//              System.out.println("Id:"+article.getArticleId()+", Title:"+article.getTitle()
//                      +", Category: "+article.getCategory());
//        }
//    }
//
//    @Test
//    public void addArticleDemo() {
//    	HttpHeaders headers = getHeaders();  
//        RestTemplate restTemplate = new RestTemplate();
//	    String url = "http://localhost:8080/user/article";
//	    Article objArticle = new Article();
//	    objArticle.setTitle("Spring REST Security using Hibernate");
//	    objArticle.setCategory("Spring");
//        HttpEntity<Article> requestEntity = new HttpEntity<Article>(objArticle, headers);
//        URI uri = restTemplate.postForLocation(url, requestEntity);
//        System.out.println(uri.getPath());    	
//    }
//
//    @Test
//    public void updateArticleDemo() {
//    	HttpHeaders headers = getHeaders();  
//        RestTemplate restTemplate = new RestTemplate();
//	    String url = "http://localhost:8080/user/article";
//	    Article objArticle = new Article();
//	    objArticle.setArticleId(1);
//	    objArticle.setTitle("Update:Java Concurrency " + new Date().toString());
//	    objArticle.setCategory("Java");
//        HttpEntity<Article> requestEntity = new HttpEntity<Article>(objArticle, headers);
//        restTemplate.put(url, requestEntity);
//    }
//
//    @Test
//    public void deleteArticleDemo() {
//    	HttpHeaders headers = getHeaders();  
//        RestTemplate restTemplate = new RestTemplate();
//	    String url = "http://localhost:8080/user/article/{id}";
//        HttpEntity<Article> requestEntity = new HttpEntity<Article>(headers);
//        restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, Void.class, 4);        
//    }

}
