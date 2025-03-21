package srcs.rmi.service;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FunctionService<P extends Serializable, R extends Serializable> extends Remote, Serializable, Cloneable{
    String getName() throws RemoteException;
    R invoke(P param) throws RemoteException;
    FunctionService migrateTo(Host host) throws RemoteException;
}
