/********************************************************************************************************************
 * FILENAME: NLP.java
 * 
 * ROLE: Natural Language Processing. Morphological analysis
 * 		
 * VARIABLES:
 * 	public String loweredResource										// the resource (lower case if Eng)
 * 	public boolean is_korean											// flag to check the resource is Kor or Eng
 *  public int totalNumberOfWord										// total number of words
 * 
 * 	public HashMap<String, Integer> allHash;							// NLP result container. 1 unit == a content
 * 	public ArrayList<HashMap<String, Integer>> sentenceHash;			// NLP result container. 1 unit == 1 sentence
 * 	public ArrayList<HashMap<String, Integer>> paragraphHash;			// NLP result container. 1 unit == 1 paragraph
 * 
 * METHODS:
 * 	public NLP(config config)											// Natural Language Processing module
 * 	public String readStringFromFile(File path) 						// read string from file
 * 	public boolean boolean_korean(String text)							// document language checker (Kor or Eng)
 * 	public boolean isKorean(String Paragraph_token) 					// term language checker (Kor or Eng)
 * 	public String findFileEncoding(File file)							// find what kind of encoding the given document is.
 * 	public String [] getUniqueArrayList(String [] keywordList)			// get keyword list without overlap.
 * 
 ********************************************************************************************************************/

package f2.com.kirc.core.nlp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Character.UnicodeBlock;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import org.mozilla.universalchardet.UniversalDetector;

import f1.com.kirc.core.config.Config;
import f1.com.kirc.core.config.Constants;


/**
 * 인풋으로 들어온 텍스트에서 명사를 추출 함.
 * allHash = 문서에 나온 명사들의 term frequency
 * sentenceHash = 각 문장마다 등장한 명사들의 term frequency
 * paragraphHash = 각 문단마다 등장한 명사들의 term frequency
 * ***/
public class NLP{

	/* variables for pre-NLP settings */
	public String loweredResource;
	public boolean is_korean = false;
	public int totalNumberOfNNWord = 0;
	public static String encode;

	//static HashSet<String> KOSCOM_DIC = null;

	/* NLP result containers */
	// 문서에 나온 명사들의 term frequency
	public  HashMap<String, Integer> allHash = new HashMap<String, Integer>();
	// 각 문장마다 등장한 명사들의 term frequency
	public  ArrayList<HashMap<String, Integer>> sentenceHash = new ArrayList<HashMap<String, Integer>>();
	// 각 문단마다 등장한 명사들의 term frequency
	public  ArrayList<HashMap<String, Integer>> paragraphHash = new ArrayList<HashMap<String, Integer>>();

