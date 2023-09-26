package com.spdemo;

import com.spdemo.controller.HomeController;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = MyMvcApplication.class)

@AutoConfigureMockMvc
@Slf4j
public class MVCTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void homeTest() throws Exception {
		log.info("into to test");
		this.mockMvc.perform(get("/home")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(containsString("Hello, World")));
	} 
 	

	@Autowired
	private HomeController controller;

	@Test
	public void testObject() throws Exception {
		Assertions.assertThat(controller).isNotNull();
		String s = controller.greeting();
		log.info(s);
	}
} 