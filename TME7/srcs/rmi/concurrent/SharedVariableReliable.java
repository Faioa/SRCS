package srcs.rmi.concurrent;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SharedVariableReliable<T extends Serializable> implements SharedVariable<T> {

    private T variable;
    private final int[] cpt;
    private final Map<Integer, Integer> countdowns;
    private final Timer timer;
    private TimerTask timeoutTask;
    private final long delay = 1000;

    private class TimeoutTask extends TimerTask {
        @Override
        public void run() {
            try {
                relacher(variable);
            } catch (RemoteException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public SharedVariableReliable(T variable) throws RemoteException {
        this.variable = variable;
        // Both a count of all current threads asking for the shared variable (cpt[0]) and a counter of the total number of accesses (cpt[1])
        cpt = new int[]{0, 0};
        // Used to associate a unique id for an access and a countdown to assure a FIFO access to the ressources
        countdowns = new HashMap<>();
        // A timer to free the ressources if it is locked but never unlocked
        timer = new Timer();
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
        synchronized (timer) {
            timeoutTask = new TimeoutTask();
            timer.schedule(timeoutTask, delay);
        }
        return variable;
    }

    @Override
    public void relacher(T value) throws RemoteException {
        synchronized (timer) {
            if (timeoutTask != null) {
                timeoutTask.cancel();
                timer.purge();
            }
        }
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
