package srcs.service.calculatrice;

import srcs.service.AppelDistant;
import srcs.service.SansEtat;

@SansEtat
public class CalculatriceAppelDistant implements Calculatrice, AppelDistant {

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
