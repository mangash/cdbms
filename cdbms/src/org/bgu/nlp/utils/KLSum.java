package org.bgu.nlp.utils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.bgu.nlp.elasticsearch.DocumentAdder;
import org.bgu.nlp.internal.format.YoavSentence;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

public class KLSum {
	public static Double CalculateKLDivergance (ProbDist distribA, ProbDist distribB, double smoothing_factor)
	{
		if (smoothing_factor < 0)
		{
			smoothing_factor = 0.0000001;
		}
		//create a union set of all available values of the distribution
		//		HashSet<Integer> valUnion = new HashSet<Integer>();
		//		valUnion.addAll(distribA.lengthProbDist.keySet());
		//		valUnion.addAll(distribB.lengthProbDist.keySet());
		double kl = 0;
		for (Entry<Integer, Integer> entry : distribA.heightProbDist.entrySet()) {
			double valA = ((double)entry.getValue())/distribA.totalDocs;
			if (valA == 0)
				continue;
			double valB = 0;
			if (distribB.heightProbDist.containsKey(entry.getKey()))
			{
				valB = ((double)distribB.heightProbDist.get(entry.getKey())/distribB.totalDocs);
			}

			// Apply smoothing factor.
			valB = (valB + smoothing_factor) /
					(1 + distribA.heightProbDist.size() * smoothing_factor);

			// Update divergence.
			kl += valA * Math.log(valA / valB);
		}

		for (Entry<Integer, Integer> entry : distribA.lengthProbDist.entrySet()) {
			double valA = ((double)entry.getValue())/distribA.totalDocs;
			if (valA == 0)
				continue;
			double valB = 0;
			if (distribB.lengthProbDist.containsKey(entry.getKey()))
			{
				valB = ((double)distribB.lengthProbDist.get(entry.getKey())/distribB.totalDocs);
			}

			// Apply smoothing factor.
			valB = (valB + smoothing_factor) /
					(1 + distribA.lengthProbDist.size() * smoothing_factor);

			// Update divergence.
			kl += valA * Math.log(valA / valB);
		}

		for (Entry<String, Integer> entry : distribA.genreProbDist.entrySet()) {
			double valA = entry.getValue();
			if (valA == 0)
				continue;
			double valB = 0;
			if (distribB.genreProbDist.containsKey(entry.getKey()))
			{
				valB = ((double)distribB.genreProbDist.get(entry.getKey())/distribB.totalDocs);
			}

			// Apply smoothing factor.
			valB = (valB + smoothing_factor) /
					(1 + distribA.genreProbDist.size() * smoothing_factor);

			// Update divergence.
			kl += valA * Math.log(valA / valB);
		}

		return kl/3;
	}

	public static void pickRepresentingHitsHigh (LinkedList<SearchHit> hitList ,SearchHit[] hits, int limit)
	{
		HashSet<Integer> pickedSet = new HashSet<Integer>();
		if (limit>=hits.length)
		{
			for (SearchHit hit:hits)
			{
				hitList.add(hit);
			}
			return;
		}
		//Initialize a probDist with the length, height
		//an genre distributions for the entire corpora
		ProbDist globalDistrib=new ProbDist(new SearchSourceBuilder());
		ProbDist localDistrib=new ProbDist();
		//Pick suitable terms, starting from offset
		int count=0;
		while (count<limit)
		{
			double currKLD=Double.MAX_VALUE;
			int currI=-1;
			YoavSentence currSent=new YoavSentence();
			for (int i=0; i<hits.length;i++)
			{
				if (!pickedSet.contains(i))
				{
					SearchHit hit=hits[i];
					YoavSentence sent=DocumentAdder.searchHitToSentence(hit);
					localDistrib.incDistrib(sent.getLength(),sent.getHeight(),sent.getGenre());
					double newKLD=CalculateKLDivergance(globalDistrib, localDistrib, -1);
					if (newKLD<currKLD)
					{
						currI=i;
						currKLD=newKLD;
					}
					localDistrib.decDistrib(sent.getLength(),sent.getHeight(),sent.getGenre());
				}
			}
			currSent=DocumentAdder.searchHitToSentence(hits[currI]);
			localDistrib.incDistrib(currSent.getLength(),currSent.getHeight(),currSent.getGenre());
			hitList.add(hits[currI]);
			pickedSet.add(currI);
			count++;
			System.out.println("chose " + currI);
		}

	}

	public static void pickRepresentingHitsMedium (LinkedList<SearchHit> hitList ,SearchHit[] hits, int limit)
	{
		HashSet<Integer> pickedSet = new HashSet<Integer>();
		if (limit>=hits.length)
		{
			for (SearchHit hit:hits)
			{
				hitList.add(hit);
			}
			return;
		}
		//Initialize a probDist with the length, height
		//an genre distributions for the entire corpora
		ProbDist globalDistrib=new ProbDist(new SearchSourceBuilder());
		ProbDist localDistrib=new ProbDist();
		//Pick suitable terms, starting from offset
		int count=0;
		for (int j=0; j<limit;j++)
		{
			double currKLD=Double.MAX_VALUE;
			int currI=-1;
			YoavSentence currSent=new YoavSentence();
			for (int i=j*(hits.length/limit);i<j*(hits.length/limit)+(hits.length/limit);i++)
			{
				if (!pickedSet.contains(i))
				{
					SearchHit hit=hits[i];
					YoavSentence sent=DocumentAdder.searchHitToSentence(hit);
					localDistrib.incDistrib(sent.getLength(),sent.getHeight(),sent.getGenre());
					double newKLD=CalculateKLDivergance(globalDistrib, localDistrib, -1);
					if (newKLD<currKLD)
					{
						currI=i;
						currKLD=newKLD;
					}
					localDistrib.decDistrib(sent.getLength(),sent.getHeight(),sent.getGenre());
				}
			}
			currSent=DocumentAdder.searchHitToSentence(hits[currI]);
			localDistrib.incDistrib(currSent.getLength(),currSent.getHeight(),currSent.getGenre());
			hitList.add(hits[currI]);
			pickedSet.add(currI);
			count++;
			System.out.println("chose " + currI);
		}

	}
}

