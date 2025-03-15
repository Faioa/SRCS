package srcs.securite;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class ChannelBasic implements Channel {

    private final Socket socket;

    public ChannelBasic(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void send(byte[] bytesArray) throws IOException {
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());
        os.writeInt(bytesArray.length);
        os.write(bytesArray);
        os.flush();
    }

    @Override
    public byte[] recv() throws IOException {
        DataInputStream in = new DataInputStream(socket.getInputStream());
        int size = in.readInt();
        return in.readNBytes(size);
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
