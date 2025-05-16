package com.metropol.credit;

import java.util.TimeZone;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CreditApplicationTests {

	@Autowired
	ResourceLoader resourceLoader;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	protected ObjectMapper objectMapper;

	public MockMvc mockMvc;

	@Before
	public void setUp() throws Exception {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

		this.mockMvc = MockMvcBuilders.webAppContextSetup(context).alwaysDo(MockMvcResultHandlers.print()).build();

	}

	protected RequestPostProcessor tokenPostProcessor() {
		return new RequestPostProcessor() {
			@Override
			public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {

				request.addHeader("Authorization", "Bearer [access token]");

				return request;
			}
		};
	}
}
