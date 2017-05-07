package com.crf.run;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.crf.crf.CRFModel;
import com.crf.filters.FilterFactory;
import com.utilities.TaggedToken;

public class ExampleMain
{

	@SuppressWarnings("unchecked")
	public static void main(String[] args)
	{
		// Load a corpus into the memory
		List<List<? extends TaggedToken<String, String> >> corpus = loadCorpusWhereTokensAndTagsAreStrings();
		
		// Create trainer factory
		CRFTrainerFactory<String, String> trainerFactory = new CRFTrainerFactory<String, String>();
		
		// Optionally call trainerFactory.setRegularizationSigmaSquareFactor() to override the default regularization factor.
		
		// Create trainer
		CRFTrainer<String,String> trainer = trainerFactory.createTrainer(
				corpus,
				(Iterable<? extends List<? extends TaggedToken<String, String> >> theCorpus, Set<String> tags) -> createFeatureGeneratorForGivenCorpus(theCorpus,tags),
				createFilterFactory());

		// Run training with the loaded corpus.
		trainer.train(corpus);
		
		// Get the model
		CRFModel<String, String> crfModel = trainer.getLearnedModel();
		
		// Save the model into the disk.
		File file = new File("example.ser");
		save(crfModel,file);
		
		////////
		
		// Later... Load the model from the disk
		crfModel = (CRFModel<String, String>) load(file);
		
		// Create a CrfInferencePerformer, to find tags for test data
		CRFInferencePerformer<String, String> inferencePerformer = new CRFInferencePerformer<String, String>(crfModel);
		
		// Test:
		List<String> test = Arrays.asList( "This is a sequence for test data".split("\\s+") );
		List<TaggedToken<String, String>> result = inferencePerformer.tagSequence(test);
		
		// Print the result:
		for (TaggedToken<String, String> taggedToken : result)
		{
			System.out.println("Tag for: "+taggedToken.getToken()+" is "+taggedToken.getTag());
		}
	}
	
	
	public static List<List<? extends TaggedToken<String, String> >> loadCorpusWhereTokensAndTagsAreStrings()
	{
		// Implement this method
		return null;
	}
	
	public static CRFFeatureGenerator<String, String> createFeatureGeneratorForGivenCorpus(Iterable<? extends List<? extends TaggedToken<String, String> >> corpus, Set<String> tags)
	{
		// Implement this method
		return null;
	}
	
	public static FilterFactory<String, String> createFilterFactory()
	{
		// Implement this method
		return null;
	}
	
	public static void save(Object object, File file)
	{
		try(ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file)))
		{
			stream.writeObject(object);
		}
		catch (IOException e)
		{
			throw new RuntimeException("Failed to save",e);
		}
	}
	
	public static Object load(File file)
	{
		try(ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file)))
		{
			return stream.readObject();
		}
		catch (ClassNotFoundException | IOException e)
		{
			throw new RuntimeException("Failed to load",e);
		}
	}
		

}

