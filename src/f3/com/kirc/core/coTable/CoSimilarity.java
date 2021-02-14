/********************************************************************************************************************
 * FILENAME: CoSimilarity.java
 * 
 * ROLE: Calculate cosimilarity
 * 		
 * VARIABLES: - 
 * 
 * METHODS:
 * 	public double[][] getCoTable																											// get coTable of cosine similarity
 * 	private double calculateCosimilarity(ArrayList<Integer> leftVector,ArrayList<Integer> rightVector)										// get cosine similarity
 * 	private ArrayList<Integer> getKeywordSentenceNumberArray(ArrayList<HashMap<String,Integer>> totalSentenceArray,String keyword)			// get keyword sentence number array
 * 
 * COMMENTS:
 * 	cosine similarity refers that, when two terms are occurred within the similar positions throuout the entire document, two terms are considered to have similar meaning.
 * 	It varies from 1.0 to 7.0. The more similar, the smaller the value is. i.e., 1.0 represents the highest cosimilarity while 7.0 represents the lowest cosimilarity.
 * 
 ********************************************************************************************************************/
package f3.com.kirc.core.coTable;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CoSimilarity{

	public CoTable getCoTable(ArrayList<HashMap<String, Integer>> compareArray, ArrayList<String> selectedListTermArray){

		StringBuilder coTableStr = new StringBuilder();
		int rowCount = selectedListTermArray.size();
		double[][] coTable = new double[rowCount][rowCount];

		for(int i =0;i<rowCount;i++)
			for(int j =0;j<rowCount;j++) coTable[i][j]=0;	

		for(int i=0;i<rowCount;i++){
			String left = selectedListTermArray.get(i);
			for(int j=0;j<selectedListTermArray.size();j++){
				if(i!=j){
					String right = selectedListTermArray.get(j);
					ArrayList<Integer> leftVector = getKeywordSentenceNumberArray(compareArray,left);
					ArrayList<Integer> rightVector = getKeywordSentenceNumberArray(compareArray,right);
					double simililarity = calculateCosimilarity(leftVector,rightVector);
					coTable[i][j]=simililarity;
					if(i>j)
						coTableStr.append(left + "," + right + "," + simililarity + "\n");
				}  
			}	
		}

		CoTable ct  = new CoTable(); 
		ct.coTable = coTable;
		ct.coTableStr = coTableStr.toString();
		return ct;
	}

	private double calculateCosimilarity(ArrayList<Integer> leftVector,ArrayList<Integer> rightVector){

		double dot = 0.0;
		double leftNorm = 0.0;
		double rightNorm = 0.0;
		double result = 0.0;

		for(int i=0;i<leftVector.size();i++){
			dot += leftVector.get(i) * rightVector.get(i);
			leftNorm = leftNorm + Math.pow(leftVector.get(i), 2);
			rightNorm += Math.pow(rightVector.get(i), 2);
		}
		/* 7점 스케일 변환 */
		if(leftNorm==0|| rightNorm == 0) result = 7.0;
		else{
			leftNorm = Math.pow(leftNorm,0.5);
			rightNorm = Math.pow(rightNorm, 0.5);
			result = dot/(leftNorm * rightNorm);
			result = 7.0-(result * 6.0);
		}
		return result;
	}

	private ArrayList<Integer> getKeywordSentenceNumberArray(ArrayList<HashMap<String,Integer>> totalSentenceArray,String keyword){
		ArrayList<Integer> result = new ArrayList<Integer>();
		for(int i=0;i<totalSentenceArray.size();i++){
			HashMap<String,Integer> aHash = totalSentenceArray.get(i);
			if(aHash.containsKey(keyword))	result.add(aHash.get(keyword));
			else result.add(0);
		}
		return result;
	}
}