/**
 * 
 */
package com.pikachu.cs431.vo;

import java.io.Serializable;

/**
 * This is the abstract message.
 * 
 * @author Tengfei Peng
 * @date 12:59:08 AM, Oct 3, 2015
 * @version 1.0
 * @since
 */
public abstract class Message implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * challenge message
	 */
	public static final int CHALLENGE_MSG = 0;
	
	/**
	 * notification message
	 */
	public static final int NOTIFICATION_MSG = 1;

	protected int sender;
	
	/**
	 * message type
	 * 0 -> challenge message
	 * 1 -> notification message
	 */
	protected int messageType;

	/**
	 * Constructors of Message.
	 * 
	 * @param sender
	 */
	public Message(int sender, int messageType)
	{
		this.sender = sender;
		this.messageType = messageType;
	}

	/**
	 * @return sender
	 */
	public int getSender()
	{
		return sender;
	}

	/**
	 * @param sender
	 */
	public void setSender(int sender)
	{
		this.sender = sender;
	}

	/**
	 * Getter of messageType.
	 * @return the messageType
	 */
	public int getMessageType()
	{
		return messageType;
	}

	/**
	 * Setter of messageType.
	 * @param messageType the messageType to set
	 */
	public void setMessageType(int messageType)
	{
		this.messageType = messageType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "Sender: " + sender + ", message type: " + messageType;
	}
	
	
}
