package srcs.rmi.service;

import java.io.Serializable;
import java.rmi.RemoteException;

public abstract class AbstractFunctionService<P extends Serializable, R extends Serializable> implements FunctionService<P, R> {

    private final String name;
    private boolean migrated;
    private FunctionService<P, R> stub;

    public AbstractFunctionService(String name) {
        this.name = name;
        migrated = false;
    }

    @Override
    public synchronized String getName() throws RemoteException {
        return name;
    }

    @Override
    public synchronized R invoke(P param) throws RemoteException {
        if (migrated)
            return stub.invoke(param);
        return perform(param);
    }

    @Override
    public synchronized FunctionService<P, R> migrateTo(Host host) throws RemoteException {
        if (migrated)
            throw new RemoteException("Service already migrated");
        try {
            stub = (FunctionService<P, R>) host.deployExistingService((FunctionService<P, R>) this.clone());
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        migrated = true;
        return stub;
    }

    protected abstract R perform (P param) throws RemoteException;

}
