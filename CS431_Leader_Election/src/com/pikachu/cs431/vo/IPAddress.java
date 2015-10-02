/**
 * 
 */
package com.pikachu.cs431.vo;

/**
 * This is the vo of IP address, contains IPv4 address and a port number.
 * @author Yuyang He
 * @date 12:29:59 AM, Oct 3, 2015
 * @version 1.0
 * @since
 */
@SuppressWarnings("rawtypes")
public class IPAddress implements Comparable
{
	/**	 * 
	 * Constructors of IPAddress.
	 */
	public IPAddress()
	{		
	}
	
	/**	 * 
	 * Constructors of IPAddress.
	 * @param ip IPv4 address
	 * @param port port number
	 */
	public IPAddress(String ip, int port)
	{		
		this.ip = ip;
		this.port = port;
	}
	
	private String ip;
	
	private int port;

	/**
	 * Getter of ip.
	 * @return the ip
	 */
	public String getIp()
	{
		return ip;
	}

	/**
	 * Setter of ip.
	 * @param ip the ip to set
	 */
	public void setIp(String ip)
	{
		this.ip = ip;
	}

	/**
	 * Getter of port.
	 * @return the port
	 */
	public int getPort()
	{
		return port;
	}

	/**
	 * Setter of port.
	 * @param port the port to set
	 */
	public void setPort(int port)
	{
		this.port = port;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Object o)
	{

		if(o==null) return 0;
		
		if(o instanceof IPAddress)
		{
			IPAddress ipAddress = (IPAddress) o;
			
			//result for ip
			//not for port
			int result = this.getIp().compareToIgnoreCase(ipAddress.getIp());
			
			if(0 != result)
			{
				return result;
			}			
			//compare port
			else
			{
				return Integer.compare(port, ipAddress.getPort());
			}			
		}
		
		return 0;			
	}	
	
	@Override
	public boolean equals(Object obj){
		return false;
		
	}
}
