package com.pikachu.cs431.services;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.pikachu.cs431.antities.Message;
import com.pikachu.cs431.util.CloseUtil;
import com.pikachu.cs431.util.InfoUtil;

public class Receive implements Runnable {

	private ObjectInputStream ois;
	private boolean isRunning = true;
	public static Map<String, String> managerMap = new HashMap<String, String>();

	public Receive(Socket socket) {
		try {
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			isRunning = false;
			CloseUtil.closeAllIO(ois);
		}
	}

	/**
	 * 
	 * @Title: MessageHandle
	 * @Description: message handle in the user client
	 * @param @param message
	 * @param @return
	 * @return Message
	 * @throws
	 */
	public Message MessageHandle(Message message) {
		Message receiveMsg = null;
		String messageType = message.getType();
		switch (messageType) {
		case InfoUtil.MESSAGE_APPLY_MEMBER:
			System.out.println(message.toString());
			break;
		case InfoUtil.MESSAGE_APPLY_BROADCAST:
			System.out.println(message.toString());
			break;
		case InfoUtil.MESSAGE_USER_LOGIN_SUCCESS:
			String role = message.getContent();
			if("1".equals(role)){
				System.out.println("Login as a member.");
			}
			else
			{
				Send.isManager = true;
				System.out.println("Login as a manager.");
			}
			break;
		case InfoUtil.MESSAGE_USER_LOGIN_FAIL:
			System.out.println("Login failed.");
			isRunning = false;
			Send.isRunning =false;
			break;
		case InfoUtil.MESSAGE_DENY_BROADCAST:
			System.out.println("Your application for broadcasting is denied.");
			break;
		case InfoUtil.MESSAGE_MANAGER_MAP:
			handleManagerMap(message.getContent());
			break;
		case InfoUtil.MESSAGE_APPROVE_MEMBER:
			Send.isStranger = false;
			System.out
					.println("Application for joining in the community has been approved by the manager. Welcome.");
			break;
		case InfoUtil.MESSAGE_DENY_MEMBER:
			System.out
					.println("Application for joining in the community has been denied by the manager.");
			break;
		case InfoUtil.MESSAGE_APPROVE_MANAGER:
			System.out.println("Application for being a manager has been approved.");
			Send.isManager = true;
			break;
		case InfoUtil.MESSAGE_DENY_MANAGER:
			System.out.println("Application for being a manager has been denied.");
			break;
		default:
			printMsg(message);
			break;
		}
		return receiveMsg;
	}

	/**
	 * @Title: printMsg
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void printMsg(Message message) {
		if (Send.isStranger) {
			for (Entry<String, String> manager : managerMap.entrySet()) {
				if (manager.getKey().equals(message.getSender())) {
					System.out.println(message.toString());
					return;
				}
			}
		} else {
			// others
			System.out.println(message.toString());
		}
	}

	/**
	 * Split string into maps. String contains managers' name and address.
	 * 
	 * @Title: handleManagerMap
	 * @Description: TODO
	 * @param @param content
	 * @return void
	 * @throws
	 */
	private void handleManagerMap(String content) {
		String[] managers = content.split(";");

		for (String manager : managers) {
			String[] details = manager.split(":");
			managerMap.put(details[0], details[1]);
		}
	}

	/**
	 * 
	 * @Title: receiveMsg
	 * @Description: receive the message
	 * @param @return
	 * @return Message
	 * @throws
	 */
	public Message receiveMsg() {
		try {
			return (Message) ois.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			isRunning = false;
			CloseUtil.closeAllIO(ois);
		}
		return null;
	}

	@Override
	public void run() {
		while (isRunning) {
			MessageHandle(receiveMsg());
		}

	}

}
