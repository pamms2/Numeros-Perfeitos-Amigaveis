package maria.numeros.perfeitos.amigaveis;

/**
 *
 * @author Maria Clara Jesus
 */

import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Servidor {
    private static int porta = 20000;
    private static BigInteger intervaloPerfeito = BigInteger.valueOf(10000);
    private static BigInteger intervaloAmigo = BigInteger.valueOf(10000);
    private static List<Socket> clientes = new ArrayList<>();
    
    public static void main(String[] args) {
        try (ServerSocket servidor = new ServerSocket(porta)) {
            System.out.println("Servidor aguardando conexao...");

            // Aguarda o primeiro cliente indefinidamente
            Socket primeiraConexao = servidor.accept();
            synchronized (clientes) {
                clientes.add(primeiraConexao);
            }
            System.out.println("Primeiro cliente conectado: " + primeiraConexao.getInetAddress());

            //espera até 3 segundos para mais conexões
            long tempoLimite = System.currentTimeMillis() + 3000;

            while (System.currentTimeMillis() < tempoLimite) {
                servidor.setSoTimeout((int) (tempoLimite - System.currentTimeMillis()));
                try {
                    Socket conexaoAdicional = servidor.accept();
                    synchronized (clientes) {
                        clientes.add(conexaoAdicional);
                    }
                    System.out.println("Outro cliente conectado: " + conexaoAdicional.getInetAddress());
                } catch (IOException e) {
                    break;
                }
            }

            //após o tempo limite ou máximo de conexões, distribui o intervalo
            distribuirIntervalos();

        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

        
    private static void distribuirIntervalos() {
        int totalClientes = clientes.size();
        BigInteger tamanhoIntervaloPerfeito = intervaloPerfeito.divide(BigInteger.valueOf(totalClientes));
        BigInteger tamanhoIntervaloAmigo = intervaloAmigo.divide(BigInteger.valueOf(totalClientes));

        for(int i = 0; i < totalClientes; i++) {
            BigInteger inicioPerfeito = tamanhoIntervaloPerfeito.multiply(BigInteger.valueOf(i));
            BigInteger inicioAmigo = tamanhoIntervaloAmigo.multiply(BigInteger.valueOf(i));
            
            BigInteger fimPerfeito;
            BigInteger fimAmigo;

            if(i == totalClientes - 1) {
                fimPerfeito = intervaloPerfeito.subtract(BigInteger.ONE);
                fimAmigo = intervaloAmigo.subtract(BigInteger.ONE);
            } else {
                fimPerfeito = inicioPerfeito.add(tamanhoIntervaloPerfeito).subtract(BigInteger.ONE);
                fimAmigo = inicioAmigo.add(tamanhoIntervaloAmigo).subtract(BigInteger.ONE);
            }

            Socket conexao = clientes.get(i);

            try {
                DataOutputStream saida = new DataOutputStream(conexao.getOutputStream());
                saida.writeUTF(inicioPerfeito.toString());
                saida.writeUTF(fimPerfeito.toString());
                saida.writeUTF(inicioAmigo.toString());
                saida.writeUTF(fimAmigo.toString());
                conexao.close();
            } catch(IOException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        clientes.clear();
    }
}
