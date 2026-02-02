package com.ptk.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.ptk.demo.service.CalculatorService;

public class TestingCalculator 
{
	
	private CalculatorService calculator= new CalculatorService();
	
	@Test
	public void Add()
	{
		assertEquals(8,calculator.add(5, 3));
	}
}
