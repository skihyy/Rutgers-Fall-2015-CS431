package com.pikachu.cs431.tool;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Close any thing can be closed.
 * 
 * @author Yuyang He
 * @date 3:30:08 AM, Oct 3, 2015
 * @version 1.0
 * @since
 */
public class CloseUtil
{

	/**
	 * Close any thing can be closed.
	 * 
	 * @param io
	 *            any thing may be closed
	 */
	public static void closeAll(Object... o)
	{		
		for (Object c : o)
		{
			if (null != c)
			{
				if(c instanceof Closeable)
				{
				    try
					{
						((Closeable)c).close();
					}
					catch (IOException e)
					{
						System.out.println("IOException occured. Check stream.");
						e.printStackTrace();
					}
				}
				 else if(c instanceof Socket)
				 {
					try
					{
						((Socket)c).close();
					}
					catch (IOException e)
					{
						System.out.println("Socket close error.");
						e.printStackTrace();
					}
				 }
				 else if(c instanceof ServerSocket)
				 {
					try
					{
						((ServerSocket) c).close();
					}
					catch (IOException e)
					{
						System.out.println("Server socket close error.");
						e.printStackTrace();
					}
				 }
			}
		}
	}
}
