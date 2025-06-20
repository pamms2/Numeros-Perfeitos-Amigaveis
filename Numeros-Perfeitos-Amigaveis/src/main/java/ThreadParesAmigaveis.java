import java.util.Map;
import java.util.Set;

// Thread que percorre um subintervalo e verifica quais pares são amigáveis
public class ThreadParesAmigaveis implements Runnable {

    private int inicio;
    private int fim;
    private int limite;

    // Estruturas compartilhadas entre threads
    private Map<Integer, Integer> paresAmigaveis;
    private Set<Integer> jaEncontrados;

    public ThreadParesAmigaveis(int inicio, int fim,Map<Integer, Integer> paresAmigaveis, Set<Integer> jaEncontrados, int limite) {
        this.inicio = inicio;
        this.fim = fim;
        this.paresAmigaveis = paresAmigaveis;
        this.jaEncontrados = jaEncontrados;
        this.limite = limite;
    }

    @Override
    public void run() {
        for (int i = inicio; i <= fim; i++) {

            synchronized (jaEncontrados) {
                if (jaEncontrados.contains(i)) continue;
            }

            int soma = somaDivisores(i);
            
            if(soma != i && soma <= limite) {
                int somaPar = somaDivisores(soma);
                if (somaPar == i) {
                    synchronized (paresAmigaveis) {
                        paresAmigaveis.put(i, soma);
                    }

                    synchronized (jaEncontrados) {
                        jaEncontrados.add(i);
                        jaEncontrados.add(soma);
                    }

                    System.out.println("(" + i + ", " + soma + ")");
                }
            }
        }
    }

    // Método auxiliar que calcula a soma dos divisores próprios de um número
    private int somaDivisores(int num) {
        if (num <= 1) return 0;

        int soma = 1;
        int raiz = (int) Math.sqrt(num);

        for (int i = 2; i <= raiz; i++) {
            if (num % i == 0) {
                soma += i;
                int outro = num / i;
                if (outro != i) {
                    soma += outro;
                }
            }
        }

        return soma;
    }
}
