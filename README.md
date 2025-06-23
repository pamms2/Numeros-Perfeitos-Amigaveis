# 🧮 Verificação de Números Perfeitos e Amigáveis em Intervalos Grandes

Este projeto tem como objetivo comparar a eficiência das soluções nas abordagens **sequencial**, **paralela** e **distribuída** para a verificação de **números perfeitos** e **pares de números amigáveis**, em grandes intervalos. Foi desenvolvido em linguagem **Java**, utilizando os ambientes de desenvolvimento **NetBeans** e **VS Code** para a implementação e execução.

## ✨ Números Perfeitos

Números perfeitos são aqueles cuja soma de seus divisores - exceto ele mesmo - resulta no próprio número.

**Exemplo:**  
- Número 28:
  - Divisores: 1, 2, 4, 7, 14, 28
  - Soma dos divisores (exceto ele mesmo): 1 + 2 + 4 + 7 + 14 = 28  
- Portanto, **28 é um número perfeito**.

### 📐 Aplicação de Euclides-Mersenne

Além da verificação direta por meio da soma dos divisores, também é possível utilizar o **Teorema de Euclides-Euler**, com a fórmula de **Euclides-Mersenne**, que relaciona números primos de Mersenne com números perfeitos pares, para encontrar números perfeitos.

Um **primo de Mersenne** é um número da forma:

> **Mₙ = 2ⁿ − 1**, onde **n** também é primo.

Segundo o teorema, todo primo de Mersenne gera um número perfeito par, pela fórmula:

> **N = 2ⁿ⁻¹ × (2ⁿ − 1)**

**Exemplo:**  
Se **n = 3**, temos:  
- **M₃ = 2³ − 1 = 7** (primo de Mersenne)  
- Número perfeito: **N = 2² × 7 = 28**

Essa relação permite encontrar números perfeitos de forma mais eficiente, sem o custo computacional de analisar divisores de um por um.

<br>

## 🤝 Pares de Números Amigáveis

Os números amigáveis são determinados em pares amigos, dois números são considerados **amigáveis** se a soma dos divisores - exceto ele mesmo - de cada um resulta no outro.

**Exemplo:**  
- Par (220, 284):
  - Divisores de 220: 1, 2, 4, 5, 10, 11, 20, 22, 44, 55, 110, 220
  - Soma dos divisores (exceto ele mesmo): 1 + 2 + 4 + 5 + 10 + 11 + 20 + 22 + 44 + 55 + 110 = 284
  - Divisores de 284: 1, 2, 4, 71, 142
  - Soma dos divisores (exceto ele mesmo): 1 + 2 + 4 + 71 + 142 = 220  
- Portanto, **220 e 284 são amigos**.

Embora existam teoremas que auxiliem no cálculo manual de pares, não encontrou-se uma fórmula que permita ganho de desempenho computacional para a identificação de pares amigáveis em intervalos grandes. 

<br>


## ⚙️ Implementação

Nesta seção são apresentadas as três abordagens de implementação desenvolvidas para o projeto: **Sequencial**, **Paralela** e **Distribuída**. Cada abordagem contém a lógica para identificação de **números perfeitos** e de **pares de números amigáveis**, por meio de métodos, sendo executadas com diferentes intervalos, em uma mesma máquina.

### 📏 Intervalos

Para atingir intervalos maiores na busca por números perfeitos, optou-se por **calcular** os números perfeitos em vez de realizar uma varredura direta. A estratégia adotada baseia-se na geração de **primos de Mersenne**, que originam números perfeitos.

Assim, o intervalo definido para a função de números perfeitos refere-se ao valor de **p**, que representa o **expoente primo** utilizado para gerar o primo de Mersenne (Mₚ = 2ᵖ − 1). A partir de **p**, a função calcula os números perfeitos correspondentes, possibilitando a geração de números com milhares de dígitos.

Por outro lado, a verificação de **pares de números amigáveis** requer uma varredura **número a número**, uma vez que não há fórmula que permita sua geração direta com eficiência computacional. Nesse caso, o intervalo é aplicado diretamente ao valor numérico a ser testado, sendo realizada a análise completa da soma de divisores e da reciprocidade entre os possíveis pares, mas aplicando melhorias matemáticas que proporcionaram melhor desempenho.

#### 📊 Tabela de Intervalos Utilizados nos Testes

