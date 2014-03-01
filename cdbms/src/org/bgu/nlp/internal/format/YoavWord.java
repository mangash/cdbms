package org.bgu.nlp.internal.format;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.bgu.nlp.utils.Constants;

public class YoavWord {
	private String properties=new String();
	private int index=-1;
	private double id=-1.0;
	private String word=new String();
	private String pos=new String();
	private String cpos=new String();
	private String deprel=new String();
	private String polar=new String();
	private String phead=new String();
	private String pdeprel=new String();
	private String lemma=new String();
	private String gender=new String();
	private String number=new String();
	private String person=new String();
	private String tense=new String();
	private String binyan=new String();
	private String type=new String();
	private double parid=-1;
	private String parword=new String();
	private String parpos=new String();
	private int height=-1;
	public YoavWord(int index, String properties)
	{
		this.properties=new String(properties);
		this.index=index;
		String[] props=this.properties.trim().split("\t");
		id= Double.parseDouble(props[0]);
		word=new String(props[1]);
		lemma=new String(props[2]);
		cpos=new String(props[3]);
		pos=new String(props[4]);
		
		if (!(props[5].equals("_")))
		{
			String[] features=props[5].trim().split("\\|");
			for (int i=0;i<features.length;i++)
			{
				String[] feature=features[i].trim().split("=");
				if (feature[0].equals("gen"))
				{
					if (gender.isEmpty())
						gender=feature[1];
					else
						gender="MF";
				}
				if (feature[0].equals("num"))
				{				
					number=feature[1];
				}
				if (feature[0].equals("per"))
				{				
					person=feature[1];
				}
				if (feature[0].equals("tense"))
				{				
					tense=feature[1];
				}
				if (feature[0].equals("type"))
				{				
					type=feature[1];
				}
				if (feature[0].equals("binyan"))
				{				
					binyan=feature[1];
				}
				if (feature[0].equals("polar"))
				{				
					polar=feature[1];
				}
			}
		}
		parid=Double.parseDouble(props[6]);
		deprel=new String(props[7]);
		phead=new String(props[8]);
		pdeprel=new String(props[9]);
	}

	// Empty constructor
	public YoavWord() {
	}
	
	//Getter for properties of this word
	public HashMap<String,Object> getProperties()
	{
		HashMap<String,Object> props=new HashMap<String,Object>(20);
		props.put("index", this.index);
		props.put("id", this.id);
		props.put("word", this.word);
		props.put("pos", this.pos);
		props.put("cpos", this.cpos);
		props.put("deprel", this.deprel);
		props.put("pdeprel", this.pdeprel);
		props.put("phead", this.phead);
		props.put("polar", this.polar);
		props.put("lemma", this.lemma);
		props.put("gender", this.gender);
		props.put("number", this.number);
		props.put("person", this.person);
		props.put("tense", this.tense);
		props.put("binyan", this.binyan);
		props.put("type", this.type);
		props.put("parid", this.parid);
		props.put("parword", this.parword);
		props.put("parpos", this.parpos);
		props.put("height", this.height);
		return props;
	}

	// Getter for word index (position in the sentence)
	public int getIndex()
	{
		return this.index;
	}
	
	// Getter for word id
	public double getId()
	{
		return this.id;
	}

	// Getter for the word itself
	public String getWord()
	{
		return this.word;
	}

	//	Getter for the word's part-of-speech
	public String getPos()
	{
		return this.pos;
	}

	//	Getter for the word's lemma
	public String getLemma()
	{
		return this.lemma;
	}
	
	//	Getter for the word's cpos
	public String getCPos()
	{
		return this.cpos;
	}

	//	Getter for the word's deprel
	public String getDepRel()
	{
		return this.deprel;
	}

	//	Getter for the polarity feature
	public String getPolar()
	{
		return this.polar;
	}
	
	//	Getter for the word's phead
	public String getPHead()
	{
		return this.phead;
	}
	
	//	Getter for the word's pdeprel
	public String getPDepRel()
	{
		return this.pdeprel;
	}
	
	//	Getter for the gender feature
	public String getGender()
	{
		return this.gender;
	}

	//	Getter for the number feature
	public String getNumber()
	{
		return this.number;
	}

	//	Getter for the person feature
	public String getPerson()
	{
		return this.person;
	}

	//	Getter for the tense feature
	public String getTense()
	{
		return this.tense;
	}
	
	//	Getter for the binyan feature
	public String getBinyan()
	{
		return this.binyan;
	}

	//	Getter for the type feature
	public String getType()
	{
		return this.type;
	}

	//	Getter for the parent id
	public double getParId()
	{
		return this.parid;
	}

	//	Getter for the parent word	
	public String getParWord()
	{
		return this.parword;
	}

	//	Getter for the parent part-of-speech
	public String getParPos()
	{
		return this.parpos;
	}

	//	Getter for the word height in the dependency tree
	public int getHeight()
	{
		return this.height;
	}

