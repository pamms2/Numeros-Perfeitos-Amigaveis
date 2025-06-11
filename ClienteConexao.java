//Lança múltiplos clientes em paralelo usando ProcessBuilder

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientesConexao {

    public static void main(String[] args) {
        int numeroDeClientes = 5; // Altere conforme o número de clientes desejado

        List<Process> processos = new ArrayList<>();

        for (int i = 1; i <= numeroDeClientes; i++) {
            try {
                ProcessBuilder pb = new ProcessBuilder(
                    "java", 
                    "-cp", 
                    "target/classes", // Se estiver usando NetBeans padrão (ou use 'target/classes' para Maven)
                    "Cliente" // Nome da classe com método main (sem .java ou .class)
                );

                pb.inheritIO(); // Herda o console do NetBeans para mostrar a saída

                Process processo = pb.start();
                processos.add(processo);

                System.out.println("Cliente " + i + " iniciado.");

                // Pequeno atraso opcional para evitar conflitos de conexão
                Thread.sleep(300);
            } catch (IOException | InterruptedException e) {
                System.err.println("Erro ao iniciar Cliente " + i + ": " + e.getMessage());
            }
        }

        System.out.println("Todos os clientes foram disparados.");
    }
}
