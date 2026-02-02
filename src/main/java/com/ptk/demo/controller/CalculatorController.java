package com.ptk.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ptk.demo.service.CalculatorService;

@RestController
@RequestMapping("/cal")
public class CalculatorController 
{
	@Autowired
	private CalculatorService calculatorService;
	
	
	 // For addition
    @GetMapping("/add")
    public String add(@RequestParam double a, @RequestParam double b)
    {
        double result = calculatorService.add(a,b);
        return "Result: " + result;
    }
	
	
}
