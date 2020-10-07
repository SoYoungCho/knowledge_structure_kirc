/********************************************************************************************************************
 * FILENAME: KSModel.java
 * 
 * ROLE: Set or get features of Knowledge Structure Model. it embraces Vertex and Edges.
 * 		
 * VARIABLES:
 * 	private String contentId															//content ID
 * 	private HashMap<String,Integer> allTermsHash										//all terms in a file
 * 	private ArrayList<HashMap<String,Integer>> sentenceArray							//list if sentences, a sentence is the unit
 * 	private ArrayList<HashMap<String,Integer>> paragraphArray							//list of paragraphes, a paragraph is the unit
 * 	private int totalNumberOfWords														//total number of words in a file
 * 	private int sourceLength															//number of terms of original source
 * 	private String type																	//type of the model
 * 	private List<Vertex> vertexes														//list of vertexes in the model
 * 	private List<Edge> edges															//list of edges in the model
 * 
 * METHODS:
 * 	public String getContentId()														// get content ID
 * 	public void setContentId(String contentId)											// set content ID
 * 
 * 	public HashMap<String, Integer> getAllTermsHash() 									// get all terms hash
 * 	public void setAllTermsHash(HashMap<String, Integer> allTermsHash)					// set all terms hash
 * 
 * 	public ArrayList<HashMap<String, Integer>> getSentenceArray() 						// get sentenceArray
 * 	public void setSentenceArray(ArrayList<HashMap<String, Integer>> sentenceArray)		// set sentenceArray
 * 
 * 	public ArrayList<HashMap<String, Integer>> getParagraphArray() 						// get paragraphArray
 * 	public void setParagraphArray(ArrayList<HashMap<String, Integer>> paragraphArray)	// set paragraphArray
 * 
 * 	public int getTotalNumberOfWords() 													// get total number of words
 * 	public void setTotalNumberOfWords(int totalNumberOfWords) 							// set total number of words
 * 
 * 	public List<Vertex> getVertexes()													// get vertexes
 * 	public void setVertexes(List<Vertex> vertexes) 										// set vertexes
 * 
 * 	public List<Edge> getEdges() 														// get edges
 * 	public void setEdges(List<Edge> edges) 												// set edges
 * 
 * 	public String getType() 															// get type
 * 	public void setType(String type)													// set type
 * 
 * 	public int getSourceLength() 														// get source length
 * 	public void setSourceLength(int sourceLength) 										// set source length
 * 
 ********************************************************************************************************************/
package f4.com.kirc.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KSModel {	
	
	private String docId;
	private HashMap<String,Integer> allTermsHash;
	private ArrayList<HashMap<String,Integer>> sentenceArray;
	private ArrayList<HashMap<String,Integer>> paragraphArray;
	private int totalNumberOfWords;
	private int sourceLength;
	private String type;
	private List<Vertex> vertexes;
	private List<Edge> edges;
	
	public String getDocId() {	return docId;	}
	public void setDocId(String docId) {	this.docId = docId;	}

	public HashMap<String, Integer> getAllTermsHash() {	return allTermsHash;	}
	public void setAllTermsHash(HashMap<String, Integer> allTermsHash) {	this.allTermsHash = allTermsHash;	}
	
	public ArrayList<HashMap<String, Integer>> getSentenceArray() {	return sentenceArray;	}
	public void setSentenceArray(ArrayList<HashMap<String, Integer>> sentenceArray) {	this.sentenceArray = sentenceArray;		}

	public ArrayList<HashMap<String, Integer>> getParagraphArray() { return paragraphArray;	}	
	public void setParagraphArray(ArrayList<HashMap<String, Integer>> paragraphArray) {	this.paragraphArray = paragraphArray;	}

	public int getTotalNumberOfWords() {	return totalNumberOfWords;	}	
	public void setTotalNumberOfWords(int totalNumberOfWords) {	this.totalNumberOfWords = totalNumberOfWords;	}

	public List<Vertex> getVertexes() {		return vertexes;	}
	public void setVertexes(List<Vertex> vertexes) {	this.vertexes = vertexes;	}
	
	public List<Edge> getEdges() {	return edges;	}
	public void setEdges(List<Edge> edges) {	this.edges = edges;	}

	public String getType() {	return type;	}
	public void setType(String type) {	this.type = type;	}
	
	public int getSourceLength() {	return sourceLength;	}
	public void setSourceLength(int sourceLength) {	this.sourceLength = sourceLength;	}
}
