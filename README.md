## 執行安裝步驟

### 1. 在本機啟動 NAT & DB

```
docker-compose up -d
```

### 2. 打包 Docker image

```
docker build -t poc .
```

### 3. 執行已打包的 Docker image

```
docker run -e DB_HOSTNAME=host.docker.internal -e DB_PORT=5432 -e DB_DATABASE=springboot -e DB_USER=postgres -e DB_PASS=postgres -p=8080:8080 poc
```

### 4. 進入網址

```
http://localhost:8080/app/login
```

## 產生 Test Report(需要設定環境變數)

```
mvn site
```
