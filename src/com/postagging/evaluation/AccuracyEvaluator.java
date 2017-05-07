package com.postagging.evaluation;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.postagging.postagger.crf.PosTagger;
import com.utilities.CRFException;
import com.utilities.TaggedToken;

/**
 *  * Evaluates the accuracy of a given {@link PosTagger} on a given test corpus.

 * @author 1001937
 *
 */
public class AccuracyEvaluator {
	
	private final Iterable<? extends List<? extends TaggedToken<String, String>>> _corpus;
	private final PosTagger _posTagger;
	private final PrintWriter _taggedTestWriter;
	
	private long _correct = 0;
	private long _incorrect = 0;
	private double _accuracy = 0.0;
	
	private static final Logger logger = Logger.getLogger(AccuracyEvaluator.class);
	
	public AccuracyEvaluator(Iterable<? extends List<? extends TaggedToken<String, String>>> corpus, PosTagger posTagger){
		this(corpus, posTagger, null);
	}
	
	public AccuracyEvaluator(Iterable<? extends List<? extends TaggedToken<String, String>>> corpus, PosTagger posTagger, PrintWriter taggedTestWriter){
		super();
		this._corpus = corpus;
		this._posTagger = posTagger;
		this._taggedTestWriter = taggedTestWriter;
	}
	
	public long getCorrect(){
		return this._correct;
	}
	
	public long getIncorrect(){
		return this._incorrect;
	}
	
	public double getAccuracy(){
		return this._accuracy;
	}
	
	public void evaluate(){
		this._correct = 0;
		this._incorrect = 0;
		this._accuracy = 0.0;
		int debug_index = 0;
		Iterator<? extends List<? extends TaggedToken<String, String>>> reader = this._corpus.iterator();
		while(reader.hasNext()){
			List<? extends TaggedToken<String, String>> taggedSentence = reader.next();
			List<String> sentence = taggedSentenceToSentence(taggedSentence);
			List<TaggedToken<String,String>> taggedByPosTagger = this._posTagger.tagSentence(sentence);
			evaluateSentence(taggedSentence,taggedByPosTagger);
			
			if (this._taggedTestWriter!=null)
			{
				this._taggedTestWriter.println(printSentence(taggedSentence));
				this._taggedTestWriter.println(printSentence(taggedByPosTagger));
			}
			
			if (logger.isDebugEnabled())
			{
				if ((debug_index%100)==0){logger.debug("Evaluated: "+debug_index);}	
			}
		}
		if (this._taggedTestWriter!=null) {this._taggedTestWriter.flush();}
		
		this._accuracy = ((double)this._correct)/((double)(this._correct+this._incorrect));
		
	}
	
	private void evaluateSentence(List<? extends TaggedToken<String, String>> taggedSentence, List<TaggedToken<String, String>> taggedByPosTagger){
	
		Iterator<? extends TaggedToken<String, String>> iteratorTaggedOriginal = taggedSentence.iterator();
		Iterator<? extends TaggedToken<String, String>> iteratorTaggedByPosTagger = taggedByPosTagger.iterator();
		while(iteratorTaggedOriginal.hasNext() && iteratorTaggedByPosTagger.hasNext()){
			TaggedToken<String, String> original = iteratorTaggedOriginal.next();
			TaggedToken<String, String> taggedByPos = iteratorTaggedByPosTagger.next();
			if(!equals(original.getToken(), taggedByPos.getToken()))
				throw new CRFException("Tokens not equal in evaluation");
			if(equals(original.getTag(), taggedByPos.getTag())){
				this._correct++;
			} else {
				this._incorrect++;
			}
		}
		if(iteratorTaggedByPosTagger.hasNext() || iteratorTaggedOriginal.hasNext())
			throw new CRFException("Sentences sizes are not equal in evaluation");
	
	}
	
	
	
	private String printSentence(List<? extends TaggedToken<String, String>> taggedSentence){
		StringBuilder sb = new StringBuilder();
		for(TaggedToken<String, String> taggedToken : taggedSentence){
			sb.append(taggedToken.getToken()).append("/").append(String.format("%-4s", taggedToken.getTag())).append(" ");
		}
		return sb.toString();
	}
	
	private List<String> taggedSentenceToSentence(List<? extends TaggedToken<String, String>> taggedSentence){
		List<String> ret = new ArrayList<String>(taggedSentence.size());
		for(TaggedToken<String, String> token : taggedSentence)
			ret.add(token.getToken());
		return ret;
	}
	
	private static boolean equals(Object obj1, Object obj2){
		if(obj1 == obj2)
			return true;
		else if(obj1==null || obj2 == null)
			return false;
		return obj1.equals(obj2);
	}
	
}
