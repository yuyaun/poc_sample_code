package com.spdemo.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spdemo.service.model.BillAmount;
import com.spdemo.service.model.WorkingDays;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Nats;
import io.nats.client.support.JsonUtils;
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
public class WorkingDaysService {

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
        log.info("init WorkingDaysService...");
        Connection nc = Nats.connect("nats://" + natsServerIp);
        connections.add(nc);
        try   {
            Dispatcher d = nc.createDispatcher((msg) -> {
                String id = new String(msg.getData(), StandardCharsets.UTF_8);
                System.out.println("service.workingDays, id: " + id + "  "  );

                WorkingDays result = getWorkingDays(id);
                ObjectMapper mapper = new ObjectMapper();
                String json = null;
                try {
                    json = mapper.writeValueAsString(result);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("JsonProcessingException Exception.", e);
                }
                nc.publish(msg.getReplyTo(),  (json).getBytes(StandardCharsets.UTF_8));

            });

            d.subscribe("service.workingDays");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public WorkingDays getWorkingDays(String id) {
        WorkingDays workingDays = new WorkingDays();
        workingDays.setUserId(id);
        workingDays.setDays(20);

        return workingDays;
    }
}
