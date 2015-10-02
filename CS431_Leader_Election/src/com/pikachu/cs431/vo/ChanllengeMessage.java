package com.pikachu.cs431.vo;

public class ChanllengeMessage extends Message {

	private static final long serialVersionUID = 1L;

	private int currentWinnerIndex;
	
	public ChanllengeMessage() {}
	
	public ChanllengeMessage(int sender,int currentWinnerIndex) {
	    super(sender);
	    this.currentWinnerIndex=currentWinnerIndex;
	}

	public int getCurrentWinnerIndex() {
		return currentWinnerIndex;
	}

	public void setCurrentWinnerIndex(int currentWinnerIndex) {
		this.currentWinnerIndex = currentWinnerIndex;
	}

	@Override
	public String toString() {
		return "ChanllengeMessage [currentWinnerIndex=" + currentWinnerIndex
				+ "]";
	}
	
}
