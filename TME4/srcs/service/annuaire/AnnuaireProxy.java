package srcs.service.annuaire;

import srcs.service.ClientProxy;

public class AnnuaireProxy extends ClientProxy implements Annuaire {

    public AnnuaireProxy(String hostname, int port) {
        super(hostname, port);
    }

    @Override
    public String lookup(String name) {
        return (String) super.invokeService("lookup", new Object[]{name});
    }

    @Override
    public void bind(String name, String value) {
        super.invokeService("bind", new Object[]{name, value});
    }

    @Override
    public void unbind(String name) {
        super.invokeService("unbind", new Object[]{name});
    }

}
