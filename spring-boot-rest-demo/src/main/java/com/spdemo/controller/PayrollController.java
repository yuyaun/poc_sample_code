package com.spdemo.controller;

import com.spdemo.service.PayRollService;
import com.spdemo.service.model.PayRoll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("app")
public class PayrollController {

	@Autowired
	PayRollService payRollService;


	/**
     * 計算薪資
     */
	@GetMapping("payroll/{id}")
	public ResponseEntity<PayRoll> getArticleById(@PathVariable("id") String id) {
		PayRoll pay = payRollService.calPayRoll(id);

		return new ResponseEntity<PayRoll>(pay, HttpStatus.OK);
	}

} 