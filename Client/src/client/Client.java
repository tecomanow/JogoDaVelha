/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import com.sun.security.ntlm.Server;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;


// Classe principal
public class Client extends JFrame{
    
    // Instância do JFrame
    // Responsável por criar a tela em que iremos adicionar os componentes
    JFrame jframe = new JFrame();
    
    // Variável outputstream
    // Usada para gerir o fluxo de saída
    private static OutputStream outputStream;
  
    // variáveis utilizadas para mostrar o símbolo escolhido pelo jogador
    private final boolean x = false;
    private final boolean o = false;
    private String last_symbol = "";
    
    // Variáveis para conexão com o servidor
    private Socket socket;
    private OutputStream ou ;
    private Writer ouw;
    private BufferedWriter bfw;
    
    // Mostra o jogador da janela que foi selecionada
    private final JLabel atual_jogador = new JLabel("");
    private final String nome_jogador;
    private String simbolo;
    
    // Botões com as escolhas possíveis para os jogadores
    JButton btn_x = new JButton("X");
    JButton btn_o = new JButton("O");
    private final JButton[] buttons = new JButton[9];
    
    // Label para mostrar o último jogador que jogou
    private final JLabel ultimo_jogador = new JLabel("Nenhum jogador");
    
    
    // Placar com as pontuações dos jogadores
    private final JLabel placarX = new JLabel("X: 0");  
    private final JLabel placarO = new JLabel("O: 0");

