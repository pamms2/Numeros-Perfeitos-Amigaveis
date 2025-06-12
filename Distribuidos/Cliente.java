// Cliente que recebe intervalo de p do servidor
 //e paraleliza a verificação de números perfeitos e amigáveis com n threads.


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cliente {
    public static void main(String[] args) throws InterruptedException {
        String servidor = "localhost";
        int porta = 20000;

        int threads = 4; //2 para números perfeitos, 2 para pares amigáveis

        try (Socket socket = new Socket(servidor, porta)) {
            System.out.println("Conectado ao servidor.");

            // Recebe os intervalos enviados pelo servidor
            DataInputStream entrada = new DataInputStream(socket.getInputStream());
            BigInteger inicioPerfeito = new BigInteger(entrada.readUTF());
            BigInteger fimPerfeito = new BigInteger(entrada.readUTF());
            BigInteger inicioAmigo = new BigInteger(entrada.readUTF());
            BigInteger fimAmigo = new BigInteger(entrada.readUTF());

            System.out.println("Intervalo recebido para numeros perfeitos: " + inicioPerfeito + " ate " + fimPerfeito);
            System.out.println("Intervalo recebido para pares amigaveis: " + inicioAmigo + " ate " + fimAmigo);

            //o pool de threads
            ExecutorService pool = Executors.newFixedThreadPool(threads);

            //divide o intervalo de números perfeitos em 2 blocos
            BigInteger totalPerfeito = fimPerfeito.subtract(inicioPerfeito).add(BigInteger.ONE);
            BigInteger tamanhoBlocoPerfeito = totalPerfeito.divide(BigInteger.valueOf(2));

            for (int i = 0; i < 2; i++) {
                BigInteger inicioBloco = inicioPerfeito.add(tamanhoBlocoPerfeito.multiply(BigInteger.valueOf(i)));
                BigInteger fimBloco;
                if (i == 1) {
                    fimBloco = fimPerfeito;
                } else {
                    fimBloco = inicioBloco.add(tamanhoBlocoPerfeito).subtract(BigInteger.ONE);
                }

                pool.execute(new ThreadNumerosPerfeitos(inicioBloco, fimBloco));
            }

            //divide o intervalo de pares amigáveis em 2 blocos
            BigInteger totalAmigo = fimAmigo.subtract(inicioAmigo).add(BigInteger.ONE);
            BigInteger tamanhoBlocoAmigo = totalAmigo.divide(BigInteger.valueOf(2));

            for (int i = 0; i < 2; i++) {
                BigInteger inicioBloco = inicioAmigo.add(tamanhoBlocoAmigo.multiply(BigInteger.valueOf(i)));
                BigInteger fimBloco;
                if (i == 1) {
                    fimBloco = fimAmigo;
                } else {
                    fimBloco = inicioBloco.add(tamanhoBlocoAmigo).subtract(BigInteger.ONE);
                }

                pool.execute(new ThreadParesAmigaveis(inicioBloco, fimBloco));
            }
            
            pool.shutdown();
            
            while (!pool.isTerminated()) {
                Thread.sleep(100);
            }

            System.out.println("Cliente finalizou o processamento.");
            DataOutputStream saida = new DataOutputStream(socket.getOutputStream());

        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
