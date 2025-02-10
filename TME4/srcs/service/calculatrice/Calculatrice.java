package srcs.service.calculatrice;

import java.io.Serializable;

public interface Calculatrice {

    int add(int a, int b);

    int sous(int a, int b);

    int mult(int a, int b);

    ResDiv div(int a, int b);

    final class ResDiv implements Serializable {

        private int quotient;
        private int reste;

        public ResDiv(int quotient, int reste) {
            this.quotient = quotient;
            this.reste = reste;
        }

        public int getQuotient() {
            return quotient;
        }

        public int getReste() {
            return reste;
        }

    }

}
