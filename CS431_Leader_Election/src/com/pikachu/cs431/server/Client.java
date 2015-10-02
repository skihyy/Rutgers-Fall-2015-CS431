package com.pikachu.cs431.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;

import com.pikachu.cs431.tool.CloseUtil;
import com.pikachu.cs431.vo.ChanllengeMessage;
import com.pikachu.cs431.vo.IPAddress;
import com.pikachu.cs431.vo.Message;


/**
 * 
 * @author tengfei peng
 * @date 下午2:29:57, 2015年10月2日
 * @version 1.0
 * @since
 */
public class Client implements Runnable,Serializable {

	private static final long serialVersionUID = 1L;

	private IPAddress ipAddress;

	public Client() {}
	
	public Client(IPAddress ipAddress){
		this.ipAddress=ipAddress;
	}
	
	static Socket getConnection(String ip,int port){
		Socket socket=null;
		try {
			socket=new Socket(ip, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return socket;
	}
	
	public static void startClient(){
		Client client = new Client();
		Thread t_client = new Thread(client);
		t_client.start(); 
	}

	@Override
	public void run() {
		//Socket socket=Client.getConnection(ipAddress.getIp(),ipAddress.getPort());
		Socket socket=Client.getConnection("127.0.0.1",33061);
		ObjectOutputStream oos=null;
		try {
			oos=new ObjectOutputStream(socket.getOutputStream());
			Message message=new ChanllengeMessage(1, 3);
			System.out.println("client=>"+message.toString());
			oos.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			CloseUtil.closeAll(oos,socket);
		}
	}

}
