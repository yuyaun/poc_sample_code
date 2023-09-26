package com.spdemo.controller.erp;

import com.spdemo.MyRestApplication;
import com.spdemo.service.erp.ErpConnectorService;
import com.spdemo.service.erp.dto.EcPoIncomingDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import java.util.Date;

@Slf4j
@SpringBootTest(classes = MyRestApplication.class)
public class ErpConnectorControllerTest {

    @Autowired
    private ErpConnectorController erpConnectorController;

    @MockBean
    private ErpConnectorService erpConnectorService;

    @Test
    public void test() throws Exception {

        PoIncomingRequest request = new PoIncomingRequest();
        request.setPoNumber("0001");
        request.setCreateAt(new Date());

        ResponseEntity<PoIncomingResponse> responseEntity = erpConnectorController.poIncomingRequest(request);

        PoIncomingResponse response = responseEntity.getBody();
        Assert.isTrue(response.getMsg().contains("上傳成功"), "回傳值不正確");


        InOrder inOrder = Mockito.inOrder(erpConnectorService);

        EcPoIncomingDto dto = new EcPoIncomingDto();
        dto.setPoNumber(request.getPoNumber());
        dto.setCreateAt(request.getCreateAt());

        inOrder.verify(erpConnectorService).poUploadIncoming(dto); // 判斷是否有呼叫到 poUploadIncoming

    }
}
