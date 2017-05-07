package com.utilities;

import java.util.Comparator;

public class AbsoluteValueComparator implements Comparator<Double>{
	
	@Override
	public int compare(Double o1, Double o2) {
		if(o1 == o2) 
			return 0;
		if(o1 == null)
			return -1;
		if(o2 == null)
			return 1;
		return Double.compare(Math.abs(o1), Math.abs(o2));
	}
}
