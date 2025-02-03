package http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SecondRequestProcessor implements RequestProcessor {

	@Override
	public void process(Socket connexion) {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(connexion.getInputStream()))) {
			String line;
			while ((line = in.readLine()) != null && !line.isEmpty()) {
				System.out.println(line);
			}
		} catch (IOException ignored) {}
	}
	
	public static void main(String[] args) {
		RequestProcessor processor = new SecondRequestProcessor();
		Serveur server = new Serveur(1234, processor);
		server.start();
	}

}
