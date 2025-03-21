package srcs.rmi.service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Host extends Remote {
    FunctionService deployNewService(String serviceName, Class<? extends FunctionService> serviceClass) throws RemoteException;
    FunctionService deployExistingService(FunctionService service) throws RemoteException;
    boolean undeployService(String serviceName) throws RemoteException;
    List<String> getServices() throws RemoteException;
}
