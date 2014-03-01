package org.bgu.nlp.exception;

public class SentenceAnalyzerException extends Exception {
	
	public String message;
	public boolean critical;
	
	public SentenceAnalyzerException(String message)
	{
		this(message,false);
	}
	
	public SentenceAnalyzerException(String message, boolean critical)
	{
		this.message=message;
		this.critical=critical;
	}
}
