package org.bgu.nlp.utils;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.bgu.nlp.HebFNProperties;
import org.bgu.nlp.elasticsearch.DocumentAdder;
import org.bgu.nlp.internal.format.YoavSentence;
import org.elasticsearch.search.SearchHit;

public class Diversifyer {
	private int N;
	private final double timeout = 30*1000;// (ms)
	private double memoArray[][];
	private boolean dirtyArray[][]; // mark the "dirty" places in the memo table
	public Diversifyer(int N){
		this.N = N;
		memoArray = new double[N][N];
		dirtyArray = new boolean[N][N];
		for (int i=0;i<N;i++)
			for(int j=0;j<N;j++){
				memoArray[i][j] = -1;
				dirtyArray[i][j] = true;
			}
	}
	public Diversifyer(){
		this(HebFNProperties.SYNTACTIC_VARIATION_TOP_RESULTS_SIZE);
	}
	/**
	 * Make sure vector norm is 1
	 * @param vec Vector to normalize
	 * @return 
	 */
	private static Vector<Double> normalize(Vector<Double> vec){
		Vector<Double> ans = new Vector<Double>();
		double norm = dist(vec,vec);
		if (norm == 0)
			return vec;
		for (Double d : vec)
			ans.add(d/norm);
		return ans;
	}
	private static double square(double a) {return a*a;} 
	/**
	 * Euclidean distance function between two sentences
	 * @param sen1 First sentence
	 * @param sen2 Second sentence
	 * @return Distance
	 */
	private static double dist(senAndDoc sen1, senAndDoc sen2){
		Vector<Double> vec1 = sen1.vec;
		Vector<Double> vec2 = sen2.vec;
		return dist(vec1,vec2);
	}
	private static double dist(Vector<Double> vec1, Vector<Double> vec2){
		double sum=0;
		ListIterator<Double> it1 = vec1.listIterator();
		ListIterator<Double> it2 = vec2.listIterator();
		while(it1.hasNext() && it2.hasNext())
			sum += square(it1.next()-it2.next());
		return Math.sqrt(sum);
	}
	/**
	 * Creates a real number vector from a sentence
	 * @param sen Sentence to turn into vector
	 * @return real number vector representing syntactic properties
	 */
	private static Vector<Double> makeSyntacticVector(YoavSentence sen) {
		Vector<Double> ret = new Vector<Double>();

		ret.add(new Double(sen.getLength()/60));
		ret.add(new Double(sen.getHeight()/30));
		ret.add(new Double(sen.getRootLocation()/sen.getLength()));
		ret.add(posToNumber(sen.getRootPos()));
		ret.add(patternToNumber(sen.getPosPattern()));
		return ret;
	}
	
	private static Double patternToNumber(String pattern) {
		double ret = 0;
		String [] posArray = pattern.split(Constants.SENTENCE_PATTERN_DELIMITER);
		double count = posArray.length;
		double offset = 0;
		for (String pos : posArray){
			ret += offset  + (posToNumber(pos) / count);
			offset += 1.0/count;
		}
		return new Double(ret);
	}
	private static double posToNumber(String pos) {
		if (pos.equals("VB"))
			return 0.1;
		else if (pos.equals("VB-TOINFINITIVE"))
			return 0.13;
		else if (pos.equals("PUNC"))
			return 0.9;
		else if (pos.equals("PREPOSITION"))
			return 0.8;
		else if (pos.equals("PRP-DEM"))
			return 0.81;
		else if (pos.equals("JJ"))
			return 0.92;
		else if (pos.equals("PRP"))
			return 0.82;
		else if (pos.equals("NN"))
			return 0.5;
		else if (pos.equals("NN_S_PP"))
			return 0.52;
		else if (pos.equals("S_PRN"))
			return 0.56;
		else if (pos.equals("NNT"))
			return 0.53;
		else if (pos.equals("NNP"))
			return 0.47;
		else if (pos.equals("CC-SUB"))
			return 0.7;
		else if (pos.equals("CONJ"))
			return 0.64;
		else if (pos.equals("CC-COORD"))
			return 0.68;
		else if (pos.equals("CC-REL"))
			return 0.66;
		else if (pos.equals("CD"))
			return 0.65;
		else if (pos.equals("CDT"))
			return 0.64;
		else if (pos.equals("REL-SUBCONJ"))
			return 0.75;
		else if (pos.equals("TEMP-SUBCONJ"))
			return 0.76;
		else if (pos.equals("RB"))
			return 0.4;
		else if (pos.equals("AT"))
			return 0.85;
		else if (pos.equals("DT"))
			return 0.87;
		else if (pos.equals("JJT"))
			return 0.95;
		else if (pos.equals("COP"))
			return 0.05;
		else if (pos.equals("DEF"))
			return 0.48;
		else if (pos.equals("MD"))
			return 0.62;
		else if (pos.equals("BN"))
			return 0.32;
		else if (pos.equals("BNT"))
			return 0.33;
		else if (pos.equals("PRP-PERS"))
			return 0.78;
		else if (pos.equals("P"))
			return 0.88;
		else if (pos.equals("QW"))
			return 0.98;
		else if (pos.equals("COP-TOINFINITIVE"))
			return 0.16;
		else if (pos.equals("EX"))
			return 0.18;
		else if (pos.equals("CC"))
			return 0.69;
		else if (pos.equals("DTT"))
			return 0.14;
		else if (pos.equals("INTJ"))
			return 0.93;
		else if (pos.equals("POS")) // TODO what's POS?
			return 0.01;
		else if (pos.equals("TTL")) // TODO what's POS?
			return 0.03;
		else if (pos.equals("fail") || pos.equals("JNK")){
			return 0;
		}
		//System.out.println("never heard of: "+pos);
		return 0;
	}
	
