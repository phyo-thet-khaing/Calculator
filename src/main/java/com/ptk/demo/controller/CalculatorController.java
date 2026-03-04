package com.ptk.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ptk.demo.service.CalculatorService;

@RestController
@RequestMapping("/sum")
public class CalculatorController 
{
	@Autowired
	private CalculatorService calculatorService;
	
	

	@GetMapping
	public Integer add(@RequestParam Integer a,
	                   @RequestParam Integer b)
	{
	    return calculatorService.add(a, b);
	}


	
	
}
