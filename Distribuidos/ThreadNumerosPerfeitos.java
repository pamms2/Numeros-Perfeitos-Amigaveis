// Thread que percorre um subintervalo de p e verifica quais geram números perfeitos.
// Utiliza a fórmula Euclides-Mersenne

import java.math.BigInteger;

public class ThreadNumerosPerfeitos implements Runnable {
    private BigInteger inicio;
    private BigInteger fim;

    //Início e fim do subintervalo de p a ser verificado por esta thread 
        //que vai ser definido no cliente com base no intervalo completo
    public ThreadNumerosPerfeitos(BigInteger inicio, BigInteger fim) {
        this.inicio = inicio;
        this.fim = fim;
    }

    @Override
    public void run() {
        //percorre todos os valores de p no subintervalo
        for (BigInteger p = inicio; p.compareTo(fim) <= 0; p = p.add(BigInteger.ONE)) {
            if (primo(p)) {
                BigInteger dois = BigInteger.valueOf(2);
                //número de Mersenne correspondente
                BigInteger mersenne = dois.pow(p.intValue()).subtract(BigInteger.ONE);

                if (mersenne.isProbablePrime(10)) {
                    //calcula o número perfeito
                    BigInteger perfeito = dois.pow(p.intValue() - 1).multiply(mersenne);
                    System.out.println("Número perfeito gerado por p = " + p + ": " + perfeito);
                }
            }
        }
    }

    //verifica se é primo 
    public static boolean primo(BigInteger numero) {
        if (numero.compareTo(BigInteger.TWO) < 0) return false;
        if (numero.equals(BigInteger.TWO)) return true;
        if (numero.mod(BigInteger.TWO).equals(BigInteger.ZERO)) return false;

        BigInteger i = BigInteger.valueOf(3);
        BigInteger limite = sqrt(numero).add(BigInteger.ONE); //limite da raiz de n
        //testa n divindo por i até o limite 
        while (i.compareTo(limite) <= 0) {
            if (numero.mod(i).equals(BigInteger.ZERO)) return false;
            i = i.add(BigInteger.TWO);
        }
        return true;
    }

     //calcula a raiz inteira do BigInteger - usando busca binária
    private static BigInteger sqrt(BigInteger n) {
        BigInteger a = BigInteger.ONE;
        BigInteger b = n.shiftRight(1).add(BigInteger.ONE);
        //busca binária para encontrar a parte inteira da raiz de n
        while (b.compareTo(a) >= 0) {
            BigInteger meio = a.add(b).shiftRight(1); //na busca binária precisa testar o valor do meio do intervalo
            int cmp = meio.multiply(meio).compareTo(n);
            if (cmp > 0) {
                b = meio.subtract(BigInteger.ONE);
            } else {
                a = meio.add(BigInteger.ONE);
            }
        }
        return a.subtract(BigInteger.ONE);
    }
}
