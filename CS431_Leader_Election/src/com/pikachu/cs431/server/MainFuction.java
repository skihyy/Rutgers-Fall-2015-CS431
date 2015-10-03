/**
 * 
 */
package com.pikachu.cs431.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import com.pikachu.cs431.tool.Comparison;
import com.pikachu.cs431.tool.DBHandler;
import com.pikachu.cs431.vo.ChanllengeMessage;
import com.pikachu.cs431.vo.IPAddress;
import com.pikachu.cs431.vo.Message;
import com.pikachu.cs431.vo.NotificationMessage;

/**
 * This is the main function of a single service.
 * 
 * @author Yuyang He
 * @date 1:07:00 AM, Oct 3, 2015
 * @version 1.0
 * @since
 */
public class MainFuction
{
	// all connection addresses
	private static List<IPAddress> ipList = null;

	// current actor's id in the ipList
	private static Integer id = null;

	private static Integer totalNodes = null;

	private static Integer middlePoint = null;

	/**
	 * whether this actor has fight with other one
	 */
	private static boolean need2Start = false;

	private static boolean isOdd = false;

	private static int currentWinner;

	/**
	 * Main function.
	 * 
	 * @param args
	 *            1st parameter is the port number.
	 */
	public static void main(String[] args)
	{
		if (1 != args.length)
		{
			System.out.println("No port number is entered. Program will quit.");
			return;
		}

		Integer port = null;
		try
		{
			port = Integer.parseInt(args[0]);
		}
		catch (NumberFormatException e)
		{
			System.out.println("Invalid port number. Program will quit.");
			return;
		}

		// reading from local file to get all ips and ports
		ipList = DBHandler.readSortedIPs();

		if (0 == ipList.size())
		{
			System.out.println("Error for reading DB file. Quitting.");
			return;
		}

		IPAddress localIP = null;

		try
		{
			InetAddress localAddress = InetAddress.getLocalHost();
			localIP = new IPAddress(localAddress.getHostAddress(), port);
		}
		catch (UnknownHostException e)
		{
			System.out.println("Unknown host exception occured.");
			e.printStackTrace();
		}

		// check where is myself
		id = ipList.indexOf(localIP);
		currentWinner = id;
		totalNodes = ipList.size();

		if (0 == totalNodes % 2)
		{
			middlePoint = totalNodes / 2 - 1;
		} else
		{
			isOdd = true;
			middlePoint = (totalNodes - 1) / 2;
		}

		// 1 actor doesn't need to start
		if (1 == totalNodes)
		{
			System.out.println("The leader: " + id);
			return;
		}

		// the 1st and last one needs to start their jobs
		if (0 == id || totalNodes - 1 == id)
		{
			need2Start = true;
		}
		// 2 actors only needs node 1 connects node 2
		// node 2 connects node 1 is redundant
		if (2 == totalNodes && totalNodes - 1 == id)
		{
			need2Start = false;
		}

		if (null == id || null == ipList || null == totalNodes || null == middlePoint)
		{
			System.out.println("IP or index error occured. Quitting program.");
			return;
		}

		// server starts...
		// waiting results
		serviceStart(port);
	}

	/**
	 * This function will start initial service to finish all process to select
	 * the leader.
	 * 
	 * @param port
	 *            current port number
	 */
	private static void serviceStart(Integer port)
	{
		// now needs to send msg
		if (need2Start)
		{
			sendBattleMsg();
		}

		// if don't need to send
		// start server
		Server server = new Server(port);
		server.startServer();
		ChanllengeMessage chanllengeMessage = (ChanllengeMessage) server.getMessage();

		// comparing
		int winnerID = Comparison.compare(chanllengeMessage.getCurrentWinnerIndex(), id, middlePoint, totalNodes);
		currentWinner = winnerID;

		// then if there is a leader
		// announce to all
		if (id == middlePoint)
		{
			System.out.println("Leader is selected: " + currentWinner);
			NotificationMessage leaderMsg = new NotificationMessage(id, currentWinner);

			for (IPAddress tmp : ipList)
			{
				Client tmpClient = new Client(tmp, leaderMsg);
				Thread thread = new Thread(tmpClient);
				thread.start();
			}
		}

		// if not the leader, continue to the next player
		sendBattleMsg();

		// then waiting for results;
		Server server4Leader = new Server(port);
		server4Leader.startServer();
		NotificationMessage notificationMessage = (NotificationMessage) server.getMessage();
		int leader = notificationMessage.getLeaderIndex();
		currentWinner = leader;
		System.out.println("Leader is selected: " + currentWinner);
	}

	private static void sendBattleMsg()
	{
		IPAddress ipAddress = getNextIP();
		Message message = getFightMessage();
		Client client = new Client(ipAddress, message);
		Thread thread = new Thread(client);
		thread.start();
	}

	/**
	 * Return the fight message with current winner.
	 * 
	 * @return
	 */
	private static Message getFightMessage()
	{
		return new ChanllengeMessage(id, currentWinner);
	}

	/**
	 * Get next IP for battle.
	 * 
	 * @return next IP
	 */
	private static IPAddress getNextIP()
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
				if (middlePoint + 1 == id - 1)
				{
					return ipList.get(middlePoint);
				}

				return ipList.get(id - 1);
			}
		}
	}
}
