public class Sequencial {
   
    public static void main(String[] args) {
        long intervalo = 9223372036854775807L; 
        Sequencial seq = new Sequencial();
       
        System.out.println("Numeros perfeitos: ");
        seq.numeroPerfeito(intervalo);
        System.out.println("Pares amigaveis: ");
        seq.paresAmigaveis(intervalo);
    }
    
// Encontrando números primos através da fórmula de Euclides-Mersenne
    public static boolean primo(long p) { //testa se é um primo
        if (p < 2) return false;
        if (p == 2) return true;
        if (p % 2 == 0) return false;

        for (long i = 3; i < p; i += 2) {
            if (p % i == 0) {
                return false;
            }
        }
        return true;
    }
   
    //determinar o número é perfeito
    public void numeroPerfeito(long intervalo){
        
        //o maior número p que gera um perfeito que caiba na variável long é 31, mas deixei a lógica para ser aplicada a biginteger
        for (int p=2; p<=31; p++){ //testa os números até 31 para encontrar os primos 

            if (primo(p)) { //se o número for primo ->
                long mersenne = (long)Math.pow(2, p) - 1; //testa se ele é primo de mersenne 

                if (primo(mersenne)) { //se for primo de mersenne ->
                    long perfeito = (long)Math.pow(2, p - 1) * mersenne; //calcula o perfeito com base na fórmula euclides-mersenne
                    System.out.println(perfeito); 
                }
            }
        }
    }


/*    
//Verificar se o número é perfeito um a um
    public void numeroPerfeito(long intervalo) {
        long soma = 0;

        for (long i = 1; i <= intervalo; i++) { // roda todos os números do intervalo definido até o máximo de uma variável long

            for (long j = 1; j < i; j++) { // roda todos os números de 1 até o atual (com exceção do próprio), testando os divisores dele

                if ((i % j) == 0) { // testa se o número j é divisor de i (atual)
                    soma = soma + j; // se for, incrementa o valor à soma
                }
            }

            if (soma == i) { // após achar todos os divisores, se a soma é igual ao próprio número, ele é perfeito
                System.out.println(i);
            }
    
            soma = 0; // reseta a soma para o próximo número ser testado
        }
    }

*/

//Verificar se dois números são pares amigáveis
    public void paresAmigaveis (long intervalo){

        long soma = 0;
        long somaPar = 0;

        for(long i=1; i<intervalo; i++){ //roda todos os números até o intervalo definido, de tamanho long
            
            for(long j=1; j<i; j++){ //roda todos os números até i, buscando os divisores
                if((i%j)==0){
                    soma = soma + j; //soma os divisores
                }
            }

            if(soma==i){ //para excluir números perfeitos que gerasse um par (x,x)
                soma=0;
            }else{
                for(long k=1; k<soma; k++){ //faz a soma dos divisores da variável "soma" (que é a soma dos divisores de i)
                    if((soma%k)==0){
                        somaPar = somaPar + k; //soma os divisores
                    }
                }

                if(somaPar==i){ //compara se a soma dos divisores do parceiro é igual a i
                    System.out.println("("+i+","+soma+")"); //se for igual, imprime na tela o par de números amigos
                }
                soma=0;
                somaPar=0;    
            }
        }  
    }
}

