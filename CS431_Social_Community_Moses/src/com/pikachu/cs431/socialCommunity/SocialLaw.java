package com.pikachu.cs431.socialCommunity;

import moses.controller.Law;

/**
 * This is the raw type of the law.
 * 
 * @author Yuyang He
 * @date Nov 30, 2015
 * @version 1.0
 * @since
 */
public class SocialLaw extends Law
{
	/**
	 * database controller it can help to log in
	 */
	public static final String DATABASE = "db@171.31.242.4";

	/**
	 * manager list is only for a stranger to communicate assuming every member
	 * knows managers
	 */
	private String[] managerList = null;

	/**
	 * member list is only for a manager to broadcast
	 */
	private String[] memberList = null;

	/**
	 * These three arrays are only for a manager to deal with application.
	 * ApplicationID is the key of an application with format: applicant@127.0.0.1
	 */
	private String[] applicationID = null;

	/**
	 * Application context is the value of the application map.
	 * If it is an application to join, it has the format: apply|join.
	 * If it is an application to broadcast, it has the format: broadcast|message.
	 */
	private String[] applicationContext = null;

	/**
	 * <p>
	 * Initialization of the log in status as a stranger.
	 * <p/>
	 */
	public void adopted(String args)
	{
		doAdd("status(0)");
	}

	/**
	 * <p>
	 * The function of sending message. Sending messages with be deal by the
	 * user's status (stranger, member, or manager).
	 * <p/>
	 * <p>
	 * There are in total 4 types of actors: database, manager, member, and
	 * stranger.
	 * <p/>
	 */
	public void sent(String source, String message, String dest)
	{
		/**
		 * Status will point out the role of one user. 0 means stranger, 1 means
		 * member, and 2 means manager.
		 */
		int status = CS.fetchInt("status");

		/**
		 * Only a stranger needs a specific send function because strangers are
		 * limited to send messages. They can only send messages to the
		 * database, or to a manager. But for others (managers or members), they
		 * just need to send the message.
		 */
		if (0 == status)
		{
			sendAsStranger(source, message, dest);
		} else
		{
			doForward(source, message, dest);
		}
	}

	/**
	 * <p>
	 * A stranger can only do the following:
	 * </p>
	 * <p>
	 * 1) Communicate with database to get address of managers, format:
	 * database@171.31.242.4 hello, or
	 * </p>
	 * <p>
	 * 2) Communicate with a manager about the details of the community, format:
	 * manager@171.31.242.4 hello, or
	 * </p>
	 * <p>
	 * 3) Ask to join the community, format: manager@171.31.242.4 JOIN, or
	 * </p>
	 * <p>
	 * 4) log in, format: database@127.0.0.1 login|password.
	 * </p>
	 * 
	 * @param source
	 *            the sender of the message
	 * @param message
	 *            the context of the message
	 * @param dest
	 *            the receiver of the message
	 */
	private void sendAsStranger(String source, String message, String dest)
	{
		/**
		 * If a stranger did not have managers' address can only communicate
		 * with database (log in or ask for information is both okay).
		 */
		if (null == managerList || 0 == managerList.length)
		{
			if (!DATABASE.equals(dest))
			{
				doDeliver(Self, "Error. Please contact database " + DATABASE + " for details.", Self);
			} else
			{
				doForward(source, message, dest);
			}
		}
		/**
		 * If one has addresses of manager, can only communicate with a manager
		 * or the database.
		 */
		else
		{
			if (DATABASE.equals(dest))
			{
				doForward(source, message, dest);
			} else
			{
				/**
				 * If it is a database, it must be a manager.
				 */
				for (int i = 0; i < managerList.length; ++i)
				{
					if (managerList[i].equals(dest))
					{
						doForward(source, message, dest);
						break;
					}
				}
			}
		}
	}

