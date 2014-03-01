package org.bgu.nlp.elasticsearch;

import java.io.IOException;
import java.util.Map;
import java.util.Vector;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.bgu.nlp.internal.format.YoavSentence;
import org.bgu.nlp.internal.format.YoavWord;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.search.SearchHit;


public class DocumentAdder {	
	public static YoavWord jsonToWord (String jsonText) throws JsonParseException, IOException
	{
		JsonFactory jfactory = new JsonFactory();
		jfactory.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		JsonParser jParser = jfactory.createJsonParser(jsonText);
		
		YoavWord word=new YoavWord();
		// loop until token equal to "}"
		while (jParser.nextToken() != JsonToken.END_OBJECT) {
			
			String fieldname = jParser.getCurrentName();
			if ("index".equals(fieldname)) {
				jParser.nextToken();
				word.setIndex(jParser.getIntValue());
			}
			if ("deprel".equals(fieldname)) {
				jParser.nextToken();
				word.setDepRel(jParser.getText());
			}
			if ("cpos".equals(fieldname)) {
				jParser.nextToken();
				word.setCPos(jParser.getText());
			}
			if ("polar".equals(fieldname)) {
				jParser.nextToken();
				word.setPolar(jParser.getText());
			}
			if ("phead".equals(fieldname)) {
				jParser.nextToken();
				word.setPHead(jParser.getText());
			}
			if ("pdeprel".equals(fieldname)) {
				jParser.nextToken();
				word.setPDepRel(jParser.getText());
			}
			if ("id".equals(fieldname)) {
				jParser.nextToken();
				word.setId(jParser.getDoubleValue());
			}
			if ("word".equals(fieldname)) {
				jParser.nextToken();
				word.setWord(jParser.getText());
			}
			if ("pos".equals(fieldname)) {
				jParser.nextToken();
				word.setPos(jParser.getText());
			}
			if ("lemma".equals(fieldname)) {
				jParser.nextToken();
				word.setLemma(jParser.getText());
			}
			if ("gender".equals(fieldname)) {
				jParser.nextToken();
				word.setGender(jParser.getText());
			}
			if ("number".equals(fieldname)) {
				jParser.nextToken();
				word.setNumber(jParser.getText());
			}
			if ("person".equals(fieldname)) {
				jParser.nextToken();
				word.setPerson(jParser.getText());
			}
			if ("tense".equals(fieldname)) {
				jParser.nextToken();
				word.setWord(jParser.getText());
			}
			if ("binyan".equals(fieldname)) {
				jParser.nextToken();
				word.setBinyan(jParser.getText());
			}
			if ("type".equals(fieldname)) {
				jParser.nextToken();
				word.setType(jParser.getText());
			}
			if ("parid".equals(fieldname)) {
				jParser.nextToken();
				word.setParId(jParser.getDoubleValue());
			}
			if ("parword".equals(fieldname)) {
				jParser.nextToken();
				word.setParWord(jParser.getText());
			}
			if ("parpos".equals(fieldname)) {
				jParser.nextToken();
				word.setParPos(jParser.getText());
			}
			if ("height".equals(fieldname)) {
				jParser.nextToken();
				word.setHeight(jParser.getIntValue());;
			}
		}
		return word;
	}
	
