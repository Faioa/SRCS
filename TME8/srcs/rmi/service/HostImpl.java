package srcs.rmi.service;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HostImpl implements Host{

    private Map<String, FunctionService<? extends Serializable, ? extends Serializable>> services;
    private Registry registry;

    public HostImpl() throws RemoteException {
        services = new HashMap<>();
        registry = LocateRegistry.getRegistry(Registry.REGISTRY_PORT);
    }

    @Override
    public synchronized FunctionService deployNewService(String serviceName, Class<? extends FunctionService> serviceClass) throws RemoteException {
        if (services.containsKey(serviceName))
            throw new RemoteException("Service already deployed");
        try {
            // Instantiating new service
            FunctionService service = serviceClass.getConstructor(new Class<?>[]{serviceName.getClass()}).newInstance(new Object[]{serviceName});
            services.put(serviceName, service);

            // Exporter le service
            FunctionService stub = (FunctionService) UnicastRemoteObject.exportObject(service, Registry.REGISTRY_PORT);
            registry.rebind(serviceName, stub);

            return stub;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized FunctionService deployExistingService(FunctionService service) throws RemoteException {
        // Checking if a service was already deployed
        if (services.containsKey(service.getName())) {
            Remote existingService = services.remove(service.getName());
            UnicastRemoteObject.unexportObject(existingService, false);
        }

        services.put(service.getName(), service);

        // Rebinding
        FunctionService stub = (FunctionService) UnicastRemoteObject.exportObject(service, Registry.REGISTRY_PORT);
        registry.rebind(service.getName(), stub);

        return stub;
    }

    @Override
    public synchronized boolean undeployService(String serviceName) throws RemoteException {
        if (!services.containsKey(serviceName))
            return false;
        UnicastRemoteObject.unexportObject(services.remove(serviceName), false);
        try {
            registry.unbind(serviceName);
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public synchronized List<String> getServices() throws RemoteException {
        return new LinkedList<>(services.keySet());
    }

}
