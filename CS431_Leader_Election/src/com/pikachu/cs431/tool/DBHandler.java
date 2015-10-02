/**
 * 
 */
package com.pikachu.cs431.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.pikachu.cs431.vo.IPAddress;

/**
 * This function will read local disk to get all ips and ports.
 * 
 * @author Yuyang He
 * @date 1:13:52 AM, Oct 3, 2015
 * @version 1.0
 * @since
 */
public class DBHandler
{
	private static File file = new File("db.dat");

	/**
	 * Read local disk to get all ips and ports.
	 * @return List<IPAddress> which has been sorted.
	 */
	@SuppressWarnings("unchecked")
	public static List<IPAddress> readSortedIPs()
	{
		List<IPAddress> ipList = readIPs();
		Collections.sort(ipList);
		
		return ipList;
	}

	/**
	 * Read local disk to get all ips and ports.
	 * @return List<IPAddress>
	 */
	public static List<IPAddress> readIPs()
	{
		InputStreamReader isr = null;
		BufferedReader br = null;

		// Synchronized list
		// easy for update
		List<IPAddress> ipList = Collections.synchronizedList(new ArrayList<IPAddress>());

		if (file.isFile() && file.exists())
		{
			try
			{
				isr = new InputStreamReader(new FileInputStream(file));
				br = new BufferedReader(isr);

				String line = null;

				while (null != (line = br.readLine()))
				{
					IPAddress tmp = createIPVO(line);

					if (null != tmp && !ipList.contains(tmp))
					{
						ipList.add(tmp);
					}
				}
			}
			catch (FileNotFoundException e)
			{
				System.out.println("File db.dat cannot be found.");
				e.printStackTrace();
			}
			catch (IOException e)
			{
				System.out.println("IO exception occured when reading db.dat.");
				e.printStackTrace();
			}
			finally
			{
				if (null != br)
				{
					try
					{
						br.close();
					}
					catch (IOException e)
					{
						System.out.println("IO exception occured when closing buffered reader.");
						e.printStackTrace();
					}
				}

				if (null != isr)
				{
					try
					{
						isr.close();
					}
					catch (IOException e)
					{
						System.out.println("IO exception occured when closing input stream reader.");
						e.printStackTrace();
					}
				}
			}
		}

		return ipList;
	}

	/**
	 * Transfer a line of string to IP address object.
	 * 
	 * @param line
	 * @return
	 */
	private static IPAddress createIPVO(String line)
	{
		String details[] = line.split(" ");

		if (2 != details.length)
		{
			System.out.println("The format of IP read from the file is incorrect. Return null.");
			return null;
		}

		Integer port = null;
		try
		{
			port = Integer.parseInt(details[1]);
		}
		catch (NumberFormatException e)
		{
			System.out.println("Invalid port number. Return null.");
			return null;
		}

		return new IPAddress(details[0], port);
	}
}
