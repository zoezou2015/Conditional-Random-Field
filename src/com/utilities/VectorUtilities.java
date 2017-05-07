package com.utilities;

import java.math.BigDecimal;

import org.omg.CORBA.ARG_IN;

import static com.utilities.ArithmeticUtilities.*;


/**
 * A collection of linear algebra functions over vectors.
 * @author 1001937
 *
 */
public class VectorUtilities {
	
	/**
	 * Return the inner product of the two given vectors.
	 * @param rowVector
	 * @param columnVector
	 * @return
	 */
	public static BigDecimal product(BigDecimal[] rowVector, BigDecimal[] columnVector){
		if(rowVector.length!=columnVector.length)
			throw new CRFException("Cannot multiply vector of different sizes");
		BigDecimal ret = BigDecimal.ZERO;
		for(int i=0; i<rowVector.length; i++){
			ret = safeAdd(ret, safeMultiply(rowVector[i], columnVector[i]));
		}
		return ret;
	}
	
	/**
	 * Return the sum of two vectors, as a new vector. For example [1,2]+[3,4] = [4,6].
	 * @param vector1
	 * @param vector2
	 * @return
	 */
	public static BigDecimal[] addVectors(BigDecimal[] vector1, BigDecimal[] vector2){
		if(vector1.length!=vector2.length)
			throw new CRFException("Cannot add two vectors of different sizes");
		BigDecimal[] ret = new BigDecimal[vector1.length];
		for(int i=0; i<vector1.length; i++){
			ret[i] = safeAdd(vector1[i], vector2[i]);
		}
		return ret;
	}
	
	/**
	 * Return a new vector which is the multiplication of the given vector by a scalar.
	 * @param scalar
	 * @param vector
	 * @return
	 */
	public static BigDecimal[] multipyByScalar(BigDecimal scalar, BigDecimal[] vector){
		BigDecimal[] ret = new BigDecimal[vector.length];
		for(int i=0; i<vector.length; i++){
			ret[i] = safeMultiply(scalar, vector[i]);
		}
		return ret;
	}
	
	/**
	 * Return thr substraction of the given two vectors, as a new vector. For example [1,2]-[3,4]=[-2,-2].
	 * @param vector1
	 * @param vector2
	 * @return
	 */
	public static BigDecimal[] substractVectors(BigDecimal[] vector1, BigDecimal[] vector2){
		if(vector1.length!=vector2.length)
			throw new CRFException("Cannot substract two vectors of different sizes");
		BigDecimal[] ret = new BigDecimal[vector1.length];
		for(int i=0; i<vector1.length; i++){
			ret[i] = safeSubstract(vector1[i], vector2[i]);
		}
		return ret;
	}
	
	public static BigDecimal euclideanNormSquare(BigDecimal[] vector){
		return product(vector, vector);
	}
	
	/**
	 * Change every infinity value in the array to Double.MAX_VALUE or -Double.MAX_VALUE for negative infinity. 
	 * @param array
	 * @return
	 */
	@Deprecated
	public static final double[] changeInfinityToDoubleMax(final double[] array){
		double[] ret = new double[array.length];
		for(int i=0; i<array.length;i++){
			if(array[i]==Double.POSITIVE_INFINITY)
				ret[i] = Double.MAX_VALUE;
			else if(array[i]==Double.NEGATIVE_INFINITY)
				ret[i] = -Double.MAX_VALUE;
			else 
				ret[i] = array[i];
		}
		return ret;
	}
	
}
