package com.function.optimization;

import java.math.BigDecimal;

import com.function.DerivableFunction;
import static com.utilities.VectorUtilities.*;


/**
 * A collection of static helper functions, needed for some implementation of {@link LineSearch}.
 * @author 1001937
 *
 */
public class LineSearchUtilities {
		
	/**
	 * Returns f(x+\alpha*d), where "x" is the given point, "d" is the given direction, and "\alpha" is some scalar.
	 */
	public static BigDecimal valueForAlpha(DerivableFunction function, BigDecimal[] point, BigDecimal[] direction, BigDecimal alpha){
		return function.value(addVectors(point, multipyByScalar(alpha, direction)));		
	}
	
	/**
	 * Returns the (value of the) derivation of f(x+\alpha*d), where both x and d are considered constant vectors, and
	 * the only variable is \alpha. So f(x+\alpha*d) is a function of single variable, and the derivation is derivation
	 * by the variable \alpha.
	 * <br>
	 * The derivation is: f'(x+\alpha*d)*d
	 * 
	 * @return the value of the derivation of f(x+\alpha*d) for the given \alpha.
	 */
	public static BigDecimal derivationForAlpha(DerivableFunction funtion, BigDecimal[] point, BigDecimal[] direction, BigDecimal alpha){
		return product(funtion.gradient(addVectors(point, multipyByScalar(alpha, direction))),direction);
	}
}
