package net.emmanuelsotelo.projectdow.neuralnetwork;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import net.emmanuelsotelo.projectdow.neuralnetwork.PredictionNetwork;

public class LoadSaveNetwork {
	
	public static PredictionNetwork loadNetwork(String fileName) throws IOException, ClassNotFoundException
	{
		
		PredictionNetwork network;
		FileInputStream fis = null;
		ObjectInputStream in = null;
		fis = new FileInputStream(fileName);
		in = new ObjectInputStream(fis);
		network = (PredictionNetwork) in.readObject();
		in.close();
		
		return network;
	}
	
	public static void saveNetwork(PredictionNetwork network, String fileName) throws IOException
	{	
		
		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		fos = new FileOutputStream(fileName);
		out = new ObjectOutputStream(fos);
		out.writeObject(network);
		out.close();
	}
}
