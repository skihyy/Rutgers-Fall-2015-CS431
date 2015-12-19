package com.pikachu.cs431.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map.Entry;

import com.pikachu.cs431.antities.Message;
import com.pikachu.cs431.util.CloseUtil;
import com.pikachu.cs431.util.InfoUtil;
import com.pikachu.cs431.util.MessageUtil;

public class Send implements Runnable
{
	public static String myName;
	public static boolean isManager = false;
	public static boolean isStranger = false;
	private BufferedReader console;
	protected ObjectOutputStream oos;
	public static boolean isRunning = true;
	private Message message;

	public Send()
	{
		console = new BufferedReader(new InputStreamReader(System.in));
	}

	public Send(Socket socket, String name)
	{
		this();
		Send.isStranger = true;
		Send.myName = name;
		try
		{
			oos = new ObjectOutputStream(socket.getOutputStream());

			String ip = socket.getInetAddress().toString() + "/" + socket.getLocalPort();
			if (ip.startsWith("/"))
			{
				ip = ip.substring(1);
			}

			this.message = new Message(Send.myName + System.currentTimeMillis(), InfoUtil.MESSAGE_NEW_STRANGER,
			        InfoUtil.MESSAGE_NEW_STRANGER, name + ";" + ip, InfoUtil.SERVER_IP);
			oos.writeObject(message);
			oos.flush();
			message = null;
		}
		catch (IOException e)
		{
			isRunning = false;
			CloseUtil.closeAllIO(console, oos);
		}
	}

	public Send(Socket socket, Message message)
	{
		this();
		try
		{
			oos = new ObjectOutputStream(socket.getOutputStream());
			this.message = message;
			Send.myName = message.getContent().split(":")[0];
		}
		catch (IOException e)
		{
			isRunning = false;
			CloseUtil.closeAllIO(console, oos);
		}
	}

	/**
	 * read the message from the console
	 * 
	 * @return
	 */
	private String getMsgFromConsole()
	{
		String message = null;
		try
		{
			message = console.readLine();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return message;
	}

	/**
	 * send message to other user
	 */
	public void sendMsg(Message message)
	{
		if (isStranger)
		{
			String msg = getMsgFromConsole();
			if (null != msg && msg.length() > 0)
			{
				Message newMsg = MessageUtil.msgHandle(msg, Send.myName);
				
				if(null == newMsg)
				{
					return;
				}
				
				String receiver = newMsg.getReceiver();

				for (Entry<String, String> manager : Receive.managerMap.entrySet())
				{
					if (manager.getKey().equalsIgnoreCase(receiver))
					{
						try
						{
							oos.writeObject(newMsg);
							oos.flush();
						}
						catch (IOException e)
						{
							isRunning = false;
							CloseUtil.closeAllIO(console, oos);
						}
						break;
					}
				}
			}
			return;
		}

		// if not message pass in, read the message from console
		if (null == message)
		{
			String msg = getMsgFromConsole();

			if (null != msg && msg.length() > 0)
			{
				Message newMsg = MessageUtil.msgHandle(msg, Send.myName);
				
				if(null == newMsg)
				{
					return;
				}
				
				if (!isManager)
				{
					String type = newMsg.getType();
					switch (type)
					{
						case InfoUtil.MESSAGE_APPROVE_BROADCAST:
						case InfoUtil.MESSAGE_APPROVE_MANAGER:
						case InfoUtil.MESSAGE_APPROVE_MEMBER:
						case InfoUtil.MESSAGE_DENY_BROADCAST:
						case InfoUtil.MESSAGE_DENY_MANAGER:
						case InfoUtil.MESSAGE_DENY_MEMBER:
							return;
					}
				}

				try
				{
					oos.writeObject(newMsg);
					oos.flush();
				}
				catch (IOException e)
				{
					isRunning = false;
					CloseUtil.closeAllIO(console, oos);
				}
			}
		} else
		{
			try
			{

				if (!isManager)
				{
					String type = message.getType();
					switch (type)
					{
						case InfoUtil.MESSAGE_APPROVE_BROADCAST:
						case InfoUtil.MESSAGE_APPROVE_MANAGER:
						case InfoUtil.MESSAGE_APPROVE_MEMBER:
						case InfoUtil.MESSAGE_DENY_BROADCAST:
						case InfoUtil.MESSAGE_DENY_MANAGER:
						case InfoUtil.MESSAGE_DENY_MEMBER:
							return;
					}
				}

				oos.writeObject(message);
				oos.flush();
			}
			catch (IOException e)
			{
				isRunning = false;
				CloseUtil.closeAllIO(console, oos);
			}
		}

	}

	@Override
	public void run()
	{
		if (null != message && InfoUtil.MESSAGE_CHECK_USER.endsWith(message.getType()))
		{
			sendMsg(message);
			message = null;
		}
		while (isRunning)
		{
			sendMsg(message);
		}
	}

}
