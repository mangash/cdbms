package org.bgu.nlp;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class HebFNProperties {

	public static String DEFAULT_DIVERSIFY = "false";
	public static String PROPERTIES_FILE = "./resources/configuration/config.properties";
	public static String UPLOAD_PATH = "./resources/upload/uploaded_files/";
	//global server properties
	public static String ELASTIC_SEARCH_MASTER_NODE_URL = "127.0.0.1";
	public static int ELASTIC_SEARCH_COMMUNICATION_PORT = 9300;
	public static String ELASTIC_SEARCH_CLUSTER_NAME = "HebFN-Cluster01";
	public static String ELASTIC_SEARCH_INDEX_NAME = "hebfn";
	public static String ELASTIC_SEARCH_DOCUMENT_TYPE = "sentence";
	public static int JETTY_PORT=8089;
	public static long JETTY_TIMEOUT=30000;
	public static int ELASTIC_SEARCH_PORT=9200;
	public static String DEP_PARSER_URL="www.cs.bgu.ac.il/~nlpproj/depparseV2/parse";
	public static String DEP_PARSE_SOURCE="yoavDepParserV2";
	
	public static String CONST_PARSER_URL="http://www.cs.bgu.ac.il/~yoavg/constparse/parse";
	public static String CONST_PARSE_METHOD="auto";
	public static String CONST_PARSE_SOURCE="yoavConstParserV1";
	public static String CONST_PARSE_FORMAT="treebank";
	
	public static String DEFAULT_SENTENCE_DOC_NAME="_";
	public static int DEFAULT_SENTENCE_DOC_POSITION=1;
	public static String DEFAULT_SENTENCE_GENRE="general";
	public static String DEFAULT_SENTENCE_GENERAL_INFO="";
	
	public static double MAX_UPLOAD_SIZE=10000;
	public static int SYNTACTIC_VARIATION_TOP_RESULTS_SIZE=200;
	public static int PRE_DIVERSIFICATION_MAX_QUERY_RESULTS = 2000;
	public static int DEFAULT_NUMBER_OF_QUERY_RESULTS = 200;
	public static void init()
	{
		Properties props = new Properties();
		System.out.println("reading properties from file " + PROPERTIES_FILE); 
    	try {
               //load a properties file
    		props.load(new FileInputStream(PROPERTIES_FILE));
    		
            //initialize properties from file
    		for (String key : props.stringPropertyNames())
    		{
    			String value=props.getProperty(key).trim();
    			System.out.println(key+" = "+value);
    			try {
    			switch (key.toUpperCase().trim())
    			{
    				case "UPLOAD_PATH":
    					UPLOAD_PATH=value;
    					break;
    				case "ELASTIC_SEARCH_MASTER_NODE_URL":
    					ELASTIC_SEARCH_MASTER_NODE_URL=value;
    					break;
    				case "ELASTIC_SEARCH_COMMUNICATION_PORT":
    					ELASTIC_SEARCH_COMMUNICATION_PORT=Integer.parseInt(value);
    					break;
    				case "ELASTIC_SEARCH_CLUSTER_NAME":
    					ELASTIC_SEARCH_CLUSTER_NAME=value;
    					break;
    				case "ELASTIC_SEARCH_INDEX_NAME":
    					ELASTIC_SEARCH_INDEX_NAME=value;
    					break;
    				case "ELASTIC_SEARCH_DOCUMENT_TYPE":
    					ELASTIC_SEARCH_DOCUMENT_TYPE=value;
    					break;
    				case "JETTY_PORT":
    					JETTY_PORT=Integer.parseInt(value);
    					break;
    				case "JETTY_TIMEOUT":
    					JETTY_TIMEOUT=Long.parseLong(value);
    					break;
    				case "ELASTIC_SEARCH_PORT":
    					ELASTIC_SEARCH_PORT=Integer.parseInt(value);
    					break;
    				case "DEP_PARSER_URL":
    					DEP_PARSER_URL=value;
    					break;
    				case "DEP_PARSE_SOURCE":
    					DEP_PARSE_SOURCE=value;
    					break;
    				case "CONST_PARSER_URL":
    					CONST_PARSER_URL=value;
    					break;
    				case "CONST_PARSE_METHOD":
    					CONST_PARSE_METHOD=value;
    					break;
    				case "CONST_PARSE_SOURCE":
    					CONST_PARSE_SOURCE=value;
    					break;
    				case "CONST_PARSE_FORMAT":
    					CONST_PARSE_FORMAT=value;
    					break;
    				case "DEFAULT_SENTENCE_DOC_NAME":
    					DEFAULT_SENTENCE_DOC_NAME=value;
    					break;
    				case "DEFAULT_SENTENCE_DOC_POSITION":
    					DEFAULT_SENTENCE_DOC_POSITION=Integer.parseInt(value);
    					break;
    				case "DEFAULT_SENTENCE_GENRE":
    					DEFAULT_SENTENCE_GENRE=value;
    					break;
    				case "DEFAULT_SENTENCE_GENERAL_INFO":
    					DEFAULT_SENTENCE_GENERAL_INFO=value;
    					break;
    				case "MAX_UPLOAD_SIZE":
    					MAX_UPLOAD_SIZE=Double.parseDouble(value);
    					break;
    				case "SYNTACTIC_VARIATION_TOP_RESULTS_SIZE":
    					SYNTACTIC_VARIATION_TOP_RESULTS_SIZE=Integer.parseInt(value);
    					break;
    				case "PRE_DIVERSIFICATION_MAX_QUERY_RESULTS":
    					PRE_DIVERSIFICATION_MAX_QUERY_RESULTS=Integer.parseInt(value);
    					break;
    				case "DEFAULT_NUMBER_OF_QUERY_RESULTS":
    					DEFAULT_NUMBER_OF_QUERY_RESULTS=Integer.parseInt(value);
    					break;
    				default:
    					System.out.println("Unrecognized property: \"" + key + "\"");
    			}
    			}
    			catch (Exception ex1)
    			{
    				System.out.print("Failed initializing property \"" + key + "\". using default value");
    			}
    		}
    	} catch (IOException ex2) {
    		System.out.println("Failed reading properties file " + PROPERTIES_FILE + "\n using defaults instead...");
        }
    	
	}
}