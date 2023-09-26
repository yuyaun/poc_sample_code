package com.spdemo.service.erp.dto;


import lombok.Data;

import java.util.Date;

@Data
public class FormatToInternalTransformedEvent {
    private String poNumber; // 單號
    private String data; // 轉換後資料
    private Date createAt; // 建立日期
}
