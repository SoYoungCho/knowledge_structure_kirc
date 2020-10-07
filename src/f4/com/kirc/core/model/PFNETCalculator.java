/********************************************************************************************************************
 * FILENAME: PFNETCalculator.java
 * 
 * ROLE: calculating pathfinder network 
 * 		
 * VARIABLES:
 * 	private double prxDataMatrix[][];																	// PRX data matrix
 * 	private double tempDataMatrix[][];																	// temporary storage
 * 	private boolean edgeFlagCheckMatrix[][];															// 
 * 	private boolean[] nodeCheckArray;																	// 
 * 	private int nodeCount;																				//
 * 	private ArrayList<String> pfResult;																	//
 * 
 * METHODS:
 * 	public PFNETCalculator(PRXModel prxModel, String prxFileParentPath)									// constructor for initial setting.
 *	private void startPathfinderAlgorithm()																// starting pathfinder algorithm
 *	private void PreCalculateMethod()																	// copy original to temp matrix
 *	private boolean chooseMinValue()																	// choose minimum value in a given prx matrix
 *	private boolean checkInitialDirectLinkValidation(int _X, int _Y)									// check initial insert condition
 *	private boolean checkPathfinderValidation(int startPos, int finalPos)								// check the pathfinder validity
 *	private double findMaxValue(double a, double b)														// return bigger element
 *	private void setNodeFlag(int minX, int minY) 														// setup flag matrix
 *	private double getMinimumLinkWeight(ArrayList<String> pfResult)										//
 *	private double getMaximumLinkWeight(PRXModel prxModel, ArrayList<String> pfResult)					//
 *	public void writePFResult(String prxFileParentPath,ArrayList<String> pfResult, PRXModel prxModel)	//
 *
 * COMMENTS:
 * 	This 'core' module has been coded by Jung Hoon Kim.
 * 
 ********************************************************************************************************************/
package f4.com.kirc.core.model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import f1.com.kirc.core.config.Config;

// SUPREMUM_DISTANCE-BASED PATHFINDER NETWORK ALGORITHM.
public class PFNETCalculator {
	
	private double prxDataMatrix[][];					//matrix check
	private double tempDataMatrix[][];
	private boolean edgeFlagCheckMatrix[][];			//each edge check
	private boolean[] nodeCheckArray;					//each node check
	private int nodeCount;
	private ArrayList<String> pfResult;

	public PFNETCalculator(PRXModel prxModel, Config config, String prxFileParentPath){
		
		prxDataMatrix = prxModel.getPrxMatrix();
		nodeCount = prxModel.getNodeCount();
		
		edgeFlagCheckMatrix = new boolean[nodeCount][nodeCount];
		tempDataMatrix = new double[nodeCount][nodeCount];
		nodeCheckArray = new boolean[nodeCount];
		
		PRXModel.PRX_MAX_VALUE = prxModel.getMaximumValue();
		
		pfResult = new ArrayList<String>();
		startPathfinderAlgorithm();

		writePFResult(prxFileParentPath, config, pfResult, prxModel);
	}
	
	private void startPathfinderAlgorithm() {	
		boolean checkException = true;
		PreCalculateMethod();
		while(true){	
			checkException = chooseMinValue();
			if(checkException == false)	break;
		}
	}
	
	private void PreCalculateMethod() {
		
		for(int i = 0; i < nodeCount; i++){	//copy original to temp matrix
			for(int j = 0; j < nodeCount; j++){	tempDataMatrix[i][j] = prxDataMatrix[i][j];	}
		}
		
		for(int k = 0; k < nodeCount; k++){
			for(int i = 0; i < nodeCount; i++){
				for(int j = 0; j < nodeCount; j++){
					if(tempDataMatrix[i][j] > findMaxValue(tempDataMatrix[i][k], tempDataMatrix[k][j])){	//path update
						tempDataMatrix[i][j] =  findMaxValue(tempDataMatrix[i][k], tempDataMatrix[k][j]);
					}
				}
			}
		}
	}

