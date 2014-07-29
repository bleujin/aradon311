package net.ion.bleujin.aradon;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;

import net.ion.framework.util.IOUtil;

public class HelloClient {

	public static void main(String[] args) throws Exception {
		Socket socket = new Socket(InetAddress.getLocalHost(), 8111);
		OutputStream output = socket.getOutputStream();
		output.write("GET / HTTP/1.0\r\n\r\n\r\n".getBytes(Charset.defaultCharset()));

		InputStream input = socket.getInputStream();
		IOUtil.copyNClose(input, System.out);
		socket.close();
	}
}
