/**
 * 
 */
package com.pikachu.cs431.server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

import com.pikachu.cs431.vo.Message;



/**
 * 
 * @author Tengfei peng
 * @date 下午2:03:42, 2015年10月2日
 * @version 1.0
 * @since
 */
public class Server implements Runnable,Serializable {
	
	private static final long serialVersionUID = 1L;

	private Socket socket;

	private static ServerSocket serverSocket;
	
	public Server(Socket socket) {
		this.socket = socket;
	}

	public static void startServer(int port){
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		System.out.println("waiting for client connection.....");
		while (true) {
			try {
				Socket socket = serverSocket.accept();
				System.out.println("accepet singal from client");
				new Thread(new Server(socket)).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void run() {
		
		ObjectInputStream ois=null;	    
	    try {
			ois=new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
			Object o=ois.readObject();
			if(!(o instanceof Message)){ 
				throw new Exception("NOT the Message");
			}	
			Message message=(Message)o;
			System.out.println(message.toString());
			
			// call the comparable algorithm
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			//CloseUtil.closeAll(ois,socket,serverSocket);
		}
	    
	}

}
