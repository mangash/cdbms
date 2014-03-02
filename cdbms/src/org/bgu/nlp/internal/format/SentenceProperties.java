package org.bgu.nlp.internal.format;

import java.util.Properties;

import org.bgu.nlp.HebFNProperties;

public class SentenceProperties extends Properties{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3899712577469364269L;

	public	SentenceProperties()
	{
		setProperty("doc_name", HebFNProperties.DEFAULT_SENTENCE_DOC_NAME);
		setProperty("doc_position", Integer.toString(HebFNProperties.DEFAULT_SENTENCE_DOC_POSITION));
		setProperty("genre", HebFNProperties.DEFAULT_SENTENCE_GENRE);
		setProperty("dep_parser_url", HebFNProperties.DEP_PARSER_URL);
		setProperty("dep_parse_method", HebFNProperties.DEP_PARSE_METHOD);
		setProperty("dep_parse_source", HebFNProperties.DEP_PARSE_SOURCE);
		setProperty("const_parser_url", HebFNProperties.CONST_PARSER_URL);
		setProperty("const_parse_method", HebFNProperties.CONST_PARSE_METHOD);
		setProperty("const_parse_source", HebFNProperties.CONST_PARSE_SOURCE);
		setProperty("const_parse_format", HebFNProperties.CONST_PARSE_FORMAT);
		setProperty("general", HebFNProperties.DEFAULT_SENTENCE_GENERAL_INFO);
	}
}
