package com.utilities;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import javax.print.attribute.standard.RequestingUserName;

import org.omg.CORBA.INTERNAL;

public class TopK_DataStructure<T> {
	
	private final int _k;
	private final int _array_length;
	private final T[] _storage;
	
	private int _index=0; // the index of next item to insert.
	private Comparator<T> _comparator = null;
	
	public TopK_DataStructure(int k){
		this(k, null);
	}
	
	@SuppressWarnings("unchecked")
	public TopK_DataStructure(int k, Comparator<T> comparator){
		super();
		this._k =k;
		this._comparator = comparator;
		this._array_length = k*k+k;
		this._storage = (T[]) Array.newInstance(Object.class, _array_length);
		if(this._k<=0){
			throw new CRFException("k<=0");
		}
		this._index=0;
	}
	
	/**
	 * Insert an item to this data-structure. This item might be discarded later when it is absolutely sure that it is not
	 * one of the top-k items that were inserted to this data-structure.
	 * @param item
	 */
	public void insert(T item)
	{
		this._storage[this._index] = item;
		++this._index;
		
		if (this._index>this._array_length) {
			throw new CRFException("BUG");
		}
		if (this._index==this._array_length){
			sortAndShrink();
		}
	}
	
	
	
	/**
	 * Get the top-k items among all the items that were inserted to this data-structure so far.
	 * <B>Note that this method has time complexity of O(k*log(k)).</B>
	 * @return
	 */
	public ArrayList<T> getTopK(){
		if(this._index>this._k){
			sortAndShrink();
		} 
		ArrayList<T> topKAsList = new ArrayList<T>();
		for(int i=0; i<this._k;i++)
			topKAsList.add(this._storage[i]);
		return topKAsList;
	
	}
	
	
	private void sortAndShrink(){
		if(this._comparator==null){
			Arrays.sort(this._storage,0,this._index,Collections.reverseOrder());
		} else {
			Arrays.sort(this._storage,0,this._index, Collections.reverseOrder(this._comparator));
		}
		this._index = this._k;
	}
	

}
