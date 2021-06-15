# JogoDaVelha
Este projeto foi feito como atividade avaliativa da matéria de Redes de Computadores ministrada pelo professor Jorge Lima na Universidade Estadual de Santa Cruz.

O programa consiste em um jogo da velha onde os jogadores se comunicam com o auxílio do servidor, o protocolo de transporte utilizado para comunicação foi o TCP pois garante a entrega de todos os dados que estão sendo enviados, logo, ao fazer uma jogada é garantido que a resposta a essa escolha será visualizada por ambos jogadores.
## 1. Cliente 
Na pasta cliente se encontra a classe cliente e a classe jogada.\
Na classe cliente é feita a interface do jogo. Cada posição do jogo da velha é indicada por 	um botão, de forma que sejam 9 botões alinhados 3 a 3.\
A classe cliente possui cinco métodos: 
### 1.1  Connect
Cria um objeto Socket que servirá para comunicação entre o cliente e o servidor.\
Salva os dados da comunicação e informa se houve a conexão.
### 1.2 SendMessage
Envia os dados do cliente para o servidor com o nome do jogador, o símbolo escolhido e a 	posição de sua jogada.
### 1.3 Listen
Interpreta a mensagem recebida do Servidor.
Podem ser recebidas 10 tipos de mensagens
#### 1.3.1 Sair – Quando a conexão será encerrada.
#### 1.3.2 Aguarde – Quando o adversário ainda não se conectou.
#### 1.3.3 Reinicio_x – Quando a escolha de símbolos entra em conflito. Nesse caso o 		adversário já escolheu o símbolo X, portanto o símbolo do jogador será O.
#### 1.3.4 Reinicio_o – Quando a escolha de símbolos entra em conflito. Nesse caso o 		adversário já escolheu o símbolo O, portanto o símbolo do jogador será X.
#### 1.3.5 Pronto – Quando a conexão entre os dois clientes e o servidor é um sucesso, 		portanto o jogo pode começar.
#### 1.3.6 Xganhou – Informa que o jogador que escolheu o símbolo X ganhou.
#### 1.3.7 Oganhou – Informa que o jogador que escolheu o símbolo O ganhou.
#### 1.3.8 Empate – Informa que não houve ganhador.
#### 1.3.9 Não é sua vez! - Quando o jogador tenta jogar na vez do adversário.
#### 1.3.10 Demais mensagens são informações da jogada feita pelo adversário.
### 1.4 Exit
Método responsável por informar a desconexão do cliente com o servidor.
### 1.5 Clear 
Método responsável por definir todos os botões para o valor nulo, indicando que o jogo está no início.
## 2. Servidor
Na pasta servidor encontra-se a classe servidor.\
Na classe servidor são salvas as mensagens recebidas para definir uma conexão com os clientes.\
A classe também é responsável pela criação do socket com a porta para a comunicação entre os clientes, pela definição da lógica utilizada e análise das jogadas feitas.\
Antes de se iniciar o jogo a classe verifica a conexão com os clientes e não permite a conexão de mais que dois clientes e define todos os botões dos clientes como nulos, não selecionados.\
A classe servidor possui cinco métodos:
### 2.1 Run
Este método é responsável por instanciar e interpretar as mensagens dos clientes e salvar em 	uma lista para futuras consultas.\
Podem ser recebidas 3 tipos de mensagens:
#### 2.1.1 X – Indica que um cliente selecionou o símbolo X, se o adversário já se conectou e escolheu o mesmo símbolo, envia a mensagem ‘Reinicio_x’ para o cliente. Se o adversário ainda não se conectou envia a mensagem ‘Aguarde’ para o cliente. Caso os símbolos estejam definidos, envia para todos a mensagem ‘Pronto’. 
#### 2.1.2 O – Indica que um cliente selecionou o símbolo O, se o adversário já se conectou e escolheu o mesmo símbolo, envia a mensagem ‘Reinicio_x’ para o cliente. Se o adversário ainda não se conectou envia a mensagem ‘Aguarde’ para o cliente. Caso os símbolos estejam definidos, envia para todos a mensagem ‘Pronto’. 
#### 2.1.3 Demais mensagens são informações da jogada feita, a partir de tais mensagens o servidor verifica se a posição escolhida já foi selecionada, caso não tenha sido, 			verifica se o jogador está tentando fazer duas jogadas seguidas, em caso afirmativo envia a mensagem ‘Não é sua vez!’ para o mesmo.
### 2.2 SendToLast
Este método é responsável por enviar uma mensagem para o último jogador conectado.
### 2.3 SendToAll
Este método é responsável por enviar uma mensagem para todos os jogadores conectados.
### 2.4 CheckWinner
Método responsável pela verificação do ganhador.\
Verifica as possibilidades de vitória para ambos símbolos, caso haja um vencedor envia uma 	mensagem informando o ganhador.\
Verifica se os nove botões já foram selecionados, caso positivo e nenhum vencedor ainda 	definido, determina empate.
### 2.5 ClearClicks
Método responsável por limpar as jogadas, determinando todos os botões como não 	selecionados.
