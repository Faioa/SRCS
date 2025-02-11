package srcs.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public abstract class ClientProxy {

    private final String hostname;
    private final int port;

    public ClientProxy(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public Object invokeService(String name, Object[] params) throws MyProtocolException {
        // Opening the Socket
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(InetAddress.getByName(hostname), port));


            // Sending data to the server
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeUTF(name);

            // Writing params as their right types
            for (Object param : params) {
                Class<?> type = param.getClass();
                 if (type == Byte.class) {
                    out.writeByte((byte) param);
                } else if (type == Short.class) {
                    out.writeShort((short) param);
                } else if (type == Integer.class) {
                    out.writeInt((int) param);
                } else if (type == Long.class) {
                    out.writeLong((long) param);
                } else if (type == Float.class) {
                    out.writeFloat((float) param);
                } else if (type == Double.class) {
                    out.writeDouble((double) param);
                } else if (type == Boolean.class) {
                    out.writeBoolean((boolean) param);
                } else if (type == Character.class) {
                    out.writeChar((char) param);
                } else if (type == String.class) {
                    out.writeUTF((String) param);
                } else {
                    out.writeObject(param);
                }
            }
            out.flush();

            // Getting data from the server
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Object o = in.readObject();

            // Finding if the returned object is an Exception
            if (o instanceof MyProtocolException)
                throw (MyProtocolException) o;
            return o;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
