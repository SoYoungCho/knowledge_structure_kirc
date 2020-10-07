/********************************************************************************************************************
 * FILENAME: CoOccurence.java
 * 
 * ROLE: Calculate cooccurence	
 * 
 * VARIABLES: - 
 * 
 * METHODS:
 * 	public double[][] getCoTable(ArrayList<HashMap<String, Integer>> compareArray,ArrayList<String> selectedListTermArray)		// get coTable of cooccurence
 * 
 * COMMENTS:
 * 	Cooccurence refers that, when two terms are occurred within the same sentence(paragraph), we consider the two terms has cooccurence.
 * 	It varies from 1.0 to 7.0. The more related, the smaller the value is. i.e., 1.0 represents the highest cooccurence while 7.0 represents the lowest cooccurence.
 * 
 ********************************************************************************************************************/
package f3.com.kirc.core.coTable;

import java.util.ArrayList;
import java.util.HashMap;

public class CoOccurence{

	public CoTable getCoTable(ArrayList<HashMap<String, Integer>> compareArray,ArrayList<String> selectedListTermArray) {

		int rowCount = selectedListTermArray.size();
		double[][] coTable = new double[rowCount][rowCount];

		for(int i =0;i<rowCount;i++)
			for(int j =0;j<rowCount;j++) coTable[i][j]=0;

		for(int i=0;i<selectedListTermArray.size();i++){
			String left = selectedListTermArray.get(i);
			for(int j=0;j<selectedListTermArray.size();j++){
				if(i!=j){
					String right = selectedListTermArray.get(j);
					for(int k=0;k<compareArray.size();k++){
						HashMap<String, Integer> source =compareArray.get(k); 
						if(source.containsKey(left)&&source.containsKey(right)){	coTable[i][j] = coTable[i][j]+1;		}
					}
				}
			}	
		}

		double maxCo = 1;

		/* find max co occurance */
		for(int i =0;i<rowCount;i++){
			for(int j =0;j<rowCount;j++){
				if(coTable[i][j]>maxCo){	maxCo = coTable[i][j];	}
			}
		}

		/* adjust 7 scale */
		for(int i =0;i<rowCount;i++){
			for(int j =0;j<rowCount;j++){
				double temp =7.0-(6.0*(coTable[i][j]/maxCo));

				if(temp == 0.0) {	temp = 1.0;		}
				coTable[i][j] = temp;
			}
		}

		StringBuilder coTableStr = new StringBuilder();
		for(int i=0;i<selectedListTermArray.size();i++){
			String left = selectedListTermArray.get(i);
			for(int j=0;j<selectedListTermArray.size();j++){
				if(i!=j || i > j){
					String right = selectedListTermArray.get(j);
					coTableStr.append(left + "," + right + "," + coTable[i][j] + "\n");
				}
			}	
		}
		CoTable ct  = new CoTable(); 
		ct.coTable = coTable;
		ct.coTableStr = coTableStr.toString();
		return ct;
	}
}