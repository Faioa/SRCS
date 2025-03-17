package srcs.rmi.concurrent.test;

import org.junit.After;
import org.junit.Before;
import srcs.rmi.concurrent.SharedVariable;
import srcs.rmi.concurrent.SharedVariableClassical;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class SystemDeployer {

    private SharedVariable<Integer> variable = null;
    private Registry registry = null;

    @Before
    public void setUp() throws RemoteException, AlreadyBoundException {
        variable = new SharedVariableClassical<>(0);
        SharedVariable<Integer> stub = (SharedVariable<Integer>)UnicastRemoteObject.exportObject(variable, 1099);

        try {
            registry = LocateRegistry.createRegistry(1099);
        } catch (RemoteException e) {
            registry = LocateRegistry.getRegistry(1099);
        }
        registry.rebind(TestSharedVariableClassical.nameService, stub);
    }

    @After
    public void tearDown() throws RemoteException, NotBoundException {
        UnicastRemoteObject.unexportObject(variable, false);
        registry.unbind(TestSharedVariableClassical.nameService);
        variable = null;
    }

}
