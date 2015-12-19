package com.pikachu.cs431.antities;

import java.io.Serializable;

import com.pikachu.cs431.util.InfoUtil;

/**
 * message include sender, receiver, content, type and messageId
 * 
 * @author kaka
 *
 */
public class Message implements Serializable {

	private static final long serialVersionUID = 1L;
	private String sender;
	private String type; // msg type (1.reqMember 2.chat 3. reqBroadcast )
	private String content;
	private String messageId;
	private String receiver; // format (ip/port)

	public Message(String messageId, String content, String type,
			String sender, String receiver) {
		this.messageId = messageId;
		this.content = content;
		this.type = type;
		this.sender = sender;
		this.receiver = receiver;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	@Override
	public String toString() {
		if (InfoUtil.MESSAGE_APPLY_BROADCAST.equals(type)
				|| InfoUtil.MESSAGE_APPLY_MEMBER.equals(type)) {
			return sender + ": " + type + "(" + content + ")";
		}
		return sender + " : " + content;

	}

}
