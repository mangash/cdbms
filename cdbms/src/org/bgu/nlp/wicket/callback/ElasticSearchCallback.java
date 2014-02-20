package org.bgu.nlp.wicket.callback;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.request.resource.AbstractResource.WriteCallback;
import org.apache.wicket.request.resource.IResource;
import org.bgu.nlp.HebFNProperties;
import org.bgu.nlp.elasticsearch.ClientBuilder;
import org.bgu.nlp.elasticsearch.DocumentAdder;
import org.bgu.nlp.internal.format.YoavSentence;
import org.bgu.nlp.utils.Diversifyer;
import org.bgu.nlp.utils.KLSum;
import org.elasticsearch.action.search.SearchPhaseExecutionException;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.SearchHit;

public class ElasticSearchCallback extends WriteCallback{


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
			if (params.containsKey("query")&&(params.containsKey("hitsnum"))&&
					(params.containsKey("diversify"))&&(params.containsKey("pagenum")))
			{
				String queryText=params.get("query")[0];
				int hitsNum=Integer.parseInt(params.get("hitsnum")[0]);
				String diversify=params.get("diversify")[0];
				int pageNum=Integer.parseInt(params.get("pagenum")[0]);

				Client client = ClientBuilder.client();
				if (!("false".equalsIgnoreCase(diversify)))
				{
					try {

						SearchResponse countResponse = client.prepareSearch(HebFNProperties.ELASTIC_SEARCH_INDEX_NAME)
								.setTypes(HebFNProperties.ELASTIC_SEARCH_DOCUMENT_TYPE)
								.setExtraSource(queryText)
								.setSize(0)
								.execute()
								.actionGet();
						int count=(int)countResponse.getHits().getTotalHits();
						//int preDiversityHitsSize=Math.min(HebFNProperties.PRE_DIVERSIFICATION_MAX_QUERY_RESULTS, count);
						int preDiversityHitsSize=Math.min(hitsNum*50, count);

						SearchResponse response= client.prepareSearch(HebFNProperties.ELASTIC_SEARCH_INDEX_NAME)
								.setTypes(HebFNProperties.ELASTIC_SEARCH_DOCUMENT_TYPE)
								.setExtraSource(queryText)
								.setSize(preDiversityHitsSize)
								.setFrom(0)
								.execute()
								.actionGet();

						System.out.println ("requested a Pre-Diversification set of size: " + preDiversityHitsSize);

						SearchHit[] hits = response.getHits().getHits();
						LinkedList<SearchHit> hitsList=new LinkedList<SearchHit>();

						if ("medium".equalsIgnoreCase(diversify))
						{
							KLSum.pickRepresentingHitsMedium(hitsList, hits, hitsNum*10);
						}
						else if ("high".equalsIgnoreCase(diversify))
						{
							KLSum.pickRepresentingHitsHigh(hitsList, hits, hitsNum*10);
						}
						else if ("low".equalsIgnoreCase(diversify))
						{
							SearchHit[] tmpArr=Arrays.copyOfRange(hits, 0, Math.min(hits.length,hitsNum*10));
							hitsList =	new LinkedList<SearchHit>(Arrays.asList(tmpArr));	
						}

						System.out.println ("recieved a Pre-Diversification set of size: " + hitsList.size());
						StringBuilder sb=new StringBuilder();
						Diversifyer diversifyer=new Diversifyer(hitsList.size());
						try {
							diversifyer.diversify(hitsList);
							int hitsCounter=hitsList.size();
							System.out.println("num of results after diversification: " + hitsList.size());
							sb.append("{ \"requested_query\": ");
							sb.append(queryText);
							sb.append(", ");
							sb.append("\"hits\": [");
							int startIndex=Math.min(hitsNum*pageNum,hitsCounter);
							int endIndex=Math.min(startIndex+hitsNum,hitsCounter);
							int k=startIndex;
							for (SearchHit hit : hitsList.subList(startIndex, endIndex))
							{
								YoavSentence sentence=DocumentAdder.searchHitToSentence(hit);
								sb.append(sentence.toJson(true));
								k++;
								if (k<endIndex)
								{
									sb.append(",");
								}
							}
							sb.append("] }");
							writer.write(sb.toString());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					catch (SearchPhaseExecutionException spe)
					{
						writer.write(spe.getLocalizedMessage());
					}
				}
				else
				{
					try{
						SearchRequestBuilder srb=client.prepareSearch(HebFNProperties.ELASTIC_SEARCH_INDEX_NAME)
								.setTypes(HebFNProperties.ELASTIC_SEARCH_DOCUMENT_TYPE)
								.setSize(hitsNum)
								.setFrom(0)
								.setExtraSource(queryText);
						SearchResponse  response= srb.execute().actionGet();
						SearchHit[] hits = response.getHits().getHits();
						List<SearchHit> hitsList = new LinkedList<SearchHit>(Arrays.asList(hits));
						int hitsCounter=hitsList.size();
						StringBuilder sb=new StringBuilder();
						sb.append("{ \"requested_query\": ");
						sb.append(queryText);
						sb.append(", ");
						sb.append("\"hits\": [");
						int k=0;
						for (SearchHit hit : hitsList)
						{
							YoavSentence sentence=DocumentAdder.searchHitToSentence(hit);
							sb.append(sentence.toJson(true));
							k++;
							if (k<hitsCounter)
							{
								sb.append(",");
							}
						}
						sb.append("] }");
						writer.write(sb.toString());
					}
					catch (SearchPhaseExecutionException spe)
					{
						writer.write(spe.getLocalizedMessage());
					}
				}
			}
			else
			{
				writer.write("Missing Parameters. Expecting 'query' and 'hitsnum'");
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
