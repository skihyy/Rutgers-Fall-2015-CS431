package com.pikachu.cs431.tool;

import java.io.Closeable;

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
	public static void closeAll(Closeable... io)
	{
		for (Closeable c : io)
		{
			if (null != c)
			{
				try
				{
					c.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
