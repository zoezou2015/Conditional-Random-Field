package com.postagging.postagger.crf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.crf.crf.CRFModel;
import com.crf.filters.CRFFilteredFeature;
import com.crf.run.CRFTrainer;
import com.utilities.AbsoluteBigDecimalValueComparator;
import com.utilities.CRFException;
import com.utilities.MiscellaneousUtilities;
import com.utilities.StringUtilities;
import com.utilities.TaggedToken;

public class CRFPosTaggerTrainer implements PosTaggerTrainer<List<? extends List<? extends TaggedToken<String, String>>>>
{
	public static final String SAVE_LOAD_FILE_NAME = "crfptmdl.ser";
	public static final String HUMAN_READABLE_FILE_NAME = "rdbl_mdl.txt";

private final CRFTrainer<String, String> crfTrainer;
	
	private CRFPosTagger crfPosTagger = null;
	
	private static final Logger logger = Logger.getLogger(CRFPosTaggerTrainer.class);
	
	public CRFPosTaggerTrainer(CRFTrainer<String, String> crfTrainer)
	{
		super();
		this.crfTrainer = crfTrainer;
	}



	@Override
	public void train(List<? extends List<? extends TaggedToken<String, String>>> corpus)
	{
		crfTrainer.train(corpus);
		crfPosTagger = new CRFPosTagger(crfTrainer.getInferencePerformer());
	}

	
	
	@Override
	public CRFPosTagger getTrainedPosTagger()
	{
		if (null==crfPosTagger) throw new CRFException("Not yet trained.");
		return this.crfPosTagger;
	}

	@Override
	public void save(File modelDirectory)
	{
		if (null==crfPosTagger) {throw new CRFException("Not trained.");}
		
		if (!modelDirectory.exists()) {throw new CRFException("Given directory: "+modelDirectory.getAbsolutePath()+" does not exist.");}
		if (!modelDirectory.isDirectory()) {throw new CRFException("The loader requires a directory, but was provided with a file: "+modelDirectory.getAbsolutePath()+".");}

		CRFModel<String, String> crfModel = crfTrainer.getLearnedModel();
		
		try(ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(new File(modelDirectory,SAVE_LOAD_FILE_NAME))))
		{
			outputStream.writeObject(crfModel);
		}
		catch (IOException e)
		{
			throw new CRFException("Saving the pos tagger failed.",e);
		}
		
		try
		{
			createHumanReadableModelFile(new File(modelDirectory,HUMAN_READABLE_FILE_NAME),crfModel);
		}
		catch(RuntimeException e)
		{
			logger.error("Could not write a human readable file. However, this does NOT cause the program to stop.",e);
		}
	}
	
	
	
	
	private void createHumanReadableModelFile(File file, CRFModel<String, String> crfModel)
	{
		try(PrintWriter writer = new PrintWriter(file))
		{
			writer.println("This is a human readable model file, provided for convenience only. It is NOT used by the system at all. Changing, and even deleting this file has no effect on the system, and the loaded pos-tagger.");
			
			CRFFilteredFeature<String, String>[] features = crfModel.getFeatures().getFilteredFeatures();
			ArrayList<BigDecimal> parameters = crfModel.getParameters();
			if (features.length!=parameters.size())
			{
				throw new CRFException("features.length!=parameters.size()");
			}

			Map<Integer,BigDecimal> parametersMap = MiscellaneousUtilities.listToMap(parameters);
			List<Integer> sortedIndexes = MiscellaneousUtilities.sortByValue(parametersMap, Collections.reverseOrder(new AbsoluteBigDecimalValueComparator()));
			
			for (int index : sortedIndexes)
			{
				BigDecimal parameter = parametersMap.get(index);
				writer.printf("%s\t\t%s\n", StringUtilities.bigDecimalToString(parameter), features[index].getFeature().toString());
			}
		}
		catch (FileNotFoundException e)
		{
			throw new CRFException("Could not write human-readable model file.",e);
		}
	}


	
}
