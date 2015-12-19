package com.pikachu.cs431.util;

import java.net.InetAddress;

import com.pikachu.cs431.antities.Message;

public class MessageUtil
{

	/**
	 * extract the word format such as (ip : port) into an array
	 * 
	 * @param content
	 * @return
	 */
	public static String[] getStrings(String content)
	{
		return content.split(":");
	}

	/**
	 * msg format: (sender@receiver type content)
	 * 
	 * @param message
	 * @return
	 */
	public static Message msgHandle(String message, String sender)
	{
		String details[] = message.split(" ");

		if (2 > details.length || null == sender)
		{
			return null;
		}

		if (details[0].startsWith("@"))
		{
			details[0] = details[0].substring(1);
		}

		StringBuilder sb = new StringBuilder();

		switch (details[1])
		{
			case InfoUtil.MESSAGE_APPLY_DECISION:
			case InfoUtil.MESSAGE_APPLY_MANAGER:
			case InfoUtil.MESSAGE_APPLY_MEMBER:
			case InfoUtil.MESSAGE_APPROVE_MANAGER:
			case InfoUtil.MESSAGE_CHAT:
			case InfoUtil.MESSAGE_CHECK_USER:
			case InfoUtil.MESSAGE_DENY_BROADCAST:
			case InfoUtil.MESSAGE_DENY_MANAGER:
			case InfoUtil.MESSAGE_MANAGER_MAP:
			case InfoUtil.MESSAGE_NAME_EXISTS:
			case InfoUtil.MESSAGE_NEW_STRANGER:
			case InfoUtil.MESSAGE_SYSTEM_MESSAGE:
			case InfoUtil.MESSAGE_USER_LOGIN_FAIL:
			case InfoUtil.MESSAGE_USER_LOGIN_SUCCESS:
				return new Message(sender + System.currentTimeMillis(), details[1], details[1], sender, details[0]);
			case InfoUtil.MESSAGE_APPROVE_MEMBER:
			case InfoUtil.MESSAGE_DENY_MEMBER:
				return new Message(sender + System.currentTimeMillis(), details[1], InfoUtil.MESSAGE_APPLY_DECISION,
				        sender, details[0]);
			case InfoUtil.MESSAGE_APPROVE_BROADCAST:
				for (int i = 2; i < details.length; ++i)
				{
					sb.append(details[i] + " ");
				}
				return new Message(sender + System.currentTimeMillis(), sb.toString(), InfoUtil.MESSAGE_APPROVE_BROADCAST, details[0],
				        details[0]);
			case InfoUtil.MESSAGE_APPLY_BROADCAST:
				for (int i = 2; i < details.length; ++i)
				{
					sb.append(details[i] + " ");
				}
				return new Message(sender + System.currentTimeMillis(), sb.toString(), InfoUtil.MESSAGE_APPLY_BROADCAST, sender,
				        details[0]);
		}
		for (int i = 1; i < details.length; ++i)
		{
			sb.append(details[i] + " ");
		}
		return new Message(sender + System.currentTimeMillis(), sb.toString(), InfoUtil.MESSAGE_CHAT, sender,
		        details[0]);
	}

	/**
	 * return the receiver format (IP-Port)
	 * 
	 * @param ip
	 * @param port
	 * @return
	 */
	public static String getReceiver(String ip, int port)
	{
		StringBuilder sb = new StringBuilder();
		String portAfterHandle = String.valueOf(port);
		return sb.append(ip).append("/").append(portAfterHandle).toString();
	}

	/**
	 * 
	 * @Title: ipHandle @Description: change the ip format /127.0.0.1 to
	 *         127.0.0.1 @param @param ipAddress @param @return @return
	 *         String @throws
	 */
	public static String ipHandle(InetAddress ipAddress)
	{
		String ip = ipAddress.toString();
		if (ip.startsWith("/"))
		{
			return ip.substring(1, ip.length());
		}
		return ip;
	}

	/**
	 * 
	 * @Title: loginMsg @Description: send msg to the server to check
	 *         login @param @param username @param @param password @param @param
	 *         serverIP @param @return @return Message @throws
	 */
	public static Message getLoginMsg(String username, String password, String serverIP, InetAddress clientIp, int port)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(username).append(":").append(password);
		Message loginMsg = new Message(username + System.currentTimeMillis(), sb.toString(),
		        InfoUtil.MESSAGE_CHECK_USER, MessageUtil.getReceiver(MessageUtil.ipHandle(clientIp), port), serverIP);
		return loginMsg;
	}

	/**
	 * 
	 * @Title: getLoginMsg @Description: return the login info @param @param
	 *         message @param @return @return String[] @throws
	 */
	public static String[] getLoginMsg(String content)
	{
		String[] loginInfo = content.split(":");
		return loginInfo;
	}

}
