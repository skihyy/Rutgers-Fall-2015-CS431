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

import com.pikachu.cs431.tool.CloseUtil;
import com.pikachu.cs431.vo.Message;

/**
 * 
 * @author Tengfei peng
 * @date 下午2:03:42, 2015年10月2日
 * @version 1.0
 * @since
 */
public class Server implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Message message;

	private ServerSocket serverSocket;
	
	private int port;

	/**
	 * Constructors of Server.
	 * @param port
	 */
	public Server(Integer port)
	{
		this.port = port;
	}

	public void startServer() {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		System.out.println("Waiting for client connection...");
		
		while (true) {
			try {
				Socket socket = serverSocket.accept();
				System.out.println("accepet singal from client");
				// new Thread(new Server(socket)).start();
				ObjectInputStream ois = null;
				try {
					ois = new ObjectInputStream(new BufferedInputStream(
							socket.getInputStream()));
					Object o = ois.readObject();
					if (!(o instanceof Message)) {
						throw new Exception("NOT the Message");
					}
					
					message = (Message) o;
					System.out.println(message.toString());

					// call the comparable algorithm

				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					CloseUtil.closeAll(ois,socket,serverSocket);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Getter of message.
	 * @return the message
	 */
	public Message getMessage()
	{
		return message;
	}

	/**
	 * Setter of message.
	 * @param message the message to set
	 */
	public void setMessage(Message message)
	{
		this.message = message;
	}

}
