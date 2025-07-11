package srcs.securite;

import java.io.IOException;
import java.net.InetAddress;

public class ChannelDecorator implements Channel {

    private final Channel channel;

    public ChannelDecorator(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void send(byte[] bytesArray) throws IOException {
        channel.send(bytesArray);
    }

    @Override
    public byte[] recv() throws IOException {
        return channel.recv();
    }

    @Override
    public InetAddress getRemoteHost() {
        return channel.getRemoteHost();
    }

    @Override
    public int getRemotePort() {
        return channel.getRemotePort();
    }

    @Override
    public InetAddress getLocalHost() {
        return channel.getLocalHost();
    }

    @Override
    public int getLocalPort() {
        return channel.getLocalPort();
    }

}
