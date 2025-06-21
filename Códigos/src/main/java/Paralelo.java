import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class Paralelo {

    /**
     Testa a primalidade de um número usando o método de divisão por tentativa,
     otimizado para verificar divisores apenas até a raiz quadrada do número.
     */
    public static boolean ehPrimo(int numero) {
        if (numero < 2) return false;
        if (numero == 2) return true;
        if (numero % 2 == 0) return false;

        int limite = (int) Math.sqrt(numero) + 1;
        for (int i = 3; i <= limite; i += 2) {
            if (numero % i == 0) return false;
        }
        return true;
    }

    /**
     Valida a segunda metade da relação de amizade.
     Utilizado após já se ter calculado a soma dos divisores do primeiro número.
     */
    public static boolean amizadeReciproca(int numOriginal, int amigoEmPotencial) {
        return somaDivisores(amigoEmPotencial) == numOriginal;
    }

    /**
     Calcula a soma dos divisores próprios de um número.
     A otimização i*i <= num explora o fato de que divisores vêm em pares (i, num/i).
     Percorrer até a raiz quadrada é suficiente para encontrar todos os pares.
     */
    private static int somaDivisores(int num) {
        int outroDivisor = 0;
        int soma = 1;

        if (num <= 1) {
            return 0;
        }
        for (int i = 2; i * i <= num; i++) {
            if (num % i == 0) {
                soma += i;
                outroDivisor = num / i;
                // Evita adicionar a raiz quadrada de um quadrado perfeito duas vezes.
                if (i != outroDivisor) {
                    soma += outroDivisor;
                }
            }
        }
        return soma;
    }

    /**
     Tarefa executável para buscar números perfeitos em paralelo.
     A busca é baseada na relação entre primos 'p' e os Primos de Mersenne.
     */
    static class threadBuscaPerfeito implements Runnable {
        int numInicial;
        int numFinal;
        int limiteMax;
        ConcurrentSkipListSet<BigInteger> resultado;

        public threadBuscaPerfeito(int numInicial, int numFinal, int limiteMax) {
            this.numInicial = numInicial;
            this.numFinal = numFinal;
            this.limiteMax = limiteMax;
        }

        @Override
        public void run() {

            // Cada thread testa uma faixa de expoentes 'p' para a fórmula de Euclides-Euler.
            for (int p = numInicial; p <= numFinal; p++) {
                if (ehPrimo(p)) {
                    BigInteger dois = BigInteger.valueOf(2);
                    BigInteger mersenne = dois.pow(p).subtract(BigInteger.ONE);

                    if (mersenne.isProbablePrime(10)) {
                        BigInteger perfeito = dois.pow(p - 1).multiply(mersenne);

                        System.out.println("Numero perfeito gerado por p = " + p + ": " + perfeito);
                    }
                }
            }
        }
    }

    /**
     Tarefa executável para buscar números amigáveis em paralelo.
     Implementa uma busca otimizada de pares alíquotas.
     */
    static class threadBuscaAmigavel implements Runnable {
        int numInicial;
        int numFinal;
        int limiteMax;
        Map<Integer, Integer> paresAmigaveis; // Armazena os pares encontrados.
        Set<Integer> amigaveisJaEncontrados; // Cache para otimização de performance.

        public threadBuscaAmigavel(int numInicial, int numFinal, int limiteMax, Map<Integer, Integer> paresAmigaveis, Set<Integer> amigaveisJaEncontrados) {
            this.numInicial = numInicial;
            this.numFinal = numFinal;
            this.limiteMax = limiteMax;
            this.paresAmigaveis = paresAmigaveis;
            this.amigaveisJaEncontrados = amigaveisJaEncontrados;
        }

        @Override
        public void run() {
            for (int i = numInicial; i <= numFinal; i++) {
                // Verifica o cache compartilhado antes de qualquer cálculo pesado.
                if (amigaveisJaEncontrados.contains(i)) continue;
                int divisores = somaDivisores(i);
                /**
                 Garante que cada par (a,b) com a < b
                 seja processado apenas uma vez, quebrando a simetria do problema.
                */
                if (divisores > i && divisores <= limiteMax) {
                    if (amizadeReciproca(i, divisores)) {
                        // Atualiza as estruturas de dados compartilhadas.
                        paresAmigaveis.put(i, divisores);
                        amigaveisJaEncontrados.add(i);
                        // Adiciona também o segundo membro do par para otimizar futuras buscas.
                        amigaveisJaEncontrados.add(divisores);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        int numThreads = 8;
        int intervaloPerfeito = 12000;
        int intervaloAmigo = 50000000;

        
        Map<Integer, Integer> paresAmigaveis = new ConcurrentSkipListMap<>(); // Armazena de forma ordenada e segura entre as threads.
        Set<Integer> amigaveisJaEncontrados = java.util.concurrent.ConcurrentHashMap.newKeySet(); // Conjunto para evitar duplicatas e otimizar a busca.

        System.out.println("Iniciando busca por numeros perfeitos e amigaveis (em paralelo)...");

        List<Thread> todasAsThreads = new java.util.ArrayList<>();
        int tamanhoIntervaloAmigo = intervaloAmigo / numThreads;
        int tamanhoIntervaloPerfeito = intervaloPerfeito / numThreads;


        for (int i = 0; i < numThreads; i++) { // O trabalho é dividido em faixas de busca para cada thread.
            int inicioP = i * tamanhoIntervaloPerfeito + 1;
            int inicio = i * tamanhoIntervaloAmigo + 1;
            int fimP = (i == numThreads - 1) ? intervaloPerfeito : (i + 1) * tamanhoIntervaloPerfeito;
            int fim = (i == numThreads - 1) ? intervaloAmigo : (i + 1) * tamanhoIntervaloAmigo;
            
            Thread tPerfeitos = new Thread(new threadBuscaPerfeito(inicioP, fimP, intervaloPerfeito));
            todasAsThreads.add(tPerfeitos);
            
            Thread t = new Thread(new threadBuscaAmigavel(inicio, fim, intervaloAmigo, paresAmigaveis, amigaveisJaEncontrados));
            todasAsThreads.add(t);
            
            tPerfeitos.start();  
            t.start();   
        }

        /** 
         Aguarda a finalização de todas as threads antes de imprimir os resultados.
         O método join() bloqueia a thread principal até que a thread alvo complete sua execução.
        */
        for (Thread thread : todasAsThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\nPares Amigaveis ate " + intervaloAmigo + ":");
        for (Map.Entry<Integer, Integer> par : paresAmigaveis.entrySet()) {
            System.out.println("(" + par.getKey() + ", " + par.getValue() + ")");
        }
    }
}
