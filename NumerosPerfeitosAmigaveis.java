import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;


public class NumerosPerfeitosAmigaveis{


    public static boolean ehPrimo(long n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        for (long i = 5; i * i <= n; i = i + 6) {
            if (n % i == 0 || n % (i + 2) == 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean perfeito(long num) {
        return somaDivisores(num) == num;
    }

    // Funções para números amigáveis.
    public static boolean amigavel(long a, long b) {
        return (somaDivisores(a) == b && somaDivisores(b) == a);
    }

    public static boolean amizadeReciproca(long numOriginal, long amigoEmPotencial) {
        return somaDivisores(amigoEmPotencial) == numOriginal;
    }
    
    // Função para calcular a soma dos divisores.
    private static long somaDivisores(long num) {
        long outroDivisor = 0;
        long soma = 1;

        if(num <= 1){
            return 0;
        }
        for (long i = 2; i * i <= num; i++) {
            if (num % i == 0) {
                soma += i;
                outroDivisor = num/i;
                if(i != outroDivisor){
                    soma += outroDivisor;
                }
            }
        }
        return soma;
    }


    static class threadBuscaPerfeito implements Runnable {
        // A variável a de limite ('numFinal')
        // e a de resultado continuam sendo usadas. Adicionei um ID para a thread.
        long numInicial = 0;
        long numFinal = 0;
        long limiteMax = 0; // Adicionado para ter o limite total
        int threadId = 0; // Adicionado para identificar a primeira thread
        ConcurrentSkipListSet<Long> resultado;

        public threadBuscaPerfeito(long numInicial, long numFinal, long limiteMax, int threadId, ConcurrentSkipListSet<Long> resultado) {
            this.numInicial = numInicial;
            this.numFinal = numFinal;
            this.limiteMax = limiteMax;
            this.threadId = threadId;
            this.resultado = resultado;
        }

        @Override
        public void run() {
            // Apenas a primeira thread (ID 0) executará a lógica para evitar trabalho duplicado.
            if (threadId != 0) {
                return;
            }

            // A busca agora itera sobre 'p' para gerar os primos de Mersenne.
            for (int p = 2; ; p++) {
                // Se 'p' for primo
                if (ehPrimo(p)) {
                    // calcula o candidato a primo de Mersenne (M_p = 2^p - 1)
                    long mersenneCandidate = (1L << p) - 1;

                    // Se M_p também for primo
                    if (ehPrimo(mersenneCandidate)) {
                        // calcula o número perfeito correspondente.
                        long numPerfeito = (1L << (p - 1)) * mersenneCandidate;

                        // Se o número gerado estiver dentro do limite, adiciona ao resultado.
                        if (numPerfeito <= limiteMax) {
                            resultado.add(numPerfeito);
                        } else {
                            // Se ultrapassou o limite, para, pois os próximos serão maiores.
                            break;
                        }
                    }
                }
                // Adicionei uma verificação de segurança para 'p' para evitar loops infinitos
                // caso o limite seja muito grande (long overflow). 2^63 já estoura o long.
                if (p > 62) {
                    break;
                }
            }
        }
    }

    // A thread de busca de amigáveis.
    static class threadBuscaAmigavel implements Runnable {
        long numInicial;
        long numFinal;
        long limiteMax;
        Map<Long, Long> paresAmigaveis;
        Set<Long> amigaveisJaEncontrados;

        public threadBuscaAmigavel(long numInicial, long numFinal, long limiteMax, Map<Long, Long> paresAmigaveis, Set<Long> amigaveisJaEncontrados) {
            this.numInicial = numInicial;
            this.numFinal = numFinal;
            this.limiteMax = limiteMax;
            this.paresAmigaveis = paresAmigaveis;
            this.amigaveisJaEncontrados = amigaveisJaEncontrados;
        }
        
        @Override
        public void run() {
            for (long i = numInicial; i <= numFinal; i++) {
                if (amigaveisJaEncontrados.contains(i)) {
                    continue;
                }
                long divisores = somaDivisores(i);
                if (divisores > i && divisores <= limiteMax) {
                    if (amizadeReciproca(i, divisores)) {
                        paresAmigaveis.put(i, divisores);
                        amigaveisJaEncontrados.add(i);
                        amigaveisJaEncontrados.add(divisores);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        Scanner leitor = new Scanner(System.in);

        System.out.print("Digite o número máximo para a verificação: ");
        long numMax = leitor.nextLong();

        System.out.print("Digite a quantidade de Threads a ser utilizada para cada busca: ");
        int numThreads = leitor.nextInt();
        leitor.close();

        ConcurrentSkipListSet<Long> numPerfeito = new ConcurrentSkipListSet<>();
        Map<Long, Long> paresAmigaveis = new ConcurrentSkipListMap<>();
        Set<Long> amigaveisJaEncontrados = java.util.concurrent.ConcurrentHashMap.newKeySet();

        System.out.println("Iniciando busca por números perfeitos e amigáveis (em paralelo)...");
        
        List<Thread> todasAsThreads = new java.util.ArrayList<>();
        long tamanhoIntervalo = numMax / numThreads;

        // =================================================================================
        // AJUSTE  'threadBuscaPerfeito'
        // Mantive o loop para criar as threads, preservando a estrutura
        // original. No entanto, passei um ID ('i') e o limite máximo ('numMax')
        // para o construtor, para que a nova lógica funcione corretamente (apenas thread 0 trabalha).
        // =================================================================================
        for (int i = 0; i < numThreads; i++) {
            long inicio = i * tamanhoIntervalo + 1;
            long fim = (i == numThreads - 1) ? numMax : (i + 1) * tamanhoIntervalo;
            // Passei o limite máximo e o ID da thread
            Thread t = new Thread(new threadBuscaPerfeito(inicio, fim, numMax, i, numPerfeito));
            todasAsThreads.add(t);
            t.start();
        }

        // loop para a busca de amigáveis 
        for (int i = 0; i < numThreads; i++) {
            long inicio = i * tamanhoIntervalo + 1;
            long fim = (i == numThreads - 1) ? numMax : (i + 1) * tamanhoIntervalo;
            Thread t = new Thread(new threadBuscaAmigavel(inicio, fim, numMax, paresAmigaveis, amigaveisJaEncontrados));
            todasAsThreads.add(t);
            t.start();
        }


        for (Thread thread : todasAsThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Todas as buscas foram finalizadas.");
        System.out.println("\n-- Resultados --");
        System.out.println("Números Perfeitos: " + numPerfeito);
        System.out.println("Pares de Números Amigos encontrados:");
        for (Map.Entry<Long, Long> par : paresAmigaveis.entrySet()) {
            System.out.println("(" + par.getKey() + ", " + par.getValue() + ")");
        }
    }
}