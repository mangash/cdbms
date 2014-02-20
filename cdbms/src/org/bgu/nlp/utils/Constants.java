package org.bgu.nlp.utils;


public class Constants {
	
	protected Constants() {}
	
	//Regular Expressions and Patterns:
	public static final String REGEX_WHITENOTES = "(\\s)*";
	public static final String REGEX_BRACKETED = REGEX_WHITENOTES + "\\(" + "(.)*" + "\\)" + REGEX_WHITENOTES;

	public static final String REGEX_FUNCTION_CALL = "([a-zA-Zא-ת\\-]+)( ?)\\(.*\\)";
	public static final String REGEX_LITERAL = "([^\\\"\\&\\|]*\\\"[^\\\"]*\"[^\\\"\\&\\|]*)*[^\\\"\\&\\|]*"; 
	public static final String REGEX_PROPERTY_QUERY = "([^\"]*\"[^\"]*\"[^\"]*)*[^\"]*\\.(.)*";

	public static final String REGEX_QUOTES_EXPRESSION = "\"(.)*\"";
	public static final String REGEX_SINGLETON = REGEX_WHITENOTES + "\\(" + REGEX_WHITENOTES + "(([^\\|\\&\\(\\)\\s])*|(" + REGEX_QUOTES_EXPRESSION + "))" + REGEX_WHITENOTES + "\\)" + REGEX_WHITENOTES;
	
	public static final String REGEX_WORD = REGEX_WHITENOTES + "(!?)[\\.=\\\"\\-a-zA-Z0-9א-ת]+" + REGEX_WHITENOTES;
	
	//constants for bracket queries
	public static final String BRACKET = "@B-R-A-C-K-E-T@";
	public static final String INVISIBLE_BRACKET_START = "@S-T-A-R-T@";
	public static final String INVISIBLE_BRACKET_END = "@E-N-D@";
	public static final String REPLACE = "@R-E-P-L-A-C-E@";
	
	//parsing constants
	public static final String FROM = "from";
	public static final String TO = "to";
	public static final String LAST_CONTAINING_SPAN = "last";
	public static final int PROXIMITY_TO_NEGATIVE = 1000;
	
	//query syntax
	public final static char _AND = '&'; 
	public final static char _LB = '(';
	public final static String _LONG_NOT = "not";
	public final static char _NOT = '!';
	public final static char _OR = '|';
	public final static char _QUOTED = '\"';		
	public final static char _RB = ')';
	
	public final static String[] operatorLongNames = { "or", "או", "and", "וגם", "not", "ללא" };
	public final static char[] operatorShortNames = { _OR , _OR, _AND, _AND, _NOT, _NOT };
	
	
	//delimiters
	public static final String PROPERTY_VALUES_DELIMITER = ":";
	public static final char TOKENIZER_FIELD_MULTIVALUE_SEPARATOR = ';';
	public static final String SENTENCE_PATTERN_DELIMITER = System.getProperty("line.separator");
	
	//uploading constants	
	public static final String PROPERTY_FIELD = "property";
	public static final String SENTENCE_FIELD = "sentence";
	public static final String BATCH_KEY_FIELD = "batchKey";
	public static final String WORD_PROPERTY_NAME = "word";
	public static final String DATABASE_FIELD_NAME = "database";	
	public static final String SENTENCE_PROPERTY_FIELD = "sentenceproperty";
		
	public static final String EOL = System.getProperty("line.separator");
	public static final String EMPTY_SPAN_NAME = "";
	public static final String SPACE_VALUE = "space";
	public static final String DEFAULT_CONFIGURATION_FILE_PATH = "resources/settings/configuration.txt";
	
	//grammar field names, necessary to understand parse trees
	public static final String PREFIX_PROPERTY_NAME = "prefix";
	public static final String SUFFIX_PROPERTY_NAME = "suffix";
	public static final String POS_PROPERTY_NAME    = "pos";
	public static final String LEMMA_PROPERTY_NAME    = "lemma";
	public static final String BASE_PROPERTY_NAME    = "base";
	
	public static final String PARENT_ID_DEP_PROPERTY_NAME    	= "parid";
	public static final String ID_DEP_PROPERTY_NAME    			= "id";
	public static final String PARENT_DIST_DEP_PROPERTY_NAME	= "pardist";		
	public static final String PARENT_POS_DEP_PROPERTY_NAME		= "parpos";
	public static final String PARENT_WORD_DEP_PROPERTY_NAME	= "parword";
	public static final String HEIGHT_DEP_PROPERTY_NAME			= "height";
	public static final String CONLL_MAIN_DELIMITER = "\t";
	public static final String CONLL_FEATS_DELIMITER = "|";
	public static final String POS_PATTERN_DELIMITER = ";";
	
	public static final String REGEX_INDEX="([0-9])+\\.([0-9])";
	public static final String REGEX_MAIN_DELIMITER = "\t";
	public static final String REGEX_MAIN_PLACEHOLDER="[^\t]*";
	public static final String REGEX_FEATS_PLACEHOLDER="(^[\\|])*";
	//public static final String REGEX_CONLL="@ID@\t@WORD@\t@LEMMA@";
}
