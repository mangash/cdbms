package org.bgu.nlp.query;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import org.bgu.nlp.HebFNProperties;
import org.bgu.nlp.utils.Constants;
import org.bgu.nlp.utils.Pair;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.MultiTermQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RegexpQueryBuilder;
import org.elasticsearch.index.query.SpanMultiTermQueryBuilder;
import org.elasticsearch.index.query.SpanNearQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

public class QueryParser {
	private HashMap<Integer,HashMap<String, HashMap<String, LinkedList<Pair<String,String[]>>>>> wordsParams;
	private HashMap<String, HashMap<String, LinkedList<Pair<String,String[]>>>> sentenceParams;
	private LinkedList<Pair<Pair<Integer, Integer>,Integer>> spanParams;
	private Properties queryParams;

	public QueryParser(HttpServletRequest httpServletRequest) {
		init(httpServletRequest);
	}
	
	public void init(HttpServletRequest httpServletRequest) {
		wordsParams=new HashMap<Integer,HashMap<String, HashMap<String, LinkedList<Pair<String,String[]>>>>>();
		sentenceParams=new HashMap<String, HashMap<String, LinkedList<Pair<String,String[]>>>>();
		spanParams = new LinkedList<Pair<Pair<Integer, Integer>,Integer>>();
		queryParams=new Properties();
		
		queryParams.setProperty("results", Integer.toString(HebFNProperties.DEFAULT_NUMBER_OF_QUERY_RESULTS));
		queryParams.setProperty("diversify", HebFNProperties.DEFAULT_DIVERSIFY);
		queryParams.setProperty("page", "1");
		
		Map<String,String[]> paramsMap=httpServletRequest.getParameterMap();
		Enumeration<String> paramsNames = httpServletRequest.getParameterNames();
		
		String param;
		while (paramsNames.hasMoreElements())
		{
			param=paramsNames.nextElement();
			if (isWordParam(param))
			{
				String[] tuple=param.toLowerCase().split("\\.");
				int wordIndex=Integer.parseInt(tuple[0].substring(1,tuple[0].length()));
				if (wordsParams.get(wordIndex)==null)
				{
					wordsParams.put(wordIndex, new HashMap<String, HashMap<String, LinkedList<Pair<String,String[]>>>>());					
				}
				if (wordsParams.get(wordIndex).get(tuple[2])==null)
				{
					wordsParams.get(wordIndex).put(tuple[2],new HashMap<String, LinkedList<Pair<String,String[]>>>());
				}
				if (wordsParams.get(wordIndex).get(tuple[2]).get(tuple[1])==null)
				{
					wordsParams.get(wordIndex).get(tuple[2]).put(tuple[1], new LinkedList<Pair<String,String[]>>());
				}
				wordsParams.get(wordIndex).get(tuple[2]).get(tuple[1]).add(new Pair<String,String[]>(param, paramsMap.get(param)));
			}
			else if (isSentenceParam(param))
			{
				String[] tuple=param.toLowerCase().split("\\.");
				if (sentenceParams.get(tuple[2])==null)
				{
					sentenceParams.put(tuple[2],new HashMap<String, LinkedList<Pair<String,String[]>>>());
				}
				if (sentenceParams.get(tuple[2]).get(tuple[1])==null)
				{
					sentenceParams.get(tuple[2]).put(tuple[1], new LinkedList<Pair<String,String[]>>());
				}
				sentenceParams.get(tuple[2]).get(tuple[1]).add(new Pair<String,String[]>(param, paramsMap.get(param)));
			}
			else if (isSpanParam(param))
			{
				String[] tuple=param.toLowerCase().split("\\~");
				int firstWordIndex=Integer.parseInt(tuple[0].substring(1,tuple[0].length()));
				int secondWordIndex=Integer.parseInt(tuple[1].substring(1,tuple[1].length()));
				Integer slopValue=Integer.parseInt(paramsMap.get(param)[0]);
				spanParams.add(new Pair<Pair<Integer,Integer>,Integer>(new Pair<Integer,Integer>(firstWordIndex,secondWordIndex),slopValue));
			}
			else if (param.equalsIgnoreCase("diversify") ||
					(param.equalsIgnoreCase("results")) ||
					(param.equalsIgnoreCase("page")))
			{
				queryParams.setProperty(param,paramsMap.get(param)[0].toLowerCase());
			}
		}
	}

