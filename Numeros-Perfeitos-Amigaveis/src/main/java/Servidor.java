import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor {
    private static int porta = 20000;               // Porta de conexão
    private static int intervaloPerfeito = 12000;   // Intervalo total para verificar números perfeitos
    private static int intervaloAmigo = 50000000;   // Intervalo total para verificar pares amigáveis
    private static List<Socket> clientes = new ArrayList<>(); // Lista de clientes conectados

    public static void main(String[] args) {
        try (ServerSocket servidor = new ServerSocket(porta)) {
            System.out.println("Servidor aguardando conexao...");

            // Aguarda o primeiro cliente indefinidamente
            Socket primeiraConexao = servidor.accept();
            synchronized (clientes) {
                clientes.add(primeiraConexao);
            }
            System.out.println("Primeiro cliente conectado: " + primeiraConexao.getInetAddress());

            // Espera até 10 segundos para mais conexões
            long tempoLimite = System.currentTimeMillis() + 10000;

            while (System.currentTimeMillis() < tempoLimite) {
                servidor.setSoTimeout((int) (tempoLimite - System.currentTimeMillis()));
                try {
                    Socket conexaoAdicional = servidor.accept();
                    synchronized (clientes) {
                        clientes.add(conexaoAdicional);
                    }
                    System.out.println("Outro cliente conectado: " + conexaoAdicional.getInetAddress());
                } catch (IOException e) {
                    break; // Se o timeout expirar, sai do loop
                }
            }

            // Após o tempo limite ou máximo de conexões, distribui o intervalo
            distribuirIntervalos();

        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Método para dividir os intervalos de verificação entre os clientes conectados   
    private static void distribuirIntervalos() {
        int totalClientes = clientes.size();

        // Calcula somatório de pesos: S = 2^0 + 2^1 + ... + 2^(n-1)
        int somaPesos = 0;
        for (int i = 0; i < totalClientes; i++) {
            somaPesos += Math.pow(2, totalClientes - i - 1);
        }

        // Gera os tamanhos dos intervalos para cada cliente (números perfeitos)
        int[] tamanhosPerfeito = new int[totalClientes];
        for (int i = 0; i < totalClientes; i++) {
            int peso = (int) Math.pow(2, totalClientes - i - 1);
            tamanhosPerfeito[i] = (int) ((double) intervaloPerfeito * peso / somaPesos);
        }

        // Divide igualmente o intervalo dos pares amigáveis
        int tamanhoIntervaloAmigo = intervaloAmigo / totalClientes;

        int inicioPerfeito = 0;

        for (int i = 0; i < totalClientes; i++) {
            int inicioAmigo = tamanhoIntervaloAmigo * i;
            int fimPerfeito = inicioPerfeito + tamanhosPerfeito[i] - 1;
            int fimAmigo = inicioAmigo + tamanhoIntervaloAmigo - 1;

            // Garante que o último cliente pegue até o final exato do intervalo
            if (i == totalClientes - 1) {
                fimPerfeito = intervaloPerfeito;
                fimAmigo = intervaloAmigo;
            }

            Socket conexao = clientes.get(i);

            try {
                DataOutputStream saida = new DataOutputStream(conexao.getOutputStream());

                // Envia intervalos como inteiros
                saida.writeInt(inicioPerfeito);
                saida.writeInt(fimPerfeito);
                saida.writeInt(inicioAmigo);
                saida.writeInt(fimAmigo);
                saida.writeInt(intervaloAmigo);

                conexao.close();
            } catch (IOException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }

            // Atualiza início do próximo intervalo de números perfeitos
            inicioPerfeito = fimPerfeito + 1;
        }

        clientes.clear();
    }
}