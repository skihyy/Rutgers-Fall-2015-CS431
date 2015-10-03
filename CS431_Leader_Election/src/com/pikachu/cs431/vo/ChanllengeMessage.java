package com.pikachu.cs431.vo;

public class ChanllengeMessage extends Message
{

	private static final long serialVersionUID = 1L;

	private int currentWinnerIndex;

	public ChanllengeMessage(int sender, int currentWinnerIndex)
	{
		super(sender, Message.CHALLENGE_MSG);
		this.currentWinnerIndex = currentWinnerIndex;
	}

	public int getCurrentWinnerIndex()
	{
		return currentWinnerIndex;
	}

	public void setCurrentWinnerIndex(int currentWinnerIndex)
	{
		this.currentWinnerIndex = currentWinnerIndex;
	}

	@Override
	public String toString()
	{
		return "Sender: " + super.sender + ", message type: " + super.messageType + ", winner ID: " + currentWinnerIndex;
	}

}
