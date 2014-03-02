package org.bgu.nlp.internal.format;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.bgu.nlp.exception.SentenceAnalyzerException;
import org.bgu.nlp.utils.Constants;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentBuilderString;

public class YoavSentence {
	//	General sentence state 
	private String doc_name;
	private int doc_position=1;
	private String text;
	private String genre;
	private String id;

	//	Dependency-related state
	private String dep_parser_url;
	private String dep_parse_method;
	private String dep_parse_source;
	private String tokenized_text;
	private int length=-1;
	private int height=-1;
	private int root_location=-1;
	private String root_pos;
	private String pattern;
	private Vector<YoavWord> yoavWords;

	//	Constituency-related state	
	private String const_parser_url;
	private String const_parse_method;
	private String const_parse_format;
	private String const_parse_source;
	private String const_parse_contents="";

	//	General additions
	private String general;

	// Empty Constructor
	public YoavSentence() {
	}

	//	Constructor
	//	Mandatory Properties:
	//		"doc_name"			- the name of the document this sentence was extracted
	//							  from.
	//		"doc_position"		- the position of the sentence in the document it was
	//							  extracted from.
	//		"text" 				- the sentence that is about to be parsed
	//		"genre" 			- the genre of the sentence (i.e. "tapuz", "medical" etc.).
	//		"dep_parser_url" 	- the url for dependency parser
	//		"dep_parse_source"	- description of the source for dependency parsing
	//							  (i.e. "yoavDepParser","yaelDepParser" etc).
	//		"const_parse_method"- how was the constituency parsing done
	//							  ("auto" for parsing via the constituency parser
	//							   pointed by "const_parse_url", and
	//							   "manual" for manually entering the content of
	//							   constituency parsing).
	//		"const_parser_url" 	- the url for constituency parser (can be null)
	//		"const_parse_source"- description of the source for constituency parsing
	//							  (i.e. "yoavConstParser","manualTreeBank" etc).
	//		"const_parse_contents"- in case "const_parse_method" is set to "manual", this will
	//							  dictate the content of constituency parsing
	//		"const_parse_format"- what format is returned from constituency parser
	//							  (i.e. "treebank", "conll" etc)
	//		"general"			- Any extra information regarding the sentence
	
	//		"dep_parse_method"- how was the dependency parsing done
	//							  ("auto" for parsing via the constituency parser
	//							   pointed by "const_parse_url", and
	//							   "manual" for manually entering the content of
	//							   dependency parsing).

