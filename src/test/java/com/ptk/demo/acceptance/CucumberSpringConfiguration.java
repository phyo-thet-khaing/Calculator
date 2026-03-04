package com.ptk.demo.acceptance;

import org.springframework.boot.test.context.SpringBootTest;

import com.ptk.demo.CalculatorApplication;

import io.cucumber.spring.CucumberContextConfiguration;

@CucumberContextConfiguration
@SpringBootTest(classes = CalculatorApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CucumberSpringConfiguration 
{
	
}