	/**
	 * <p>
	 * </p>
	 * The arriving message will be handled due to the role of a user.
	 * </p>
	 * <p>
	 * 1) A stranger will not receive the message if the message is from a
	 * member. She can only receive the message from the database or a manager.
	 * The reason why there must be a handler dealing the message is that in
	 * send function, nothing is handled with the destination of a message. So
	 * even though a member sends a message to a stranger, it can be sent
	 * successfully but now the controller of the stranger will refuse this
	 * message.
	 * </p>
	 * <p>
	 * 2£© If a member receives a message, it is simple and easy that the message
	 * will be just printed out.
	 * </p>
	 * <p>
	 * 3) If a manager receives a message, it could be a chat message which just
	 * needs to be printed out. But if it is an application of joining in the
	 * community or broadcasting, the message will be automatically handled and
	 * put in to a application list. And the message printed out will be about
	 * the application instead of the original message.
	 * </p>
	 * <p>
	 * 4£© Database is still in consideration.
	 * </p>
	 */
	public void arrived(String source, String message, String dest)
	{
		/**
		 * If the receiver is not the right guy, leave it.
		 */
		if (!Self.equals(dest))
		{
			return;
		}

		/**
		 * Database manager will handle messages to the database.
		 */
		if (DATABASE.equals(Self) && dest.equals(Self))
		{
			dbHandler(source, message, dest);
			return;
		}

		int status = CS.fetchInt("status");

		/**
		 * 0 means a stranger.
		 */
		if (0 == status)
		{
			arrivedAsStranger(source, message, dest);
		}
		/**
		 * 1 means a member.
		 */
		else if (1 == status)
		{
			arrivedAsMember(source, message, dest);
		}
		/**
		 * 2 means a manager
		 */
		else
		{
			arrivedAsManager(source, message, dest);
		}
	}

	/**
	 * <p>
	 * If a manager receives a message, it could be a chat message which just
	 * needs to be printed out. But if it is an application of joining in the
	 * community or broadcasting, the message will be automatically handled and
	 * put in to a application list. And the message printed out will be about
	 * the application instead of the original message.
	 * </p>
	 * <p>
	 * Received messages are listed according to the following situations:
	 * </p>
	 * <p>
	 * 1) Messages from a stranger, format: stranger@127.0.0.1 hello, or
	 * </p>
	 * <p>
	 * 2) Application made by a stranger or a member, format: manager@127.0.0.1
	 * apply|join, or manager@127.0.0.1 broadcast|message, or
	 * </p>
	 * </p>
	 * <p>
	 * 3) A decision made by the manager herself, format: manager@127.0.0.1
	 * applicationID|approved (and the system will automatically send the result
	 * to the applicant with the following format: applicant@127.0.0.1
	 * application|approved).
	 * </p>
	 * 
	 * @param source
	 *            the sender of the message
	 * @param message
	 *            the context of the message
	 * @param dest
	 *            the receiver of the message
	 */
	private void arrivedAsManager(String source, String message, String dest)
	{
		/**
		 * If the manager is the sender, meaning either she just chats with
		 * herself, or she makes a decision.
		 */
		if (Self.equals(source))
		{
			/**
			 * if it is a decision, the format is manager@127.0.0.1
			 * applicationID|approved.
			 */
			if (message.endsWith("|approved") || message.endsWith("|denied"))
			{
				String[] details = message.split("|");

				String context = getApplication(details[0]);

				/**
				 * "apply|join" means this application is to join in the
				 * community.
				 */
				if (context.equals("apply|join"))
				{

					if ("approved".equals(details[1]))
					{
						doDeliver(Self, "Your application for join the community has been approved.", details[0]);

						// let it in
					} else
					{
						doDeliver(Self, "Your application for join the community has been denied.", details[0]);
					}
				}
				/**
				 * broadcasting
				 */
				else
				{
					if ("approved".equals(details[1]))
					{
						doDeliver(Self, "Your application for broadcasting a message has been approved.", details[0]);

						for (int i = 0; i < memberList.length; ++i)
						{
							doDeliver(source, context, memberList[i]);
						}
					} else
					{
						doDeliver(Self, "Your application for broadcasting a message has been denied.", details[0]);
					}
				}
			}
			/**
			 * She just chats with herslef.
			 */
			else
			{
				doDeliver(source, message, dest);
			}
		}
		/**
		 * This means others sends to the manager a message. If it is nor
		 * starting with "apply|" or "broadcast|", it is just a chat message.
		 */
		else if (!message.startsWith("apply|") && !message.startsWith("broadcast|"))
		{
			doDeliver(source, message, dest);
		}
		/**
		 * It is a application.
		 */
		else
		{
			String[] details = message.split("|");

			/**
			 * Application to join the community.
			 */
			if (details[0].equalsIgnoreCase("apply") && details[1].equalsIgnoreCase("join"))
			{
				/**
				 * If successfully applied.
				 */
				if (addApplication(source, "apply|join"))
				{
					doDeliver(Self, "User " + source + " applys to join in the community.", Self);
					doDeliver(Self, "Using following message to make a decision:", Self);
					doDeliver(Self, Self + " " + source + "|approved/denied", Self);
				}
				/**
				 * Send back to the applicant about the duplicate application.
				 */
				else
				{
					doForward(Self, "You have a pending application. Please wait until the pending one handled.",
					        source);
				}
			}
			/**
			 * Application to broadcast a message.
			 */
			else if (details[0].equals("broadcast"))
			{
				/**
				 * If successfully applied.
				 */
				if (addApplication(source, "apply|join"))
				{
					addApplication(source, details[1]);

					doDeliver(Self, "User " + source + " applys to broadcast the message: " + details[1], Self);
					doDeliver(Self, "Using following message to make a decision:", Self);
					doDeliver(Self, Self + " " + source + "|approved/denied", Self);
				}
				/**
				 * Send back to the applicant about the duplicate application.
				 */
				else
				{
					doForward(Self, "You have a pending application. Please wait until the pending one handled.",
					        source);
				}
			}
		}
	}

