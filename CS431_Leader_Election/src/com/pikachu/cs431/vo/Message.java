/**
 * 
 */
package com.pikachu.cs431.vo;

/**
 * This is the msg used for communication between sockets.
 * @author Yuyang He
 * @date 12:59:08 AM, Oct 3, 2015
 * @version 1.0
 * @since
 */
public class Message
{
	private int currentWinnerIndex;

	/**
	 * Getter of currentWinnerIndex.
	 * @return the currentWinnerIndex
	 */
	public int getCurrentWinnerIndex()
	{
		return currentWinnerIndex;
	}

	/**
	 * Setter of currentWinnerIndex.
	 * @param currentWinnerIndex the currentWinnerIndex to set
	 */
	public void setCurrentWinnerIndex(int currentWinnerIndex)
	{
		this.currentWinnerIndex = currentWinnerIndex;
	}
	
	
}
