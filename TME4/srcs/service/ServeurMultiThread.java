package srcs.service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServeurMultiThread {

    private final int port;
    private final Class<? extends Service> serviceClass;
    private final Service serviceGlobal;

    public ServeurMultiThread(int port, Class<? extends Service> serviceClass) {
        Service tmp_s = null;
        this.port = port;
        this.serviceClass = serviceClass;
        if (serviceClass.getAnnotation(EtatGlobal.class) != null) {
            try {
                tmp_s = serviceClass.getConstructor().newInstance();
            } catch (Exception ignored) {}
        }
        serviceGlobal = tmp_s;
    }

    public void listen() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while(true) {
                Socket c = serverSocket.accept();
                Service s = getService();
                new Thread(() -> {
                    s.execute(c);
                }).start();
            }
        } catch (IOException ignored) {}
    }

    private Service getService() throws IllegalStateException {
        if (serviceClass == null)
            throw new IllegalStateException();

        if (serviceClass.getAnnotation(EtatGlobal.class) != null) {
            return serviceGlobal;
        } else if (serviceClass.getAnnotation(SansEtat.class) != null) {
            try {
                return serviceClass.getConstructor().newInstance();
            } catch (Exception ignored) {}
        }
        throw new IllegalStateException("Service not annoted as SansEtat or EtatGlobal.");
    }

}