	/**
	 * <p>
	 * A member will just printed out all message she receives no matter what
	 * kind of messages it is.
	 * </p>
	 * 
	 * @param source
	 *            the sender of the message
	 * @param message
	 *            the context of the message
	 * @param dest
	 *            the receiver of the message
	 */
	private void arrivedAsMember(String source, String message, String dest)
	{
		doDeliver(source, message, dest);
	}

	/**
	 * <p>
	 * A stranger will not receive the message if the message is from a member.
	 * She can only receive the message from the database or a manager. The
	 * reason why there must be a handler dealing the message is that in send
	 * function, nothing is handled with the destination of a message. So even
	 * though a member sends a message to a stranger, it can be sent
	 * successfully but now the controller of the stranger will refuse this
	 * message.
	 * </p>
	 * <p>
	 * Situations when a stranger receives:
	 * </p>
	 * <p>
	 * 1) Successfully logged in, format: stranger@127.0.0.1
	 * login|success|manager|member1@127.0.0.1|member2@127.0.0.2, or
	 * stranger@127.0.0.1 login|success|member, or stranger@127.0.0.1
	 * login|failed, or
	 * </p>
	 * <p>
	 * 2) Log in denied, format: login|failed, or
	 * </p>
	 * <p>
	 * 3) Messages from managers, format: stranger@127.0.0.1 hello.
	 * </p>
	 * 
	 * @param source
	 *            the sender of the message
	 * @param message
	 *            the context of the message
	 * @param dest
	 *            the receiver of the message
	 */
	private void arrivedAsStranger(String source, String message, String dest)
	{

		/**
		 * Database will send log in information, or addresses of managers.
		 */
		if (DATABASE.equals(source))
		{
			String[] details = message.split("|");

			/**
			 * Log in result is sent.
			 */
			if (details[0].equals("login"))
			{
				/**
				 * Successfully log in, then see whether it is a manager or a
				 * member.
				 */
				if (details[1].equals("success"))
				{
					/**
					 * At least it is a member.
					 */
					doIncr("status", 1);

					/**
					 * manager's status = 2
					 */
					if (details[2].equals("manager"))
					{
						doIncr("status", 1);

						/**
						 * stranger@127.0.0.1
						 * login|success|manager|member1@127.0.0.1|member2@127.0
						 * .0.2
						 */
						memberList = new String[details.length - 3];

						for (int i = 3; i < details.length; ++i)
						{
							/**
							 * A manager needs the list of members to broadcast
							 * a message.
							 */
							memberList[i - 3] = details[3];
						}
					}

					doDeliver(Self, "Log in success.", Self);
				} else
				{
					doDeliver(Self, "Log in failed.", Self);
				}
			}
		}
		/**
		 * A message from others, which needs to be verified with the manager
		 * list to make sure the message is from a manager other than someone
		 * else.
		 */
		else
		{
			for (int i = 0; i < managerList.length; ++i)
			{
				if (managerList[i].equals(source))
				{
					doDeliver(source, message, dest);
					break;
				}
			}
		}
	}