	public static YoavSentence searchHitToSentence(SearchHit hit)
	{
		YoavSentence sentence = new YoavSentence();
		Map<String, Object> src=hit.getSource();
		
		//	General sentence parameters
		String id=(String)	hit.getId();
		String docName=(String) src.get("doc_name");
		int docPosition=(Integer) src.get("doc_position");
		String text=(String) src.get("text");
        String genre=(String) src.get("genre");
        
        //	Dependency-related state
        String tokenizedText=(String) src.get("tokenized_text");
    	String dep_parse_source=(String) src.get("dep_parse_source");
        int root_location=(Integer) src.get("root_location");
        int length=(Integer) src.get("length");
        int height=(Integer) src.get("height");
        String root_pos=(String) src.get("root_pos");
        String pattern=(String) src.get("pattern");
        
    	//	Constituency-related parameters	
    	String const_parse_method=(String) src.get("const_parse_method");
    	String const_parse_format=(String) src.get("const_parse_format");
    	String const_parse_source=(String) src.get("const_parse_source");
    	String const_parse_contents=(String) src.get("const_parse_contents");
    	
    	//	General parameters
    	String general=(String) src.get("general"); 
        
        JSONObject wordsJsonObject;
        JSONArray wordsArray=null;
		try {
			wordsJsonObject = new JSONObject(hit.getSourceAsString());
        	wordsArray=wordsJsonObject.getJSONArray("words");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
        Vector<YoavWord> yoavWords=new Vector<YoavWord>();

        for (int j=0;j<wordsArray.length(); j++)
        {
        	try {
				yoavWords.add(DocumentAdder.jsonToWord(wordsArray.get(j).toString()));
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
        }
        
        sentence.setId(id);
        sentence.setDocName(docName);
        sentence.setDocPosition(docPosition);
        sentence.setText(text);
        sentence.setTokenizedText(tokenizedText);
        sentence.setWords(yoavWords);
        sentence.setGenre(genre);
        sentence.setRootLocation(root_location);
        sentence.setHeight(height);
        sentence.setLength(length);
        sentence.setRootPos(root_pos);
        sentence.setPattern(pattern);
        sentence.setWords(yoavWords);
        sentence.setDepParseSource(dep_parse_source);
        sentence.setConstParseMethod(const_parse_method);
        sentence.setConstParseSource(const_parse_source);
        sentence.setConstParseFormat(const_parse_format);
        sentence.setConstParseContents(const_parse_contents);
        sentence.setGeneral(general);
		
		return sentence;
	}
	
	/**
     * function to escape the string from bad chars for the search
     *
     * @param str the String that should be escaped
     * @return an escaped String
     */
    public static String smartEscapeString(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '\\' || c == '+' || c == '-' || c == '!' || 
            		c =='(' || c == ')' || c == ':' || c == '^' || 
            		c == '[' || c == ']' || c == '\"'
            		|| c == '{' || c == '}' || c == '~'
                    || c == '?' || c == '|' || c == '&' || c == ';'
                    || (!Character.isSpaceChar(c) && Character.isWhitespace(c))) 
            {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }

	public static YoavSentence getResponseToSentence(
			GetResponse hit) {
		YoavSentence sentence = new YoavSentence();
		Map<String, Object> src=hit.getSource();
		
		//	General sentence parameters
		String id=(String)	hit.getId();
		String docName=(String) src.get("doc_name");
		int docPosition=(Integer) src.get("doc_position");
		String text=(String) src.get("text");
        String genre=(String) src.get("genre");
        
        //	Dependency-related state
        String tokenizedText=(String) src.get("tokenized_text");
    	String dep_parse_source=(String) src.get("dep_parse_source");
        int root_location=(Integer) src.get("root_location");
        int length=(Integer) src.get("length");
        int height=(Integer) src.get("height");
        String root_pos=(String) src.get("root_pos");
        String pattern=(String) src.get("pattern");
        
    	//	Constituency-related parameters	
    	String const_parse_method=(String) src.get("const_parse_method");
    	String const_parse_format=(String) src.get("const_parse_format");
    	String const_parse_source=(String) src.get("const_parse_source");
    	String const_parse_contents=(String) src.get("const_parse_contents");
    	
    	//	General parameters
    	String general=(String) src.get("general"); 
        
        JSONObject wordsJsonObject;
        JSONArray wordsArray=null;
		try {
			wordsJsonObject = new JSONObject(hit.getSourceAsString());
        	wordsArray=wordsJsonObject.getJSONArray("words");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
        Vector<YoavWord> yoavWords=new Vector<YoavWord>();

        for (int j=0;j<wordsArray.length(); j++)
        {
        	try {
				yoavWords.add(DocumentAdder.jsonToWord(wordsArray.get(j).toString()));
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
        }
        
        sentence.setId(id);
        sentence.setDocName(docName);
        sentence.setDocPosition(docPosition);
        sentence.setText(text);
        sentence.setTokenizedText(tokenizedText);
        sentence.setWords(yoavWords);
        sentence.setGenre(genre);
        sentence.setRootLocation(root_location);
        sentence.setHeight(height);
        sentence.setLength(length);
        sentence.setRootPos(root_pos);
        sentence.setPattern(pattern);
        sentence.setWords(yoavWords);
        sentence.setDepParseSource(dep_parse_source);
        sentence.setConstParseMethod(const_parse_method);
        sentence.setConstParseSource(const_parse_source);
        sentence.setConstParseFormat(const_parse_format);
        sentence.setConstParseContents(const_parse_contents);
        sentence.setGeneral(general);
		
		return sentence;
	}
	
}
