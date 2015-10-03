/**
 * 
 */
package com.pikachu.cs431.tool;

import com.pikachu.cs431.server.Client;
import com.pikachu.cs431.vo.IPAddress;
import com.pikachu.cs431.vo.Message;

/**
 * This class is a tool class used to send message.
 * 
 * @author Yuyang He
 * @date 1:26:49 AM, Oct 4, 2015
 * @version 1.0
 * @since
 */
public class MsgTool
{

	/**
	 * Sendng a challenge message.
	 * 
	 * @param ipAddress
	 * @param message
	 */
	public static void sendMessage(IPAddress ipAddress, Message message)
	{
		Client client = new Client(ipAddress, message);
		Thread thread = new Thread(client);
		thread.start();
	}

}
