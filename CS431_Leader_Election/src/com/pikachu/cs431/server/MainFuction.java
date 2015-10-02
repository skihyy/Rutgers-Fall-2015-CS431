/**
 * 
 */
package com.pikachu.cs431.server;

import java.util.List;

import com.pikachu.cs431.tool.DBHandler;
import com.pikachu.cs431.vo.IPAddress;

/**
 * This is the main function of a single service.
 * @author Yuyang He
 * @date 1:07:00 AM, Oct 3, 2015
 * @version 1.0
 * @since
 */
public class MainFuction
{
	/**
	 * Main function.
	 * @param args 1st parameter is the port number.
	 */
	public static void main(String [] args)
	{
		if(1 != args.length)
		{
			System.out.println("No port number is entered. Program will quit.");
			return;
		}
		
		Integer port = null;
		try
		{
			port = Integer.parseInt(args[0]);
		}
		catch(NumberFormatException e)
		{
			System.out.println("Invalid port number. Program will quit.");
			return;
		}
		
		//reading from local file to get all ips and ports
		List<IPAddress> ipList = DBHandler.readSortedIPs();
		
		//server starts...
	}
}
