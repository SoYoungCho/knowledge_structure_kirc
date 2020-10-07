package f4.com.kirc.core.model;

import java.util.ArrayList;
import java.util.HashMap;

public class NlpResultModel {
	public String doc_id;
	
	public HashMap<String,Integer> allTermsHash;
	public ArrayList<HashMap<String,Integer>> sentenceArray;
	public ArrayList<HashMap<String,Integer>> paragraphArray;
	
	public int totalNumberOfNNWords;
	public int text_length;
	public int sentence_num;
	public int paragraph_num;
	
	}
