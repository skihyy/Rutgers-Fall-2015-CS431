package com.pikachu.cs431.antities;

/**
 * user include username, password, role, ipAddress and port
 * @author kaka
 *
 */
public class User {
	
    private String username;
    private String password;
    private int role; // 1: user  2 : manager
    private String ipAddress;
    private int port ; 
    
	public User(String username, String password, int role, String ipAddress, int port) {
		this.username = username;
		this.password = password;
		this.role = role;
		this.ipAddress = ipAddress;
		this.port = port;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public int getRole() {
		return role;
	}
	public void setRole(int role) {
		this.role = role;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	   
}
