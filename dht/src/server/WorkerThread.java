package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class WorkerThread implements Runnable{
	private Socket clientSocket;
        private String name;
	public WorkerThread(Socket s,String name) {
		clientSocket = s;
                this.name = name;
	}
	public void run() {
		//taken from Server4SingleClient
		PrintWriter socketOut = null;
		BufferedReader socketIn = null;
		
		try {			
			//will use socketOut to send text to the server over the socket
			socketOut = new PrintWriter(clientSocket.getOutputStream(), true);
			//will use socketIn to receive text from the server over the socket
			socketIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (IOException e) {
			System.out.println("Cannot get I/O for the connection.");
			e.printStackTrace();
			System.exit(0);
		}
		
		/**
		 * Protocol (known to both parties): 
		 * Server blocks on a message from the client.
		 * Client sends a message and blocks on the server's response.
		 * Upon receipt of message, server will respond: "You said: " + message
		 * This continues for 3 rounds.
		 */
		
                //int i=0;
		String message = null;
                boolean x=true;
                while (x) {                
                    //System.out.println("Round (" + (i+1) + ")");
                    //System.out.println("Waiting for a message from the client.");
                    
                    try {
                            message = socketIn.readLine();
                    } catch (IOException e) {
                            e.printStackTrace();
                    }

                    if(message.equals("close"))
                    {
                        socketOut.println("Connection Closed");
                        x=false;
                    } 
                    //socketOut.println("You said: " + message);
                    if(message.equals("NextPort"))
                        socketOut.println(clientSocket.getLocalPort());
                    else if(message.equals("NextName"))
                        socketOut.println(this.name);
                    else
                    {
                        socketOut.println("Connected");
                        //System.out.println();
                    }
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
}
