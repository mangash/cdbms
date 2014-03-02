package org.bgu.nlp.wicket.callback;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.AbstractResource.WriteCallback;
import org.bgu.nlp.HebFNProperties;
import org.bgu.nlp.elasticsearch.ClientBuilder;
import org.bgu.nlp.exception.SentenceAnalyzerException;
import org.bgu.nlp.internal.format.SentenceProperties;
import org.bgu.nlp.internal.format.YoavSentence;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;

public class AddCallback extends WriteCallback{

	@Override
	public void writeData(IResource.Attributes attributes) throws IOException
	{
		//	1.	get the POST request that was sent to this resource
		HttpServletRequest httpServletRequest = (HttpServletRequest) attributes.getRequest().getContainerRequest();
		httpServletRequest.setCharacterEncoding("UTF-8");

		//	2.	initialize a Http Response Writer. any data returned callback data
		//		should be written to this writer.
		OutputStream outputStream = attributes.getResponse().getOutputStream();
		Writer writer = new OutputStreamWriter( outputStream, Charset.forName("UTF-8"));

		//	3.	In case of a GET or a POST request, the request will be processed. 
		if (httpServletRequest.getMethod().equalsIgnoreCase("GET") ||
				httpServletRequest.getMethod().equalsIgnoreCase("POST"))
		{
			Map<String,String[]> params=httpServletRequest.getParameterMap();
			//	Initial sentence properties with default values
			SentenceProperties sentenceProperties = new SentenceProperties();
			if (!params.isEmpty())
			{
				// Update sentence properties according to parameters
				for (String property : params.keySet())
				{
					sentenceProperties.setProperty(property, params.get(property)[0]);
				}
			}
			String doc;
			try {
				// Based on the  POSTed sentence properties, create a new analyzed sentence
				YoavSentence resultSentence =new YoavSentence(sentenceProperties);
				//	If the preview mode is enabled, return the analyzed sentence without
				//	indexing it into to the database
				if (params.containsKey("preview")&&"true".equalsIgnoreCase(params.get("preview")[0]))
				{
					writer.write(resultSentence.toJson(false));
				}
				//	If preview mode is not enabled, proceed with indexing of the analyzed
				//	sentence
				else
				{
				Client client = ClientBuilder.client();
				IndexResponse response = client.prepareIndex(HebFNProperties.ELASTIC_SEARCH_INDEX_NAME,HebFNProperties.ELASTIC_SEARCH_DOCUMENT_TYPE)
						.setSource(resultSentence.toJson(false))
						.execute()
						.actionGet();
				resultSentence.setId(response.getId());
				doc=resultSentence.toJson(true);
				writer.write(doc);
				}
			} catch (SentenceAnalyzerException e) {
				writer.write("Error has occured while parsing sentence:\n" + e.message);
			}
		}
		//	In case of a request that is neither POST nor GET request, the the callback 
		//	will ignore the request altogether,	and state that the request should be a
		//	GET or a POST request.
		else
		{
			writer.write("only POST and GET requests are allowed");
		}
		//	4.	Close the output stream writer when done writing
		writer.close();
	}
}
