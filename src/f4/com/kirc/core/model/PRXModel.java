/********************************************************************************************************************
 * FILENAME: PRXModel.java
 * 
 * ROLE: it represents PRX Model (Proximity Model)
 * 		
 * VARIABLES:
 *  public static final double MAX_VALUE									// Initial Maximum proximity value 
 * 	public static final int INITIAL_VALUE									// Initial Minimum proximity value
 * 	public static double PRX_MAX_VALUE										// Maximum proximity value
 * 	public static double PRX_MIN_VALUE										// Minimum proximity value
 * 
 * 	private String dataFileName												// Line 1: Identification as data file
 * 	private String dataType													// Line 2: Type of data = "distance"(="dist", "dissimilarity", "diss") or "similarity"(="sim")
 * 	private int nodeCount													// Line 3: Number of nodes
 * 	private int decimalPlaces												// Line 4: Number of decimal places
 * 	private double minimumValue												// Line 5: Minimum data value
 * 	private double maximumValue												// Line 6: Maximum data value	
 * 	private String orderOfDataValues										// Line 7: Order of data values = symmetric or upper (triangle) or lower (triangle)
 * 	private double[][] prxMatrix											// Line 8: PRX Data matrix
 * 
 * METHODS:
 * 	public String getDataFileName() 										//	return dataFileName
 * 	public void setDataFileName(String dataFileName) 						//	this.dataFileName = dataFileName
 * 
 * 	public String getDataType() 											//	return dataType
 * 	public void setDataType(String dataType) 								//	this.dataType = dataType
 * 
 * 	public int getNodeCount() 												//	return nodeCount
 * 	public void setNodeCount(int nodeCount) 								//	this.nodeCount = nodeCount
 * 
 * 	public String getOrderOfDataValues() 									//	return orderOfDataValues
 * 	public void setOrderOfDataValues(String orderOfDataValues) 				//	this.orderOfDataValues = orderOfDataValues
 * 
 * 	public int getDecimalPlaces() 											//	return decimalPlaces
 * 	public void setDecimalPlaces(int decimalPlaces) 						//	this.decimalPlaces = decimalPlaces
 * 
 * 	public double getMinimumValue() 										//	return minimumValue
 * 	public void setMinimumValue(double minimumValue) 						//	this.minimumValue = minimumValue
 * 
 * 	public double getMaximumValue() 										//	return maximumValue
 * 	public void setMaximumValue(double maximumValue) 						//	this.maximumValue = maximumValue
 * 
 * 	public double[][] getPrxMatrix() 										//	return prxMatrix
 * 	public void setPrxMatrix(double[][] prxMatrix) 							//	this.prxMatrix = prxMatrix
 * 
 *  public PRXModel(File inputPRXFilePath)									//	Generates PRX Model
 ********************************************************************************************************************/
package f4.com.kirc.core.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class PRXModel {
	
	public static final double MAX_VALUE = 65536.0;
	public static final int INITIAL_VALUE = -1;
	public static double PRX_MAX_VALUE;
	public static double PRX_MIN_VALUE;
	
	private String dataFileName;													// Line 1: Identification as data file
	private String dataType;														// Line 2: Type of data = "distance"(="dist", "dissimilarity", "diss") or "similarity"(="sim")
	private int nodeCount;															// Line 3: Number of nodes
	private int decimalPlaces;														// Line 4: Number of decimal places
	private double minimumValue;													// Line 5: Minimum data value
	private double maximumValue;													// Line 6: Maximum data value
	private String orderOfDataValues;												// Line 7: Order of data values = symmetric or upper (triangle) or lower (triangle)
	private double[][] prxMatrix;													// Line 8: PRX Data
	
	public String getDataFileName() {	return dataFileName;	}
	public void setDataFileName(String dataFileName) {	this.dataFileName = dataFileName;	}
	public String getDataType() {	return dataType;	}
	public void setDataType(String dataType) {	this.dataType = dataType;	}
	public int getNodeCount() {	return nodeCount;	}
	public void setNodeCount(int nodeCount) {	this.nodeCount = nodeCount;	}
	public String getOrderOfDataValues() {	return orderOfDataValues;	}
	public void setOrderOfDataValues(String orderOfDataValues) {	this.orderOfDataValues = orderOfDataValues;	}
	public int getDecimalPlaces() {	return decimalPlaces;	}
	public void setDecimalPlaces(int decimalPlaces) {	this.decimalPlaces = decimalPlaces;	}
	public double getMinimumValue() {	return minimumValue;	}
	public void setMinimumValue(double minimumValue) {	this.minimumValue = minimumValue;	}
	public double getMaximumValue() {	return maximumValue;	}
	public void setMaximumValue(double maximumValue) {	this.maximumValue = maximumValue;	}
	public double[][] getPrxMatrix() {	return prxMatrix;	}
	public void setPrxMatrix(double[][] prxMatrix) {	this.prxMatrix = prxMatrix;	}	
	
	public PRXModel(File inputPRXFilePath)
	{
		String line = null;
		int lineCnt = 0;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(inputPRXFilePath));
			
			int i = 0;
			int j;
			
			while ((line = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line);

				switch(lineCnt) {
					case 0: 														// Data file name
						setDataFileName(line);
						break;
					case 1:															// Type of data
						setDataType(line);
						break;
					case 2: 														// No. of Nodes
						line = st.nextToken();
						setNodeCount(Integer.parseInt(line));
						prxMatrix = new double[getNodeCount()][getNodeCount()];
						break;
					case 3 : 														// Decimal places
						line = st.nextToken();
						setDecimalPlaces(Integer.parseInt(line));
						break;
					case 4 : 														// Minimum
						line = st.nextToken();
						setMinimumValue(Double.parseDouble(line));
						break;
					case 5 : 														// Maximum
						line = st.nextToken();
						setMaximumValue(Double.parseDouble(line));
						break;
					case 6: 														// Matrix Style
						String[] temp = line.split(":");
						setOrderOfDataValues(temp[0]);
						break;
					default : 														// Get Proximity Matrix
						if(getOrderOfDataValues().equalsIgnoreCase("symmetric") || getOrderOfDataValues().equalsIgnoreCase("symmetric matrix")) {
							j=0;
							while(st.hasMoreTokens()) {
								line = st.nextToken();
								prxMatrix[i][j] = Double.parseDouble(line);
								j++;
							}
							i++;
						}else if(getOrderOfDataValues().equalsIgnoreCase("upper") || getOrderOfDataValues().equalsIgnoreCase("upper triangle")) {
							j = i + 1;
							while(st.hasMoreTokens()) {
								line = st.nextToken();
								prxMatrix[i][j] = Double.parseDouble(line);
								prxMatrix[j][i] = prxMatrix[i][j];
								j++;
							}
							i++;	
						}else if(getOrderOfDataValues().equalsIgnoreCase("lower") || getOrderOfDataValues().equalsIgnoreCase("lower triangle")) {
							j = i+1;
							while(st.hasMoreTokens()) {
								line = st.nextToken();
								prxMatrix[j][i] = Double.parseDouble(line);
								prxMatrix[i][j] = prxMatrix[j][i];
								j++;
							}
							i++;
						}else {
							System.err.println("[USAGE] Check Order of Data Values => symmetric or upper (triangle) or lower (triangle)!");
							System.exit(0);
						}
						break;
				}
				lineCnt++;
			}
			setPrxMatrix(prxMatrix);
			br.close();			
			
		}catch(IOException ex) {
			System.out.println("ex = "+ex.toString());
		}	
	}
}