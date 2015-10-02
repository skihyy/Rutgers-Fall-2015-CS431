/**
 * 
 */
package com.pikachu.cs431.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.pikachu.cs431.tool.Comparison;
import com.pikachu.cs431.vo.IPAddress;

/**
 * @author Yuyang He
 * @date 9:58:42 AM, Sep 26, 2015
 * @version 1.0
 * @since
 */
public class Test
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Comparison.compare(1, 2, 3, 5);
		Comparison.compare(1, 2, 3, 5);
		Comparison.compare(2, 2, 3, 5);
		Comparison.compare(2, 5, 7, 15);
		Comparison.compare(12, 9, 7, 15);
	}
}