	public void preprocessing(Config config, String content){

		//printStepNLP(config);

		String nn_string ="";

		String[] NN_tokens=null;
		//config.encodetype = encode;

		is_korean = boolean_korean(content);
		if(!is_korean) loweredResource = content.toLowerCase().replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s\\.\n]", " ");
		else loweredResource = content.replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s\\.\n]", " ");

		String [] token = null;

		/**replace \\. and \n to ¸ and ¶ to avoid deletion of \\. and \n after performing POS tagger.**/
		loweredResource =loweredResource.replaceAll("\\.", Constants.INDICATOR_SYMBOL_PERIOD);  
		loweredResource =loweredResource.replaceAll("\n", Constants.INDICATOR_SYMBOL_NEW_LINE);

		/* run the pos tagger considering the language type */ 
		if(!is_korean){	
			try{
				// NOTE: 형태소 분석후 '명사'만 추출함
				nn_string = English_postagger.tagger_NNphrase(config.targetFileName,loweredResource);
			} catch(java.lang.OutOfMemoryError e){
				System.out.println(e.toString()+"\t"+config.targetFileName);
			}
			NN_tokens = nn_string.split(" "); 

			//1. eng term counting from NN tokens
			for(String temp : NN_tokens){
				if(temp.equals(Constants.INDICATOR_CHARACTOR_NEW_LINE)||temp.equals(Constants.INDICATOR_CHARACTOR_PERIOD)
						||temp.equals("lsb")||temp.equals("rsb")) continue;
				temp =temp.replaceAll("_", " ");//특수문자 제거
				temp = temp.trim();
				if(temp.equals("")|temp.equals(" ")) continue;
				if(is_korean){
					boolean type = true;
					type = isKorean(temp);
					if(type){
						if (!allHash.containsKey(temp))		
							allHash.put(temp, 1);
						else 										
							allHash.put(temp,allHash.get(temp) + 1);
					}
				}else{
					if (!allHash.containsKey(temp))		
						allHash.put(temp, 1);
					else 										
						allHash.put(temp,allHash.get(temp) + 1);
				}
			}
		}
		else			
		{
			// NOTE: 형태소 분석후 '명사'만 추출함
			nn_string= Korean_postagger.tagger(loweredResource);
			token = nn_string.split(" ");	//token[] contains all the valid terms after stemmed.
			totalNumberOfNNWord = token.length;
			//for korean term counting	
			for(String temp : token){
				temp =temp.replaceAll("_", " ");
				temp = temp.trim();
				if(is_korean){
					boolean type = true;
					type = isKorean(temp);
					if(type){
						if (!allHash.containsKey(temp))		
							allHash.put(temp, 1);
						else 										
							allHash.put(temp,allHash.get(temp) + 1);
					}
				}else{
					if (!allHash.containsKey(temp))		
						allHash.put(temp, 1);
					else 										
						allHash.put(temp,allHash.get(temp) + 1);
				}
			}
		}

		totalNumberOfNNWord = allHash.size();

		/* sentence-wise seperation */
		String[] SentenceArray = null;
		String nn_sentence = nn_string.replaceAll(Constants.INDICATOR_CHARACTOR_NEW_LINE, "");
		if(!is_korean) SentenceArray = nn_sentence.split(Constants.INDICATOR_CHARACTOR_PERIOD);
		else SentenceArray = nn_string.split(Constants.INDICATOR_SYMBOL_PERIOD);
		for(String Sentence : SentenceArray){  
			//noun phrase format: noun1_noun2. so delete '_' after split nn_string to noun tokens.
			HashMap<String , Integer> map = new HashMap<String , Integer>();
			for(String Sentence_token : Sentence.split(" ")){
				if(Sentence_token.equals("")) continue;
				Sentence_token = Sentence_token.replace("_", " ");
				Sentence_token = Sentence_token.trim();
				if(is_korean){
					boolean type = true;
					type = isKorean(Sentence_token);
					if(type){
						if (!map.containsKey(Sentence_token))		map.put(Sentence_token, 1);
						else 										map.put(Sentence_token,map.get(Sentence_token) + 1);
					}
				}else{
					if (!map.containsKey(Sentence_token))		map.put(Sentence_token, 1);
					else 										map.put(Sentence_token,map.get(Sentence_token) + 1);
				}
			}
			sentenceHash.add(map);
		}

		/* paragraph-wise seperation */
		String[] ParagraphArray = null;
		String nn_paragraph = nn_string.replaceAll(Constants.INDICATOR_CHARACTOR_PERIOD, "");
		if(!is_korean) ParagraphArray = nn_paragraph.split(Constants.INDICATOR_CHARACTOR_NEW_LINE);
		else ParagraphArray = nn_string.split(Constants.INDICATOR_SYMBOL_NEW_LINE);
		for(String Paragraph : ParagraphArray) 
		{
			HashMap<String , Integer> map = new HashMap<String , Integer>();
			for(String Paragraph_token : Paragraph.split(" ")){
				if(Paragraph_token.equals("")) continue;
				Paragraph_token = Paragraph_token.replace("_", " ");
				Paragraph_token = Paragraph_token.trim();
				if(is_korean){
					boolean type = true;
					type = isKorean(Paragraph_token);
					if(type){
						if (!map.containsKey(Paragraph_token))		map.put(Paragraph_token, 1);
						else 										map.put(Paragraph_token,map.get(Paragraph_token) + 1);
					}
				}else{
					if (!map.containsKey(Paragraph_token))		map.put(Paragraph_token, 1);
					else 										map.put(Paragraph_token,map.get(Paragraph_token) + 1);
				}
			}
			paragraphHash.add(map);
		}
		//System.out.println("\n--------------------------------------------------------");
	}


	/* read string from file */
	public static  String readStringFromFile(File path) {
		String text = "";
		try {
			encode=findFileEncoding(path);
			BufferedReader br  =  new BufferedReader(new InputStreamReader(new FileInputStream(path),encode));

			String line;

			while ((line = br.readLine()) != null) 
			{

				text += line+"\n";
			}

			br.close();
			br = null;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return text;
	}

	/* Korean checker for entire document*/ 
	public static boolean boolean_korean(String text){
		char ch;
		double total_korean_count = 0;
		double total_eng_count = 0;
		for(int i=0; i<text.length(); i++)
		{
			ch = text.charAt(i);
			if (ch>=0xAC00 && ch<=0xD7A3)	 total_korean_count++;			//'가' - '힣'
			if (ch>=0x41 && ch<=0x7A)	total_eng_count++;					//'A' - 'z'
		}

		//		System.out.println("\ttext length: "+text.length());
		//		System.out.println("\t# of kor words: "+total_korean_count);
		//		System.out.println("\t# of eng words: "+total_eng_count);
		if(total_korean_count > total_eng_count/2)	return true;
		else 										return false;
	}

	/* Korean checker for token*/
	public static boolean isKorean(String Paragraph_token) 
	{
		boolean type = true;
		for(int j = 0 ; j < Paragraph_token.length() ; j++)
		{
			char ch = Paragraph_token.charAt(j);
			Character.UnicodeBlock unicodeBlock = Character.UnicodeBlock.of(ch);
			if(!(UnicodeBlock.HANGUL_SYLLABLES.equals(unicodeBlock) ||
					UnicodeBlock.HANGUL_COMPATIBILITY_JAMO.equals(unicodeBlock) ||
					UnicodeBlock.HANGUL_JAMO.equals(unicodeBlock)))
			{type = false; break;}
		}
		return type;
	}

	public static String findFileEncoding(File file) throws IOException {
		byte[] buf = new byte[4096];
		java.io.FileInputStream fis = new java.io.FileInputStream(file);

		// (1)
		UniversalDetector detector = new UniversalDetector(null); 
		// (2)
		int nread;
		while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
			detector.handleData(buf, 0, nread);
		}
		// (3)
		detector.dataEnd();

		// (4)
		String encoding = detector.getDetectedCharset();
		if (encoding != null) {
			System.out.println("\tDetected encoding = " + encoding);
		} else {
			System.out.println("\tNo encoding detected.");
		}
		fis.close();
		// (5)
		detector.reset();

		return encoding;
	}

	public void printStepNLP(Config config){
		System.out.println("STEP 2. Natural Language Processing...");
		System.out.println("\tResource File Location is: " + config.getTargetFile());
		System.out.println("\tResource File Name is: " + config.title);
	}

}