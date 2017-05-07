package com.function.optimization;

import java.math.BigDecimal;

import com.function.DerivableFunction;
import com.function.Function;
import com.function.TwiceDerivableFunction;
import com.utilities.CRFException;
/**
 * Represent "-f(x)" for a given function "f(x)".
 * <BR>
 * This negated function can be used when maximization of a function is needed, but a minimization algorithm is available.
 * Just minimize "-f(x)", and the resulting "x" is the point of the maximum for "f(x)".
 *  
 * @author 1001937
 *
 */
public class NegatedFunction extends TwiceDerivableFunction{
	
	private final Function _function;
	private final DerivableFunction _derivableFunction;
	private final TwiceDerivableFunction _twiceDerivableFunction;
	private final int _theSize;
	
	private NegatedFunction(Function function, DerivableFunction derivableFunction, TwiceDerivableFunction twiceDerivableFunction){
		
		super();
		this._function = function;
		this._derivableFunction = derivableFunction;
		this._twiceDerivableFunction = twiceDerivableFunction;
		if(this._function!=null){
			this._theSize = this._function.size();
		} else if(this._twiceDerivableFunction!=null){
			this._theSize = this._derivableFunction.size();
		} else if(this._twiceDerivableFunction != null){
			this._theSize = this._twiceDerivableFunction.size();
		} else {
			throw new CRFException("BUG");
		}
	}
	
	private BigDecimal[] negate(BigDecimal[] array){
		BigDecimal[] ret = new BigDecimal[array.length];
		for(int i=0; i<array.length; i++){
			ret[i] = array[i].negate();
		}
		return ret;
	}
	
	public static NegatedFunction fromFunction(Function function){
		return new NegatedFunction(function, null, null);
	}
	
	public static NegatedFunction fromDerivableFunction(DerivableFunction derivableFunction){
		return new NegatedFunction(null,derivableFunction, null);
	}
	
	public static NegatedFunction fromTwiceDerivableFunction(TwiceDerivableFunction twiceDerivableFunction){
		return new NegatedFunction(null, null, twiceDerivableFunction);
	}
	
	@Override
	public int size() {
		return this._theSize;
	}
	
	@Override
	public BigDecimal value(BigDecimal[] point) {
		if(this._function!=null)
			return this._function.value(point).negate();
		else if(this._derivableFunction!=null)
			return this._derivableFunction.value(point).negate();
		else if(this._twiceDerivableFunction!=null)
			return this._twiceDerivableFunction.value(point).negate();
		else throw new CRFException("BUG");
	}
	
	@Override
	public BigDecimal[] gradient(BigDecimal[] point) {
		if(this._derivableFunction!=null)
			return negate(this._derivableFunction.gradient(point));
		else if(this._twiceDerivableFunction!=null)
			return negate(this._twiceDerivableFunction.gradient(point));
		else 
			throw new CRFException("BUG");
	}
	
	@Override
	public BigDecimal[][] hessian(BigDecimal[] point) {
		if(this._twiceDerivableFunction!=null){
			BigDecimal[][] ret = new BigDecimal[this._theSize][this._theSize];
			BigDecimal[][] originalHessian = this._twiceDerivableFunction.hessian(point);
			for(int i=0; i<this._theSize;i++)
				for(int j=0;j<this._theSize;j++){
					ret[i][j] = originalHessian[i][j].negate();
				}
			return ret;
		}
		else 
			throw new CRFException("BUG");
	}
	
}
