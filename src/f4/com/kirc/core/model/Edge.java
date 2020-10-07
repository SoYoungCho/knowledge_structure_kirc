/********************************************************************************************************************
 * FILENAME: Edge.java
 * 
 * ROLE: Edge defines one edge in the Knowledge Structure
 * 		
 * VARIABLES:
 * 	private String id															// Edge ID
 * 	private Vertex source														// source vertex ID
 * 	private Vertex destination													// destination vertex ID
 * 	private double weight														// weight of the Edge
 * 
 * METHODS:
 * 	public Edge(String id, Vertex source, Vertex destination, double weight)	// set the features which defines the edge
 * 	public String getId()														// get Edge ID
 * 	public Vertex getDestination() 												// return the destination node of the edge
 * 	public Vertex getSource()													// return the source node of the edge
 * 	public double getWeight()													// return the weight of the edge
 * 	public String toString()													// show the edge in the form of [source destination]
 * 	public boolean equals(Edge e)												// check if two edges are the same or not
 * 
 ********************************************************************************************************************/
package f4.com.kirc.core.model;

public class Edge  {
	private String id; 
	private Vertex source;
	private Vertex destination;
	private double weight; 
	  
	public Edge(String id, Vertex source, Vertex destination, double weight) {	
		this.id = id;
		this.source = source;
		this.destination = destination;
		this.weight = weight;
	}
	  
	public String getId() {	return id;	}
	public Vertex getDestination() {	return destination;	}
	public Vertex getSource() {	return source;	}  
	public double getWeight() {	return weight;	}  
	public String toString() {	return source + " " + destination;	}
	
	public boolean equals(Edge e) {
		if(this.getId().equals(e.getId()) && this.getSource().equals(e.getSource())
				&& this.getDestination().equals(e.getDestination()) && this.getWeight() == e.getWeight())
			return true;
		else 
			return false;
	}
} 