package com.crf.filters;


public class TwoTagsFilter<K,G> extends Filter<K, G> {
	private final G _previousTag;
	private final G _currentTag;
	
	private transient int _hashCodeValue = 0;
	private transient boolean _hashCodeCalculated = false;
	
	public TwoTagsFilter(G previousTag, G currentTag){
		this._currentTag = currentTag;
		this._previousTag = previousTag;
	}
	
	@Override
	public int hashCode() {
		if(this._hashCodeCalculated){
			return this._hashCodeValue;	
		} else {final int prime = 31;
			int result = 1;
			result = prime*result + ((this._currentTag==null)? 0: this._currentTag.hashCode());
			result = prime*result + ((this._previousTag==null)? 0: this._previousTag.hashCode());
			this._hashCodeCalculated = true;
			this._hashCodeValue = result;
			return result;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return false;
		if(obj == null)
			return false;
		TwoTagsFilter<K, G> other = (TwoTagsFilter<K, G>) obj;
		if(this._currentTag == null){
			if(other._currentTag != null)
				return false;
		} else if(!this._currentTag.equals(other._currentTag))
			return false;
		if(this._previousTag == null){
			if(other._previousTag != null)
				return false;
		} else if(!this._previousTag.equals(other._previousTag))
			return false;
		
		return true;
	}
}
