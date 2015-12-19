package com.pikachu.cs431.services;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.pikachu.cs431.antities.Message;
import com.pikachu.cs431.antities.User;
import com.pikachu.cs431.util.CloseUtil;
import com.pikachu.cs431.util.DBUtil;
import com.pikachu.cs431.util.InfoUtil;
import com.pikachu.cs431.util.MessageUtil;
import com.pikachu.cs431.util.VoteUtil;

public class Server
{
	public static List<String> memberApplicant = new ArrayList<String>();
	private Socket socket;
	private ServerSocket ss;
	private static Map<String, MyChannel> channelMap = new HashMap<String, MyChannel>();
	private static Map<String, String> currentOnlineUser = new HashMap<String, String>();
	public static Map<String, Message> managerReq = new HashMap<String, Message>();
	public static Map<String, Integer> approveManagerMap = new HashMap<String, Integer>();
	public static Map<String, Integer> denyManagerMap = new HashMap<String, Integer>();
	public static int currentOnlineManagers;

	public static Map<String, MyChannel> getChannelMap()
	{
		return channelMap;
	}

	public static Map<String, String> getCurrentOnlineUser()
	{
		return currentOnlineUser;
	}

	/**
	 * 
	 * @Title: login @Description: login into the community, return true(login
	 *         successfully), other return false(login fail) @param @param
	 *         username @param @param password @param @return @return
	 *         boolean @throws
	 */
	static String isExistUser(String username, String password)
	{
		String role = DBUtil.getExistUser(username, password);

		return role;
	}

