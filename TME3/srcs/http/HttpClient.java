package http;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;

public class HttpClient {
	
	public static void main(String[] args) {
		SocketAddress proxy = new InetSocketAddress("proxy", 3128);
		Socket c = new Socket(new Proxy(Proxy.Type.SOCKS, proxy));
		try {
			c.connect(new InetSocketAddress("www.google.fr", 80));
			InputStream in = c.getInputStream();
			DataOutputStream out = new DataOutputStream(c.getOutputStream());
			out.writeChars("GET /toto.html HTTP 1.1");
			do {
				int i = in.read();
				System.out.write(i);
			} while (in.available() != 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
