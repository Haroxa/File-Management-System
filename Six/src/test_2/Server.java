package test_2;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
public class Server extends Thread {
	private static ArrayList<BufferedWriter> clients;           
	private static ServerSocket server; 
	private String nome;
	private Socket con;
	private InputStream in;    
	private BufferedReader bfr;
	  
	public Server(Socket con){
		this.con = con;
		try {
			in  = con.getInputStream();
			bfr = new BufferedReader(
					new InputStreamReader(in));
		} catch (IOException e) {
			e.printStackTrace();
		}                          
	}    
	  
	public void run(){          
		try{                                 
			String msg;
		    OutputStream ou =  this.con.getOutputStream();
		    BufferedWriter bfw = new BufferedWriter(
		    						new OutputStreamWriter(ou)); 
		    clients.add(bfw);
		    nome = msg = bfr.readLine();
		              
		    while(!"Logout".equalsIgnoreCase(msg) && msg != null){           
		    	msg = bfr.readLine();
		    	sendToAll(bfw, msg);
		    	System.out.println(msg);                                              
		    }		                                     
		}catch (Exception e) {
			e.printStackTrace();
		}                       
	}
	  
	public void sendToAll(BufferedWriter bwOutput, String msg) throws  IOException{
	    BufferedWriter bwS;	     
	    for(BufferedWriter bw :  clients){
	    	bwS = (BufferedWriter)bw;
	    	if(!(bwOutput == bwS)){
	    		bw.write(nome + " -> " + msg+"\r\n");
	    		bw.flush(); 
	    	}
	    }          
	}
	  
	public static void main(String []args) {		   
		try{
			JLabel lblMessage = new JLabel("Server Port:");
		    JTextField txtPort = new JTextField("12345");
		    Object[] texts = {lblMessage, txtPort };  
		    JOptionPane.showMessageDialog(null, texts);
		    server = new ServerSocket(Integer.parseInt(txtPort.getText()));
		    clients = new ArrayList<BufferedWriter>();
		    JOptionPane.showMessageDialog(null,"Active Server at Port: "+ txtPort.getText());
		   
		    while(true){
		    	System.out.println("Waiting for connection...");
		    	Socket con = server.accept();
																																																																																									System.out.println("Client connected...");
		    	Thread t = new Server(con);
		        t.start();   
		    }		                             
		}catch (Exception e) {
		    e.printStackTrace();
		}                       
	}                   
} 