package com.pikachu.cs431.vo;

/**
 * Challenge message class.
 * 
 * @author Yuyang He
 * @date 2:54:32 AM, Oct 4, 2015
 * @version 1.0
 * @since
 */
public class ChanllengeMessage extends Message
{

	private static final long serialVersionUID = 1L;

	private int currentWinnerIndex;

	/**
	 * * Constructors of ChanllengeMessage.
	 * 
	 * @param sender
	 *            sender of the message
	 * @param currentWinnerIndex
	 *            current actor's winner ID
	 */
	public ChanllengeMessage(int sender, int currentWinnerIndex)
	{
		super(sender, Message.CHALLENGE_MSG);
		this.currentWinnerIndex = currentWinnerIndex;
	}

	/**
	 * Getter of currentWinnerIndex.
	 * 
	 * @return the currentWinnerIndex
	 */
	public int getCurrentWinnerIndex()
	{
		return currentWinnerIndex;
	}

	/**
	 * Setter of currentWinnerIndex.
	 * 
	 * @param currentWinnerIndex
	 *            the currentWinnerIndex to set
	 */
	public void setCurrentWinnerIndex(int currentWinnerIndex)
	{
		this.currentWinnerIndex = currentWinnerIndex;
	}

	/**
	 * Getter of serialversionuid.
	 * 
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}

	@Override
	public String toString()
	{
		return "Sender: " + super.sender + ", message type: " + super.messageType + ", winner ID: "
		        + currentWinnerIndex;
	}

}
