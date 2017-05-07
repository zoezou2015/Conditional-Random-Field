package com.postagging.postagger.crf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import com.crf.crf.CRFModel;
import com.crf.run.CRFInferencePerformer;
import com.utilities.CRFException;

public class CRFPosTaggerLoader implements PosTaggerLoader{
	/*
	 * (non-Javadoc)
	 * @see org.postagging.postaggers.PosTaggerLoader#load(java.io.File)
	 */
	@Override
	public CRFPosTagger load(File directory)
	{
		if (!directory.exists()) {throw new CRFException("Given directory: "+directory.getAbsolutePath()+" does not exist.");}
		if (!directory.isDirectory()) {throw new CRFException("The loader requires a directory, but was provided with a file: "+directory.getAbsolutePath()+".");}
		
		try(ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(new File(directory, CRFPosTaggerTrainer.SAVE_LOAD_FILE_NAME ))))
		{
			@SuppressWarnings("unchecked")
			CRFModel<String, String> model = (CRFModel<String, String>) inputStream.readObject();
			return new CRFPosTagger(new CRFInferencePerformer<String, String>(model));
		}
		catch (IOException | ClassNotFoundException e)
		{
			throw new CRFException("Loading pos tagger failed."+e);
		}
	}

}
