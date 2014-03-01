package org.bgu.nlp.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.bgu.nlp.HebFNProperties;
import org.bgu.nlp.elasticsearch.ClientBuilder;
import org.bgu.nlp.elasticsearch.DocumentAdder;
import org.bgu.nlp.internal.format.YoavSentence;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

public class ProbDist {
	public HashMap<Integer, Integer> lengthProbDist= new HashMap<Integer, Integer>();
	public HashMap<Integer, Integer> heightProbDist= new HashMap<Integer, Integer>();
	public HashMap<String, Integer> genreProbDist= new HashMap<String, Integer>();
	public long totalDocs=0;

	public ProbDist (SearchSourceBuilder ssb)
	{
		Client client = ClientBuilder.client();
		AggregationBuilder lengthAb=AggregationBuilders.histogram("length").field("length").interval(1);
		AggregationBuilder heightAb=AggregationBuilders.histogram("height").field("height").interval(1);
		AggregationBuilder genreAb=AggregationBuilders.terms("genre").field("genre");
		ssb.aggregation(lengthAb).aggregation(heightAb).aggregation(genreAb);
		SearchResponse globalAggResponse = client.prepareSearch(HebFNProperties.ELASTIC_SEARCH_INDEX_NAME)
				.setTypes(HebFNProperties.ELASTIC_SEARCH_DOCUMENT_TYPE)
				.internalBuilder(ssb)
				.setSize(0)
				.execute()
				.actionGet();
		totalDocs=globalAggResponse.getHits().getTotalHits();
		try {
			JSONObject aggJsonObj = new JSONObject(globalAggResponse.toString()).getJSONObject("aggregations");
			JSONArray lengthAgg=aggJsonObj.getJSONArray("length");
			JSONArray heightAgg=aggJsonObj.getJSONArray("height");
			JSONArray genreAgg=aggJsonObj.getJSONObject("genre").getJSONArray("buckets");
			for (int i=0;i<lengthAgg.length();i++)
			{
				Integer length = (int) lengthAgg.getJSONObject(i).get("key");
				Integer count = (int) lengthAgg.getJSONObject(i).get("doc_count");
				lengthProbDist.put(length, count);
			}
			for (int i=0;i<heightAgg.length();i++)
			{
				Integer height = (int) heightAgg.getJSONObject(i).get("key");
				Integer count = (int) heightAgg.getJSONObject(i).get("doc_count");
				heightProbDist.put(height, count);
			}
			for (int i=0;i<genreAgg.length();i++)
			{
				String genre = (String) genreAgg.getJSONObject(i).get("key");
				Integer count = (int) genreAgg.getJSONObject(i).get("doc_count");
				genreProbDist.put(genre, count);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public ProbDist ()
	{

	}
	
	public void incDistrib(int length,int height,String genre)
	{
		totalDocs=totalDocs+1;
		if (lengthProbDist.containsKey(length))
		{
			int value = lengthProbDist.get(length);
			lengthProbDist.put(length, value+1);
		}
		else
		{
			lengthProbDist.put(length, 1);
		}
		if (heightProbDist.containsKey(height))
		{
			int value = heightProbDist.get(height);
			heightProbDist.put(height, value+1);
		}
		else
		{
			heightProbDist.put(height, 1);
		}
		if (genreProbDist.containsKey(genre))
		{
			int value = genreProbDist.get(genre);
			genreProbDist.put(genre, value+1);
		}
		else
		{
			genreProbDist.put(genre, 1);
		}
	}

	public void decDistrib(int length,int height,String genre)
	{
		if ((lengthProbDist.containsKey(length))&&
				(heightProbDist.containsKey(height))&&
						(genreProbDist.containsKey(genre)))
		{
			totalDocs=totalDocs-1;
			int value = lengthProbDist.get(length);
			if (value>=1)
			{
				lengthProbDist.put(length, value-1);
			}
			value = heightProbDist.get(height);
			if (value>=1)
			{
				heightProbDist.put(height, value-1);
			}
			value = genreProbDist.get(genre);
			if (value>=1)
			{
				genreProbDist.put(genre, value-1);
			}
		}
	}
	
		
	public void printProbDists()
	{
		System.out.println("Length Probability Distribution:");
		for (Entry<Integer, Integer> entry : lengthProbDist.entrySet())
		{
			System.out.println(entry.getKey() + " : " + ((double)entry.getValue())/totalDocs);
		}
		System.out.println("\nHeight Probability Distribution:");
		for (Entry<Integer, Integer> entry : heightProbDist.entrySet())
		{
			System.out.println(entry.getKey() + " : " + ((double)entry.getValue())/totalDocs);
		}
		System.out.println("\nGenre Probability Distribution:");
		for (Entry<String, Integer> entry : genreProbDist.entrySet())
		{
			System.out.println(entry.getKey() + " : " + ((double)entry.getValue())/totalDocs);
		}
	}
}
