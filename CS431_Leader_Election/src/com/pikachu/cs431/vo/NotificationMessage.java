package com.pikachu.cs431.vo;

/**
 * Notificating msg to all nodes.
 * 
 * @author Yuyang He
 * @date 3:32:21 AM, Oct 3, 2015
 * @version 1.0
 * @since
 */
public class NotificationMessage extends Message
{

	private static final long serialVersionUID = 1L;

	private int leaderIndex;

	/**
	 * Constructors of NotificationMessage.
	 * 
	 * @param sender
	 * @param leaderIndex
	 */
	public NotificationMessage(int sender, int leaderIndex)
	{
		super(sender, Message.NOTIFICATION_MSG);
		this.leaderIndex = leaderIndex;
	}

	/**
	 * @return
	 */
	public int getLeaderIndex()
	{
		return leaderIndex;
	}

	/**
	 * @param leaderIndex
	 */
	public void setLeaderIndex(int leaderIndex)
	{
		this.leaderIndex = leaderIndex;
	}

	@Override
	public String toString()
	{
		return "Sender: " + super.sender + ", message type: " + super.messageType + ", leader ID: " + leaderIndex;
	}

}
