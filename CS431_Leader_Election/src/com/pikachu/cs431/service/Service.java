/**
 * 
 */
package com.pikachu.cs431.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import com.pikachu.cs431.server.Server;
import com.pikachu.cs431.tool.Comparison;
import com.pikachu.cs431.tool.MsgTool;
import com.pikachu.cs431.vo.ChanllengeMessage;
import com.pikachu.cs431.vo.IPAddress;
import com.pikachu.cs431.vo.Message;
import com.pikachu.cs431.vo.NotificationMessage;

/**
 * The service of leader election.
 * 
 * @author Yuyang He
 * @date 12:25:11 AM, Oct 4, 2015
 * @version 1.0
 * @since
 */
public class Service
{
	/**
	 * all actors' address
	 */
	private List<IPAddress> ipList;

	/**
	 * current actor's ID
	 */
	private Integer id;

	/**
	 * current winner's ID
	 */
	private Integer currentWinner;

	/**
	 * whether the total number of actors is odd
	 */
	private boolean isOdd;

	/**
	 * total number of actors
	 */
	private Integer totalNodes;

	/**
	 * the middle point actor's ID if it is even, then it is max (ID - 1) / 2
	 */
	private Integer middlePoint;

	/**
	 * whether current actor needs to start the battle
	 */
	private boolean needToStart;

	/**
	 * Indicating whether middle nodes has its first challenge when the total
	 * number of nodes are even.
	 */
	private boolean middleNodesChallenged = false;

	/**
	 * Constructors of Service.
	 * 
	 * @param ipList
	 *            All actors' IP addresses list
	 */
	public Service(List<IPAddress> ipList, int port)
	{
		this.ipList = ipList;
		setNeedToStart(false);
		initialzation(port);
	}

	/**
	 * Initialing the service.
	 */
	private void initialzation(int port)
	{
		IPAddress localIP = null;

		try
		{
			InetAddress localAddress = InetAddress.getLocalHost();
			localIP = new IPAddress(localAddress.getHostAddress(), port);
		}
		catch (UnknownHostException e)
		{
			System.out.println("Unknown host exception occured. Quitting program.");
			e.printStackTrace();
		}

		// check the position of current actor
		id = ipList.indexOf(localIP);
		currentWinner = id;
		totalNodes = ipList.size();

		if (0 == totalNodes % 2)
		{
			middlePoint = totalNodes / 2 - 1;
		} else
		{
			isOdd = true;
			middleNodesChallenged = true;
			middlePoint = (totalNodes - 1) / 2;
		}

		// whether the current actor should start its fight
		startingControl();
	}

	/**
	 * Whether the current actor should start its fight.
	 */
	private void startingControl()
	{
		// when 2 actors, only one actor needs to start the fight
		// which is the second one
		// because the first one is the middle node who will say to all nodes
		if (2 == totalNodes)
		{
			if (1 == id)
			{
				setNeedToStart(true);
			}
		}
		// > 3 nodes
		// the first one and last one should start their jobs
		else if (2 < totalNodes)
		{
			if (0 == id || totalNodes - 1 == id)
			{
				setNeedToStart(true);
			}

			// even node and total number > 2
			// at least 4 nodes
			// middle nodes need a fight
			if (!isOdd)
			{
				if (middlePoint + 1 == id)
				{
					setNeedToStart(true);
				}
			}
		}

	}