	/* choose minimum value in a given prx matrix */
	private boolean chooseMinValue() {	
		double minValue = PRXModel.PRX_MAX_VALUE; // 6.999
		int minX = PRXModel.INITIAL_VALUE; // -1zz
		int minY = PRXModel.INITIAL_VALUE; // -1
		
		for(int i = 0; i < nodeCount; i++){
			for(int j = i+1; j < nodeCount; j++){	
				if(edgeFlagCheckMatrix[i][j] == false && prxDataMatrix[i][j] < minValue && checkPathfinderValidation(i, j) == true) {	//find minimum value
					minValue = prxDataMatrix[i][j];
					minX = i;	minY = j;
				}
			}
		}
		
		if(minX == -1 || minY == -1) {	//can't find minimum value 
			return false;
		}
		
		int CheckX = minX+1;
		int CheckY = minY+1;
				
		pfResult.add(CheckX+"\t"+CheckY+"\t"+minValue);
		setNodeFlag(minX, minY);	//set flag value
		return true;
	}
	
	private boolean checkInitialDirectLinkValidation(int _X, int _Y){
		return ((nodeCheckArray[_X]==false) || (nodeCheckArray[_Y]==false));
	}	
	
	private boolean checkPathfinderValidation(int startPos, int finalPos) { 
		if(checkInitialDirectLinkValidation(startPos, finalPos) == true){	return true;	}	
		return (tempDataMatrix[startPos][finalPos] < prxDataMatrix[startPos][finalPos] || tempDataMatrix[startPos][finalPos] == -1.0 || tempDataMatrix[startPos][finalPos] >= PRXModel.PRX_MAX_VALUE ) ? false : true;	//return acceptable condition
	}
	
	
	private double findMaxValue(double a, double b){	return a > b ? a : b;	}

	private void setNodeFlag(int minX, int minY) {	
		nodeCheckArray[minX] = true;
		nodeCheckArray[minY] = true;
		edgeFlagCheckMatrix[minX][minY] = true;
	}
	
	private double getMinimumLinkWeight(ArrayList<String> pfResult) {
		double minValue = PRXModel.PRX_MAX_VALUE;
		
		for(int i=0; i<pfResult.size(); i++) {
			String[] temp = pfResult.get(i).split("\\s+");
			double weight = Double.parseDouble(temp[2]);
			if(weight < minValue) {
				minValue = weight;
			}
		}
		return minValue;
	}
	
	private double getMaximumLinkWeight(PRXModel prxModel, ArrayList<String> pfResult) {
		double maxValue = prxModel.getMinimumValue();
		
		for(int i=0; i<pfResult.size(); i++) {
			String[] temp = pfResult.get(i).split("\\s+");
			double weight = Double.parseDouble(temp[2]);
			if(weight > maxValue) {
				maxValue = weight;
			}
		}
		return maxValue;
	}
	
	public void writePFResult(String prxFileParentPath, Config config, ArrayList<String> pfResult, PRXModel prxModel) {

		String pfFilePath = config.prxpfFile;
		StringBuilder linkInfo = new StringBuilder();
		linkInfo.append("links:\r\n");
		linkInfo.append("Node1\tNode2\tWeight\r\n");
		for(int i=0; i<pfResult.size(); i++) {
			linkInfo.append((pfResult.get(i)+"\r\n"));
		}
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(pfFilePath));
		
			bw.write("PFNET "+prxModel.getDataFileName().toUpperCase()+".PRX"); bw.newLine();	// Line 1: PFNET DATA.PRX
			bw.write(prxModel.getNodeCount() + " nodes"); bw.newLine();							// Line 2: No. Of Nodes
			bw.write(pfResult.size() + " links"); bw.newLine();									// Line 3: No. Of Links
			bw.write("undirected"); bw.newLine();												// Line 4: Edge Type
			bw.write((prxModel.getNodeCount()-1) + " q ("+(prxModel.getNodeCount()-1) + " = n-1)"); bw.newLine();	// Line 5: Q Parameter
			bw.write("infinite r"); bw.newLine();												// Line 6: R Parameter
			bw.write(Double.toString(getMinimumLinkWeight(pfResult)) + " minimum link weight"); bw.newLine();		// Line 7: Minimum Link Weight
			bw.write(Double.toString(getMaximumLinkWeight(prxModel, pfResult)) + " maximum link weight"); bw.newLine();		// Line 8: Maximum Link Weight
			bw.write(linkInfo.toString()); bw.newLine();															// Line 9: Link Information
			
			bw.close();
		} catch(IOException ex) {
			System.out.println("ex = "+ex.toString());
		}
	}
}