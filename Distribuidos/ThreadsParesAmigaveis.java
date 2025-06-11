import java.math.BigInteger;

//Thread que percorre um subintervalo de p e verifica quais pares são amigáveis
public class ThreadParesAmigaveis implements Runnable{
    private BigInteger inicio; //Início do intervalo que será verificado
    private BigInteger fim;    //fim do intervalo que será verificado
    
    //Início e fim do subintervalo p a ser verificado por esta thread
    //que vai ser definidio no cliente com base no intervalo completo
    public ThreadParesAmigaveis(BigInteger inicio, BigInteger fim) { //Construtor da thread
        this.inicio = inicio; 
        this.fim = fim;       
    }
    
    public void run() {
        BigInteger soma = BigInteger.ZERO;    //soma dos divisores de 'i' (um dos números do intervalo) 
        BigInteger somaPar = BigInteger.ZERO; //soma dos divisores do resultado da soma dos divisores da variável 'soma'
        
        //percorre todos os núemros do intervalo
        for(BigInteger i = inicio; i.compareTo(fim) <= 0; i = i.add(BigInteger.ONE)) {
            
            //cálculo da soma dos divisores de i, até i/2
            soma = somaDivisores(i);
            
            if(soma.equals(i)) { //caso seja um número perfeito zera a soma
                soma = BigInteger.ZERO;
            } else {
                //calculo da soma dos divisores de 'soma'
                somaPar = somaDivisores(soma);

                //verifica se é um par amigável
                if (somaPar.equals(i)) {
                    System.out.println("(" + i + "," + soma + ")");
                }
            }
        }
        
    }
    
    // Método auxiliar para calcular a soma dos divisores próprios de um número
    private BigInteger somaDivisores(BigInteger n) {
        BigInteger soma = BigInteger.ZERO;    //inicializa a soma como zero
        BigInteger limite = n.divide(BigInteger.TWO); //define o limite dos divisores é a metade do número
        for (BigInteger j = BigInteger.ONE; j.compareTo(limite) <= 0; j = j.add(BigInteger.ONE)) {
            if (n.mod(j).equals(BigInteger.ZERO)) {
                soma = soma.add(j); //soma na variável caso o resto da divisão seja zero
            }
        }
        return soma; //retorna a soma dos divisores
    }
}
