package com.pikachu.cs431.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;

import com.pikachu.cs431.tool.CloseUtil;
import com.pikachu.cs431.vo.IPAddress;
import com.pikachu.cs431.vo.Message;

/**
 * 
 * @author Yuyang He
 * @date 3:35:35 AM, Oct 3, 2015
 * @version 1.0
 * @since
 */
public class Client implements Runnable, Serializable
{

	private static final long serialVersionUID = 1L;

	private IPAddress ipAddress;

	private Message message;

	public Client(IPAddress ipAddress, Message message)
	{
		this.ipAddress = ipAddress;
		this.message = message;
	}

	static Socket getConnection(String ip, int port)
	{
		Socket socket = null;
		try
		{
			socket = new Socket(ip, port);
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return socket;
	}

	@Override
	public void run()
	{
		// Socket
		Socket socket = Client.getConnection(ipAddress.getIp(), ipAddress.getPort());

		ObjectOutputStream oos = null;
		try
		{
			oos = new ObjectOutputStream(socket.getOutputStream());

			System.out.println("Sending message: " + message.toString());
			oos.writeObject(message);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			CloseUtil.closeAll(oos, socket);
		}
	}

}
