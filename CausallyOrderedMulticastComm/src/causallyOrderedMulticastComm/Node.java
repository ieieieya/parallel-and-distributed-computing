/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package causallyOrderedMulticastComm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;


/**
 *
 * @author Gokay
 */
public class Node implements Runnable{
    	private static final String _serverName = "localhost";
	private InetAddress _group;
        private MulticastSocket _multicastSocket;
	public String _nodeName;
	private int _portNumber = 6000;
        protected ArrayList<ArrayList<Integer>> _queue;
        private InetAddress _multicastAddress;
        private InetAddress _localHost;
        private int _sleepTime;
        public JTextArea _textArea;

    public Node(String Name,int SleepTime) throws UnknownHostException {
        
       
            this._nodeName = Name;
            this._sleepTime = SleepTime;
            this._group = InetAddress.getByName("228.5.6.7");
            this._multicastAddress = InetAddress.getByName("230.0.0.1");
            this._localHost = InetAddress.getLocalHost();
          
            _textArea = new JTextArea(1000, 0);
            _textArea.setEditable(false);
    }
    
    public void sendMessage(String Message) throws IOException, InterruptedException
    {
        DatagramSocket dgramSocket = new DatagramSocket();
        DatagramPacket packet = null;
      try {

        // serialize the multicast message

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream (bos);
        out.writeObject(new String("Message : " + Message));
        out.flush();
        out.close();

        // Create a datagram packet and send it

        packet = new DatagramPacket(bos.toByteArray(),
                                    bos.size(),
                                    _multicastAddress,
                                    _portNumber);



        // send the packet

        dgramSocket.send(packet);

        System.out.println("sending multicast message");
      }catch (IOException ioe) {

        System.out.println("error sending multicast");

        ioe.printStackTrace(); System.exit(1);

      }
        
    }
    
    public void run() {

    try {

      System.out.println("Setting up multicast receiver");

      _multicastSocket = new MulticastSocket(_portNumber);

      _multicastSocket.joinGroup(_multicastAddress);

    } catch(IOException ioe) {

      System.out.println("Trouble opening multicast port");

      ioe.printStackTrace();      System.exit(1);

    }


    DatagramPacket packet;

    System.out.println("Multicast receiver set up ");

    while (true) {

      try {
        Thread.sleep(this._sleepTime);
        byte[] buf = new byte[1000];

        packet = new DatagramPacket(buf,buf.length);

        //System.out.println("McastReceiver: waiting for packet");

        _multicastSocket.receive(packet);

        

        ByteArrayInputStream bistream = 

          new ByteArrayInputStream(packet.getData());

        ObjectInputStream ois = new ObjectInputStream(bistream);

        String value = (String) ois.readObject();

        _textArea.append(value + "\n");

        // ignore packets from myself, print the rest

        //if (!(packet.getAddress().equals(localHost))) {

          System.out.println(this._nodeName +" Received multicast packet: "+

                           value + " from: " + packet.getAddress());

        //} 

        ois.close();

        bistream.close();

      } catch(IOException ioe) {

        System.out.println("Trouble reading multicast message");

        ioe.printStackTrace();  System.exit(1);

      } catch (ClassNotFoundException cnfe) {

        System.out.println("Class missing while reading mcast packet");

        cnfe.printStackTrace(); System.exit(1);

      } catch (InterruptedException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

  }
    
    public void closeConnection() throws IOException
    {
        this._multicastSocket.leaveGroup(_multicastAddress);
    }
}