package http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class FirstRequestProcessor implements RequestProcessor {

	@Override
	public void process(Socket connexion) {
		try (InputStream in = connexion.getInputStream()) {
			while (in.available() != 0)
				System.out.write(in.read());
		} catch (IOException ignored) {}
	}
	
	public static void main(String[] args) {
		RequestProcessor processor = new FirstRequestProcessor();
		Serveur server = new Serveur(1234, processor);
		server.start();
	}

}
