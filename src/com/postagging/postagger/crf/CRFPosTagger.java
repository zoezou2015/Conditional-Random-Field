package com.postagging.postagger.crf;

import java.util.List;

import com.crf.run.CRFInferencePerformer;
import com.utilities.TaggedToken;

public class CRFPosTagger implements PosTagger {
	
	private final CRFInferencePerformer<String, String> inferencePerformer;
	
	public CRFPosTagger(CRFInferencePerformer<String, String> inferencePerformer)
	{
		this.inferencePerformer = inferencePerformer;
	}

	@Override
	public List<TaggedToken<String,String>> tagSentence(List<String> sentence)
	{
		return inferencePerformer.tagSequence(sentence);
	}
	
	
	

}
