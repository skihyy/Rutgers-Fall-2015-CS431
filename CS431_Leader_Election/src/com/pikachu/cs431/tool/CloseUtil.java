package com.pikachu.cs431.tool;

import java.io.Closeable;
import java.io.IOException;

public class CloseUtil{

     public static void closeAll(Closeable... io){
    	 for(Closeable c: io){
    		 if(null !=c){
    			 try {
					c.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
    		 }
    	 }
     }
}
