package com.ptk.demo.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CalculatorService 
{
	@Cacheable(value = "sum")
	public Integer add(Integer a, Integer b) {
	    try {
	        System.out.println("loading");
	        Thread.sleep(3000);
	    } catch (InterruptedException e) {
	        e.printStackTrace();
	    }
	    return a + b;
	}

}