	private QueryBuilder parseQuery(String[] paramKey, int index, String queryValue)
	{
		switch(paramKey[index].toLowerCase())
		{
		case "match":
			return QueryBuilders.matchQuery(paramKey[1], queryValue);
		case "match_all":
			return QueryBuilders.matchAllQuery();
		case "range":
			return parseRange(paramKey, index+1, queryValue);
		}
		return null;
	}
	
	private QueryBuilder parseRange (String[] paramKey, int index, String queryValue)
	{
		if (index==paramKey.length)
		{
			String[] range=queryValue.split("\\,");
			if (range.length==2)
			{
			int start = Integer.parseInt(range[0].trim());
			int end = Integer.parseInt(range[1].trim());
			return QueryBuilders.rangeQuery(paramKey[1]).from(start).to(end);
			}
			return null;
		}
		int val=Integer.parseInt(queryValue); 
		switch(paramKey[index].toLowerCase())
		{
		case "gt":
			return QueryBuilders.rangeQuery(paramKey[1]).gt(val);
		case "gte":
			return QueryBuilders.rangeQuery(paramKey[1]).gte(val);
		case "lt":
			return QueryBuilders.rangeQuery(paramKey[1]).lt(val);
		case "lte":
			return QueryBuilders.rangeQuery(paramKey[1]).lte(val);
		}
		return null;
	}
	
	public boolean isSpanParam(String param)
	{
		String[] tmp=param.split("\\.");
		if (tmp.length>1)
			return false;
		String[] tuple=param.split("\\~");
		if (tuple.length==2)
		{
			for (int i=0;i<tuple.length;i++)
			{
				if (!(tuple[0].substring(0,1).equalsIgnoreCase("W")) ||
					!(tuple[0].substring(1,tuple[0].length()).matches("\\d+")))
				{
						return false;
				}
			}
			return true;
		}
		return false;
	}
	
	public int getPage()
	{
		int pageNum=Integer.parseInt(queryParams.getProperty("page"))-1;
		if (pageNum<0)
			return 0;
		return pageNum;
	}
	
	private boolean isWordParam(String param)
	{
		String[] tuple=param.split("\\.");
		if ((tuple[0].substring(0,1).equalsIgnoreCase("W")) &&
			(tuple[0].substring(1,tuple[0].length()).matches("\\d+")))
		{
			return true;
		}
		return false;
	}

	private boolean isSentenceParam(String param)
	{
		String[] tuple=param.split("\\.");
		if ((tuple[0].length()==1) &&
			(tuple[0].equalsIgnoreCase("S")))
		{
			return true;
		}
		return false;
	}
	
