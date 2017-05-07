package com.utilities;

import java.lang.reflect.Array;

/**
 * An array that starts not in index 0, but some other index.
 * <P>
 * A Java array starts in 0. This class provides an array which starts in another index.
 * So, for example, if the starting index is -3, and the length of the array is 5, then
 * the array indexes are [-3, -2, -1, 0, 1].
 * @author 1001937
 *
 * @param <T>
 */
public class ArbitraryRangeArray<T> {
	private final int _length;
	private final int _firstIndex;
	
	private T[] _array;
	
	@SuppressWarnings("uncheck")
	public ArbitraryRangeArray(int length, int firstIndex)
	{
		super();
		this._length = length;
		this._firstIndex = firstIndex;
		
		this._array = (T[]) Array.newInstance(Object.class, this._length); // new T[length]				
	}
	
	public T get(int index){
		return this._array[index-this._firstIndex];
	}
	
	public void set(int index, T value) {
		this._array[index-this._firstIndex] = value;
		
	}
	
	public int length() {
		return this._length;
	}
	
	public int getFirstIndex() {
		return this._firstIndex;
	}
	
}
