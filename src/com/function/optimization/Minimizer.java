package com.function.optimization;

import java.math.BigDecimal;

import com.function.Function;

public abstract class Minimizer <F  extends Function>{
	
	/**
	 * The function for which it is required to find the point in which it gets its minimum.
	 */
	protected final F function;
	
	public Minimizer(F function){
		this.function = function;
	}
	
	/*
	 * Find the vector x, where f(x) is minimized.
	 */
	public abstract void find();
	
	
	/**
	 * Return f(x), where x is the vector found by the function {@link #find()}.
	 * @return f(x), where x is the vector found by the function {@link #find()}.
	 */
	public abstract BigDecimal getValue();
	
	
	/**
	 * Return "x" -- the vector found by the function {@link #find()}.
	 * @return "x" -- the vector found by the function {@link #find()}.
	 */
	public abstract BigDecimal[] getPoint();
	
}
