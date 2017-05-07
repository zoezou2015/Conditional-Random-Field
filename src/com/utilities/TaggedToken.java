package com.utilities;


public class TaggedToken<K, G> {
	
	private final K _token;
	private final G _tag;
	
	public TaggedToken(K token, G tag){
		super();
		this._token = token;
		this._tag = tag;
	}
	
	public K getToken(){
		return this._token;
	}
	
	public G getTag(){
		return this._tag;
	}
	
	@Override
	public String toString(){
		return "CRFTaggedToken [getToken()=" + getToken() +", getTag() = "+ getTag()+ "]";
	}
	
	@Override
	public int hashCode(){
		final int prime = 31;
		int result =1;
		result = prime*result + ((this._tag==null)?0:this._tag.hashCode());
		result = prime*result + ((this._token == null)? 0: this._token.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj){
		if(this==obj)
			return true;
		if(obj==null)
			return false;
		if(this.getClass()!=obj.getClass())
			return false;
		TaggedToken<?, ?> other = (TaggedToken<?, ?>) obj;
		if (this._tag == null){
			if(other._tag != null)
				return false;
		} else if(!this._tag.equals(other._tag)){
			return false;
		}
		if(this._token == null){
			if(other._token != null){
				return false;
			} else if (!this._token.equals(other._token)){
				return false;
			}
		}
		return true;
	}
	
}
