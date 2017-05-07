package com.utilities;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;

/**
 * Collection of utilities for handling exceptions.
 * @author 1001937
 *
 */
public class ExceptionUtil {
	public static final String TITLE_PROBLEMS = "Summary of problems";
	public static final String HEADER_PROBLEMS = generateStringOfCharacter('*', 20)+TITLE_PROBLEMS+generateStringOfCharacter('*',20);
	public static final String FOOTER_PROBLEMS = generateStringOfCharacter('*', HEADER_PROBLEMS.length());
	
	/**
	 * Returns the exception's message plug all nested
	 * exceptions messages.
	 * @param throwable an Exception (or Error)
	 * 
	 * @return The exception's message plug all nested
	 * exceptions messages.
	 */
	public static String getMessages (Throwable throwable){
		
		StringBuffer buffer = new StringBuffer();
		while(throwable != null){
			if(throwable.getMessage()!=null){
				buffer.append(throwable.getClass().getSimpleName()).append(": ").append(throwable.getMessage()).append("\n");
			}
			throwable = throwable.getCause();
		}
		return buffer.toString();
	}
	
	/**
	 * Prints a string that describes the given exception
	 * into the given PrintStream
	 * 
	 * @param throwable
	 * @param printStream
	 */
	public static void outputException(Throwable throwable, PrintStream printStream)
	{
		throwable.printStackTrace(printStream);
		printStream.println();
		printStream.println(HEADER_PROBLEMS);
		printStream.println(ExceptionUtil.getMessages(throwable));
		printStream.println(FOOTER_PROBLEMS);
	}
	
	/**
	 * Prints a string that describes the given exception
	 * into the given PrintWriter
	 * 
	 * @param throwable
	 * @param printWriter
	 */
	public static void outputException(Throwable throwable, PrintWriter printWriter)
	{
		throwable.printStackTrace(printWriter);
		printWriter.println();
		printWriter.println(HEADER_PROBLEMS);
		printWriter.println(ExceptionUtil.getMessages(throwable));
		printWriter.println(FOOTER_PROBLEMS);
	}
	
	public static void logException(Throwable throwable, Logger logger)
	{
		logger.error("Exception/Error:\n",throwable);
		logger.error("\n"+HEADER_PROBLEMS+"\n"+
		ExceptionUtil.getMessages(throwable)+"\n"+
		FOOTER_PROBLEMS
		);
	}
	
	public static String getStackTrace(Throwable t)
	{
		StringWriter stringWriter = new StringWriter();
		t.printStackTrace(new PrintWriter(stringWriter));
		String ret = stringWriter.toString();
		try{stringWriter.close();}catch(IOException e){}
		return ret;
	}
	
	
	public static String generateStringOfCharacter(char character, int length){
		char[] array = new char[length];
		for(int i=0; i<length; i++)
			array[i] = character;
		return new String(array);
	}
}
