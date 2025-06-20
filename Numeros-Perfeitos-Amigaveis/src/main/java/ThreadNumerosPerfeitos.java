import java.math.BigInteger;

// Thread que percorre um subintervalo de p e verifica quais geram números perfeitos.
// Utiliza a fórmula Euclides-Mersenne
public class ThreadNumerosPerfeitos implements Runnable {
    private int inicio;
    private int fim;

    //Início e fim do subintervalo de p a ser verificado por esta thread 
        //que vai ser definido no cliente com base no intervalo completo
    public ThreadNumerosPerfeitos(int inicio, int fim) {
        this.inicio = inicio;
        this.fim = fim;
    }

    @Override
    public void run() {
        for (int p = inicio; p <= fim; p++) {
            if (primo(p)) {
                BigInteger dois = BigInteger.valueOf(2);
                //número de Mersenne correspondente
                BigInteger mersenne = dois.pow(p).subtract(BigInteger.ONE);

                if (mersenne.isProbablePrime(10)) {
                    //calcula o número perfeito
                    BigInteger perfeito = dois.pow(p - 1).multiply(mersenne);
                    System.out.println("Numero perfeito gerado por p = " + p + ": " + perfeito);
                }
            }
        }
    }

    //verifica se é primo 
    public static boolean primo(int numero) {
        if (numero < 2) return false;
        if (numero == 2) return true;
        if (numero % 2 == 0) return false;
        
        //testa n divindo por i até o limite 
        int limite = (int)Math.sqrt(numero) + 1;
        
        for(int i = 3; i <= limite; i += 2) {
            if (numero % i == 0) return false;
        }
        return true;
    }

}