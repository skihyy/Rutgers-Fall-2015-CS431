package com.pikachu.cs431.util;

public class InfoUtil {

	public final static String CHOICE_1 = "1";
	public final static String CHOICE_2 = "2";
	public final static String CHOICE_3 = "3";
	public final static String USER = "1";
	public final static String MANAGER = "2";
	public final static String COMMUNITY_DISC = "This community is a small chat room for all the user";
	public final static String MEMBER_DISC = "member_disc";
	public final static String SERVER_IP = "127.0.0.1";
	public final static String SERVER_NAME = "Controller";
	
	// Message Type
	public final static String MESSAGE_MANAGER_MAP = "manager_map";
	public final static String MESSAGE_NEW_STRANGER = "new_stranger";
	public final static String MESSAGE_APPLY_DECISION = "apply_decision";
	public final static String MESSAGE_APPLY_MEMBER = "apply_member";
    public final static String MESSAGE_APPROVE_MEMBER = "approved_member";
    public final static String MESSAGE_DENY_MEMBER = "deny_member";
    public final static String MESSAGE_APPLY_BROADCAST = "apply_broadcast";
    public final static String MESSAGE_APPROVE_BROADCAST = "approved_broadcast";
    public final static String MESSAGE_DENY_BROADCAST = "deny_broadcast" ; 
    public final static String MESSAGE_CHECK_USER = "user_check";
    public final static String MESSAGE_USER_LOGIN_SUCCESS = "login_success";
    public final static String MESSAGE_USER_LOGIN_FAIL = "login_fail";
    public final static String MESSAGE_CHAT = "chat";
    public final static String MESSAGE_NAME_EXISTS = "name_exists";
    public final static String MESSAGE_APPLY_MANAGER = "apply_manager" ;
    public final static String MESSAGE_APPROVE_MANAGER = "approved_manager" ;
    public final static String MESSAGE_DENY_MANAGER = "deny_manager" ;
    public final static String MESSAGE_SYSTEM_MESSAGE = "system_message" ;
    
    
	/**
	 * 
	 * @Title: welcomeMsg
	 * @Description: welcome Msg
	 * @param
	 * @return void
	 * @throws
	 */
	public static void welcomeMsg() {
		System.out
				.println("----CHOOSE ACTIONS----");
		System.out.println("1. Login");
		System.out.println("2. New here");
		System.out.println("3. Logout");
		System.out.println("Choose a option: ");
	}

	/**
	 * 
	 * @Title: chatTips
	 * @Description: message send tips
	 * @param
	 * @return void
	 * @throws
	 */
	public static void chatTips() {
		System.out.println();
		System.out.println("======choose one user to chat=======");
		System.out
				.println("**Message sending format: (sender@receiverIP/receiverPort MessageType MessageContent) ");
	}
}
