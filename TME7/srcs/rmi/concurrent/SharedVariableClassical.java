package srcs.rmi.concurrent;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.util.HashMap;
import java.util.Map;

public class SharedVariableClassical<T extends Serializable> implements SharedVariable<T> {

    private T variable;
    private final int[] cpt;
    private final Map<Integer, Integer> countdowns;

    public SharedVariableClassical(T variable) throws RemoteException {
        this.variable = variable;
        // Both a count of all current threads asking for the shared variable (cpt[0]) and a counter of the total number of accesses (cpt[1])
        cpt = new int[]{0, 0};
        countdowns = new HashMap<>();
    }

    @Override
    public T obtenir() throws RemoteException {
        int id;
        int counter;
        synchronized (cpt) {
            id = cpt[1]++;
            counter = cpt[0]++;
        }

        synchronized (countdowns) {
            countdowns.put(id, counter);
            try {
                while (countdowns.get(id) > 0) {
                    countdowns.wait();
                }
            } catch (InterruptedException _) {
                Thread.currentThread().interrupt();
            }
            countdowns.remove(id);
        }
        return variable;
    }

    @Override
    public void relacher(T value) throws RemoteException {
        synchronized (cpt) {
            cpt[0]--;
        }
        synchronized (countdowns) {
            countdowns.forEach( (id, counter) -> countdowns.put(id, counter - 1));
            variable = value;
            countdowns.notifyAll();
        }
    }

}