	//	Setter for the gender feature
	public void setGender (String newGender)
	{
		if (newGender!=null)
		{
			this.gender=new String(newGender);
		}
	}

	//	Setter for the number feature
	public void setNumber (String newNumber)
	{
		if (newNumber!=null)
		{		
			this.number=new String(newNumber);
		}
	}
	
	//	Setter for the polarity feature
	public void setPolar (String newPolar)
	{
		if (newPolar!=null)
		{		
			this.polar=new String(newPolar);
		}
	}

	//	Setter for the person feature
	public void setPerson (String newPerson)
	{
		if (newPerson!=null)
		{
			this.person=new String(newPerson);
		}
	}

	//	Setter for the tense feature
	public void setTense (String newTense)
	{
		if (newTense!=null)
		{
			this.tense=new String(newTense);
		}
	}
	
	//	Setter for the index
	public void setIndex (int newIndex)
	{
			this.index=newIndex;
	}
	
	//	Setter for the id
	public void setId (Double newId)
	{
			this.id=newId;
	}
	
	//	Setter for the deprel
	public void setDepRel (String newDepRel)
	{
			this.deprel=newDepRel;
	}
	
	//	Setter for the cpos
	public void setCPos (String newCPos)
	{
			this.cpos=newCPos;
	}
	
	//	Setter for the phead
	public void setPHead (String newPHead)
	{
			this.phead=newPHead;
	}
	
	//	Setter for the pdeprel
	public void setPDepRel (String newPDepRel)
	{
			this.pdeprel=newPDepRel;
	}
	
	//	Setter for the word
	public void setWord (String newWord)
	{
			this.word=newWord;
	}
	
	//	Setter for the word's part-of-speech
	public void setPos(String newPos) {
		this.pos=newPos;
	}
	
	//	Setter for the word's lemma
	public void setLemma(String newLemma) {
		this.lemma=newLemma;
	}

	//	Setter for the binyan feature
	public void setBinyan (String newBinyan)
	{
		if (newBinyan!=null)
		{
			this.binyan=new String(newBinyan);
		}
	}
	
	//	Setter for the type feature
	public void setType (String newType)
	{
		if (newType!=null)
		{
			this.type=new String(newType);
		}
	}

	//	Setter for the parent id
	public void setParId (double newId)
	{
		this.parid=newId;
	}

	//	Setter for the parent word
	public void setParWord (String newParWord)
	{
		if (newParWord!=null)
		{
			this.parword=new String(newParWord);
		}
	}

	//	Setter for the parent part-of-speech
	public void setParPos(String newParPos)
	{
		if (newParPos!=null)
		{
			this.parpos=new String(newParPos);
		}
	}

	//	Setter for the word height in the dependency tree
	public void setHeight(int newHeight)
	{
		this.height=newHeight;
	}

	public String toString()
	{
		return this.properties;
	}

	public String asConll() {
		StringBuilder sb=new StringBuilder();
		sb.append(this.id);
		sb.append(Constants.CONLL_MAIN_DELIMITER);
		sb.append(this.word);
		sb.append(Constants.CONLL_MAIN_DELIMITER);
		sb.append(this.lemma);
		sb.append(Constants.CONLL_MAIN_DELIMITER);
		sb.append(this.cpos);
		sb.append(Constants.CONLL_MAIN_DELIMITER);
		sb.append(this.pos);
		sb.append(Constants.CONLL_MAIN_DELIMITER);
		sb.append(this.featsAsConll());
		sb.append(Constants.CONLL_MAIN_DELIMITER);
		sb.append(this.parid);
		sb.append(Constants.CONLL_MAIN_DELIMITER);
		sb.append(this.deprel);
		sb.append(Constants.CONLL_MAIN_DELIMITER);
		sb.append(this.phead);
		sb.append(Constants.CONLL_MAIN_DELIMITER);
		sb.append(this.pdeprel);
		return sb.toString();
	}

	private String featsAsConll() {
		StringBuilder sb=new StringBuilder();
		List<String> nonEmptyFeats = new LinkedList<String>();
		if (!this.person.isEmpty())
			nonEmptyFeats.add("person="+this.person);
		if (!this.gender.isEmpty())
			nonEmptyFeats.add("gender="+this.gender);
		if (!this.number.isEmpty())
			nonEmptyFeats.add("number="+this.number);
		if (!this.binyan.isEmpty())
			nonEmptyFeats.add("binyan="+this.binyan);
		if (!this.tense.isEmpty())
			nonEmptyFeats.add("tense="+this.tense);
		if (!this.polar.isEmpty())
			nonEmptyFeats.add("polar="+this.polar);
		if (!this.type.isEmpty())
			nonEmptyFeats.add("type="+this.type);
		int counter=0;
		for (String feat : nonEmptyFeats)
		{
			sb.append(feat);
			counter++;
			if (counter<nonEmptyFeats.size())
				sb.append(Constants.CONLL_FEATS_DELIMITER);
		}
		return sb.toString();
	}

}
