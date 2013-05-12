package net.emmanuelsotelo.projectdow.neuralnetwork;

import java.io.Serializable;
import java.util.Date;

public class Data implements Serializable, Comparable<Data>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Date date;
	private double data;

	public Data() {
		// TODO Auto-generated constructor stub
	}
	
	public Data(Data data ){
		
		this(data.getDate(), data.getData());
	}
	
	
	public Data(Date date, double data) {
		
		this.data = data;
		this.date = date;
	}
	
	public int compareTo(final Data other) {

		return getDate().compareTo(other.getDate());
	}
	
	public double getData(){
		
		return data;
	}
	
	public Date getDate(){
		
		return date;
	}
	
	public void setData(double data){
	
		this.data = data;
	}
	
	public void setDate(Date date){
		
		this.date = date;
	}
	
	

}
