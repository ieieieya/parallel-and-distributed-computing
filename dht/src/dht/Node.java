/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dht;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import server.ServerThread;
/**
 * 
 * @author Gokay
 */

public class Node implements Runnable{
	private static String serverName = "localhost";
	
	protected String _name;
	protected int _portNumber;
	protected int _portNumberOtherNode;
	protected boolean _setupNode;
	protected boolean _firstNode;
	protected ArrayList<Node> _nodeList=new ArrayList<Node>();
	protected ServerSocket _serverSocket;
        private Socket clientSocket;
        private PrintWriter socketOut;
        private BufferedReader socketIn;
        
	public Node(String Name, int PortNumber, int PortNumberOtherNode,
			boolean SetupNode, boolean Firstnode) {
		this._name = Name;
		this._portNumber = PortNumber;
		this._portNumberOtherNode = PortNumberOtherNode;
		this._setupNode = SetupNode;
		this._firstNode = Firstnode;
	}
	
	public void connectOtherNode()
	{
		clientSocket = null;
		socketOut = null;
		socketIn = null;
		
		try {
			//create socket and connect to the server
			clientSocket = new Socket(serverName, this._portNumberOtherNode );
			//will use socketOut to send text to the server over the socket
			socketOut = new PrintWriter(clientSocket.getOutputStream(), true);
			//will use socketIn to receive text from the server over the socket
			socketIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch(UnknownHostException e) { //if serverName cannot be resolved to an address
			System.out.println("Who is " + serverName + "?");
			e.printStackTrace();
			System.exit(0);
		} catch (IOException e) {
			System.out.println("Cannot get I/O for the connection.");
			e.printStackTrace();
			System.exit(0);
		}

		System.out.println("Message sent, waiting for the server's response.");
		String response = null;
		try {
			response = socketIn.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Server's response was: \n\t\"" + response + "\"");
	}
        
        public void closeConnection()
        {
                 
                try {
			//DataOutputStream dOut = new DataOutputStream(clientSocket.getOutputStream());

                        // Send first message
                        //dOut.writeByte(1);
                        //dOut.writeUTF("This is the first type of message.");
			//will use socketOut to send text to the server over the socket
			socketOut.println("sdeneme");
			//will use socketIn to receive text from the server over the socket
			socketIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch(UnknownHostException e) { //if serverName cannot be resolved to an address
			System.out.println("Who is " + serverName + "?");
			e.printStackTrace();
			System.exit(0);
		} catch (IOException e) {
			System.out.println("Cannot get I/O for the connection.");
			e.printStackTrace();
			System.exit(0);
		}
                String response = null;
		try {
			response = socketIn.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
                //close all streams
		socketOut.close();
		try {
			socketIn.close();
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        }

	@Override
	public void run() {
		//Server4SingleClient serverSocket=new Server4SingleClient();
                ServerThread    serverSocket=new ServerThread();
		serverSocket.establishConnection(this._serverSocket, this._portNumber,this._name);
	}
}