package com.crf.filters;

import java.beans.FeatureDescriptor;
import java.io.Serializable;

import javax.annotation.PreDestroy;

import com.crf.crf.CRFFeature;

public class CRFFilteredFeature<K, G> implements Serializable {
	
	private final CRFFeature<K, G> _feature;
	private final Filter<K, G> _filter;
	private final boolean _whenNotFilteredIsAlwaysOne;
	
	public CRFFilteredFeature(CRFFeature<K, G> feature, Filter<K, G> filter, boolean whenNotFilteredIsAlwaysOne){
		super();
		this._feature = feature;
		this._filter = filter;
		this._whenNotFilteredIsAlwaysOne = whenNotFilteredIsAlwaysOne;
	}
	
	public CRFFeature<K, G> getFeature(){
		return this._feature;
	}
	
	public Filter<K, G> getFilter(){
		return this._filter;
	}
	
	
	/**
	 * Returns true if it is <B>guaranteed</B> that the feature returns 1.0 for any input for which its filter 
	 * is equal to the filter returned by {@link #getFilter()}.
	 * @return
	 */
	public boolean isWhenNotFilteredIsAlwaysOne(){
		return this._whenNotFilteredIsAlwaysOne;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime*result + ((this._feature == null)? 0: this._feature.hashCode());
		result = prime*result + ((this._filter==null)? 0: this._filter.hashCode());
		result = prime*result + (this._whenNotFilteredIsAlwaysOne? 1231 :1237);
		
		return result;
	}
	
	
	@Override
	public boolean equals(Object obj){
		if(this == obj)
			return true;			
		if(obj == null)
			return false;
		if(this.getClass() != obj.getClass())
			return false;
		CRFFilteredFeature<K, G> other = (CRFFilteredFeature<K, G>) obj;
		if(this._feature == null){
			if(other._feature != null)
				return false;
		} else if(!this._feature.equals(other._feature)){
			return false;
		}
		
		if(this._filter == null){
			if(other._filter != null){
				return false;
			}
		} else if (!this._filter.equals(other._filter)){
			return false;
		}
		
		return true;
	}
	
}
