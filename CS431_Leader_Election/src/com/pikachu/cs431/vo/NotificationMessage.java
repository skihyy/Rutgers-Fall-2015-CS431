package com.pikachu.cs431.vo;

public class NotificationMessage extends Message {

	private static final long serialVersionUID = 1L;

	private int leaderIndex;
	
	public NotificationMessage() {}
	
	
	public NotificationMessage(int sender, int leaderIndex){
		super(sender);
		this.leaderIndex=leaderIndex;
	}


	public int getLeaderIndex() {
		return leaderIndex;
	}


	public void setLeaderIndex(int leaderIndex) {
		this.leaderIndex = leaderIndex;
	}


	@Override
	public String toString() {
		return "NotificationMessage [leaderIndex=" + leaderIndex + "]";
	}
	
}
