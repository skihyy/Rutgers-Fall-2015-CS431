package com.pikachu.cs431.util;

import java.io.Closeable;
import java.io.IOException;


public class CloseUtil {

	/**
	 * 
	* @Title: closeAllIO
	* @Description: close all IO interface
	* @param @param args    
	* @return void    
	* @throws
	 */
	public static void closeAllIO(Closeable ...args){
		for(Closeable io : args){
			if(null != io){
				try {
					io.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
}