    // Label de espera enquanto o 2º jogador não se conecta
    private final JLabel lblMessageAguarde = new JLabel("Aguardando próximo jogador!");
    
    
    // Classe client
    public Client(){
        
        // Label para recepção do jogador
        JLabel lblMessage = new JLabel("Olá, insira seu nome :D");
        lblMessage.setForeground(Color.BLACK);
        JTextField txtNome = new JTextField("");
        
        // Definindo cores para as possibilidades do jogador
        btn_x.setForeground(Color.RED);
        btn_o.setForeground(Color.BLUE);

        // Listener para o botão X
        btn_x.addActionListener((ActionEvent ae) -> {
            System.out.println(btn_x.getText());
            last_symbol = btn_x.getText();             // Guarda o último símbolo selecionado
            btn_x.setEnabled(false);                   // Desativa o botão X após escolher ele
            btn_o.setEnabled(true);                    // Ativa o botão O - Serve para não deixar ambos os botões desabilitados
            
            this.simbolo = "X";                        // Armazena o símbolo do cliente
        });
        
        
        // Listener para o botão O
        btn_o.addActionListener(((ae) -> {
            System.out.println(btn_o.getText());
            last_symbol = btn_o.getText();             // Guarda o último símbolo selecionado
            btn_o.setEnabled(false);                   // Desativa o botão O após escolher ele
            btn_x.setEnabled(true);                    // Ativa o botão X - Serve para não deixar ambos os botões desabilitados

            this.simbolo = "O";                        // Armazena o símbolo do cliente
        }));
        
        
        Object[] texts = {lblMessage, txtNome, btn_x, btn_o }; // Armazena os componentes da tela inicial
        JOptionPane.showMessageDialog(null, texts);            // Insere os componentes na tela e mostra eles
        this.nome_jogador = txtNome.getText();                 // Armazena o nome do joador
        
        atual_jogador.setText(this.nome_jogador + " - " + last_symbol); // Mostra o jogador atual e o símbolo que ele escolheu
        
        
        // Métodos aplicados à classe
        setVisible(true);                            // Torna vísivel
        setTitle("Jogo da Velha - Projeto Redes I"); // Aidiona o título
        setDefaultCloseOperation(EXIT_ON_CLOSE);     // Procedimento ao fechar
        setLayout(null);                             // Nenhum layout pré-definido
        setBounds(300,300,800,500);                  // Tamanho da janela
 
        // Label para mostrar o jogador + símbolo
        JLabel textAtual = new JLabel("Jogador:");
        textAtual.setForeground(Color.BLACK);
        
        // Mostra o último símbolo com a cor dependendo do símbolo
        if(last_symbol.equals("X")){
            atual_jogador.setForeground(Color.RED);
        }else{
            atual_jogador.setForeground(Color.BLUE);
        }
        
        // Mostra informações referentes ao jogador da janela atual
        add(textAtual);
        add(atual_jogador);
        textAtual.setBounds(400,50,100,30);
        atual_jogador.setBounds(400,75,100,30);
        
        // Mostra informações referentes ao último jogador
        JLabel textJogador = new JLabel("Ultimo jogador:");
        textJogador.setForeground(Color.BLACK);
        ultimo_jogador.setForeground(Color.GRAY);
        add(textJogador);
        add(ultimo_jogador);
        textJogador.setBounds(400,125,100,30);
        ultimo_jogador.setBounds(400, 150, 100, 30);
        
        // Mostra o placar
        JLabel textPlacar = new JLabel("Placar:");
        textPlacar.setForeground(Color.BLACK);
        placarX.setForeground(Color.RED);
        placarO.setForeground(Color.BLUE);
        add(textPlacar);
        textPlacar.setBounds(400, 200, 100, 30);
        
        // Placar do X
        add(placarX);
        placarX.setBounds(400, 225, 100, 30);
                 
        // Placar do O
        add(placarO);
        placarO.setBounds(480, 225, 100, 30);

        //Variável para definir a posição dos botões na tela        
        int position_buttons = 0;
        
        //For percorre a linha para preencher com botões
        for(int i = 0; i < 3; i++){
            //Para cada linha, preenche uma coluna com botões
            for(int j = 0 ; j < 3; j++){
                buttons[position_buttons] = new JButton();
                buttons[position_buttons].setFont(new Font("Arial", Font.BOLD, 40));
                buttons[position_buttons].setBounds((100 * i) + 20, (100 * j) + 20, 95, 95);
                add(buttons[position_buttons]);
                position_buttons++;
            }
        }
        
        //Abaixo seta os eventos de clicks, onde envia a posição do botão clicado para o servidor
        buttons[0].addActionListener((ActionEvent ae) -> {
            try {
                sendMessage("0", 0);
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        buttons[1].addActionListener((ActionEvent ae) -> {
            try {
                sendMessage("1", 1);
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        buttons[2].addActionListener((ActionEvent ae) -> {
            try {
                sendMessage("2", 2);
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        buttons[3].addActionListener((ActionEvent ae) -> {
            try {
                sendMessage("3", 3);
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        buttons[4].addActionListener((ActionEvent ae) -> {
            try {
                sendMessage("4", 4);
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        buttons[5].addActionListener((ActionEvent ae) -> {
            try {
                sendMessage("5", 5);
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        buttons[6].addActionListener((ActionEvent ae) -> {
            try {
                sendMessage("6", 6);
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        buttons[7].addActionListener((ActionEvent ae) -> {
            try {
                sendMessage("7", 7);
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        buttons[8].addActionListener((ActionEvent ae) -> {
            try {
                sendMessage("8", 8);
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
    }
    
    // Funçao main
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        //Instancia um cliente e chama os métodos de conectar e de listener
        Client c = new Client();
        c.connect();
        c.listen();
        
    }
    
    // Função de conexão
    private void connect(){
        
                                                        // Tenta conectar
        try {
        
            socket = new Socket("127.0.0.1", 1478);     // Cria Socket no localhost, porta 1478
            outputStream = socket.getOutputStream();    // Pega o fluxo de saída do socket e armazena
            ouw = new OutputStreamWriter(outputStream); // Adiciona o fluxo de saída do socket a uma ponte de fluxo de caracteres
            bfw = new BufferedWriter(ouw);              // Armazena esses caracteres em um Buffer de escrita                

            ouw.write(simbolo + "\r");                  // Informa o símbolo
            bfw.flush();                                // Envia o conteúdo do buffer ao servidor
    
        } catch (IOException e) {                       // Caso não consiga mostra exceção
            System.out.println(e.getMessage());
        }
        
    }
    
    
    // Função para envio de mensagem ao servidor
    // Recebe a mensagem e a posição que o jogador clicou na matriz 3x3 do jogo da velha
    public void sendMessage(String msg, int position) throws IOException{
        
        // Informações referentes ao jogador atual
        String jogador_atual = nome_jogador;
        String simbolo_atual = simbolo;
        String posicao = String.valueOf(position);
        
        // Concatena essas informações em uma string, separados por um underline
        String enviar = jogador_atual + "_" + simbolo_atual + "_" + posicao;
        
        // Escreve no buffer e envia ao servidor
        bfw.write(enviar + "\r");
        bfw.flush();      
        
    }
    
    // Função para recebimento de mensaggens do servidor
    public void listen() throws IOException, ClassNotFoundException{
            
        // Variáveis de fluxo de dados e buffer para leitura
        InputStream in = socket.getInputStream();
        InputStreamReader inr = new InputStreamReader(in);
        BufferedReader bfr = new BufferedReader(inr);

        // Conteúdo da mensagem
        String msg = "";
        String[] args;       
        
        // Enquanto o conteúdo da mensagem não for sair da aplicação
        while(!"Sair".equalsIgnoreCase(msg)){
            
            // Checa se o buffer está pronto para leitura
            if(bfr.ready()){
                // Caso esteja, lê uma linha do buffer
                msg = bfr.readLine();               
                                                                                              // =-=-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-
                if(msg.equals("Aguarde")){                                                    // Caso o segundo jogador ainda não tenha entrado
                    JLabel lblMessage = new JLabel("Aguardando próximo jogador!");            // Informação a ser mostrado ao primeiro jogador sobre isso
                    JOptionPane.showMessageDialog(null, lblMessage);                          // Posiciona na tela essa mensagem
                    lblMessageAguarde.setBounds(100, 300, 200, 100);                          // Adiciona na tela
                    add(lblMessageAguarde);                                                   // =-=-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-
                }else if(msg.equals("Reinicio_x")){                                           // Caso o 2º jogador tenha escolhido X mesmo o 1º já tendo escolhido
                    JLabel lblMessage = new JLabel("Símbolos iguais, seu símbolo será: O");   //
                    JOptionPane.showMessageDialog(null, lblMessage);                          //
                    simbolo = "O";                                                            // Troca o símbolo para o O   
                    atual_jogador.setText(this.nome_jogador + " - " + simbolo);               // Altera o símbolo na interface
                    atual_jogador.setForeground(Color.BLUE);                                  //
                    clear();                                                                  // =-=-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-
                }else if(msg.equals("Reinicio_o")){                                           // Caso o 2º jogador tenha escolhido O mesmo o 1º já tendo escolhido
                    JLabel lblMessage = new JLabel("Símbolos iguais, seu símbolo será: X");   //
                    JOptionPane.showMessageDialog(null, lblMessage);                          //
                    simbolo = "X";                                                            // Troca o símbolo para o X   
                    atual_jogador.setText(this.nome_jogador + " - " + simbolo);               // Altera o símbolo na interface
                    atual_jogador.setForeground(Color.RED);                                   //
                    clear();                                                                  // =-=-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-
                }else if(msg.equals("Pronto")){                                               // Se tudo ocorrer bem
                    lblMessageAguarde.setText("");                                            // =-=-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-
                }else if(msg.contains("Xganhou")){                                            // Caso o X ganhe
                    String[] splited = msg.split("_");                                        // Separa a string ao achar underline
                    String placarx = "X: " + splited[1];                                      // Pontução do Jogador com o símbolo X
                    this.placarX.setText(placarx);                                            // Setá o placar do X
                    JLabel lblMessage = new JLabel("X ganhou");                               // Prepara uma Label informando que o X ganhou
                    JOptionPane.showMessageDialog(null, lblMessage);                          // Mostra a label
                    ultimo_jogador.setText("Nenhum jogador");                                 // Informa que nenhum jogador fez a última jogada ainda (nova partida)
                    ultimo_jogador.setForeground(Color.GRAY);                                 // Seta a cor do texto como cinza
                    clear();                                                                  // =-=-=-=--=-=-=-=-=-=-=-=- Limpa a matriz 3x3  =-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-
                }else if(msg.contains("Oganhou")){                                            // Caso o O ganhe
                    String[] splited = msg.split("_");                                        // Separa a string ao achar underline
                    String placaro = "O: " + splited[1];                                      // Pontução do Jogador com o símbolo O
                    this.placarO.setText(placaro);                                            // Setá o placar do O
                    JLabel lblMessage = new JLabel("O ganhou");                               // Prepara uma Label informando que o O ganhou
                    JOptionPane.showMessageDialog(null, lblMessage);                          // Mostra a label
                    ultimo_jogador.setText("Nenhum jogador");                                 // Informa que nenhum jogador fez a última jogada ainda (nova partida)
                    ultimo_jogador.setForeground(Color.GRAY);                                 // Seta a cor do texto como cinza
                    clear();                                                                  // =-=-=-=--=-=-=-=-=-=-=-=- Limpa a matriz 3x3  =-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-
                }else if(msg.equals("Empate")){                                               // Caso dê empate
                    JLabel lblMessage = new JLabel(msg);                                      // Prepara a Label para mostrar ao jogadores
                    JOptionPane.showMessageDialog(null, lblMessage);                          // Mostra a Label
                    clear();                                                                  // =-=-=-=--=-=-=-=-=-=-=-=- Limpa a matriz 3x3  =-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-
                }else if(msg.equals("Não é sua vez!")){                                       // Caso um jogador tente jogar sem ser sua vez
                    JLabel lblMessage = new JLabel("Não é a sua vez. Aguarde!");              // Prepara a Label com a mensagem de aviso
                    JOptionPane.showMessageDialog(null, lblMessage);                          // Mostra a Label
                }else{                                                                        // =-=-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-
                    args = msg.split("_");                                                    // Separa a string ao achar underline
                    int positionButton = Integer.parseInt(args[2]);                           // Recebe a posição em que o usuário clicou
                    String jogador = args[0];                                                 // Recebe o jogador
                    String simboloo = args[1];                                                // Recebe o símbolo do jogador
                                                                                              //
                    buttons[positionButton].setText(simboloo);                                // Seta o símbolo do jogador na posição da matriz 3x3 que ele escolheu
                                                                                              //
                    if(simboloo.equals("X")){                                                 // Se o símbolo for X 
                        ultimo_jogador.setForeground(Color.RED);                              // Mostra em vermelho
                    }else{                                                                    // Se for O
                        ultimo_jogador.setForeground(Color.BLUE);                             // Mostra em azul
                    }                                                                         //
                    ultimo_jogador.setText(jogador);                                          // Informa o último jogador a jogar
                }
            }
        }
         
    }
    
    // Método para saída do jogador
    public void exit() throws IOException{
        
        // Fecha os métodos
        sendMessage("Sair", 0);
        bfw.close(); 
        ouw.close();
        ou.close();
        socket.close();
    }

    // Função auxiliar para limpar da matriz 3x3 do jogo as jogadas antigas
    private void clear() {
        for(int i = 0; i < 9; i++){
            buttons[i].setText("");            
        }
    }
    
}