	public YoavSentence(SentenceProperties props) throws SentenceAnalyzerException
	{
		//	1.	Store the original sentence and analyzer URLs in this objects state
		this.doc_name=props.getProperty("doc_name");
		this.doc_position=Integer.parseInt(props.getProperty("doc_position"));
		this.text=props.getProperty("text");
		this.pattern=props.getProperty("pattern");
		//System.out.println("text : " + this.text);
		this.genre=props.getProperty("genre");
		//System.out.println("genre : " + this.genre);
		this.dep_parser_url=props.getProperty("dep_parser_url");
		//System.out.println("dep_parser_url : " + this.dep_parser_url);

		this.dep_parse_method=props.getProperty("dep_parse_method");
		//System.out.println("dep_parse_method : " + this.dep_parse_method);
		
		this.dep_parse_source=props.getProperty("dep_parse_source");
		//System.out.println("dep_parse_source : " + this.dep_parse_source);
		this.const_parser_url=props.getProperty("const_parser_url");
		//System.out.println("const_parser_url : " + this.const_parser_url);
		this.const_parse_method=props.getProperty("const_parse_method");
		//System.out.println("const_parse_method : " + this.const_parse_method);
		this.const_parse_source=props.getProperty("const_parse_source");
		//System.out.println("const_parse_source : " + this.const_parse_source);
		this.const_parse_format=props.getProperty("const_parse_format");
		//System.out.println("const_parse_format : " + this.const_parse_format);
		this.const_parse_contents=props.getProperty("const_parse_contents");
		//System.out.println("const_parse_format : " + this.const_parse_contents);
		this.general=props.getProperty("general");
		//System.out.println("general : " + this.general);
		
		//	2. Perform Dependency parsing on the sentence
		String line = "";
		StringBuilder sb=new StringBuilder();
		int i=0;
		if (!("manual".equals(this.dep_parse_method)))
		{
		
			//	3.	Analyze the sentence using Yoav's Dependency Parser
			//		(see the private method analyze() for more details)
			InputStream depInputStream;
			try {
				  
				depInputStream= analyze(this.text, this.dep_parser_url);
	
			}
			catch (SentenceAnalyzerException e)
			{
				e.critical=true;
				throw e;
			}
			//	4.	Initialize a Buffered reader using the stream that was returned from
			//		dependency analyzer
			InputStreamReader depInputStreamReader=new InputStreamReader(depInputStream,Charset.forName("UTF-8"));
			BufferedReader depResponseBuffer = new BufferedReader(depInputStreamReader);
	
			// 	5.	Initialize the yoavWords Vector by reading words from response,
			//		line by line (each line contains the properties of one yoavWord)
			this.yoavWords = new Vector<YoavWord>();
			try{
				while ((line = depResponseBuffer.readLine()) != null) {
					if (!line.trim().isEmpty())
					{
						YoavWord yoavWord = new YoavWord(i, line);
						this.yoavWords.add(yoavWord);
						sb.append(yoavWord.getWord());
						sb.append(" ");
						i++;
					}
				}
			} catch (IOException e)
			{
				throw new SentenceAnalyzerException("Problem reading response from parser at: " + this.dep_parser_url, true);
			}
		}
		else if ("manual".equals(this.dep_parse_method))
		{
			StringBuilder rawTextSb=new StringBuilder();
			BufferedReader reader = new BufferedReader(new StringReader(this.pattern));
			this.yoavWords = new Vector<YoavWord>();
			try{
				while ((line = reader.readLine()) != null) {
					if (!line.trim().isEmpty())
					{
						YoavWord yoavWord = new YoavWord(i, line);
						this.yoavWords.add(yoavWord);
						sb.append(yoavWord.getWord());
						sb.append(" ");
						if ((yoavWord.getId() % 1 == 0)&&(i!=0))
						{
							rawTextSb.append(" ");
						}
						rawTextSb.append(yoavWord.getWord());
						i++;
					}
				}
				this.text=rawTextSb.toString().trim();
			} catch (IOException e)
			{
				throw new SentenceAnalyzerException("Problem reading CONLL pattern", true);
			}
		}
		
		this.tokenized_text=sb.toString().trim();
		this.length=i;
		adjustWordsParentsInfo();
		//	Adding Alon's sentence properties
		adjustWordsHeightsInfo();
		adjustPatternInfo();

		//	6. Perform Constituency parsing on the sentence
		if (!("manual".equals(this.const_parse_method))&&!this.const_parser_url.isEmpty())
		{
			this.const_parse_method=new String("auto");
			//	6.1.	Analyze the sentence using Yoav's Constituency Parser
			//			(see the private method analyze() for more details)
			InputStream constInputStream;
			try {
				constInputStream = analyze(this.text, this.const_parser_url);
				//	6.2.	Initialize a Buffered reader using the stream that was returned from
				//			constituency analyzer
				InputStreamReader constInputStreamReader=new InputStreamReader(constInputStream,Charset.forName("UTF-8"));
				BufferedReader constResponseBuffer = new BufferedReader(constInputStreamReader);
				// 	6.3.	Initialize the sentence's constSource by reading the response
				line = "";
				sb=new StringBuilder();
				while ((line = constResponseBuffer.readLine()) != null) {
					if (!line.trim().isEmpty())
					{
						sb.append(line);
						sb.append("\n");
					}
				}
				this.const_parse_contents=sb.toString().trim();
			}
			catch (Exception e) {
				this.const_parse_contents=new String("");
			}
		}
		else if ("manual".equals(this.const_parse_method))
		{
			this.const_parse_contents=props.getProperty("const_parse_contents");
		}
	}


