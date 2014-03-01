package org.bgu.nlp.wicket.callback;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.request.resource.AbstractResource.WriteCallback;
import org.apache.wicket.request.resource.IResource;
import org.bgu.nlp.HebFNProperties;
import org.bgu.nlp.elasticsearch.ClientBuilder;
import org.bgu.nlp.elasticsearch.DocumentAdder;
import org.bgu.nlp.internal.format.YoavSentence;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequestBuilder;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.client.Client;

public class IdSearchCallback extends WriteCallback{


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
		//		Otherwise, the the callback will ignore the request altogether,
		//		and state that the request should be a GET or a POST request.
		if (httpServletRequest.getMethod().equalsIgnoreCase("GET") ||
				httpServletRequest.getMethod().equalsIgnoreCase("POST"))
		{
			Map<String,String[]> params=httpServletRequest.getParameterMap();
			if (params.containsKey("id"))
			{
				String ids[]=params.get("id");
				Client client = ClientBuilder.client();
				MultiGetRequestBuilder mgrb = new MultiGetRequestBuilder(client);
				for (String id : ids)
				{
					mgrb.add(HebFNProperties.ELASTIC_SEARCH_INDEX_NAME, HebFNProperties.ELASTIC_SEARCH_DOCUMENT_TYPE, id);
				}
				MultiGetResponse  multiGetResponse= mgrb
							.execute()
							.actionGet();
			
				MultiGetItemResponse[] responses=multiGetResponse.getResponses();
				
				StringBuilder sb=new StringBuilder();
				sb.append("{ \"hits\": [");
				int k=0;
				int hitsCounter=responses.length;
				for (MultiGetItemResponse response : responses)
				{
					GetResponse hit = response.getResponse();
					YoavSentence sentence=new YoavSentence();
					try {
					sentence=DocumentAdder.getResponseToSentence(hit);
					sb.append("{ \"id\": \""+response.getId()+"\", ");
					sb.append("\"found\": true, ");
					sb.append("\"sentence\" : ");
					sb.append(sentence.toJson(true));
					sb.append("}");
					}
					catch (Exception e)
					{
						sb.append("{ \"id\": \""+ids[k]+"\", ");
						sb.append("\"found\": false}");
					}
					k++;
					if (k<hitsCounter)
					{
						sb.append(",");
					}
				}
					sb.append("] }");
					writer.write(sb.toString());
			}
			else
			{
				writer.write("Missing Parameters. Expecting 'id'");
			}
		}
		else
		{
			writer.write("only POST and GET requests are allowed");
		}
		//	4.	Close the output stream writer when done writing
		writer.close();
	}

}
