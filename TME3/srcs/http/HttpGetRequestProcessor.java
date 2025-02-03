package srcs.http;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;

public class HttpGetRequestProcessor implements RequestProcessor {

    // WARNING : Should launch the program from the root directory of the project of change it
    String root = "./ressources";

    @Override
    public void process(Socket connexion) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connexion.getInputStream()))) {
            // Getting first line (HTTP method, version and ressource)
            String line = in.readLine();
            if (line == null) {
                connexion.getOutputStream().write(("HTTP/1.1 400 Bad Request\r\n\r\n").getBytes());
                return;
            }

            // Parsing first line
            String[] parts = line.split(" ");
            boolean correct = true;
            if (parts.length != 3
                    || !parts[0].equals("GET")
                    || (!parts[2].contains("HTTP/1.0")
                    && !parts[2].contains("HTTP/1.1")
                    && !parts[2].contains("HTTP/2")
                    && !parts[2].contains("HTTP/3"))) {
                correct = false;
            }

            boolean ended = false;
            while (in.ready() && (line = in.readLine()) != null) {
                // If the request is not good, we empty the stream without parsing anything
                if (! correct)
                    continue;

                // If the request's headers should be finished but there are still content to be read, we detect it and empty all the remaining data
                if (ended && !line.isEmpty()) {
                    correct = false;
                    continue;
                }

                // We detect the end of the request's headers
                if (line.isEmpty()) {
                    ended = true;
                    continue;
                }

                // Parsing headers

            }

            if (!correct) {
                connexion.getOutputStream().write(("HTTP/1.1 400 Bad Request\r\n\r\n").getBytes());
            } else {
                // Getting ressource
                Path ressourcePath = Path.of(root + parts[1]);

                // Checking if no ressource was specified : default is {root}/index.html
                if (ressourcePath.equals(Path.of(root)))
                    ressourcePath = Path.of(root + "/index.html");

                File ressourceFile = ressourcePath.toFile();
                if (ressourceFile.exists() && !ressourceFile.isDirectory()) {
                    FileInputStream fos = new FileInputStream(ressourceFile);
                    connexion.getOutputStream().write(("HTTP/1.1 200 OK\r\nContent-Length: " + ressourceFile.length() + "\r\n").getBytes());

                    // Non-exhaustive content-type management
                    String ressourceString = ressourceFile.getPath();
                    if (ressourceString.endsWith(".html"))
                        connexion.getOutputStream().write("Content-Type: text/html\r\n\r\n".getBytes());
                    else if (ressourceString.endsWith(".json"))
                        connexion.getOutputStream().write("Content-Type: application/json\r\n\r\n".getBytes());
                    else if (ressourceString.endsWith(".gif"))
                        connexion.getOutputStream().write("Content-Type: image/gif\r\n\r\n".getBytes());
                    else if (ressourceString.endsWith(".mp3"))
                        connexion.getOutputStream().write("Content-Type: audio/mpeg\r\n\r\n".getBytes());
                    else if (ressourceString.endsWith(".mp4"))
                        connexion.getOutputStream().write("Content-Type: video/mp4\r\n\r\n".getBytes());
                    else if (ressourceString.endsWith(".pdf"))
                        connexion.getOutputStream().write("Content-Type: application/pdf\r\n\r\n".getBytes());
                    else if (ressourceString.endsWith(".png"))
                        connexion.getOutputStream().write("Content-Type: image/png\r\n\r\n".getBytes());
                    else if (ressourceString.endsWith(".txt"))
                        connexion.getOutputStream().write("Content-Type: text/plain\r\n\r\n".getBytes());
                    else
                        connexion.getOutputStream().write("Content-Type: application/octet-stream\r\n\r\n".getBytes());

                    // Sending the file
                    while (fos.available() > 0) {
                        connexion.getOutputStream().write(fos.read());
                    }
                } else {
                    connexion.getOutputStream().write(("HTTP/1.1 404 Not Found\r\n\r\n").getBytes());
                }
            }
        } catch (IOException ignored) {}
    }

    public static void main(String[] args) {
        RequestProcessor processor = new HttpGetRequestProcessor();
        Serveur server = new Serveur(1234, processor);
        server.start();
    }

}
