package com.spdemo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spdemo.service.model.BillAmount;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Nats;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class BillService {

    @Value("${nats.server.ip}")
    String natsServerIp;
    

    List<Connection> connections = new ArrayList();
    @PreDestroy
    public void destory() {
    	for(Connection nc : connections) {
    		try {
    			nc.close();
    		} catch(Exception e) {}
    	}
    }

    @PostConstruct
    public void init() throws IOException, InterruptedException {
        log.info("init BillService...");

        Connection nc = Nats.connect("nats://" + natsServerIp);
        connections.add(nc);
        try {
            Dispatcher d = nc.createDispatcher((msg) -> {
                String id = new String(msg.getData(), StandardCharsets.UTF_8);
                System.out.println("service.bill, id: " + id + "  ");

                BillAmount result = getBillAmount(id);
                ObjectMapper mapper = new ObjectMapper();
                String json = null;
                try {
                    json = mapper.writeValueAsString(result);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("JsonProcessingException Exception.", e);
                }
                nc.publish(msg.getReplyTo(), (json).getBytes(StandardCharsets.UTF_8));

            });

            d.subscribe("service.bill");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public BillAmount getBillAmount(String id) {
        BillAmount amount = new BillAmount();
        amount.setUserId(id);
        amount.setTotalAmount(2000);

        return amount;
    }
}
