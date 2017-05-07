package com.crf.crf;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PreDestroy;
import javax.security.cert.CertificateEncodingException;

import org.apache.mahout.math.jet.random.Gamma;

import com.utilities.CRFException;

public class CRFTags<G> implements Serializable {
	
	private final Set<G> tags;
	private final Map<G, Set<G>> canFollow;
	private final Map<G, Set<G>> canPrecede;
	private Map<G, Set<G>> canPrecedeNonNull; // 
	private Map<G, Set<G>> precedeWhenFirst;
	
	public CRFTags(Set<G> tags, Map<G, Set<G>>canFollow, Map<G, Set<G>> canPrecede){
		this.tags = tags;
		this.canFollow = canFollow;
		this.canPrecede = canPrecede;
		
		initCanPrecedeNonNull();
		initPrecedeWhenFirst();
		sanityCheck();
		consistencyCheck();
	}
	
	public Set<G> getTags(){
		return this.tags;
	}
	
	public Map<G, Set<G>> getCanFollow(){
		return this.canFollow;
	}
	
	public Map<G, Set<G>> getCanPrecede(){
		return this.canPrecede;
	}
	
	public Map<G, Set<G>> getCanPrecedeNonNull(){
		return this.canPrecedeNonNull;
	}
	
	public Map<G, Set<G>> getPrecedeWhenFirst(){
		return this.precedeWhenFirst;
	}
	
	private void initCanPrecedeNonNull(){
		this.canPrecedeNonNull = new LinkedHashMap<G, Set<G>>();
		for(G tag : this.canPrecede.keySet()){
			
			Set<G> newSet = new LinkedHashSet<G>();
			for(G precede : this.canPrecede.get(tag)){
				if(precede!=null)
					newSet.add(precede);
			}
			this.canPrecedeNonNull.put(tag, newSet);
		}
	}
	
	
	private void initPrecedeWhenFirst(){
		this.precedeWhenFirst = new LinkedHashMap<G, Set<G>>();
		boolean debug_firstDetected = false;
		for(G tag : this.canPrecede.keySet()){
			
			if(this.canPrecede.get(tag).contains(null)){
				this.precedeWhenFirst.put(tag, (Set<G>) Collections.singleton(null));
				debug_firstDetected = true;
			} else {
				this.precedeWhenFirst.put(tag, (Set<G>) Collections.emptySet());
			}
		}
		
		if(!debug_firstDetected) 
			throw new CRFException("Error: no tag has null in its can-precede set. This means that no tag can appear as the first tag in a sentence, which is an error.");
	}
	
	
	private void sanityCheck(){
		if(!this.canPrecede.keySet().containsAll(this.tags))
			throw new CRFException("map keys do not contain all tags");
		if(!this.canFollow.keySet().containsAll(this.tags))
			throw new CRFException("map keys do not contain all tags");
		if(this.canPrecedeNonNull.keySet().containsAll(this.tags))
			throw new CRFException("map keys do not contain all tags");
		if(this.precedeWhenFirst.keySet().containsAll(this.tags))
			throw new CRFException("map keys do not contain all tags");
		
		if(this.tags.contains(null))
			throw new CRFException("tags (the set of tags) should not contain null.");
		
		for(G tag: this.canPrecedeNonNull.keySet()){
			if(this.canPrecedeNonNull.get(tag).contains(null))
				throw new CRFException("Bug");
		}
		
		for(G tag : this.canFollow.keySet()){
			if(this.canFollow.get(tag).contains(null))
				throw new CRFException("Error: null appears as a tag that can follow a given tag, which is an error, since null is a virtual tag for the virtual token the precedes the first token");
		}
		
		if(!this.canFollow.containsKey(null))
			throw new CRFException("Error: canFollow does not contain null key. This means that it is not specificed which tags can be assigned to a first token of a sentence");
	}
	
	private void consistencyCheck(){
		for(G tag : this.canPrecede.keySet()){
			for(G precede : this.canPrecede.get(tag))
				if(!this.canFollow.get(precede).contains(tag))
					throw new CRFException("canFollow and canPrecede are inconsistent.");
		}
		
		for(G tag : this.canFollow.keySet()){
			for(G follow : this.canFollow.get(tag))
				if(!this.canPrecede.get(follow).contains(tag))
					throw new CRFException("canFollow and canPrecede are inconsistent.");
		}
		
	}
	
}
