/********************************************************************************************************************
 * FILENAME: English_postagger.java
 * 
 * ROLE: English pos tagger
 * 		
 * VARIABLES: 
 * 	public String engTaggerLocation										// English pos tagger location
 * 
 * METHODS: 
 * 	public String tagger(String text, String engTaggerLocation)			// tagging to a text	
 * 	public String analyze(String temp)									// analysing morpheme whether it is valid or not
 * 
 * COMMENT:
 * 	stanford-postagger-3.6.0.jar is exploited
 * 
 ********************************************************************************************************************/
package f2.com.kirc.core.nlp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.AttributeFactory;
import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.Version;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import f1.com.kirc.core.config.Config;
import f1.com.kirc.core.config.Constants;

public class English_postagger {
	static MaxentTagger tagger = null;
	static EnglishAnalyzer analyzer = null;
	static CharArraySet stopWordSet = null;

	public static void main(String[] args){
		String NN_tokens = null;
		English_postagger engtagger = new English_postagger(new Config().engTaggerLocation);
		String str = "decision trees";
		//String str = "cats. someone. purpose. this is a sentence\nApple I bought in the local store nearby. this is another sentence\n Apple banana is good.";
		System.out.println("original:"+str);
		str = str.replaceAll("\n", Constants.INDICATOR_SYMBOL_NEW_LINE);
		str = str.replaceAll("\\.", Constants.INDICATOR_SYMBOL_PERIOD);  
		System.out.println("analyzed: "+tagger_NNphrase("0", str));

		//System.out.println("trim trim trim ".trim());
	}

	public English_postagger(String engTaggerLocation){
		CharArraySet stemExclusionSet = new CharArraySet(Version.LUCENE_4_9, 0, true);//CharArraySet.copy(Version.LUCENE_4_9,);
		stemExclusionSet.add(Constants.INDICATOR_CHARACTOR_PERIOD);
		stemExclusionSet.add(Constants.INDICATOR_CHARACTOR_NEW_LINE);

		stopWordSet = CharArraySet.copy(Version.LUCENE_4_9, EnglishAnalyzer.getDefaultStopSet());
		stopWordSet.add("ml");
		stopWordSet.add("l");
		stopWordSet.add("mg");//
		stopWordSet.add("mmhg");
		stopWordSet.add("ng");
		stopWordSet.add("hg");
		stopWordSet.add("g");
		stopWordSet.add("kg");
		stopWordSet.add("s");
		stopWordSet.add("ms");
		stopWordSet.add("sec");
		stopWordSet.add("min");
		stopWordSet.add("hour");
		stopWordSet.add("hours");
		stopWordSet.add("seconds");
		stopWordSet.add("miniutes");
		stopWordSet.add("o");
		stopWordSet.add("oz");
		stopWordSet.add("once");
		stopWordSet.add("mol");
		stopWordSet.add("m");
		stopWordSet.add("mm");
		stopWordSet.add("nm");
		stopWordSet.add("gram");
		stopWordSet.add("uml");
		stopWordSet.add("yo");
		stopWordSet.add("day");
		stopWordSet.add("days");
		stopWordSet.add("year");
		stopWordSet.add("week");
		stopWordSet.add("month");
		stopWordSet.add("years");
		stopWordSet.add("weeks");
		stopWordSet.add("months");
		stopWordSet.add("bpm");

		analyzer =new EnglishAnalyzer(Version.LUCENE_4_9, stopWordSet, stemExclusionSet);
		tagger = new MaxentTagger(engTaggerLocation);		

	}
	public static String tagger( String text) {

		//ArrayList<String> NN_Tokens = new ArrayList<String>();
		//Date start = new Date();
		String tagged = tagger.tagString(text);					// The tagged string
		//System.out.println("tagger self " + (new Date().getTime() - start.getTime()) + " milliseconds");

		String [] _NN = tagged.split(" ");
		StringBuilder _NN_single_tokens = new StringBuilder();
		for(String temp : _NN){
			if(temp.contains("_NN")){ // check whether the term is noun or not
				temp=temp.substring(0,temp.indexOf("_"));
				if(temp.length()>1)	_NN_single_tokens.append(temp+" ");
			}
		}
		//_NN_Token = analyze(_NN_Token);  // check stopword, filter out non-valid words
		return _NN_single_tokens.toString();
		//	_NN_Token = analyze(_NN_Token);  // stemming, check stopword, filter out non-valid words
	}

