package com.spdemo.service.erp.dto;


import lombok.Data;

import java.util.Date;

@Data
public class EcPoResultUpdatedEvent {
    private String poNumber; // 單號
    private Date createAt; // 建立日期
    private String status;
}
