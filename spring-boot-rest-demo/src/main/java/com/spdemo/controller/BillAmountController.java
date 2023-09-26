package com.spdemo.controller;

import com.spdemo.service.BillService;
import com.spdemo.service.model.BillAmount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("app")
public class BillAmountController {

	@Autowired
	BillService billService;

	/**
	 * 使用者銷帳金額
	 */
	@GetMapping("billAmount/{id}")
	public ResponseEntity<BillAmount> getArticleById(@PathVariable("id") String id) {
		BillAmount bill = billService.getBillAmount(id);

		return new ResponseEntity<BillAmount>(bill, HttpStatus.OK);
	}

} 