package nl.pvdberg.pnet.server.util;

import java.net.InetSocketAddress;

import nl.pvdberg.pnet.event.PNetListener;
import nl.pvdberg.pnet.server.Server;

public class ServerDecorator implements Server {
    protected final Server server;

    public ServerDecorator(final Server server) {
        this.server = server;
    }

    @Override
    public void setListener(final PNetListener serverListener) {
        server.setListener(serverListener);
    }

    @Override
    public boolean start(InetSocketAddress address) {
        return server.start(address);
    }

    @Override
    public void stop() {
        server.stop();
    }
}
