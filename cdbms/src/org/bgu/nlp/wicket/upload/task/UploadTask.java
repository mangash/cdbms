package org.bgu.nlp.wicket.upload.task;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.bgu.nlp.HebFNProperties;
import org.bgu.nlp.elasticsearch.ClientBuilder;
import org.bgu.nlp.elasticsearch.DocumentAdder;
import org.bgu.nlp.exception.SentenceAnalyzerException;
import org.bgu.nlp.internal.format.SentenceProperties;
import org.bgu.nlp.internal.format.YoavSentence;
import org.bgu.nlp.wicket.model.ProgressModel;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;

public class UploadTask implements Runnable
{
	private ProgressModel pm;
	private File uploadedFile;
	private File dumpFile;
	private long startLine;
	private String genre;
	private boolean doDump;

	public UploadTask (ProgressModel progressModel, File uploadedFile, File dumpFile, String genre, long startLine, boolean doDump)
	{
		this.uploadedFile=uploadedFile;
		this.dumpFile=dumpFile;
		this.doDump=doDump;
		if (!genre.trim().isEmpty())
		{
			this.genre=genre;
		}
		else
		{
			this.genre=HebFNProperties.DEFAULT_SENTENCE_GENRE;
		}
		this.pm = progressModel;
		this.startLine=startLine;
	}

	@Override
	public void run ()
	{
		FileInputStream is=null;
		try {
			is = new FileInputStream(uploadedFile);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		BufferedReader requestBuffer=new BufferedReader(new InputStreamReader(is,Charset.forName("UTF-8")));
		String lineInput= "";
		//info("file MD5:"+ uploadedFile.getMD5());
		long linenum=0;
		long goodSentences=0;
		long badSentences=0;
		String doc;
		pm.status="Starting Indexing Process...";
		Client client = ClientBuilder.client();
		try {
			while ((lineInput=requestBuffer.readLine())!=null)
			{
				linenum++;
				if (linenum>=this.startLine)
				{
					if (!lineInput.trim().isEmpty())
					{
						try {
							SentenceProperties sentenceProperties = new SentenceProperties();
							sentenceProperties.setProperty("text", lineInput);
							sentenceProperties.setProperty("genre", this.genre);
							doc=new YoavSentence(sentenceProperties).toJson(false);
							client.prepareIndex(HebFNProperties.ELASTIC_SEARCH_INDEX_NAME,HebFNProperties.ELASTIC_SEARCH_DOCUMENT_TYPE)
							.setSource(doc)
							.execute();
							goodSentences++;
						} catch (SentenceAnalyzerException e) {
							if (!e.critical)
							{
								badSentences++;
								pm.status="Error while processing sentence number: "+linenum;
								if (doDump)
								{
									try {
										PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(dumpFile, true)));
										out.println(lineInput);
										out.close();
									} catch (IOException e1) {
										//oh noes!
									}
								}
							}
							else
							{
								throw e;
							}
						}
					}
					pm.status="Indexed Sentences : "+linenum+"\n Good Sentences: " + goodSentences+ "\n Bad Sentences: " + badSentences;
				}
				else
				{
					pm.status="Skipping sentence number: "+linenum;
				}
			}
			// some operations
			pm.status = "Finished processing a total of "+ linenum +" sentences\n Good Sentences: " + goodSentences+ "\n Bad Sentences: " + badSentences;
		} catch (IOException e2) {
			pm.status="Error while processing sentences from file. Stopped at sentence number: "+linenum;
			e2.printStackTrace();
		} catch (SentenceAnalyzerException e3)
		{
			pm.status="Error while processing sentences from file.\nCause: " + e3.message + "\nStopped at sentence number: "+linenum;
		}
		try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		uploadedFile.delete();
	}
}