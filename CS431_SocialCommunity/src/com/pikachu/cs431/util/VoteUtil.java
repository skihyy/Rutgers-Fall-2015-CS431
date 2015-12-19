/**
* @ClassName: VoteUtil
* @Description: TODO
* @author Tengfei Peng
* @date 2015骞�12鏈�14鏃� 涓婂崍10:18:59
*
* 
*/  
package com.pikachu.cs431.util;

/**
 * @author kaka
 *
 */
public class VoteUtil {

	
   public static boolean isProvedMemberToBeManager(int onlineManagers, int votes){
	   if(1 == onlineManagers % 2)
	   {
		   ++onlineManagers;
	   }
	   return (votes >= onlineManagers/2)? true : false;
	
   } 	
   
   public static boolean isProvedManagerToMember(int onlineManagers, int votes){
	   return (votes >= onlineManagers/2)? true : false;
   }
   
   
   public static boolean isProvedMemberToStranger(int onlineManagers, int votes){
	   return (votes > onlineManagers/2)? true : false;
   }
}
