package com.crf.filters;

/**
 *  A filter which filters only by the tag of token.
 * @author 1001937
 *
 * @param <K>
 * @param <G>
 */
public class TagFilter<K, G> extends Filter<K, G> {
	
	private final G _currentTag;
	public TagFilter(G currentTag){
		this._currentTag = currentTag;
	}
	
	@Override
	public int hashCode(){
		final int prime = 31;
		int result = 1; 
		result = prime*result + ((this._currentTag == null)? 0: this._currentTag.hashCode());		
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this._currentTag == obj)
			return true;
		if(obj == null)
			return false;
		if(this.getClass() != obj.getClass())
			return false;
		TagFilter<K, G> other = (TagFilter<K, G>) obj;
		if(this._currentTag == null){
			if(other._currentTag != null)
				return false;
		} else if(!this._currentTag.equals(other._currentTag)){
			return false;
		}
 			
		return true;
	}
	
}
