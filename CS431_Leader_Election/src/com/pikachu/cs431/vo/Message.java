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

	private int sender;
	
	/**
	 * msg mode
	 * 0 -> challenge
	 * 1 -> notification
	 */
	private int mode;

	/**
	 * Constructors of Message.
	 */
	public Message()
	{
	}

	/**
	 * Constructors of Message.
	 * 
	 * @param sender
	 */
	public Message(int sender)
	{
		this.sender = sender;
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
	 * Getter of mode.
	 * @return the mode
	 */
	public int getMode()
	{
		return mode;
	}

	/**
	 * Setter of mode.
	 * @param mode the mode to set
	 */
	public void setMode(int mode)
	{
		this.mode = mode;
	}
}