	public static String analyze(String temp){ 
		temp = temp.replaceAll(Constants.INDICATOR_SYMBOL_NEW_LINE, Constants.INDICATOR_CHARACTOR_NEW_LINE);
		temp = temp.replaceAll(Constants.INDICATOR_SYMBOL_PERIOD, Constants.INDICATOR_CHARACTOR_PERIOD);

		StringBuilder text = new StringBuilder();
		try {

			TokenStream stream = analyzer.tokenStream("contents", new StringReader(temp));

			//TokenStream stream2=new PorterStemFilter(stream);								// filter out invalid terms
			stream.reset();

			CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);

			while(stream.incrementToken())	{
				text.append(term.toString()+" ");
			}
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return text.toString();
	}

	/**text에서 명사를 추출하는 메소드
	 * 연속으로 명사가 등장하면 명사구로 간주하고, 하나로 묶음
	 * 예) decision tree -> decision_tree_ ***/
	public static String tagger_NNphrase( String id, String text) throws java.lang.OutOfMemoryError {

		ArrayList<String> NN_Tokens = new ArrayList<String>();
		StringBuilder NN_sb = new StringBuilder();
		String tagged= "";

		tagged  = tagger.tagString(text);					// The tagged string

		String [] tokens = tagged.split(" ");

		boolean is_previous_one_noun = false;
		String previous_term="";
		boolean is_current_one_noun = false;

		int count=0;

		/**NOTE 명사가 연속으로 등장하면 명사구로 판단
		 * stanford nlp는 코모란과달리 사용자 사전을 지원하지 않아서
		 * 구(phrase)인 전문용어들을 처리하기 힘듦
		 * 따라서 휴리스틱한 방법으로 명사가 연속으로 등장하면 명사구로 판단함
		 * 이 방법을 쓰고 싶지 않다면 tagger 메소드 사용하면 됨.*/
		for(String temp : tokens){
			if(stopWordSet.contains(temp.substring(0,temp.indexOf("_")))){
				is_previous_one_noun = false;
				previous_term = "";
				continue;
			}

			else if(temp.contains(Constants.INDICATOR_SYMBOL_PERIOD)||temp.contains(Constants.INDICATOR_SYMBOL_NEW_LINE)){

				temp=temp.substring(0,temp.indexOf("_"));
				NN_Tokens.add(temp);

				is_previous_one_noun = false;
				previous_term = "";
				continue;
			}
			else if(temp.contains("_NN")){ // check whether the term is noun or not

				if(is_previous_one_noun == false && count>=0){	
					temp=temp.substring(0,temp.indexOf("_"));	

					NN_Tokens.add(temp);

					is_previous_one_noun = true;
					previous_term = temp;
				}					
				else{
					temp=temp.substring(0,temp.indexOf("_"));

					int size = NN_Tokens.size();
					try{NN_Tokens.remove(size-1);}
					catch(Exception e){}
					/**append last "_" to avoid stemming of noun phrase.
					 * if input of analyzer(stemming method) is "decision_tree",
					 * then the output would be "decision_tr", which is wrongly stemmed.
					 * if append "_" in the last position, 
					 * the output is "decision_tree_".
					 * "_" will be removed in NLP.java*/
					String newWord = previous_term +"_"+ temp+"_"; 
					NN_Tokens.add(newWord);	
					previous_term= newWord;
				}
			}
			else{
				is_previous_one_noun = false;
				previous_term = "";
			}
			count++;
		}
		for(int i = 0 ; i < NN_Tokens.size() ; i ++){
			// 예를들어 decision_trees_ <이런 명사구를 decis_tree_로 stemming 
			if(NN_Tokens.get(i).contains("_"))
				NN_Tokens.set(i,analyze(NN_Tokens.get(i).replaceAll("__|_", " ")).replace(" ","_"));
			NN_sb.append(NN_Tokens.get(i)+" ");
		}
		return analyze(NN_sb.toString());
	}
}
