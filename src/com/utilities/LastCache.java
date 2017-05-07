package com.utilities;

/**
 * A "cache" which remembers one iter only.
 * The cache remembers the value for a given key. When a new key-value pair is put, then the previous is forgotten. 
 * 
 * @author 1001937
 *
 * @param <K> cannot be null
 * @param <V> cannot be null
 */
public class LastCache<K,V> {
	
	private  K _key = null;
	private  V _value = null;
	
	/**
	 * Remeber the given value for the given key. Forget the older key-value pair. 
	 * @param key A key, cannot be null.
	 * @param value A value corresponds to this key. Cannot be null.
	 */
	public synchronized void put(K key, V value){
		if (key==null)
			throw new CRFException("null key");
		if(value==null)
			throw new CRFException("null value");
		this._key = key;
		this._value = value;
	}

	/**
	 * Get the value that was put earlier for this key. Return null if the value is unknown. 
	 * @param key
	 * @return
	 */
	public synchronized V get(K key){
		if(key==null)
			throw new CRFException("null key");
		else if(key.equals(this._key)){
			return this._value;
		} else {
			return null;
		}
	}
}