|  | Intervalo para `p` | Intervalo para Pares Amigáveis |
|---------|------------------------------------------|----------------------------|
| Teste 1 | Até 20.000 | Até 100.000.000 |
| Teste 2 | Até 12.000 | Até 50.000.000 |
| Teste 3 | Até 10.000 | Até 30.000.000 |

--- 

### 🧵 Sequencial

A implementação sequencial realiza o processamento utilizando apenas um núcleo de CPU, com execução linear das tarefas.

#### Função de Verificação de Números Perfeitos:


#### Função de Verificação de Pares Amigáveis:  


### 🧩 Paralela

A versão paralela distribui a carga de trabalho entre múltiplas threads...

### 🌐 Distribuída

Na abordagem distribuída, o processamento é dividido entre múltiplos clientes, que se conectam a um servidor central via Sockets TCP. Cada cliente recebe um intervalo específico de trabalho e executa suas tarefas utilizando múltiplas threads internas. A distribuição dos intervalos é feita de forma exponencial, favorecendo os primeiros clientes conectados.
<br>
Embora os testes tenham sido realizados na mesma máquina, a arquitetura simula um ambiente real distribuído, com múltiplos processos independentes e comunicação em rede. 

#### Divisão de Intervalos no Servidor

#### Divisão de Subintervalos para Threads

## 📈 Análise

### 💻 Configuração da Máquina

| Componente             | Especificação                                 |
|------------------------|-----------------------------------------------|
| Processador            | Intel(R) Core(TM) i5-10210U CPU @ 1.60GHz     |
| Memória RAM            | 8,00 GB                                       |
| Sistema Operacional    | Windows 11 64 bits                            |
| Quantidade de núcleos  | 4                                             |
| Armazenamento          | 477 GB SSD                                    |

<br>

### ⚖️ Comparativo

| | Intervalo Perfeito (p) | Intervalo Amigável (n) | Sequencial | Paralela | Distribuída |
|---------|------------------------|------------------------|-----------------|-------------|----------------|
| Teste 1 | Até 20.000             | Até 100.000.000        |     2:30:00     |    47:45    |      37:08     |
| Teste 2 | Até 12.000             | Até 50.000.000         |      42:18      |    6:59     |      7:37      |
| Teste 3 | Até 10.000             | Até 30.000.000         |     19:45       |    3:18     |       3:20     |

<br>

### 📈 Teste de Escalabilidade

#### 🧩 Paralelo

| Threads | Tempo (min) |
|---------|-------------|
| 4       | 8:55        |
| 6       | 6:59        |
| 8       | 8:04        |

#### 🌐 Distribuído

| Threads - Clientes | Tempo (min) |
|--------------------|-------------|
| 2 - 2              | 8:33        |
| 2 - 3              | 7:37        |
| 3 - 2              | 7:13        |
| 2 - 4              | 6:09        |
| 4 - 2              | 6:54        |

### 📝 Visão Geral
Os testes realizados visam comparar as três abordagens distintas. Por meio deles, foi possível observar que:
- Na versão **sequencial**, todas as operaçoes são realizadas em um único fluxo de execução, o que resulta em maior tempo de processamento devido à ausÊncia de paralelismo; <br>
- A implementação **paralela** explora múltiplas threads dentro de uma mesma máquina para dividir o trabalho, permitindo a execução simultânea de várias tarefas e, consequentemente, reduzindo o tempo total de processamento; <br>
- Já a abordagem **distribuída** simula um ambiente distribuído, onde múltiplos clientes se conectam a um servidor central via sockets TCP e recebem intervalos de trabalho para processar. Como todas as execuções ocorrem em uma única máquina, cada cliente utiliza múltiplas threads internas para paralelizar seu processamento localmente. Essa combinação de distribuição entre processos independentes (clientes), paralelismo interno (threads) e divisão exponencial de intervalo de números perfeitos, permite uma divisão eficiente da carga, tornando essa abordagem mais rápida que a sequencial e, em muitos casos, até mesmo mais eficiente que a paralela tradicional.

<br>

## 📂 Estrutura dos Arquivos

- **/códigos** – Contém o código-fonte Java (sequencial, paralelo e distribuído)
- **/docs** – Documentação técnica e gráficos de desempenho
- **/outputs** – Resultados das execuções em diferentes intervalos
- **README.md** – Arquivo de descrição do projeto

## 🧱 Desafios e Soluções


## 🚀 Melhorias

- Adição de suporte a intervalos ainda maiores com uso de BigInteger.


## ✅ Conclusão


