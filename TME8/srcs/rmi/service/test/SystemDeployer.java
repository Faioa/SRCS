package srcs.rmi.service.test;

import org.junit.After;
import org.junit.Before;
import srcs.rmi.service.Host;
import srcs.rmi.service.HostImpl;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class SystemDeployer {

    private Registry registry = null;
    private Host host1;
    private Host host2;

    @Before
    public void setUp() throws RemoteException, NotBoundException {
        if (registry == null) {
            try {
                registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            } catch (RemoteException e) {
                registry = LocateRegistry.getRegistry(Registry.REGISTRY_PORT);
            }
        }

        host1 = new HostImpl();
        host2 = new HostImpl();

        Remote stub1 = UnicastRemoteObject.exportObject(host1, Registry.REGISTRY_PORT);
        Remote stub2 = UnicastRemoteObject.exportObject(host2, Registry.REGISTRY_PORT);

        registry.rebind("host1", stub1);
        registry.rebind("host2", stub2);
    }

    @After
    public void tearDown() throws RemoteException, NotBoundException {
        UnicastRemoteObject.unexportObject(host1, false);
        UnicastRemoteObject.unexportObject(host2, false);
        registry.unbind("host1");
        registry.unbind("host2");
    }

}
