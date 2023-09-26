package com.spdemo.controller;

import com.spdemo.service.WorkingDaysService;
import com.spdemo.service.model.WorkingDays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("app")
public class WorkingDaysController {

	@Autowired
	WorkingDaysService workingDaysService;

	/**
     * 上班時數
     */
	@GetMapping("workingDays/{id}")
	public ResponseEntity<WorkingDays> getArticleById(@PathVariable("id") String id) {
		WorkingDays workingDays = workingDaysService.getWorkingDays(id);

		return new ResponseEntity<WorkingDays>(workingDays, HttpStatus.OK);
	}

} 