	/**
	 * 
	 * @Title: listen @Description: listen to the clients @param @param
	 *         port @return void @throws
	 */
	public void listen(int port)
	{
		System.out.println("----SYSTEM STARTED----");
		try
		{
			ss = new ServerSocket(port);
			while (true)
			{
				socket = ss.accept();
				MyChannel channel = new MyChannel(socket);
				String receiver = MessageUtil.getReceiver(MessageUtil.ipHandle(socket.getInetAddress()),
				        socket.getPort());
				System.out.println("----NEW  CONNECTION----");
				System.out.println("From: " + receiver);
				channelMap.put(receiver, channel);
				new Thread(channel).start();
			}

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	public static void main(String[] args)
	{
		if (args[0] == null || args.length == 0)
		{
			try
			{
				throw new Exception("Null port.");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		int port = Integer.valueOf(args[0]);
		new Server().listen(port);
	}

}

/**
 * 
 * myChannel means each client has its independent thread to communicate
 *
 */
class MyChannel implements Runnable
{

	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private boolean isRunning = true;
	private String ip = null;
	private int port = 0;

	public MyChannel(Socket socket)
	{
		try
		{
			ois = new ObjectInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());

			ip = MessageUtil.ipHandle(socket.getInetAddress());
			port = socket.getPort();
		}
		catch (IOException e)
		{
			isRunning = false;
			CloseUtil.closeAllIO(ois, oos);
		}

	}

	/**
	 * 
	 * @Title: HandleMsg @Description: server message Handle @param @param
	 *         message @param @return @return Message @throws
	 */
	private void HandleMsg(Message message)
	{
		Message replyMsg = null;
		String msgType = message.getType();

		switch (msgType)
		{
			case InfoUtil.MESSAGE_CHECK_USER: // check the user login
				String content = message.getContent();
				String[] msgs = MessageUtil.getLoginMsg(content);
				String role = Server.isExistUser(msgs[0], msgs[1]); // username:
				                                                    // msgs[0]
				                                                    // password:
				                                                    // msgs[1]

				if (null != role)
				{
					// put the sender's name and sender's ip/port into the map
					Server.getCurrentOnlineUser().put(msgs[0], message.getSender());
					replyMsg = new Message(InfoUtil.SERVER_NAME + System.currentTimeMillis(), role,
					        InfoUtil.MESSAGE_USER_LOGIN_SUCCESS, InfoUtil.SERVER_NAME, message.getSender());

					DBUtil.updateUserIPAddress(msgs[0], ip, port);
				} else
				{
					replyMsg = new Message(InfoUtil.SERVER_NAME + System.currentTimeMillis(),
					        InfoUtil.MESSAGE_USER_LOGIN_FAIL, InfoUtil.MESSAGE_USER_LOGIN_FAIL, InfoUtil.SERVER_NAME,
					        message.getSender());
				}
				sendMsgToOneUser(replyMsg);
				break;
			case InfoUtil.MESSAGE_NEW_STRANGER:
				// send back manager map
				String managerMapInList = DBUtil.getManagerAllInfoFromMap();

				String[] senderDetails = message.getSender().split(";");

				Server.getCurrentOnlineUser().put(senderDetails[0], senderDetails[1]);

				replyMsg = new Message(InfoUtil.SERVER_NAME + System.currentTimeMillis(), managerMapInList,
				        InfoUtil.MESSAGE_MANAGER_MAP, InfoUtil.SERVER_NAME, senderDetails[1]);
				sendMsgToOneUser(replyMsg);
				break;
			case InfoUtil.MESSAGE_CHAT: // chat message
				replyMsg = refineMessage(message);
				sendMsgToOneUser(replyMsg);
				break;

			case InfoUtil.MESSAGE_APPLY_BROADCAST:
				replyMsg = refineMessage(message);
				sendMsgToOneManager(replyMsg);
				break;

			case InfoUtil.MESSAGE_APPLY_MEMBER:
				// check the database whether is exist the user, just transfer
				// this
				memberApplicationSender(message);
				break;

			case InfoUtil.MESSAGE_APPROVE_BROADCAST: // broadcast the message
				replyMsg = message;
				sendMsgToOthers(replyMsg);
				break;

			case InfoUtil.MESSAGE_DENY_BROADCAST: // deny to broadcast the
			                                      // message
				sendSystemMsgToUser(message);
				break;

			case InfoUtil.MESSAGE_APPLY_DECISION: // aprove member
				memberApplicationDecisioner(message);
				break;

			case InfoUtil.MESSAGE_APPLY_MANAGER: // apply to be a manager
				// check if there is manager online
				if (null == getOnlineManagers() || 0 == getOnlineManagers().size())
				{
					replyMsg = new Message(InfoUtil.SERVER_IP + System.currentTimeMillis(), "No online managers.",
					        InfoUtil.MESSAGE_SYSTEM_MESSAGE, InfoUtil.SERVER_IP, message.getSender());
					//replyMsg = refineMessage(replyMsg);
					sendSystemMsgToUser(replyMsg);
					return;
				}

				Map<String, Message> messagesMap = Server.managerReq;
				if (messagesMap.containsKey(message.getSender()))
				{
					replyMsg = new Message(InfoUtil.SERVER_IP + System.currentTimeMillis(),
					        "Duplicate application is abandoned.", InfoUtil.MESSAGE_SYSTEM_MESSAGE, InfoUtil.SERVER_IP,
					        message.getSender());
					//replyMsg = refineMessage(replyMsg);
					sendSystemMsgToUser(replyMsg);
				} else
				{
					messagesMap.put(message.getSender(), message);
					replyMsg = message;
					sendMsgToAllManagers(replyMsg);
				}
				break;

			case InfoUtil.MESSAGE_APPROVE_MANAGER:
				getOnlineManagers();
				String receivePerson = message.getReceiver();
				System.out.println("sendPerson: " + receivePerson);
				if (Server.approveManagerMap.containsKey(receivePerson))
				{
					Server.approveManagerMap.put(receivePerson, Server.approveManagerMap.get(receivePerson) + 1);
				} else
				{
					Server.approveManagerMap.put(receivePerson, 1);
				}
				System.out.println("Approve map: " + Server.approveManagerMap.get(receivePerson) + "Online managers: "
				        + Server.currentOnlineManagers);

				/*
				 * int numberOfDeny = 0; if
				 * (Server.denyManagerMap.containsKey(receivePerson)) {
				 * numberOfDeny = Server.denyManagerMap.get(receivePerson); }
				 */
				/*
				 * if (numberOfDeny +
				 * Server.approveManagerMap.get(receivePerson) ==
				 * Server.currentOnlineManagers) {
				 */
				boolean isPassed = VoteUtil.isProvedMemberToBeManager(Server.currentOnlineManagers,
				        Server.approveManagerMap.get(receivePerson));
				if (isPassed)
				{
					System.out.println("Approved");
					DBUtil.updateUserRole(message.getReceiver(), 2);
					Send.isManager = true;
					replyMsg = new Message(message.getMessageId(), "Your request to be a manager has been approved.",
					        InfoUtil.MESSAGE_APPROVE_MANAGER, message.getSender(), message.getReceiver());

					sendSystemMsgToUser(replyMsg);
					System.out.println("Message send back");
					Server.denyManagerMap.remove(receivePerson);
					Server.approveManagerMap.remove(receivePerson);
					Server.managerReq.remove(receivePerson);
				} /*
				   * else { System.out.println("DisApproved"); replyMsg = new
				   * Message( message.getMessageId(),
				   * "Your request to be a manager has been denied, Sorry.",
				   * InfoUtil.MESSAGE_DENY_MANAGER, InfoUtil.SERVER_IP,
				   * message.getReceiver()); }
				   */

				/* } */
				break;
			case InfoUtil.MESSAGE_DENY_MANAGER:
				String denier = message.getReceiver();
				getOnlineManagers();
				if (Server.denyManagerMap.containsKey(denier))
				{
					Server.denyManagerMap.put(denier, Server.denyManagerMap.get(denier) + 1);
				} else
				{
					Server.denyManagerMap.put(denier, 1);
				}

				boolean isDenied = VoteUtil.isProvedMemberToBeManager(Server.currentOnlineManagers,
				        Server.denyManagerMap.get(denier));
				if (isDenied)
				{
					System.out.println("Denied");
					replyMsg = new Message(message.getMessageId(),
					        "Your request to be a manager has been denied, Sorry.", InfoUtil.MESSAGE_DENY_MANAGER,
					        message.getSender(), message.getReceiver());
					;

					sendSystemMsgToUser(replyMsg);
					System.out.println("Message send back");
					Server.denyManagerMap.remove(denier);
					Server.approveManagerMap.remove(denier);
					Server.managerReq.remove(denier);
				}
				break;
			default:
				break;
		}
	}

	/**
	 * This function will change the receiver in the message into his address.
	 * 
	 * @param replyMsg
	 * @return
	 */
	private Message refineMessage(Message replyMsg)
	{
		Map<String, String> onlineUserMap = Server.getCurrentOnlineUser();
		String nameOfReceiver = replyMsg.getReceiver();

		if (onlineUserMap.containsKey(nameOfReceiver))
		{
			replyMsg.setReceiver(onlineUserMap.get(nameOfReceiver));
		}

		return replyMsg;
	}

	/**
	 * Deal the decision made by the manager about member application.
	 * 
	 * @Title: memberApplicationDecisioner @param @param message @return
	 *         void @throws
	 */
	private void memberApplicationDecisioner(Message message)
	{
		String sender = message.getSender(), name = message.getReceiver();

		Map<String, String> userMap = DBUtil.getManagerMap();
		for (Entry<String, String> user : userMap.entrySet())
		{
			// decision is made by a manager
			if (user.getKey().equals(sender))
			{

				for (String applicant : Server.memberApplicant)
				{
					if (name.equals(applicant))
					{
						if (message.getContent().equalsIgnoreCase(InfoUtil.MESSAGE_APPROVE_MEMBER))
						{
							int passwd = (int) (Math.random() * 100000);
							User newUser = new User(name, passwd + "", 1, "0.0.0.0", 0);
							DBUtil.addNewUser(newUser);
							message.setContent("You have been approved as a member. Your password is " + passwd);
						}

						String receiver = message.getReceiver();
						String ip = Server.getCurrentOnlineUser().get(receiver);

						if (null != ip)
						{
							message.setReceiver(ip);
							message = refineMessage(message);
							sendMsgToOneUser(message);
						}
						// delete the applicant
						Server.memberApplicant.remove(name);

						return;
					}
				}
			}
		}
	}

	/**
	 * Put application into map, send it to the manager.
	 * 
	 * @Title: memberApplicationSender @param @param message @return
	 *         void @throws
	 */
	private void memberApplicationSender(Message message)
	{
		String name = message.getSender();
		Message nameExist = new Message(System.currentTimeMillis() + "", InfoUtil.MESSAGE_NAME_EXISTS,
		        InfoUtil.MESSAGE_NAME_EXISTS, message.getReceiver(), message.getSender());
		for (String applicant : Server.memberApplicant)
		{
			if (applicant.equals(name))
			{
				sendMsgToOneUser(nameExist);
				return;
			}
		}

		Map<String, String> userMap = DBUtil.getAllUserInfoList();
		for (Entry<String, String> user : userMap.entrySet())
		{
			if (user.getKey().equals(name))
			{
				sendMsgToOneUser(nameExist);
				return;
			}
		}

		Server.memberApplicant.add(name);
		Message application = new Message(System.currentTimeMillis() + "",
		        "Actor " + name + " wants to join in the community.\nReply \"" + "@" + message.getSender() + " "
		                +" " + InfoUtil.MESSAGE_APPROVE_MEMBER + "\" for approve."
		                + "\nReply \"" + "@" + message.getSender() + " " + " "
		                + InfoUtil.MESSAGE_DENY_MEMBER + "\" for deny.",
		        name, message.getSender(), message.getReceiver());
		application = refineMessage(application);
		sendMsgToOneUser(application);
	}

	/**
	 * 
	 * @Title: receiveMsg @Description: read message from the
	 *         client @param @return @return Message @throws
	 */
	private void receiveMsg()
	{
		try
		{
			Message recieveMsg = (Message) ois.readObject();

			System.out.println("------NEW MESSAGE------");
			System.out.println("ID:       " + recieveMsg.getMessageId());
			System.out.println("Type:     " + recieveMsg.getType());
			System.out.println("Content:  " + recieveMsg.getContent());
			System.out.println("Sender:   " + recieveMsg.getSender());
			System.out.println("Receiver: " + recieveMsg.getReceiver());

			HandleMsg(recieveMsg);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			isRunning = false;
			CloseUtil.closeAllIO(ois);
		}
	}

	/**
	 * 
	 * @Title: getManager @Description: @param @return an manager @return
	 *         String @throws
	 */
	private String getOneManager()
	{
		String receiver = null;
		List<String> managers = DBUtil.getAllManagers();
		Map<String, String> currentOnlineUsers = Server.getCurrentOnlineUser();
		for (String manager : managers)
		{
			if (currentOnlineUsers.containsKey(manager))
			{
				receiver = currentOnlineUsers.get(manager);
			}
		}
		return receiver;
	}

	/**
	 * 
	 * @Title: getOnlineManagers @Description: get all online
	 *         managers @param @return @return List<String> @throws
	 */
	private List<String> getOnlineManagers()
	{
		List<String> managers = DBUtil.getAllManagers();
		List<String> onlineManagers = new ArrayList<String>();
		String receiver = null;
		Map<String, String> currentOnlineUsers = Server.getCurrentOnlineUser();
		for (String manager : managers)
		{
			if (currentOnlineUsers.containsKey(manager))
			{
				receiver = currentOnlineUsers.get(manager);
				onlineManagers.add(receiver);
			}
		}
		Server.currentOnlineManagers = onlineManagers.size();
		return onlineManagers;
	}

	/**
	 * 
	 * @Title: sendMsg @Description: send message to other user @param @param
	 *         message @return void @throws
	 */
	private void sendMsg(Message message)
	{
		if (message == null)
			return;
		try
		{
			oos.writeObject(message);
			oos.flush();
		}
		catch (IOException e)
		{
			isRunning = false;
			CloseUtil.closeAllIO(oos);
		}
	}

	/**
	 * 
	 * @Title: sendMsgToOthers @Description: send the message to other
	 *         users @param sender -onlineuser/ip-port/ip-port-channel @return
	 *         void @throws
	 */
	public void sendMsgToOthers(Message afterHandleMsg)
	{
		if (null != afterHandleMsg)
		{
			String applicant = afterHandleMsg.getReceiver() + " says to all";
			String sender = afterHandleMsg.getSender();
			String reciever = null;
			if (Server.getCurrentOnlineUser().containsKey(sender))
			{
				reciever = Server.getCurrentOnlineUser().get(sender);
			}
			for (Map.Entry<String, MyChannel> others : Server.getChannelMap().entrySet())
			{
				if (null != reciever && reciever.equals(others.getKey()))
				{
					continue;
				}
				MyChannel channel = others.getValue();
				//afterHandleMsg.setReceiver(reciever);
				afterHandleMsg.setSender(applicant);
				channel.sendMsg(afterHandleMsg);
			}
		}
	}

	/**
	 * 
	 * @Title: sendMsgToOneUser @Description: send message to one
	 *         user @param @return void @throws
	 */
	public void sendMsgToOneUser(Message afterHandleMsg)
	{
		if (null != afterHandleMsg)
		{
			String receiver = afterHandleMsg.getReceiver();
			MyChannel channel = getOnlineUserChannel(receiver);
			if (null != channel)
			{
				channel.sendMsg(afterHandleMsg);
			} else
			{
				// save the message into Concurrent queue
			}
		}
	}

	/**
	 * 
	 * @Title: sendMsgToManager @Description: send the message to a
	 *         manager @param @param afterHandleMsg @return void @throws
	 */
	public void sendMsgToOneManager(Message afterHandleMsg)
	{
		if (null != afterHandleMsg)
		{
			String manager = getOneManager();
			MyChannel channel = getOnlineUserChannel(manager);
			if (null != channel)
			{
				channel.sendMsg(afterHandleMsg);
			} else
			{
				// save the message in the queue
			}
		}
	}

	/**
	 * @Title: sendMsgToAllManagers @Description: send the request message to
	 *         All managers @param @param replyMsg @return void @throws
	 */
	private void sendMsgToAllManagers(Message afterHandleMsg)
	{
		if (null != afterHandleMsg)
		{
			List<String> managers = getOnlineManagers();
			for (String manager : managers)
			{
				MyChannel channel = getOnlineUserChannel(manager);
				if (null != channel)
				{
					channel.sendMsg(afterHandleMsg);
				} else
				{
					// save the message in the queue
				}
			}
		}
	}

	/**
	 * 
	 * @Title: sendDenyBrocastMsgToUser @Description: send back the deny
	 *         broadcast message to that user @param @param
	 *         afterHandleMsg @return void @throws
	 */
	public void sendSystemMsgToUser(Message afterHandleMsg)
	{
		if (null != afterHandleMsg)
		{
			String reciever = null;
			Map<String, String> onlineUsers = Server.getCurrentOnlineUser();
			if (onlineUsers.containsKey(afterHandleMsg.getReceiver()))
			{
				reciever = onlineUsers.get(afterHandleMsg.getReceiver());
				MyChannel channel = getOnlineUserChannel(reciever);
				if (null != channel)
				{
					channel.sendMsg(afterHandleMsg);
				} else
				{
					// save the message in the queue
				}
			}
		}
	}

	/**
	 * 
	 * @Title: getOnlineUserChannel @Description: send the message to the online
	 *         user @param @param receiver @param @return @return
	 *         MyChannel @throws
	 */
	private MyChannel getOnlineUserChannel(String receiver)
	{
		Map<String, MyChannel> channels = Server.getChannelMap();
		if (channels.containsKey(receiver))
		{
			return channels.get(receiver);
		}
		return null;
	}

	@Override
	public void run()
	{
		while (isRunning)
		{
			receiveMsg();
		}
	}
}