package com.spdemo.service.erp.dto;


import lombok.Data;

import java.util.Date;

@Data
public class EcPoUploadIncomingEvent {
    private String poNumber; // 單號
    private Date createAt; // 建立日期
}
