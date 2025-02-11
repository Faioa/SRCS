package srcs.service.calculatrice;

import srcs.service.ClientProxy;

public class CalculatriceProxy extends ClientProxy implements Calculatrice {

    public CalculatriceProxy(String hostname, int port) {
        super(hostname, port);
    }

    @Override
    public int add(int a, int b) {
        return (Integer) super.invokeService("add", new Object[]{a, b});
    }

    @Override
    public int sous(int a, int b) {
        return (Integer) super.invokeService("sous", new Object[]{a, b});
    }

    @Override
    public int mult(int a, int b) {
        return (Integer) super.invokeService("mult", new Object[]{a, b});
    }

    @Override
    public ResDiv div(int a, int b) {
        return (ResDiv) super.invokeService("div", new Object[]{a, b});
    }
}
