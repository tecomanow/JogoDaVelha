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

/**
 *
 * @author MateusR
 */
public class Client extends JFrame{
    
    JFrame jframe = new JFrame();

    /**
     * @param args the command line arguments
     */
    
    private static OutputStream outputStream;
    //private static ObjectOutputStream objectOutputStream;
    
    // Mostrar o símbolo escolhido pelo jogador
    private boolean x = false;
    private boolean o = false;
    private String last_symbol = "";
    
    private Socket socket;
    private OutputStream ou ;
    private Writer ouw;
    private BufferedWriter bfw;
    
    // Mostra o jogador da janela que foi selecionada
    private JLabel atual_jogador = new JLabel("");
    private String nome_jogador;
    private String simbolo;
    
    JButton btn_x = new JButton("X");
    JButton btn_o = new JButton("O");
    private JButton[] buttons = new JButton[9];
    private JLabel ultimo_jogador = new JLabel("Nenhum jogador");
    
    private JLabel placarX = new JLabel("X: 0");  
    private JLabel placarO = new JLabel("O: 0");
    /*private JLabel placarX_valor = new JLabel("2");
    private JLabel placarO_valor = new JLabel("3");*/
    private JLabel lblMessageAguarde = new JLabel("Aguardando próximo jogador!");
    
    /*CRIAR VARIÁVEL BOOLEAN PARA VERIFICAR SE O OUTRO USER JA SE CONECTOU
    ESSA VARIAVEL STARTA COM FALSE, E MUDA PARA TRUE QUANDO A MSG RECEBIDA FOR "PRONTO"
    
    FAZER A VERIFICAÇÃO AO CLICAR COM O BOTÃO DO JOGO, ANTES DE ENVIAR A MENSAGEM
    COM A POSIÇÃO DO CLICK. ISSO IMPEDE DE JOGAR ANTES DO SEGUNDO JOGADOR COMEÇAR*/
    
    /*FALTA VERIFICAR SE O X OU SE O JÁ FOI ESCULHIDO, PARA NÃO PERMITIR QUE O OUTRO ESCOLHA TAMBÉM*/
    
    public Client(){
        
        JLabel lblMessage = new JLabel("Olá, insira seu nome :D");
        lblMessage.setForeground(Color.BLACK);
        JTextField txtNome = new JTextField("");
        
        btn_x.setForeground(Color.RED);
        btn_o.setForeground(Color.BLUE);
        //JTextField txtSimbolo = new JTextField("");
//        JButton btn_x = new JButton("X");
//        JButton btn_o = new JButton("O");
//        
   
//        btn_x.setBackground(Color.CYAN);
        
//        if(x){
//            btn_x.setEnabled(false);
//        }else if(o){
//           btn_o.setEnabled(false); 
//        }
//        
        btn_x.addActionListener((ActionEvent ae) -> {
            System.out.println(btn_x.getText());
            last_symbol = btn_x.getText();   // Guarda o último símbolo selecionado
            btn_x.setEnabled(false);
            btn_o.setEnabled(true);
//            if(!o){
//                btn_o.setEnabled(true);
//            }            
            this.simbolo = "X";
        });
        
        btn_o.addActionListener(((ae) -> {
            System.out.println(btn_o.getText());
            last_symbol = btn_o.getText();  // Guarda o último símbolo selecionado
            btn_o.setEnabled(false);
            btn_x.setEnabled(true);
//            if(!x){
//                btn_x.setEnabled(true);
//            }
            this.simbolo = "O";
        }));
        
        Object[] texts = {lblMessage, txtNome, btn_x, btn_o };
        JOptionPane.showMessageDialog(null, texts);
        this.nome_jogador = txtNome.getText();
        
        // Mostra o jogador atual e o símbolo que ele escolheu
        
        atual_jogador.setText(this.nome_jogador + " - " + last_symbol);
        //this.simbolo = "X";
        //System.out.println(txtNome.getText());
        
        setVisible(true);
        setTitle("Jogo da Velha - Projeto Redes I");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setBounds(300,300,800,500);
 
        // Label para mostrar o jogador + símbolo
        JLabel textAtual = new JLabel("Jogador:");
        textAtual.setForeground(Color.BLACK);
        
        if(last_symbol.equals("X")){
            atual_jogador.setForeground(Color.RED);
        }else{
            atual_jogador.setForeground(Color.BLUE);
        }
        add(textAtual);
        add(atual_jogador);
        textAtual.setBounds(400,50,100,30);
        atual_jogador.setBounds(400,75,100,30);
        
        JLabel textJogador = new JLabel("Ultimo jogador:");
        textJogador.setForeground(Color.BLACK);
        ultimo_jogador.setForeground(Color.GRAY);
        add(textJogador);
        add(ultimo_jogador);
        textJogador.setBounds(400,125,100,30);
        ultimo_jogador.setBounds(400, 150, 100, 30);
        
        JLabel textPlacar = new JLabel("Placar:");
        textPlacar.setForeground(Color.BLACK);
        placarX.setForeground(Color.RED);
        placarO.setForeground(Color.BLUE);
        add(textPlacar);
        textPlacar.setBounds(400, 200, 100, 30);
        
        
        add(placarX);
        placarX.setBounds(400, 225, 100, 30);
                 
        
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
    
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // TODO code application logic here
        
        Client c = new Client();
        c.connect();
        c.listen();
        
    }
    