	/**
	 * This function will start initial service to finish all process to select
	 * the leader.
	 * 
	 * @param port
	 *            port number of current actor
	 */
	public void startService(int port)
	{
		if (needToStart)
		{
			IPAddress ipAddress = getNextIPAddress();
			MsgTool.sendMessage(ipAddress, new ChanllengeMessage(id, currentWinner));
		}

		// if do not need to challenge
		// start server, waiting other's challenge
		Server server = new Server(port);
		server.startServer();

		Message message = server.getMessage();

		while (Message.CHALLENGE_MSG == message.getMessageType())
		{
			ChanllengeMessage cmsg = (ChanllengeMessage) message;

			int winnerID = Comparison.compare(cmsg.getCurrentWinnerIndex(), id, middlePoint, totalNodes);
			currentWinner = winnerID;

			// if middle point has fight
			// and
			// total number is even
			if (middlePoint == id && middleNodesChallenged)
			{
				System.out.println("Leader is selected: " + currentWinner);
				NotificationMessage leaderMsg = new NotificationMessage(id, currentWinner);

				IPAddress currentActor = ipList.get(id);

				for (IPAddress tmp : ipList)
				{
					if (!tmp.equals(currentActor))
					{
						MsgTool.sendMessage(tmp, leaderMsg);
					}
				}
				break;
			}

			if (middlePoint == id && !middleNodesChallenged)
			{
				middleNodesChallenged = true;
			} else
			{
				IPAddress ipAddress = getNextIPAddress();
				MsgTool.sendMessage(ipAddress, new ChanllengeMessage(id, currentWinner));
			}

			server.startServer();
			message = server.getMessage();
		}

		if (middlePoint != id && middlePoint + 1 != id)
		{
			// notification message received
			NotificationMessage nmsg = (NotificationMessage) message;
			int leaderID = nmsg.getLeaderIndex();
			// winner is leader
			this.currentWinner = leaderID;
		}
	}

	/**
	 * Getter of ipList.
	 * 
	 * @return the ipList
	 */
	public List<IPAddress> getIpList()
	{
		return ipList;
	}

	/**
	 * Setter of ipList.
	 * 
	 * @param ipList
	 *            the ipList to set
	 */
	public void setIpList(List<IPAddress> ipList)
	{
		this.ipList = ipList;
	}

	/**
	 * Getter of id.
	 * 
	 * @return the id
	 */
	public Integer getId()
	{
		return id;
	}

	/**
	 * Setter of id.
	 * 
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id)
	{
		this.id = id;
	}

	/**
	 * Getter of currentWinner.
	 * 
	 * @return the currentWinner
	 */
	public Integer getCurrentWinner()
	{
		return currentWinner;
	}

	/**
	 * Setter of currentWinner.
	 * 
	 * @param currentWinner
	 *            the currentWinner to set
	 */
	public void setCurrentWinner(Integer currentWinner)
	{
		this.currentWinner = currentWinner;
	}

	/**
	 * Getter of isOdd.
	 * 
	 * @return the isOdd
	 */
	public boolean isOdd()
	{
		return isOdd;
	}

	/**
	 * Setter of isOdd.
	 * 
	 * @param isOdd
	 *            the isOdd to set
	 */
	public void setOdd(boolean isOdd)
	{
		this.isOdd = isOdd;
	}

	/**
	 * Getter of totalNodes.
	 * 
	 * @return the totalNodes
	 */
	public Integer getTotalNodes()
	{
		return totalNodes;
	}

	/**
	 * Setter of totalNodes.
	 * 
	 * @param totalNodes
	 *            the totalNodes to set
	 */
	public void setTotalNodes(Integer totalNodes)
	{
		this.totalNodes = totalNodes;
	}

	/**
	 * Getter of middlePoint.
	 * 
	 * @return the middlePoint
	 */
	public Integer getMiddlePoint()
	{
		return middlePoint;
	}

	/**
	 * Setter of middlePoint.
	 * 
	 * @param middlePoint
	 *            the middlePoint to set
	 */
	public void setMiddlePoint(Integer middlePoint)
	{
		this.middlePoint = middlePoint;
	}

	/**
	 * Getter of needToStart.
	 * 
	 * @return the needToStart
	 */
	public boolean isNeedToStart()
	{
		return needToStart;
	}

	/**
	 * Setter of needToStart.
	 * 
	 * @param needToStart
	 *            the needToStart to set
	 */
	public void setNeedToStart(boolean needToStart)
	{
		this.needToStart = needToStart;
	}

	/**
	 * Get next IP for challenge.
	 * 
	 * @return next actor's IP address
	 */
	private IPAddress getNextIPAddress()
	{
		if (id <= middlePoint)
		{
			return ipList.get(id + 1);
		} else
		{
			if (isOdd)
			{
				return ipList.get(id - 1);
			} else
			{
				// even condition
				// if 8 nodes (ID from 0 - 7)
				// node 5 should connect 3
				// instead of 4
				if (middlePoint + 1 == id - 1)
				{
					return ipList.get(middlePoint);
				}

				return ipList.get(id - 1);
			}
		}
	}
}