	/**
	 * Calculates the distance sum, divided by N. To be MaxSummed over
	 * @param set result set
	 * @return the sum of the distances. negative if set has identical sentences
	 */
	private double f(Vector<senAndDoc> set){
		double sum = 0;
		int identicals = 0;

		for (int i=0;i<N;i++)
			for (int j=i+1;j<N;j++){
				if (dirtyArray[i][j]){
					memoArray[i][j] = dist(set.elementAt(i),set.elementAt(j));
					dirtyArray[i][j] = false;
				}
				if (memoArray[i][j] == 0) // identicals, very very bad
					identicals ++;
				sum += memoArray[i][j];
			}
		if (identicals > 0)
			return -identicals;
		else
			return sum/N;
	}
	
	private static class senAndDoc{
		public YoavSentence sen;
		public SearchHit doc;
		public Vector<Double> vec;
		public senAndDoc(YoavSentence sen, SearchHit hit, Vector<Double> vec) {
			this.sen = sen;
			this.doc = hit;
			this.vec = vec;
		}
	}
	
	private static String synVectorToString(Vector<Double> vec){
		StringBuilder builder = new StringBuilder();
		ListIterator<Double> it = vec.listIterator();
		builder.append("(");
		if (it.hasNext())
			builder.append(new DecimalFormat("#.###").format(it.next()));
		while (it.hasNext())
			builder.append(";"+new DecimalFormat("#.###").format(it.next()));
		builder.append(")");
		return builder.toString();
	}
	
	public void diversify(List<SearchHit> hitsList) throws Exception {
		long startTime = System.currentTimeMillis();
		Vector<senAndDoc> topSet = new Vector<senAndDoc>();
		if (hitsList.size()<N){
			System.out.println("too small to diversify: only "+hitsList.size()+" results");
			return;
		}
		System.out.println("diversifying, total results = "+hitsList.size()+"; N = "+N+
				"; populating initial set");
		populateInitialSet(topSet,hitsList.subList(0, N));
		System.out.println("initial set has: "+topSet.size()+" sentences. f(initial)="+f(topSet));
		double bestScore = f(topSet);
		for (SearchHit hit : hitsList.subList(0, hitsList.size())){
			YoavSentence newSen = DocumentAdder.searchHitToSentence(hit);
			senAndDoc newSenDoc = new senAndDoc(newSen, hit, makeSyntacticVector(newSen));
			int i;
			for (i=0;i<N;i++){
				Vector<senAndDoc> newSet = new Vector<senAndDoc>();
				newSet.addAll(topSet);
				newSet.remove(i);
				newSet.add(i,newSenDoc);
				makeMemoDirty(i);	// mark "true" in dirtyArray
				double newScore = f(newSet);
				if (newScore > bestScore){
					topSet = newSet;
					bestScore = newScore;
					// debugging
					Vector<Double> vec = makeSyntacticVector(newSen);
					System.out.println("Switched in: "+synVectorToString(vec)+
							". new f(set) = "+f(topSet));
					break;
				}
			}
			if (System.currentTimeMillis() - startTime > timeout){
				System.out.println("Calculation timed out.");
				break;
			}
		}
		makeMemoDirty();
		System.out.println("Final set has: "+topSet.size()+" sentences. f(final)="+bestScore);
		System.out.println("Diversification took "+(double)(System.currentTimeMillis()-startTime)/1000+" seconds.");
		if (topSet.size() != N)
			throw new Exception("Error in diversification algorithm: set size getting out of hand");
		hitsList.clear();
		for (senAndDoc hit : topSet)
			hitsList.add(hit.doc);
	}
	private void makeMemoDirty(int index) {
		for (int i=0;i<N;i++){
			this.dirtyArray[index][i] = true;
			this.dirtyArray[i][index] = true;
		}
	}
	private void makeMemoDirty() {
		for (int i=0;i<N;i++)
			for (int j=0;j<N;j++)
				this.dirtyArray[i][j] = true;
	}
	private void populateInitialSet(Vector<senAndDoc> topSet,
			List<SearchHit> hitList) {
		for (SearchHit hit : hitList){
				YoavSentence sen=DocumentAdder.searchHitToSentence(hit);
				topSet.add(new senAndDoc(sen, hit, makeSyntacticVector(sen)));
		}
	}
}
