
請先啟動程式
    MyRestApplication.java


執行測試
    RestClientTest.java
    
登入帳號
    admin/123    
    user/123    


建立測試資料庫:
```
create database springboot;


CREATE TABLE articles (
  article_id int NOT NULL  GENERATED ALWAYS AS IDENTITY,
  title varchar NOT NULL,
  category varchar NOT NULL
);


INSERT INTO articles (title, category) VALUES
	('Java Concurrency', 'Java'),
	('Hibernate HQL ', 'Hibernate'),
	('Spring MVC with Hibernate', 'Spring');


CREATE TABLE   users (
  username varchar NOT NULL,
  password varchar NOT NULL,
  full_name varchar NOT NULL,
  role varchar NOT NULL,
  country varchar NOT NULL,
  enabled int NOT NULL,
  PRIMARY KEY (username)
) ;

INSERT INTO users (username, password, full_name, role, country, enabled) VALUES
	('admin', '$2a$10$njKxwWYOowrsEDu6vIxnju8TuEDN7huNLdmrLrK7HQJ6DbgrqfJWW', 'admin', 'ROLE_ADMIN', 'India', 1),
	('user', '$2a$10$njKxwWYOowrsEDu6vIxnju8TuEDN7huNLdmrLrK7HQJ6DbgrqfJWW', 'user', 'ROLE_USER', 'India', 1);
```