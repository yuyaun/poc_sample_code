package com.spdemo.controller.erp;


import com.spdemo.service.erp.ErpConnectorService;
import com.spdemo.service.erp.dto.EcPoIncomingDto;
import com.spdemo.service.erp.dto.FormatToInternalTransformedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("apis")
public class ErpConnectorController {

    private ErpConnectorService erpConnectorService;

    public ErpConnectorController(ErpConnectorService erpConnectorService) {
        this.erpConnectorService = erpConnectorService;
    }

    /**
     * 接受被選擇上傳的 PO
     */
    @PostMapping("v1/po/request")
    public ResponseEntity<PoIncomingResponse> poIncomingRequest(@RequestBody PoIncomingRequest poRequest ) throws Exception {

        EcPoIncomingDto poIncomingDto = new EcPoIncomingDto();
        poIncomingDto.setPoNumber(poRequest.getPoNumber());
        poIncomingDto.setCreateAt(poRequest.getCreateAt());
        erpConnectorService.poUploadIncoming(poIncomingDto);

        PoIncomingResponse result = new PoIncomingResponse();
        result.setPoNumber(poRequest.getPoNumber());
        result.setMsg("單號" + poRequest.getPoNumber() + "上傳成功!");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    /**
     * 以 PO 處理結果來更新 ERP 資料庫
     */
    @PostMapping("v1/po/response")
    public ResponseEntity<PoResultUpdateResponse> poResultUpdate(@RequestBody FormatToInternalTransformedEvent event ) throws Exception {

        erpConnectorService.poResultUpdate(event);

        PoResultUpdateResponse result = new PoResultUpdateResponse();
        result.setPoNumber(event.getPoNumber());
        result.setMsg("單號" + event.getPoNumber() + "處理成功!");

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
