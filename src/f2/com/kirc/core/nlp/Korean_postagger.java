/********************************************************************************************************************
 * FILENAME: Korean_postagger.java
 * 
 * ROLE: Korean pos tagger
 * 		
 * VARIABLES: 
 * 	public String engTaggerLocation										// Korean pos tagger location
 * 
 * METHODS: 
 * 	public String tagger(String text)									// tagging to a text	
 * 	public boolean isKorean(String Paragraph_token) 					// analysing morpheme whether it is valid or not
 * 
 * COMMENT:
 * 	KOMORAN pos tagger is exploited
 * 
 ********************************************************************************************************************/

package f2.com.kirc.core.nlp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.Character.UnicodeBlock;
import java.util.LinkedList;
import java.util.List;

import f1.com.kirc.core.config.Config;
import f1.com.kirc.core.config.Constants;
import kr.co.shineware.nlp.komoran.core.analyzer.Komoran;
import kr.co.shineware.util.common.model.Pair;


public class Korean_postagger {
	static Komoran komoran;
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		Korean_postagger kp = new Korean_postagger(new Config().korTaggerLocation);
		Korean_postagger.tagger("데이터 마이닝");
	}
	
	public Korean_postagger(String korTaggerLocation){
		komoran = new Komoran(korTaggerLocation);
	}
	
	public static String tagger(String text){
		String text1= "";
		try
		{
			List<List<Pair<String,String>>> result = komoran.analyze(text);

			for (List<Pair<String, String>> eojeolResult : result) 
			{
				for (Pair<String, String> wordMorph : eojeolResult) 
				{
					//System.out.println(wordMorph.getFirst() + ":	" + wordMorph.getSecond());
					String tag = wordMorph.getSecond();
					// 명사, 외국어, 기타기호(문장 끝, 문단 끝을 나타내는 기호 ¶ , ¸를 남기기 위해서)만 추출
					// NOTE: 코모란 사용자 사전에 고유명사 추가하면 phrase도 명사로 처리 가능
					// 예) '바람과 함께 사라지다' -> NNP
					if(tag.equals("NNP")||tag.equals("NNG")||tag.equals("SL")||tag.equals("SW"))//
					{
						String temp=wordMorph.getFirst().toString();
						
						if(isKorean(temp)&& temp.length()>1||temp.equals(Constants.INDICATOR_SYMBOL_NEW_LINE)||temp.equals(Constants.INDICATOR_SYMBOL_PERIOD))
						{
							temp = temp.replace(" ", "_");
							text1 += temp+" ";
						}
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		/* Shutdown the work flow */
		return text1;
	}
	
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
}
