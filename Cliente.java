package maria.numeros.perfeitos.amigaveis;

/**
 *
 * @author Maria Clara Jesus
 */

// Cliente que recebe intervalo de p do servidor
 //e paraleliza a verificação de números perfeitos com n threads.

import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Cliente {
    public static void main(String[] args) {
        String servidor = "localhost";
        int porta = 20000;
        int thread = 4; //mudar para a quantidade adequada 

        try (Socket socket = new Socket(servidor, porta)) {
            System.out.println("Conectado ao servidor.");

            DataInputStream entrada = new DataInputStream(socket.getInputStream());
            
            BigInteger inicioPerfeito = new BigInteger(entrada.readUTF());
            BigInteger fimPerfeito = new BigInteger(entrada.readUTF());
            //os valores para pares amigáveis também são enviados, mas serão ignorados aqui
            entrada.readUTF(); //ignora inicioAmigo
            entrada.readUTF(); //ignora fimAmigo

            System.out.println("Intervalo recebido: " + inicioPerfeito + " até " + fimPerfeito);

            //cria um pool com n threads
            ExecutorService pool = Executors.newFixedThreadPool(thread);

            //divide o intervalo em n blocos
            BigInteger total = fimPerfeito.subtract(inicioPerfeito).add(BigInteger.ONE);
            BigInteger tamanhoBloco = total.divide(BigInteger.valueOf(thread));

            for (int i = 0; i < thread; i++) {
                BigInteger inicioBloco = inicioPerfeito.add(tamanhoBloco.multiply(BigInteger.valueOf(i)));
                BigInteger fimBloco;

                //último bloco pega até o final exato
                if (i == (thread - 1)) {
                    fimBloco = fimPerfeito;
                } else {
                    fimBloco = inicioBloco.add(tamanhoBloco).subtract(BigInteger.ONE);
                }

                //executa a thread com o subintervalo
                pool.execute(new ThreadNumerosPerfeitos(inicioBloco, fimBloco));
            }

            pool.shutdown(); //fecha o pool após envio das tarefas

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
