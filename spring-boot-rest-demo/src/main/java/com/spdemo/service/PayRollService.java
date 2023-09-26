package com.spdemo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spdemo.service.model.BillAmount;
import com.spdemo.service.model.PayRoll;
import com.spdemo.service.model.WorkingDays;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Message;
import io.nats.client.Nats;
import io.nats.client.support.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class PayRollService {

    /**
     * 計算薪資
     */
    public PayRoll calPayRoll(String id) {
        PayRoll pay = new PayRoll();
        pay.setUserId(id);
        id = "A1234567890";


        try (Connection nc = Nats.connect("nats://localhost")) {

            //取得 BillAmount

            Future<Message> incoming = nc.request("service.bill", id.getBytes(StandardCharsets.UTF_8));
            Message msg = incoming.get(500, TimeUnit.MILLISECONDS);
            String response = new String(msg.getData(), StandardCharsets.UTF_8);

            ObjectMapper mapper = new ObjectMapper();
            BillAmount billAmount = mapper.readValue(response, BillAmount.class);

            //取得 WorkingDays
            Future<Message> incoming2 = nc.request("service.workingDays", id.getBytes(StandardCharsets.UTF_8));
            Message msg2 = incoming2.get(500, TimeUnit.MILLISECONDS);
            String response2 = new String(msg2.getData(), StandardCharsets.UTF_8);

            WorkingDays workingDays = mapper.readValue(response2, WorkingDays.class);

            Integer payroll = workingDays.getDays() * 1000 + billAmount.getTotalAmount();

            pay.setAmount(payroll);

            return pay;
        } catch (Exception e) {
            log.error("PayRollService error", e);
        }



        return null;
    }
}
