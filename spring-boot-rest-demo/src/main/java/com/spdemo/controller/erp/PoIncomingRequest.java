package com.spdemo.controller.erp;

import lombok.Data;

import java.util.Date;

@Data
public class PoIncomingRequest {
    private String poNumber; // 單號
    private Date createAt; // 建立日期
}
