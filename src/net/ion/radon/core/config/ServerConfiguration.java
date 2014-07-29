package net.ion.radon.core.config;



public class ServerConfiguration {

	private final ConnectorConfiguration connectorConfig ;
	private final String id;
	private final String logConfigPath ;
	private final int shellPort ;
	private final boolean shellAutoStart ;
	
	ServerConfiguration(ConnectorConfiguration connectorConfig, String id, String logConfigPath, int shellPort, boolean shellAutoStart) {
		this.connectorConfig = connectorConfig ;
		this.id = id ;
		this.logConfigPath = logConfigPath ;
		this.shellPort = shellPort ;
		this.shellAutoStart = shellAutoStart ;
	}
	
	public String id(){
		return id ;
	}
	
	public int shellPort(){
		return shellPort ;
	}

	public boolean isShellAutoStart(){
		return shellAutoStart ;
	}
	
	public String logConfigPath(){
		return logConfigPath ;
	}

	public ConnectorConfiguration connector(){
		return connectorConfig ;
	}

	public void stopShell() {
	}

	
}
