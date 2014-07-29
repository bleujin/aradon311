package net.ion.nradon.restlet;

public class Protocol {

	public static final int UNKNOWN_PORT = -1;
	public static final Protocol AJP = new Protocol("ajp", "AJP", "Apache Jakarta Protocol", 8009);
	public static final Protocol ALL = new Protocol("all", "ALL", "Wildcard for all protocols", -1);
	public static final Protocol CLAP = new Protocol("clap", "CLAP", "Class Loader Access Protocol", -1, true);
	public static final Protocol FILE = new Protocol("file", "FILE", "Local File System Protocol", -1, true);
	public static final Protocol FTP = new Protocol("ftp", "FTP", "File Transfer Protocol", 21);
	public static final Protocol HTTP = new Protocol("http", "HTTP", "HyperText Transport Protocol", 80, "1.1");
	public static final Protocol HTTPS = new Protocol("https", "HTTPS", "HTTP", "HyperText Transport Protocol (Secure)", 443, true, "1.1");
	public static final Protocol JAR = new Protocol("jar", "JAR", "Java ARchive", -1, true);
	public static final Protocol JDBC = new Protocol("jdbc", "JDBC", "Java DataBase Connectivity", -1);
	public static final Protocol POP = new Protocol("pop", "POP", "Post Office Protocol", 110);
	public static final Protocol POPS = new Protocol("pops", "POPS", "Post Office Protocol (Secure)", 995, true);
	public static final Protocol RIAP = new Protocol("riap", "RIAP", "Restlet Internal Access Protocol", -1, true);
	public static final Protocol SDC = new Protocol("sdc", "SDC", "Secure Data Connector Protocol", -1, true);
	public static final Protocol SIP = new Protocol("sip", "SIP", "Session Initiation Protocol", 5060, "2.0");
	public static final Protocol SIPS = new Protocol("sips", "SIPS", "SIP", "Session Initiation Protocol (Secure)", 5061, true, "2.0");
	public static final Protocol SMTP = new Protocol("smtp", "SMTP", "Simple Mail Transfer Protocol", 25);
	public static final Protocol SMTPS = new Protocol("smtps", "SMTPS", "Simple Mail Transfer Protocol (Secure)", 465, true);
	public static final Protocol WAR = new Protocol("war", "WAR", "Web Archive Access Protocol", -1, true);
	public static final Protocol ZIP = new Protocol("zip", "ZIP", "Zip Archive Access Protocol", -1, true);
	private final boolean confidential;
	private final int defaultPort;
	private final String description;
	private final String name;
	private volatile String schemeName;
	private final String technicalName;
	private volatile String version;

	public static Protocol valueOf(String name) {
		Protocol result = null;
		if (name != null && !name.equals(""))
			if (name.equalsIgnoreCase(AJP.getSchemeName()))
				result = AJP;
			else if (name.equalsIgnoreCase(CLAP.getSchemeName()))
				result = CLAP;
			else if (name.equalsIgnoreCase(FILE.getSchemeName()))
				result = FILE;
			else if (name.equalsIgnoreCase(FTP.getSchemeName()))
				result = FTP;
			else if (name.equalsIgnoreCase(HTTP.getSchemeName()))
				result = HTTP;
			else if (name.equalsIgnoreCase(HTTPS.getSchemeName()))
				result = HTTPS;
			else if (name.equalsIgnoreCase(JAR.getSchemeName()))
				result = JAR;
			else if (name.equalsIgnoreCase(JDBC.getSchemeName()))
				result = JDBC;
			else if (name.equalsIgnoreCase(POP.getSchemeName()))
				result = POP;
			else if (name.equalsIgnoreCase(POPS.getSchemeName()))
				result = POPS;
			else if (name.equalsIgnoreCase(RIAP.getSchemeName()))
				result = RIAP;
			else if (name.equalsIgnoreCase(SMTP.getSchemeName()))
				result = SMTP;
			else if (name.equalsIgnoreCase(SMTPS.getSchemeName()))
				result = SMTPS;
			else if (name.equalsIgnoreCase(SIP.getSchemeName()))
				result = SIP;
			else if (name.equalsIgnoreCase(SIPS.getSchemeName()))
				result = SIPS;
			else if (name.equalsIgnoreCase(WAR.getSchemeName()))
				result = WAR;
			else if (name.equalsIgnoreCase(ZIP.getSchemeName()))
				result = ZIP;
			else
				result = new Protocol(name);
		return result;
	}

	public static Protocol valueOf(String name, String version) {
		Protocol result = valueOf(name);
		if (!version.equals(result.getVersion()))
			result = new Protocol(result.getSchemeName(), result.getName(), result.getTechnicalName(), result.getDescription(), result.getDefaultPort(), result.isConfidential(), version);
		return result;
	}

	public Protocol(String schemeName) {
		this(schemeName, schemeName.toUpperCase(), (new StringBuilder()).append(schemeName.toUpperCase()).append(" Protocol").toString(), -1);
	}

	public Protocol(String schemeName, String name, String description, int defaultPort) {
		this(schemeName, name, description, defaultPort, false);
	}

	public Protocol(String schemeName, String name, String description, int defaultPort, boolean confidential) {
		this(schemeName, name, description, defaultPort, confidential, null);
	}

	public Protocol(String schemeName, String name, String description, int defaultPort, boolean confidential, String version) {
		this(schemeName, name, name, description, defaultPort, confidential, version);
	}

	public Protocol(String schemeName, String name, String description, int defaultPort, String version) {
		this(schemeName, name, description, defaultPort, false, version);
	}

	public Protocol(String schemeName, String name, String technicalName, String description, int defaultPort, boolean confidential, String version) {
		this.name = name;
		this.description = description;
		this.schemeName = schemeName;
		this.technicalName = technicalName;
		this.defaultPort = defaultPort;
		this.confidential = confidential;
		this.version = version;
	}

	public boolean equals(Object object) {
		return (object instanceof Protocol) && getName().equalsIgnoreCase(((Protocol) object).getName());
	}

	public int getDefaultPort() {
		return defaultPort;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public String getSchemeName() {
		return schemeName;
	}

	public String getTechnicalName() {
		return technicalName;
	}

	public String getVersion() {
		return version;
	}

	public int hashCode() {
		return getName() != null ? getName().toLowerCase().hashCode() : 0;
	}

	public boolean isConfidential() {
		return confidential;
	}

	public String toString() {
		return (new StringBuilder()).append(getName()).append(getVersion() != null ? (new StringBuilder()).append("/").append(getVersion()).toString() : "").toString();
	}

}
