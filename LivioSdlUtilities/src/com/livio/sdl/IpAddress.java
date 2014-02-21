package com.livio.sdl;

/**
 * Represents an IP address and port number as 2 strings - one string for the address
 * itself and another string for the associated TCP port number.
 *
 * @author Mike Burke
 *
 */
public class IpAddress {

	private String ipAddress, tcpPort;
	
	public IpAddress(String ipAddress, String tcpPort) {
		this.ipAddress = ipAddress;
		this.tcpPort = tcpPort;
	}
	
	public IpAddress(String ipAddress, int tcpPort){
		this(ipAddress, String.valueOf(tcpPort));
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public String getTcpPort() {
		return tcpPort;
	}
	
	@Override
	public String toString(){
		return new StringBuilder().append(ipAddress).append(":").append(tcpPort).toString();
	}

}
