package srcs.service.annuaire;

import srcs.service.EtatGlobal;
import srcs.service.MyProtocolException;
import srcs.service.Service;
import srcs.service.VoidResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@EtatGlobal
public class AnnuaireService implements Annuaire, Service {

    private Map<String, String> annuaire = new HashMap<String, String>();

    @Override
    public String lookup(String name) {
        String value;
        synchronized (annuaire) {
            value = annuaire.get(name);
        }
        if (value == null)
            return "";
        return value;
    }

    @Override
    public void bind(String name, String value) {
        synchronized (annuaire) {
            annuaire.put(name, value);
        }
    }

    @Override
    public void unbind(String name) {
        synchronized (annuaire) {
            annuaire.remove(name);
        }
    }

}