	public void disconnected()
	{
		doQuit();
	}

	/**
	 * This function is just like a Map(key, value). The key is automatically
	 * self-increasing. If there is already a application by one, she cannot
	 * apply for another application. Otherwise, false will be returned.
	 * 
	 * @param applicant
	 *            the address of the applicant, format: applicant@127.0.0.1
	 * @param context
	 *            if one just wants to join the group, then it is empty; if one
	 *            wants to broadcast a message, then the context will the
	 *            message.
	 * @return true if adding success; false if there has already been an
	 *         application by the same applicant
	 */
	private boolean addApplication(String applicant, String context)
	{
		/**
		 * Initialization, id will all be set to null.
		 */
		if (null == applicationID)
		{
			applicationID = new String[10];
			applicationContext = new String[10];

			for (int i = 0; i < applicationID.length; ++i)
			{
				applicationID[i] = null;
			}
		}

		/**
		 * The index is the pointer of a new application's position.
		 */
		int index = 0;
		for (; index < applicationID.length; ++index)
		{
			if (null == applicationID[index])
			{
				break;
			}
			/**
			 * If one has an application, then she cannot apply another one.
			 */
			else if (applicationID[index].equalsIgnoreCase(applicant))
			{
				return false;
			}
		}

		/**
		 * If the original list is full, then a new list will be replace the old
		 * one with doubling the size.
		 */
		if (applicationID.length == index)
		{
			String[] tmpID = new String[applicationID.length * 2], tmpContext = new String[applicationID.length * 2];

			for (int i = 0; i < tmpID.length; ++i)
			{
				tmpID[i] = null;
			}

			for (int i = 0; i < applicationID.length; ++i)
			{
				tmpID[i] = applicationID[i];
				tmpContext[i] = applicationContext[i];
			}

			applicationID = tmpID;
			applicationContext = tmpContext;
		}

		/**
		 * Adding the new application into the map.
		 */
		applicationID[index] = applicant;
		applicationContext[index] = context;

		return true;
	}

	/**
	 * This function will get a application detail (application type and context
	 * if applicable). If nothing find, null will be returned. After getting the
	 * details, the application will be deleted.
	 * 
	 * @param applicant
	 *            key to find out the details of an application
	 * @return the application details; if it is an application for joining in
	 *         the group, "apply|join" will be returned
	 */
	private String getApplication(String applicant)
	{
		for (int i = 0; i < applicationID.length; ++i)
		{
			if (applicationID[i].equals(applicant))
			{
				/**
				 * Deletion of this entry.
				 */
				applicationID[i] = null;

				return applicationContext[i];
			}
		}
		return null;
	}
}