	private InputStream analyze(String sentenceInput, String analyzerURL) throws SentenceAnalyzerException
	{

		//	1.	Initialize a client for http Communication with Menny Adler's
		//		Hebrew Text Analyzer service
		HttpClient client = new DefaultHttpClient();

		//	2.	Create a POST request that will later be sent
		//		to Yoav's service via the http client.
		HttpPost postRequest = new HttpPost(analyzerURL);
		//HttpPost postRequest = new HttpPost("http://www.cs.bgu.ac.il/~yoavg/depparse/parse");

		//	3.	Create a Form Entity that will serve as the POST request's body.
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("text",sentenceInput));
		UrlEncodedFormEntity form;
		try {
			form = new UrlEncodedFormEntity(nameValuePairs,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new SentenceAnalyzerException("Unsupported Encoding Exception");
		}

		//	4.	Set the Form Entity as the POST request's body
		postRequest.setEntity(form);

		//	5.	Set some header parameters for the POST request
		postRequest.setHeader("Accept-Encoding", "UTF-8");

		//	6.	Send POST request and receive a response 
		HttpResponse response;
		try {
			response = client.execute(postRequest);
		} catch (ClientProtocolException e) {
			throw new SentenceAnalyzerException("Problem communicating with parser at: " + analyzerURL,true);
		} catch (IOException e) {
			throw new SentenceAnalyzerException("Problem communicating with parser at: " + analyzerURL,true);
		}
		int code=response.getStatusLine().getStatusCode();
		if (code!=HttpURLConnection.HTTP_OK)
		{
			throw new SentenceAnalyzerException("Bad Response from parser at: " + analyzerURL,true);
		}
		//	7.	return the body of the response
		try {
			return response.getEntity().getContent();
		} catch (IllegalStateException e) {
			throw new SentenceAnalyzerException("Received bad response from parser at: " + analyzerURL,true);
		} catch (IOException e) {
			throw new SentenceAnalyzerException("Received bad response from parser at: " + analyzerURL,true);
		}
	}


	//	This method is to be used after the yoavWords Vector has been
	//	initialized. it's purpose is to set the height parameter of all
	//	the words by tracing them down the sentence's dependency tree.
	private void adjustWordsParentsInfo()
	{
		for (int i=0;i<yoavWords.size();i++)
		{
			adjustWordParentInfo(yoavWords.elementAt(i));
		}
	}

	private void adjustWordParentInfo(YoavWord word)
	{
		YoavWord parent=getParent(word);
		if (parent!=null)
		{
			word.setParPos(parent.getPos());
			word.setParWord(parent.getWord());
		}
	}

	//	This method is to be used after the yoavWords Vector has been
	//	initialized. it's purpose is to set the height parameter of all
	//	the words by tracing them down the sentence's dependency tree.
	private void adjustWordsHeightsInfo()
	{
		for (int i=0;i<yoavWords.size();i++)
		{
			//	Adjust a single word height
			adjustWordHeightInfo(yoavWords.elementAt(i));
			//	Adjust global sentence height
			if (this.height<yoavWords.elementAt(i).getHeight())
			{
				this.height=yoavWords.elementAt(i).getHeight();
			}
		}
	}

	private void adjustWordHeightInfo(YoavWord word)
	{
		YoavWord parent=getParent(word);
		if (parent==null)
		{
			word.setHeight(0);
			//	Adjust global sentence root_location
			this.root_location=word.getIndex();
			//	Adjust global sentence root_pos
			this.root_pos=new String(word.getPos());
		}
		else
		{
			int parentHeight=parent.getHeight();
			if (parentHeight==-1)
			{
				adjustWordHeightInfo(parent);
				word.setHeight(parent.getHeight()+1);
			}
			else
			{
				word.setHeight(parentHeight+1);
			}
		}
	}

	public void adjustPatternInfo() {
		StringBuilder sb=new StringBuilder();
		for (int i=0;i<this.yoavWords.size();i++)
		{
			sb.append(this.yoavWords.elementAt(i).asConll());
			if (i!=this.yoavWords.size()-1)
				sb.append(Constants.SENTENCE_PATTERN_DELIMITER);
		}
		this.pattern = new String(sb.toString());
	}

	//Getter for a parent word of some word in the sentence
	private YoavWord getParent(YoavWord word)
	{
		double parentId=word.getParId();
		if (parentId==0)
		{
			return null;
		}
		else
		{
			return getWordById(parentId);
		}
	}

	//Getter for a word in the sentence by the word's id
	public YoavWord getWordById(double id)
	{
		for (int i=0;i<yoavWords.size();i++)
		{
			if (yoavWords.elementAt(i).getId()==id)
			{
				return yoavWords.elementAt(i);
			}
		}
		return null;
	}

