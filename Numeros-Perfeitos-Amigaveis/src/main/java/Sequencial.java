import java.math.BigInteger;

public class Sequencial {
   
    public static void main(String[] args) {
        int intervaloPerfeito = 12000;
        int intervaloAmigo = 50000000;
        Sequencial seq = new Sequencial();
       
        System.out.println("Numeros perfeitos: ");
        seq.numeroPerfeito(intervaloPerfeito);
        System.out.println("\nPares amigaveis: ");
        seq.buscarParesAmigaveis(intervaloAmigo);
        
    }
    
    // Encontrando números primos 
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
    
    private long somaDivisores(long num) {
        long soma = 1;
        if (num <= 1) return 0;

        for (long i = 2; i * i <= num; i++) {
            if (num % i == 0) {
                soma += i;
                long outroDivisor = num / i;
                if (i != outroDivisor) {
                    soma += outroDivisor;
                }
            }
        }
        return soma;
    }
    
    //Encontrando números perfeitos
    public void numeroPerfeito(int intervaloPerfeito){
        
        for (int p=2; p<=intervaloPerfeito; p++){

            if (primo(p)) {
                BigInteger dois = BigInteger.valueOf(2);
                BigInteger mersenne = dois.pow(p).subtract(BigInteger.ONE); //testa se é primo de mersenne 
 
                if (mersenne.isProbablePrime(10)) { //se provavelmente for primo
                    BigInteger perfeito = dois.pow(p - 1).multiply(mersenne); //calcula o perfeito com base na fórmula euclides-mersenne
                    System.out.println("Numero perfeito gerado por p = "+p+": "+perfeito); 
                }
            }
        }
    }
    
    private void buscarParesAmigaveis(long limiteMax) {
        for (long i = 2; i <= limiteMax; i++) {
            long divisores = somaDivisores(i);
            if (divisores > i && divisores <= limiteMax) {
                if (somaDivisores(divisores) == i) {
                    System.out.println("(" + i + ", " + divisores + ")");
                }
            }
        }
    }

}