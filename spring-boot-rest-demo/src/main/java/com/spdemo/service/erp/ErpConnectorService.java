package com.spdemo.service.erp;

import com.spdemo.service.erp.dto.EcPoIncomingDto;
import com.spdemo.service.erp.dto.FormatToInternalTransformedEvent;

public interface ErpConnectorService {

    /**
     * 接受訂單，處理成功後 產生 ERP.PO.Upload.Incoming 事件
     */
    void poUploadIncoming(EcPoIncomingDto incomingDto) throws Exception;

    /**
     * 以 PO 處理結果來更新 ERP 資料庫，處理成功後 產生 ERP.PO.Result.Updated 事件
     */
    void poResultUpdate(FormatToInternalTransformedEvent transformedDto) throws Exception;
}
