package srcs.securite;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class ChannelBasic implements Channel {

    private final Socket socket;

    public ChannelBasic(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void send(byte[] bytesArray) throws IOException {
        ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
        os.writeInt(bytesArray.length);
        os.write(bytesArray);
        os.flush();
    }

    @Override
    public byte[] recv() throws IOException {
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        byte[] bytesArray = new byte[in.readInt()];
        in.readFully(bytesArray);
        return bytesArray;
    }

    @Override
    public InetAddress getRemoteHost() {
        return socket.getInetAddress();
    }

    @Override
    public int getRemotePort() {
        return socket.getPort();
    }

    @Override
    public InetAddress getLocalHost() {
        return socket.getLocalAddress();
    }

    @Override
    public int getLocalPort() {
        return socket.getLocalPort();
    }
}