    private void connect(){
        
        try {
        
            socket = new Socket("127.0.0.1", 1478);
            outputStream = socket.getOutputStream();
            ouw = new OutputStreamWriter(outputStream);
            bfw = new BufferedWriter(ouw);
            System.out.println("Conectou");
            
            ouw.write(simbolo + "\r");
            bfw.flush();
    
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        
    }
    
    public void sendMessage(String msg, int position) throws IOException{
        
        String jogador_atual = nome_jogador;
        String simbolo_atual = simbolo;
        String posicao = String.valueOf(position);
        
        String enviar = jogador_atual + "_" + simbolo_atual + "_" + posicao;
        
        bfw.write(enviar + "\r");
        bfw.flush();      
        
    }
    
    public void listen() throws IOException, ClassNotFoundException{

        InputStream in = socket.getInputStream();
        InputStreamReader inr = new InputStreamReader(in);
        BufferedReader bfr = new BufferedReader(inr);

        String msg = "";
        String[] args;       
        
        while(!"Sair".equalsIgnoreCase(msg)){
            
            if(bfr.ready()){
                msg = bfr.readLine();
                
                //System.out.println(msg);

                if(msg.equals("Aguarde")){
                    JLabel lblMessage = new JLabel("Aguardando próximo jogador!");
                    JOptionPane.showMessageDialog(null, lblMessage);
                    lblMessageAguarde.setBounds(100, 300, 200, 100);
                    add(lblMessageAguarde);                                                   // =-=-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-
                }else if(msg.equals("Reinicio_x")){                                           // Caso o 2º jogador tenha escolhido X mesmo o 1º já tendo escolhido
                    JLabel lblMessage = new JLabel("Símbolos iguais, seu símbolo será: O");   //
                    JOptionPane.showMessageDialog(null, lblMessage);                          //
                    simbolo = "O";                                                            // Troca o símbolo para o O   
                    atual_jogador.setText(this.nome_jogador + " - " + simbolo);               // Altera o símbolo na interface
                    atual_jogador.setForeground(Color.BLUE);
                    clear();                                                                  // =-=-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-
                }else if(msg.equals("Reinicio_o")){                                           // Caso o 2º jogador tenha escolhido O mesmo o 1º já tendo escolhido
                    JLabel lblMessage = new JLabel("Símbolos iguais, seu símbolo será: X");   //
                    JOptionPane.showMessageDialog(null, lblMessage);                          //
                    simbolo = "X";                                                            // Troca o símbolo para o X   
                    atual_jogador.setText(this.nome_jogador + " - " + simbolo);               // Altera o símbolo na interface
                    atual_jogador.setForeground(Color.RED);
                    clear();                                                                  // =-=-=-=--=-=-=-=-=-=-=-=-=-=-=-=-=--=-=-=-=-=-=-=-=-=-=-=-
                }else if(msg.equals("Pronto")){
                    lblMessageAguarde.setText("");
                }else if(msg.contains("Xganhou")){
                    String[] splited = msg.split("_");
                    String placarx = "X: " + splited[1];
                    //System.out.println(msg);
                    this.placarX.setText(placarx);
                    JLabel lblMessage = new JLabel("X ganhou");
                    JOptionPane.showMessageDialog(null, lblMessage);
                    ultimo_jogador.setText("Nenhum jogador");
                    ultimo_jogador.setForeground(Color.GRAY);
                    clear();
                }else if(msg.contains("Oganhou")){
                    String[] splited = msg.split("_");
                    String placaro = "O: " + splited[1];
                    this.placarO.setText(placaro);
                    JLabel lblMessage = new JLabel("O ganhou");
                    JOptionPane.showMessageDialog(null, lblMessage);
                    ultimo_jogador.setText("Nenhum jogador");
                    ultimo_jogador.setForeground(Color.GRAY);
                    clear();            
                }else if(msg.equals("Empate")){
                    JLabel lblMessage = new JLabel(msg);
                    JOptionPane.showMessageDialog(null, lblMessage);
                    clear();
                }else if(msg.equals("Não é sua vez!")){
                    JLabel lblMessage = new JLabel("Não é a sua vez. Aguarde!");
                    JOptionPane.showMessageDialog(null, lblMessage);
                    System.out.println(msg); 
                }else{

                    args = msg.split("_");
                    int positionButton = Integer.parseInt(args[2]);
                    String jogador = args[0];
                    String simboloo = args[1];            

                    buttons[positionButton].setText(simboloo);
                    
                    System.out.println(simboloo);
                    if(simboloo.equals("X")){
                        ultimo_jogador.setForeground(Color.RED);
                    }else{
                        ultimo_jogador.setForeground(Color.BLUE);
                    }
                    ultimo_jogador.setText(jogador);
                }
                
                if(msg.equals("Sair")){

                }
               }else{
                 //texto.append(msg+"\r\n");
               }
        
        }
         
    }
    
    public void exit() throws IOException{

        sendMessage("Sair", 0);
        bfw.close();
        ouw.close();
        ou.close();
        socket.close();
    }

    private void clear() {
        for(int i = 0; i < 9; i++){
            buttons[i].setText("");            
        }
    }
    
}
