package com.crf.filters;

public class TokenAndTagFilter<K,G> extends Filter<K, G> {
	
	private final K _token;
	private final G _currentTag;
	private transient int _hashCodeValue = 0;
	private transient boolean _hashCodeCalculated = false;
	

	
	public TokenAndTagFilter(K token, G currentTag){
		this._token = token;
		this._currentTag = currentTag;
	}
	
	@Override
	public int hashCode() {
		
		
		if(this._hashCodeCalculated){
			return this._hashCodeValue;
		} else {
			final int prime = 31;
			int result = 0;
			result = prime*result + ((this._currentTag == null)? 0: this._currentTag.hashCode());
			result = prime*result + ((this._token == null)? 0: this._token.hashCode());
			this._hashCodeCalculated = true;
			this._hashCodeValue = result;
			return result;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(this.getClass() != obj.getClass())
			return false;
		TokenAndTagFilter<K, G> other = (TokenAndTagFilter<K, G>) obj;
		if(this._currentTag == null){
			if(other._currentTag != null)
				return false;
		} else if(!this._currentTag.equals(other._currentTag))
			return false;
		
		
		if(this._token == null){
			if(other._token != null){
				return false;
			}
		} else if(!this._token.equals(other._token))
			return false;
		
		return true;
	}
	
}