	public void createSentenceQuery(BoolQueryBuilder bqb)
	{
		if (!sentenceParams.isEmpty())
		{
		if (sentenceParams.containsKey("must"))
		{
			for (Entry<String, LinkedList<Pair<String,String[]>>> entry: sentenceParams.get("must").entrySet())
			{
				for (Pair<String,String[]> pair :  entry.getValue())
				{
					String queryString=pair.getKey();
					String[] queryValues=pair.getValue();
					for (int i =0;i<queryValues.length;i++)
					{
						bqb.must(parseQuery(queryString.split("\\."),3,queryValues[i]));
					}
				}
			}
			createWordsQuery(bqb);
		}
		else
		{
			createWordsQuery(bqb);
		}
		if (sentenceParams.containsKey("must_not"))
		{
			for (Entry<String, LinkedList<Pair<String,String[]>>> entry: sentenceParams.get("must_not").entrySet())
			{
				for (Pair<String,String[]> pair :  entry.getValue())
				{
					String queryString=pair.getKey();
					String[] queryValues=pair.getValue();
					for (int i =0;i<queryValues.length;i++)
					{
						bqb.mustNot(parseQuery(queryString.split("\\."),3,queryValues[i]));
					}
				}
			}
		}
		if (sentenceParams.containsKey("should"))
		{
			for (Entry<String, LinkedList<Pair<String,String[]>>> entry: sentenceParams.get("should").entrySet())
			{
				for (Pair<String,String[]> pair :  entry.getValue())
				{
					String queryString=pair.getKey();
					String[] queryValues=pair.getValue();
					for (int i =0;i<queryValues.length;i++)
					{
						bqb.should(parseQuery(queryString.split("\\."),3,queryValues[i]));
					}
				}
			}
		}
		}
		else
		{
			createWordsQuery(bqb);
		}
	}
	
	public void createWordsQuery(BoolQueryBuilder sentBqb)
	{
		if (!wordsParams.entrySet().isEmpty())
		{
			for (Entry<Integer,HashMap<String, HashMap<String, LinkedList<Pair<String,String[]>>>>> wordEntry: wordsParams.entrySet())
			{
				BoolQueryBuilder wordBqb=new BoolQueryBuilder();
				if (wordEntry.getValue().containsKey("must"))
				{
					for (Entry<String, LinkedList<Pair<String,String[]>>> entry: wordEntry.getValue().get("must").entrySet())
					{
						for (Pair<String,String[]> pair :  entry.getValue())
						{
							String queryString=pair.getKey();
							String[] queryValues=pair.getValue();
							for (int i =0;i<queryValues.length;i++)
							{
								wordBqb.must(parseQuery(queryString.split("\\."),3,queryValues[i]));
							}
						}
					}
				}
				if (wordEntry.getValue().containsKey("must_not"))
				{
					for (Entry<String, LinkedList<Pair<String,String[]>>> entry: wordEntry.getValue().get("must_not").entrySet())
					{
						for (Pair<String,String[]> pair :  entry.getValue())
						{
							String queryString=pair.getKey();
							String[] queryValues=pair.getValue();
							for (int i =0;i<queryValues.length;i++)
							{
								wordBqb.mustNot(parseQuery(queryString.split("\\."),3,queryValues[i]));
							}
						}
					}
				}
				if (wordEntry.getValue().containsKey("should"))
				{
					for (Entry<String, LinkedList<Pair<String,String[]>>> entry: wordEntry.getValue().get("should").entrySet())
					{
						for (Pair<String,String[]> pair :  entry.getValue())
						{
							String queryString=pair.getKey();
							String[] queryValues=pair.getValue();
							for (int i =0;i<queryValues.length;i++)
							{
								wordBqb.should(parseQuery(queryString.split("\\."),3,queryValues[i]));
							}
						}
					}
				}
				//add the filtered query to the sentence boolean query
				FilterBuilder filterBuilder = FilterBuilders.nestedFilter("words", wordBqb);
				FilteredQueryBuilder fqb= new FilteredQueryBuilder(QueryBuilders.matchAllQuery(), filterBuilder);
				sentBqb.must(fqb);
			}
		}
		createSpanQuery(sentBqb);
	}
	
	private boolean canBeSpanned(int index,String property)
	{
		boolean flag=false;
		if ((wordsParams.containsKey(index))
				&&(wordsParams.get(index).containsKey("must"))
				&&(wordsParams.get(index).get("must").containsKey(property)))
		{
			for (Pair<String, String[]> pair: wordsParams.get(index).get("must").get(property))
					{
						String[] tuple=pair.getKey().split("\\.");
						if ((tuple.length>3)&&"match".equals(tuple[3]))
						{
							flag=true;
						}
					}
		}
		return flag;
	}
	