	public String toJson(boolean includeId)
	{
		XContentBuilder xb = null;
		try {
			xb = jsonBuilder().startObject();
			if (includeId)
			{
				xb.field("_id",this.id);
			}
			xb.field("doc_name",this.doc_name);
			xb.field("doc_position",this.doc_position);
			xb.field("text",this.text);
			xb.field("tokenized_text",this.tokenized_text);
			xb.field("genre",this.genre);
			xb.field("length",this.length);
			xb.field("height",this.height);
			xb.field("root_location",this.root_location);
			xb.field("root_pos",this.root_pos);
			xb.field("pattern",this.pattern);
			xb.field("dep_parse_source",this.dep_parse_source);
			xb.field("const_parse_method",this.const_parse_method);
			xb.field("const_parse_format",this.const_parse_format);
			xb.field("const_parse_source",this.const_parse_source);
			xb.field("const_parse_contents",this.const_parse_contents);
			xb.field("general",this.general);


			//	Next, add an array of tokenized words. each word is represented
			//	by a separate JSON object and contains all the word's properties.
			xb.startArray("words");
			for (int i=0;i<this.yoavWords.size();i++)
			{
				xb.startObject();
				YoavWord word=(YoavWord)yoavWords.elementAt(i);
				HashMap<String,Object> wordProps=word.getProperties();
				for (Entry<String, Object> entry :wordProps.entrySet())
				{
					xb.field(new XContentBuilderString(entry.getKey().toString()),entry.getValue());
				}
				xb.endObject();
			}
			xb.endArray();
			xb.endObject();
			return xb.string();
		} catch (IOException e) {
			e.printStackTrace();
			return "Error During conversion to JSON";
		}
	}

	// Getter for the sentence words
	public Vector<YoavWord> getWords()
	{
		return this.yoavWords;
	}

	// Getter for the sentence doc name
	public String getDocName()
	{
		return this.doc_name;
	}
	
	// Getter for the sentence doc position
	public int getDocPosition()
	{
		return this.doc_position;
	}

	// Getter for the sentence raw text
	public String getText()
	{
		return this.text;
	}

	// Getter for the sentence id
	public String getId()
	{
		return this.id;
	}

	// Getter for the sentence tokenized text
	public String gettokenized_text() {
		return this.tokenized_text;
	}

	// Getter for the sentence length
	public int getLength()
	{
		return this.length;
	}


	public int getHeight() {
		return this.height;
	}

	public int getRootLocation() {
		return this.root_location;
	}

	public String getRootPos() {
		return this.root_pos;
	}

	public String getPattern() {
		return this.pattern;
	}
	
	public String getPosPattern() {
		StringBuilder sb=new StringBuilder();
		for (int i=0;i<this.yoavWords.size();i++)
		{
			sb.append(this.yoavWords.elementAt(i).getPos());
			if (i!=this.yoavWords.size()-1)
				sb.append(Constants.POS_PATTERN_DELIMITER);
		}
		return sb.toString();
	}

	public String getGenre()
	{
		return this.genre;
	}
	
	public void setDocName(String newDocName) {
		this.doc_name=newDocName;
	}

	public void setDocPosition(Integer newDocPosition) {
		this.doc_position=newDocPosition;
	}
	
	public void setText(String newText) {
		this.text=newText;
	}

	public void setId(String newId)
	{
		this.id=newId;
	}

	public void setTokenizedText(String new_tokenized_text) {
		this.tokenized_text=new_tokenized_text;
	}

	public void setWords(Vector<YoavWord> newWords) {
		this.yoavWords=newWords;
	}


	public void setGenre(String newGenre) {
		this.genre=newGenre;
	}

	public void setRootLocation(int new_root_location) {
		this.root_location=new_root_location;	
	}

	public void setHeight(int newHeight) {
		this.height=newHeight;
	}

	public void setRootPos(String new_root_pos) {
		this.root_pos=new_root_pos;		
	}

	public void setLength(int newLength) {
		this.length=newLength;
	}

	public void setPattern(String newPattern) {
		this.pattern=newPattern;
	}

	public void setDepParseSource(String newDepParseSource) {
		this.dep_parse_source=newDepParseSource;
	}

	public String getDepParseSource()
	{
		return this.dep_parse_source;
	}

	public void setConstParseSource(String newConstParseSource) {
		this.const_parse_source=newConstParseSource;
	}

	public String getConstParseSource()
	{
		return this.const_parse_source;
	}

	public void setConstParseMethod(String newConstParseMethod) {
		this.const_parse_method=newConstParseMethod;
	}

	public String getConstParseMethod()
	{
		return this.const_parse_method;
	}

	public void setConstParseFormat(String newConstParseFormat) {
		this.const_parse_format=newConstParseFormat;
	}

	public String getConstParseFormat()
	{
		return this.const_parse_format;
	}

	public void setConstParseContents(String newConstParseContents) {
		this.const_parse_contents=newConstParseContents;
	}

	public String getConstParseContents()
	{
		return this.const_parse_contents;
	}

	public void setGeneral(String newGeneral) {
		this.general=newGeneral;
	}

	public String getGeneral()
	{
		return this.general;
	}

}
