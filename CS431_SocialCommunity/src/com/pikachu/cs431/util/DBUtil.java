package com.pikachu.cs431.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.pikachu.cs431.antities.User;

/**
 * This class is used to connect to the database
 * 
 * @author tengfei Peng
 *
 */
public class DBUtil {

	private static PreparedStatement pstmt;

	private static ResultSet rs;

	private static Connection conn;

	/**
	 * judge a user whether is a member
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public static String getExistUser(String username, String password) {
		String sql = "SELECT role FROM user WHERE username=? AND password=?";
		try {
			pstmt = getConnection().prepareStatement(sql);
			pstmt.setString(1, username);
			pstmt.setString(2, password);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				return rs.getString("role");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAllDB(pstmt, rs, conn);
		}
		return null;
	}

	/**
	 * 
	* @Title: updateUserRole
	* @Description: update a user's role
	* @param @param username
	* @param @param role    
	* @return void    
	* @throws
	 */
	public static void updateUserRole(String username, int role){
		String sql = "UPDATE user SET role= ? WHERE username=?";
		try {
			pstmt = getConnection().prepareStatement(sql);
			pstmt.setInt(1, role);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAllDB(pstmt, conn);
		}
	}
	
	/**
	 * 
	* @Title: updateUserIPAddress
	* @Description:  update the ip address and port based to the username
	* @param @param username
	* @param @param IPAddress
	* @param @param port    
	* @return void    
	* @throws
	 */
	public static void updateUserIPAddress(String username, String IPAddress, int port) {
		String sql = "UPDATE user SET ip = ? , port = ? WHERE username=?";
		try {
			pstmt = getConnection().prepareStatement(sql);
			pstmt.setString(1, IPAddress);
			pstmt.setInt(2, port);
			pstmt.setString(3, username);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAllDB(pstmt, conn);
		}
	}

	/**
	 * return all user info include the username and ip address
	 * 
	 * @return
	 */
	public static Map<String, String> getAllUserInfoList() {
		Map<String, String> userMap = new HashMap<String, String>();
		String sql = "SELECT username,role FROM user";
		try {
			pstmt = getConnection().prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				userMap.put(rs.getString("username"), rs.getString("role"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAllDB(pstmt, rs, conn);
		}
		return userMap;
	}

	/**
	 * return all manager's usernames and ip address
	 * 
	 * @return
	 */
	public static List<String> getAllManagers() {
		List<String> managerList = new ArrayList<>();
		String sql = "SELECT username FROM user WHERE role=2";
		try {
			pstmt = getConnection().prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				managerList.add(rs.getString("username"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAllDB(pstmt, rs, conn);
		}
		return managerList;
	}

	/**
	 * add a new user and set his role as a user
	 * 
	 * @param username
	 * @param password
	 * @param role
	 * @param IPAddress
	 */
	public static void addNewUser(User user) {
		String sql = "INSERT INTO user(username,password,role,ip,port) VALUES (?,?,?,?,?)";
		try {
			pstmt = getConnection().prepareStatement(sql);
			pstmt.setString(1, user.getUsername());
			pstmt.setString(2, user.getPassword());
			pstmt.setInt(3, user.getRole());
			pstmt.setString(4, user.getIpAddress());
			pstmt.setInt(5, user.getPort());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeAllDB(pstmt, conn);
		}
	}

	/**
	 * return the connection for connect the database
	 * 
	 * @return
	 */
	private static Connection getConnection() {

		Properties pro = new Properties();
		try {
			pro.load(DBUtil.class.getResourceAsStream("/jdbc.properties"));
			String driver = pro.getProperty("driverClass");
			String url = pro.getProperty("jdbcUrl");
			String user = pro.getProperty("user");
			String passwd = pro.getProperty("password");
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, passwd);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	/**
	 * 
	 * @Title: closeAllDB
	 * @Description: close all db connection
	 * @param @param args
	 * @return void
	 * @throws
	 */
	public static void closeAllDB(Object... args) {

		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if (pstmt != null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	//key:name 
	//val:ip/port
	public static Map<String, String> getManagerMap()
	{
		Map<String, String> managerMap = new HashMap<String, String>();
		String sql = "SELECT username, ip, port FROM user WHERE role=2";
		try
		{
			pstmt = getConnection().prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				managerMap.put(rs.getString("username"), rs.getString("ip") + "/" + rs.getString("port"));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			closeAllDB(pstmt, rs, conn);
		}
		return managerMap;
	}
	
	public static String getManagerAllInfoFromMap()
	{
		StringBuilder sb = new StringBuilder();
		Map<String, String> managerMap = getManagerMap();
		
		for(Entry<String,String> manager : managerMap.entrySet())
		{
			sb.append(manager.getKey() + ":" + manager.getValue() + ";");
		}
		
		return sb.toString();
	}
}
