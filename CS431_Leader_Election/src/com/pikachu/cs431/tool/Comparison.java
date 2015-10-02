/**
 * 
 */
package com.pikachu.cs431.tool;

import java.util.Random;

/**
 * This function will compare which server is the winner.
 * 
 * @author Yuyang He
 * @date 2:08:26 AM, Oct 3, 2015
 * @version 1.0
 * @since
 */
public class Comparison
{
	/**
	 * This function will generate random number for comparison. But it will use
	 * the probability to control the winning probability since winner node may
	 * have many challenges but for the new node, it is its first time to play.
	 * 
	 * @param winner
	 *            ID of winner
	 * @param challenger
	 *            ID of current node
	 * @param middleNodeIndex
	 *            ID of the middle node
	 * @param totalNodes
	 *            total number of nodes
	 * @return the winner's ID
	 */
	public static int compare(int winner, int challenger, int middleNodeIndex, int totalNodes)
	{
		System.out.println("-------------------");
		System.out.println("Comparison starts. Node " + challenger + " is challenging node " + winner + ".");

		// one element
		// no need to compare
		if (winner == challenger)
		{
			System.out.println("Winner: node " + challenger);
			System.out.println("-------------------");
			return challenger;
		}

		// effective ID is used to convert ID > 1/2 to the mirror side
		// e.g. in total 9 nodes (ID from 0 - 8)
		// node 7 challenge node 5
		// is effective as node 1 challenge node 3
		int effectiveCurrentNodeID = -1;

		// node > 1/2
		if (challenger > middleNodeIndex)
		{
			effectiveCurrentNodeID = totalNodes - 1 - challenger;
			System.out.println("It is the same as node " + effectiveCurrentNodeID + " challenges node "
			        + (totalNodes - 1 - winner) + ".");
		} else
		{
			effectiveCurrentNodeID = challenger;
		}

		if (-1 == effectiveCurrentNodeID)
		{
			System.out.println("Something wrong. Comparison quits. Return -1.");
			System.out.println("-------------------");
			return -1;
		}

		Random random = new Random();
		int randomNum = -1;
		if (middleNodeIndex == challenger)
		{
			randomNum = random.nextInt(totalNodes + 1);
		} else
		{
			randomNum = random.nextInt(effectiveCurrentNodeID + 1);
		}
		System.out.println("Random number generated: " + randomNum);

		if (0 == randomNum)
		{
			System.out.println("Winner: node " + challenger);
			System.out.println("-------------------");
			return challenger;
		} else
		{
			System.out.println("Winner: node " + winner);
			System.out.println("-------------------");
			return winner;
		}
	}
}
