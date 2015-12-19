package com.pikachu.cs431.services;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import com.pikachu.cs431.antities.Message;
import com.pikachu.cs431.util.InfoUtil;
import com.pikachu.cs431.util.MessageUtil;

public class Client {

	public static void main(String[] args) {

		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);

		InfoUtil.welcomeMsg();
		String choice = input.next().trim();
		while (null == choice && !InfoUtil.CHOICE_1.equals(choice)
				&& InfoUtil.CHOICE_2.equals(choice)
				&& InfoUtil.CHOICE_3.equals(choice)) {
			System.out.println("Error Input, Please input the choice again!!");
			choice = input.next().trim();
		}
		Socket socket = null;
		// login to user account
		if (InfoUtil.CHOICE_1.equals(choice)) {
			System.out.println("Username: ");
			String username = input.next().trim();
			while (null == username || username.length() < 1) {
				System.out
						.println("Username cannot be null, please input again!!");
				username = input.next().trim();
			}

			System.out.println("Password: ");
			String password = input.next().trim();
			while (null == password || password.length() < 1) {
				System.out
						.println("Password cannot be null, please input again!!");
				password = input.next().trim();
			}
			// connect the socket, send the user message to
			try {
				socket = new Socket(InfoUtil.SERVER_IP, 7851);
				System.out.println("IP and port : "
						+ MessageUtil.ipHandle(socket.getLocalAddress()) + "/"
						+ socket.getLocalPort());
				Message loginMsg = MessageUtil.getLoginMsg(username, password,
						InfoUtil.SERVER_IP, socket.getLocalAddress(),
						socket.getLocalPort());
				new Thread(new Send(socket, loginMsg)).start();
				new Thread(new Receive(socket)).start();
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			// new Thread(new Send(socket)).start();
			// new Thread(new Receive(socket)).start();

			// new user request to a manager
		} else if (InfoUtil.CHOICE_2.equals(choice)) {
			System.out.println("Enter a name: ");
			String name = input.next();
			try {
				socket = new Socket(InfoUtil.SERVER_IP, 7851);

				System.out.println("Current address: "
						+ socket.getInetAddress().toString() + "/"
						+ socket.getLocalPort());

				Send send = new Send(socket, name);
				new Thread(send).start();
				new Thread(new Receive(socket)).start();

			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else if (InfoUtil.CHOICE_3.equals(choice)) {
			System.exit(0);
		} else {
			System.out.println("Incorrect input.");
		}
	}

}
