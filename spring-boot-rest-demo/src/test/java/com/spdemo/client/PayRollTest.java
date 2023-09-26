package com.spdemo.client;

import java.net.URI;
import java.util.Date;

import com.spdemo.MyRestApplication;
import com.spdemo.service.model.PayRoll;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


@SpringBootTest(classes = MyRestApplication.class) 
public class PayRollTest {
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
//    public void getPayRollDemo() {
//    	HttpHeaders headers = getHeaders();  
//        RestTemplate restTemplate = new RestTemplate();
//	    String url = "http://localhost:8080/app/payroll/{id}";
//        HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
//        ResponseEntity<PayRoll> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, PayRoll.class, "A1234567890");
//        PayRoll payRoll = responseEntity.getBody();
//        System.out.println("Id:"+payRoll.getUserId()+", Amount:" + payRoll.getAmount()); //Id:A1234567890, Amount:22000
//    }
//
}
