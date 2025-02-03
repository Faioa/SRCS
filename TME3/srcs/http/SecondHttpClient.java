package srcs.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;

public class SecondHttpClient {
	
	public static void main(String[] args) throws IOException {
		Proxy proxy = null;
		if (args.length > 1) {
			proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(InetAddress.getByName(args[0]), Integer.parseInt(args[1])));
		}
		Socket c;
		if (proxy != null) {
			c = new Socket(proxy);
		}
		else {
			c = new Socket();
		}

		c.connect(new InetSocketAddress("www.google.fr", 80));
		OutputStream out = c.getOutputStream();
		InputStream in = c.getInputStream();
		out.write("GET /toto.html HTTP/1.1\r\nHost: www.google.fr\r\n\r\n".getBytes());
		do {
			int i = in.read();
			System.out.write(i);
		} while (in.available() != 0);
	}
	
}
