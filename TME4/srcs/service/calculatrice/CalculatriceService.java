package srcs.service.calculatrice;

import srcs.service.MyProtocolException;
import srcs.service.SansEtat;
import srcs.service.Service;
import srcs.service.VoidResponse;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

@SansEtat
public class CalculatriceService implements Calculatrice, Service {

    @Override
    public int add(int a, int b) {
        return a + b;
    }

    @Override
    public int sous(int a, int b) {
        return a - b;
    }

    @Override
    public int mult(int a, int b) {
        return a * b;
    }

    @Override
    public ResDiv div(int a, int b) {
        return new ResDiv(a/b, a%b);
    }

}
