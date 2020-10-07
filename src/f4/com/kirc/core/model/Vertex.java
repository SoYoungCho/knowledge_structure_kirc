/********************************************************************************************************************
 * FILENAME: Vertex.java
 * 
 * ROLE: Vertex defines one vertex in the Knowledge Structure
 * 
 * VARIABLES:
 * 	private String id								// Vertex ID
 * 	private String name								// Vertex NAME
 * 	private int frequency							// term frequency
 * 
 * METHODS:
 * 	public Vertex(String id, String name)			// sets features of a vertex
 * 	public String getId()							// gets the id of the vertex
 * 	public String getName()							// gets the name of the vertex
 * 	public int hashCode()							// return hash code
 * 	public boolean equals(Vertex v)					// checks whether two vertexes are equal or not 
 * 	public String toString()						// returns name of the vertex
 * 
 ********************************************************************************************************************/
package f4.com.kirc.core.model;

public class Vertex {

	private String id;
	private String name;
	private float frequency;

	//centratliy score
	private int degree;
	private double ec_score;
	private double bc_score;
	private double cc_score;
	
	public Vertex(String id, String name, Float frequency) {
		this.id = id;
		this.name = name;
		this.frequency = frequency;
	}

	public String getId() {		return id;	}

	public String getName() {	return name;	}

	public Float getFrequency() { return frequency; }

	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public boolean equals(Vertex v) {

		if(this.getId().equals(v.getId()))	return true;
		else								return false;
	}

	public String toString() {	return name;	}

	public void setDegree(int degree) {this.degree = degree;}
	public void setEC( double ec_score ){ this.ec_score = ec_score; }
	public void setBC( double bc_score ){ this.bc_score = bc_score; }
	public void setCC( double cc_score ){ this.cc_score = cc_score; }
	

	public int getDegree(){ return degree;   }
	public double getEC( ){ return ec_score; }
	public double getBC( ){ return bc_score; }
	public double getCC( ){ return cc_score; }



} 