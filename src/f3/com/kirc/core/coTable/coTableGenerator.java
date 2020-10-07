/********************************************************************************************************************
 * FILENAME: coTableGenerator.java
 * 
 * ROLE: it generates coTable into 4 different modes. ss(cooccurence-sentence), sp(cooccurence-paragraph), scs(cosimilarity-sentence), pcs(cosimilarity-paragraph)
 * 
 * VARIABLES:
 * 	public int totalNumberOfWords											// total number of words
 * 	public int sourceLength													// source length
 * 	public String terms														// keywords
 * 	public ArrayList<String> selectedListTermArray							// unsorted result of the HashMap<term, frequency>
 * 	public ArrayList<Map.Entry<String, Integer>> sortedValue				// sorted result of the HashMap<term, frequency>
 * 	public int rowCount														// number of rows in the coTable
 * 	public double[][] coTable												// cotable result container
 * 
 * 	public HashMap<String,Integer> allTermsHash								// NLP result container. 1 unit == a content
 * 	public ArrayList<HashMap<String,Integer>> sentenceArray					// NLP result container. 1 unit == 1 sentence
 * 	public ArrayList<HashMap<String,Integer>> paragraphArray				// NLP result container. 1 unit == 1 paragraph
	
 * METHODS:
 * 	public coTableGenerator(config config, NLP nlp)							// it generates coTable
 * 	public static ArrayList<Map.Entry<String, Integer>> sortedMap(HashMap<String, Integer> source)
 * 
 * COMMENT:
 * 	There are 4 types of coTables. Namely, ss, ps, scs, and pcs. 
 * 		ss:	 cooccurence coTable of sentences
 * 		ps:  cooccurence coTable of paragraphs
 * 		scs: cosimilarity coTable of sentences
 * 		pcs: cosimilarity coTable of paragraphs
 * 
 ********************************************************************************************************************/
package f3.com.kirc.core.coTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.index.IndexReader;

import com.kirc.core.utils.TF_IDF;

import f1.com.kirc.core.config.Config;
import f1.com.kirc.core.config.Constants;
import f4.com.kirc.core.model.NlpResultModel;

public class coTableGenerator{
	
	public int totalNumberOfNNWords = 0;
	public int text_length = 0;
	public StringBuilder terms = new StringBuilder();
	public ArrayList<String> selectedListTermArray;
	public ArrayList<Map.Entry<String, Float>> sortedValue;
	public int rowCount = 0;
	public CoTable ct;
	public double[][] coTable;
	public String coTableGraph;
	
	public HashMap<String,Integer> allTermsHash;
	public ArrayList<HashMap<String,Integer>> sentenceArray;
	public ArrayList<HashMap<String,Integer>> paragraphArray;
	public HashMap<String, Float> tfidfTermMap;
	
	public coTableGenerator(Config config, IndexReader reader, NlpResultModel nlpresult) {
		
		//System.out.println("STEP 3. Constructing CoTable...");

		/* put document data into KnowledgeStructureModel */	
		allTermsHash = nlpresult.allTermsHash;
		sentenceArray = nlpresult.sentenceArray;
		paragraphArray = nlpresult.paragraphArray;
		totalNumberOfNNWords = nlpresult.totalNumberOfNNWords;
		text_length = nlpresult.text_length;
							
		/* extract top N terms */
		selectedListTermArray = new ArrayList<String>();
		tfidfTermMap = TF_IDF.getNN_TFIDF(allTermsHash, reader, Constants.DOC_CONTENTS_FIELD_NAME);
		sortedValue = sortedMap(tfidfTermMap);
			
		/* exception handling */
		//config.max_N_nodes = (int) Math.ceil(totalNumberOfNNWords*0.1);
		//System.out.println(totalNumberOfNNWords+","+config.max_N_nodes);
		config.top_N_nodes = config.max_N_nodes;
		if(sortedValue.size()<config.top_N_nodes) {	// top N should be smaller or equal then number of nodes.\n
			System.out.println("number of nodes: "+sortedValue.size()+"\t"+nlpresult.doc_id);
			config.top_N_nodes = sortedValue.size();
		
		}
		//System.out.println("\tWe've picked up top-"+config.top_N_nodes+" nodes");
		for (int i = 0; i < config.top_N_nodes; i++) {
	 		Map.Entry<String, Float> aMap = sortedValue.get(i);
	 		String key = (String) aMap.getKey().replace(",", "");
	 		//System.out.println(key + "\t" + aMap.getValue());
	 		//System.out.println(aMap.getValue());
	 		selectedListTermArray.add(key);
	 		terms.append(key);
	 		if(i != config.top_N_nodes-1)	terms.append(",");
	 	}
		//System.out.println();
			
		/* make cotable */
		CoOccurence cooccurence = new CoOccurence();
		CoSimilarity cosimilarity = new CoSimilarity();
			
		switch (config.cot_mode){
			case "ss":						//cooccurence coTable of sentences
				ct = cooccurence.getCoTable(sentenceArray, selectedListTermArray);
				break;
			case "ps":						//cooccurence coTable of paragraphs
				ct = cooccurence.getCoTable(paragraphArray, selectedListTermArray);
				break;
			case "scs":						//cosimilarity coTable of sentences
				ct = cosimilarity.getCoTable(sentenceArray, selectedListTermArray);
				break;
			case "pcs":						//cosimilarity coTable of paragraphs
				ct = cosimilarity.getCoTable(paragraphArray,  selectedListTermArray);
				break;
		}
		
		coTable = ct.coTable;
		coTableGraph = ct.coTableStr;
		rowCount = selectedListTermArray.size();		
		//System.out.println("\n--------------------------------------------------------");
	}	
	public static ArrayList<Map.Entry<String, Float>> sortedMap(HashMap<String, Float> source){
		
		ArrayList<Map.Entry<String, Float>> myArrayList = new ArrayList<Map.Entry<String, Float>>(source.entrySet());
		Collections.sort(myArrayList, new Comparator<Map.Entry<String, Float>>() {
			public int compare(Map.Entry<String, Float> e1, Map.Entry<String, Float> e2) {
				if(e1.getValue() == e2.getValue())		return e1.getKey().compareTo(e2.getKey());
				else									return e2.getValue().compareTo(e1.getValue());
			}			
		});
		return myArrayList;
	}
}