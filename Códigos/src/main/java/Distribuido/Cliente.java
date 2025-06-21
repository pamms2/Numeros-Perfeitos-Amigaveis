package Distribuido;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cliente {
    public static void main(String[] args) throws InterruptedException {
        String servidor = "localhost";
        int porta = 20000;
        int numThreads = 2; // Número de threads por cliente

        try (Socket socket = new Socket(servidor, porta)) {
            System.out.println("Conectado ao servidor.");
            DataInputStream entrada = new DataInputStream(socket.getInputStream());

            // Recebe intervalos do servidor
            int inicioPerfeito = entrada.readInt();
            int fimPerfeito = entrada.readInt();
            int inicioAmigo = entrada.readInt();
            int fimAmigo = entrada.readInt();
            int limite = entrada.readInt();

            System.out.println("Intervalo recebido para numeros perfeitos: " + inicioPerfeito + " ate " + fimPerfeito);
            System.out.println("Intervalo recebido para pares amigaveis: " + inicioAmigo + " ate " + fimAmigo);

            // Estruturas compartilhadas entre as threads
            Map<Integer, Integer> paresAmigaveis = new ConcurrentSkipListMap<>();
            Set<Integer> jaEncontrados = ConcurrentHashMap.newKeySet();

            // Divide o intervalo de pares amigáveis e números perfeitos entre múltiplas threads
            int intervaloAmigo = fimAmigo - inicioAmigo + 1;
            int tamanhoSubAmigo = intervaloAmigo / numThreads;
            
            int intervaloPerfeito = fimPerfeito - inicioPerfeito + 1;
            int tamanhoSubPerfeito = intervaloPerfeito / numThreads;
            
            Thread[] threadPerfeito = new Thread[numThreads];
            Thread[] threadAmigo = new Thread[numThreads];
            
            for (int i = 0; i < numThreads; i++) {
                int subInicioA = inicioAmigo + i * tamanhoSubAmigo;
                int subFimA = (i == numThreads - 1) ? fimAmigo : subInicioA + tamanhoSubAmigo - 1;
                
                int subInicioP = inicioPerfeito + i * tamanhoSubPerfeito;
                int subFimP = (i == numThreads - 1) ? fimPerfeito : subInicioP + tamanhoSubPerfeito - 1;

                threadAmigo[i] = new Thread(new ThreadParesAmigaveis(subInicioA, subFimA, paresAmigaveis, jaEncontrados, limite));
                threadPerfeito[i] = new Thread(new ThreadNumerosPerfeitos(subInicioP, subFimP));

                threadAmigo[i].start();
                threadPerfeito[i].start();
            }

            // Aguarda todas as threads terminarem
            for(Thread t : threadAmigo) {
                t.join();
            }
            
            for(Thread t : threadPerfeito) {
                t.join();
            }

            System.out.println("Cliente finalizou o processamento.");

        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
