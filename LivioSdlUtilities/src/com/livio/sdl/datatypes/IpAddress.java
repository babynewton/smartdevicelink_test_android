package com.livio.sdl.datatypes;

public class IpAddress {

	private String ipAddress, tcpPort;
	
	public IpAddress(String ipAddress, String ipPort) {
		this.ipAddress = ipAddress;
		this.tcpPort = ipPort;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public String getTcpPort() {
		return tcpPort;
	}

}
