package com.spdemo.service.erp;


import com.spdemo.MyRestApplication;
import com.spdemo.service.erp.dto.EcPoResultUpdatedEvent;
import com.spdemo.service.erp.dto.FormatToInternalTransformedEvent;
import com.spdemo.service.jetstream.JetStreamService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Date;

@Slf4j
@SpringBootTest(classes = MyRestApplication.class)
public class ErpConnectorServiceTest {

    @Autowired
    ErpConnectorService erpConnectorService;

    @MockBean
    private JetStreamService jetStreamService;


    @Test
    public void test() throws Exception {

        //Mockito.when(jetStreamService.getUserById(3))
    	//.thenReturn(new User(3, "I'm mock"));

        FormatToInternalTransformedEvent transformedEvent = new FormatToInternalTransformedEvent();
        transformedEvent.setPoNumber("000001");
        transformedEvent.setCreateAt(new Date());
        erpConnectorService.poResultUpdate(transformedEvent);

        EcPoResultUpdatedEvent event = new EcPoResultUpdatedEvent();
        event.setPoNumber(transformedEvent.getPoNumber());
        event.setCreateAt(transformedEvent.getCreateAt());
        event.setStatus("SAVED");

        InOrder inOrder = Mockito.inOrder(jetStreamService);
        //測試成功案例
        inOrder.verify(jetStreamService).publish("ERP.PO.Result.Updated", null, event); // 判斷 jetStreamService publish 是否有被呼叫, 且 event 正確
        //測試失敗案例
        //inOrder.verify(jetStreamService).publish("ERP.PO.Result.Updated.Fail", null, event); // 判斷 jetStreamService publish 是否有被呼叫


        log.info("測試成功 !");

    }

}
