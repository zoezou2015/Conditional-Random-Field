package com.function;

import java.math.BigDecimal;

/**
 * A multivariate function. A function f(x), where x is a vector, and the function returns a scalar.
 * @author 1001937
 *
 */
public abstract class Function {
	/**
	 * Returns the f(x) -- the value of the function in the given x. 
	 * @param point The "point" is x -- the input for the function.
	 * @return The value of f(x)
	 */
	public abstract BigDecimal value(BigDecimal[] point);
	
	/**
	 * The size (dimension) of the input vector (x).
	 * @return The size (dimension) of the input vector (x).
	 */
	public abstract int size();
}
