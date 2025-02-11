package srcs.service.annuaire;

import srcs.service.AppelDistant;
import srcs.service.EtatGlobal;

import java.util.HashMap;
import java.util.Map;

@EtatGlobal
public class AnnuaireAppelDistant implements Annuaire, AppelDistant {

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