	public void createSpanQuery(BoolQueryBuilder sentBqb)
	{
		for (Pair<Pair<Integer,Integer>,Integer> pair : spanParams)
		{
			int firstWordIndex=pair.getKey().getKey();
			int secondWordIndex=pair.getKey().getValue();
			if (((canBeSpanned(firstWordIndex, "word"))||(canBeSpanned(firstWordIndex, "lemma"))||(canBeSpanned(firstWordIndex, "cpos"))||(canBeSpanned(firstWordIndex, "pos")))&&
			   ((canBeSpanned(secondWordIndex, "word"))||(canBeSpanned(secondWordIndex, "lemma"))||(canBeSpanned(secondWordIndex, "cpos"))||(canBeSpanned(secondWordIndex, "pos"))))
			{
				int slop = pair.getValue();
				//Take care of regular expression for first word
				StringBuilder first_regexp_str=new StringBuilder();
				first_regexp_str.append(Constants.REGEX_INDEX+Constants.REGEX_MAIN_DELIMITER);
				if (canBeSpanned(firstWordIndex, "word"))
				{
					String firstWordValue=wordsParams.get(firstWordIndex).get("must").get("word").getFirst().getValue()[0];
					first_regexp_str.append(firstWordValue+Constants.REGEX_MAIN_DELIMITER);
				}
				else
				{
					first_regexp_str.append(Constants.REGEX_MAIN_PLACEHOLDER+Constants.REGEX_MAIN_DELIMITER);
				}
				if (canBeSpanned(firstWordIndex, "lemma"))
				{
					String firstLemmaValue=wordsParams.get(firstWordIndex).get("must").get("lemma").getFirst().getValue()[0];
					first_regexp_str.append(firstLemmaValue+Constants.REGEX_MAIN_DELIMITER);
				}
				else
				{
					first_regexp_str.append(Constants.REGEX_MAIN_PLACEHOLDER+Constants.REGEX_MAIN_DELIMITER);
				}
				if (canBeSpanned(firstWordIndex, "cpos"))
				{
					String firstCPosValue=wordsParams.get(firstWordIndex).get("must").get("cpos").getFirst().getValue()[0];
					first_regexp_str.append(firstCPosValue+Constants.REGEX_MAIN_DELIMITER);
				}
				else
				{
					first_regexp_str.append(Constants.REGEX_MAIN_PLACEHOLDER+Constants.REGEX_MAIN_DELIMITER);
				}
				if (canBeSpanned(firstWordIndex, "pos"))
				{
					String firstPosValue=wordsParams.get(firstWordIndex).get("must").get("pos").getFirst().getValue()[0];
					first_regexp_str.append(firstPosValue+Constants.REGEX_MAIN_DELIMITER);
				}
				else
				{
					first_regexp_str.append(Constants.REGEX_MAIN_PLACEHOLDER+Constants.REGEX_MAIN_DELIMITER);
				}
				first_regexp_str.append(".*");
				//Take care of regular expression for second word
				StringBuilder second_regexp_str=new StringBuilder();
				second_regexp_str.append(Constants.REGEX_INDEX+Constants.REGEX_MAIN_DELIMITER);
				if (canBeSpanned(secondWordIndex, "word"))
				{
					String secondWordValue=wordsParams.get(secondWordIndex).get("must").get("word").getFirst().getValue()[0];
					second_regexp_str.append(secondWordValue+Constants.REGEX_MAIN_DELIMITER);
				}
				else
				{
					second_regexp_str.append(Constants.REGEX_MAIN_PLACEHOLDER+Constants.REGEX_MAIN_DELIMITER);
				}
				if (canBeSpanned(secondWordIndex, "lemma"))
				{
					String secondLemmaValue=wordsParams.get(secondWordIndex).get("must").get("lemma").getFirst().getValue()[0];
					second_regexp_str.append(secondLemmaValue+Constants.REGEX_MAIN_DELIMITER);
				}
				else
				{
					second_regexp_str.append(Constants.REGEX_MAIN_PLACEHOLDER+Constants.REGEX_MAIN_DELIMITER);
				}
				if (canBeSpanned(secondWordIndex, "cpos"))
				{
					String secondCPosValue=wordsParams.get(secondWordIndex).get("must").get("cpos").getFirst().getValue()[0];
					second_regexp_str.append(secondCPosValue+Constants.REGEX_MAIN_DELIMITER);
				}
				else
				{
					second_regexp_str.append(Constants.REGEX_MAIN_PLACEHOLDER+Constants.REGEX_MAIN_DELIMITER);
				}
				if (canBeSpanned(secondWordIndex, "pos"))
				{
					String secondPosValue=wordsParams.get(secondWordIndex).get("must").get("pos").getFirst().getValue()[0];
					second_regexp_str.append(secondPosValue+Constants.REGEX_MAIN_DELIMITER);
				}
				else
				{
					second_regexp_str.append(Constants.REGEX_MAIN_PLACEHOLDER+Constants.REGEX_MAIN_DELIMITER);
				}
				second_regexp_str.append(".*");
				
				
				//add the filtered query to the sentence boolean query
				RegexpQueryBuilder first_regex_query= new RegexpQueryBuilder("pattern", first_regexp_str.toString());
				RegexpQueryBuilder second_regex_query= new RegexpQueryBuilder("pattern", second_regexp_str.toString());
				SpanMultiTermQueryBuilder first_smtq = new SpanMultiTermQueryBuilder(first_regex_query);
				SpanMultiTermQueryBuilder second_smtq = new SpanMultiTermQueryBuilder(second_regex_query);

				SpanNearQueryBuilder snq = new SpanNearQueryBuilder()
				.clause(first_smtq)
				.clause(second_smtq)

				
//				.clause(QueryBuilders.spanTermQuery("text",firstWordValue))
//				.clause(QueryBuilders.spanTermQuery("text",secondWordValue))
				.slop(slop)
				.inOrder(true)
				.collectPayloads(false);
				sentBqb.must(snq);
			}
		}
	}
	
//	private QueryBuilder parseSpan (String[] paramKey, int index, String[] paramValue)
//	{
//		int slop = Integer.parseInt(paramValue[0]);
//		return QueryBuilders
//				.nestedQuery("words", QueryBuilders.spanNearQuery()
//				.clause(QueryBuilders.spanTermQuery("words.word","value1"))
//				.clause(QueryBuilders.spanTermQuery("words.word","value2"))
//				.slop(slop)
//				.inOrder(true)
//				.collectPayloads(false));
//			//return QueryBuilders.spanTermQuery(clauses, slop).doXContent(builder, params);
//		//return null;
//	}
	
	public String toJson()
	{
		SearchSourceBuilder ssb = new SearchSourceBuilder().query(QueryBuilders.boolQuery());
		BoolQueryBuilder bqb=new BoolQueryBuilder();
		createSentenceQuery(bqb);
		ssb.query(bqb);
		return(ssb.toString());
	}
	
	public BoolQueryBuilder getQuery()
	{
		BoolQueryBuilder bqb=new BoolQueryBuilder();
		createSentenceQuery(bqb);
		return bqb;
	}
	
	public boolean diversify()
	{
		if (!queryParams.containsKey("diversify"))
		{
			return false;
		}
		if ("false".equals(queryParams.getProperty("diversify")))
		{
			return false;
		}
		return true;
	}
	
	public String getDiversify()
	{
		if (!queryParams.containsKey("diversify"))
			return null;
		return queryParams.getProperty("diversify");
	}
	
	public int resultsNum()
	{
		if (queryParams.containsKey("results"))
		{
			return Integer.parseInt(queryParams.getProperty("results"));
		}
		return HebFNProperties.DEFAULT_NUMBER_OF_QUERY_RESULTS;
	}
}
