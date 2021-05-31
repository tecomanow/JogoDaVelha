/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import com.sun.security.ntlm.Server;
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

    /**
     * @param args the command line arguments
     */
    
    private static OutputStream outputStream;
    //private static ObjectOutputStream objectOutputStream;
    
    private Socket socket;
    private OutputStream ou ;
    private Writer ouw;
    private BufferedWriter bfw;
    
    private String nome_jogador;
    private String simbolo;
    
    private JButton[] buttons = new JButton[9];
    private JLabel ultimo_jogador = new JLabel("Nenhum jogador");
    
    private JLabel placarX = new JLabel("X: 0");  
    private JLabel placarO = new JLabel("O: 0");
    /*private JLabel placarX_valor = new JLabel("2");
    private JLabel placarO_valor = new JLabel("3");*/
    
    public Client(){
        
        JLabel lblMessage = new JLabel("Olá, insira seu nome :D");
        JTextField txtNome = new JTextField("");
        //JTextField txtSimbolo = new JTextField("");
        JButton btn_x = new JButton("X");
        JButton btn_o = new JButton("O");
        
        btn_x.addActionListener((ActionEvent ae) -> {
            System.out.println(btn_x.getText());
            btn_o.setEnabled(false);
            this.simbolo = "X";
        });
        
        btn_o.addActionListener(((ae) -> {
            System.out.println(btn_o.getText());
            btn_x.setEnabled(false);
            this.simbolo = "O";
        }));
        
        Object[] texts = {lblMessage, txtNome, btn_x, btn_o };
        JOptionPane.showMessageDialog(null, texts);
        this.nome_jogador = txtNome.getText();
        //this.simbolo = "X";
        //System.out.println(txtNome.getText());
        
        setVisible(true);
        setTitle("Jogo da Velha - Projeto Redes I");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setBounds(300,300,800,500);
        
        JLabel textJogador = new JLabel("Ultimo jogador:");
        add(textJogador);
        add(ultimo_jogador);
        textJogador.setBounds(400,50,100,30);
        ultimo_jogador.setBounds(400, 75, 100, 30);
        
        JLabel textPlacar = new JLabel("Placar");
        add(textPlacar);
        textPlacar.setBounds(400, 125, 100, 30);
        
        
        add(placarX);
        placarX.setBounds(400, 150, 100, 30);
                 
        
        add(placarO);
        placarO.setBounds(480, 150, 100, 30);

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
                enviarMensagem("0", 0);
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        buttons[1].addActionListener((ActionEvent ae) -> {
            try {
                enviarMensagem("1", 1);
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        buttons[2].addActionListener((ActionEvent ae) -> {
            try {
                enviarMensagem("2", 2);
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        buttons[3].addActionListener((ActionEvent ae) -> {
            try {
                enviarMensagem("3", 3);
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        buttons[4].addActionListener((ActionEvent ae) -> {
            try {
                enviarMensagem("4", 4);
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        buttons[5].addActionListener((ActionEvent ae) -> {
            try {
                enviarMensagem("5", 5);
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        buttons[6].addActionListener((ActionEvent ae) -> {
            try {
                enviarMensagem("6", 6);
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        buttons[7].addActionListener((ActionEvent ae) -> {
            try {
                enviarMensagem("7", 7);
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        buttons[8].addActionListener((ActionEvent ae) -> {
            try {
                enviarMensagem("8", 8);
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
    }
    
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // TODO code application logic here
        
        Client c = new Client();
        c.connect();
        c.escutar();
        
    }
    
    private void connect(){
        
        try {
        
            socket = new Socket("10.100.15.208", 1478);
            outputStream = socket.getOutputStream();
            //objectOutputStream = new ObjectOutputStream(outputStream);
            //objectOutputStream.flush();
            ouw = new OutputStreamWriter(outputStream);
            bfw = new BufferedWriter(ouw);
            // objectOutputStream = new ObjectOutputStream(outputStream);
            //objectOutputStream.flush();
            //bfw.write("Olá servidor");
            bfw.flush();
            
            //objectOutputStream.writeObject("Olá servidor");
            //objectOutputStream.close();
            //clientSocket.close(); 
            //String msg = "Olá servidor";
            //enviarMensagem(msg);
            
       
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        
    }
    
    public void enviarMensagem(String msg, int position) throws IOException{
        
        //bfw.write(msg);
        
        String jogador_atual = nome_jogador;
        String simbolo_atual = simbolo;
        String posicao = String.valueOf(position);
        
        String enviar = jogador_atual + "_" + simbolo_atual + "_" + posicao;
        
        //Jogada j = new Jogada();
        //j.setNome_jogador(nome_jogador);
        //j.setPosicao_click(position);
        //j.setSimbolo(simbolo);
        
        //objectOutputStream.writeObject(j);
        //objectOutputStream.flush();
        
        bfw.write(enviar + "\r");
        bfw.flush();
        
        /*if(msg.equals("Sair")){
          bfw.write("Desconectado \r\n");
          //texto.append("Desconectado \r\n");
        }else{
          
          //texto.append( txtNome.getText() + " diz -> " +         txtMsg.getText()+"\r\n");
        }
         
         //txtMsg.setText("");*/
        
        
    }
    
    public void escutar() throws IOException, ClassNotFoundException{

        InputStream in = socket.getInputStream();
        InputStreamReader inr = new InputStreamReader(in);
        BufferedReader bfr = new BufferedReader(inr);
        //ObjectInputStream oin = new ObjectInputStream(in);
        String msg = "";
        String[] args;
        
        //Jogada jogada = (Jogada)ObjectInputStream.readO;
        //msg = bfr.readLine();
        //System.out.println(msg);  
        
        
        while(!"Sair".equalsIgnoreCase(msg)){
            
            if(bfr.ready()){
                msg = bfr.readLine();
                
                
                if(msg.contains("Xganhou")){
                    String[] splited = msg.split("_");
                    String placarx = "X: " + splited[1];
                    //System.out.println(msg);
                    this.placarX.setText(placarx);
                    JLabel lblMessage = new JLabel("X ganhou");
                    JOptionPane.showMessageDialog(null, lblMessage);
                    limpar();
                }else if(msg.contains("Oganhou")){
                    String[] splited = msg.split("_");
                    String placaro = "O: " + splited[1];
                    this.placarO.setText(placaro);
                    JLabel lblMessage = new JLabel("O ganhou");
                    JOptionPane.showMessageDialog(null, lblMessage);
                    limpar();            
                }else if(msg.equals("Empate")){
                    JLabel lblMessage = new JLabel(msg);
                    JOptionPane.showMessageDialog(null, lblMessage);
                    limpar();
                }else{
                    System.out.println(msg);

                    args = msg.split("_");
                    int positionButton = Integer.parseInt(args[2]);
                    System.out.println(args[2]);

                    String jogador = args[0];
                    System.out.println(jogador);

                    String simboloo = args[1];
                    System.out.println(simboloo);              

                    buttons[positionButton].setText(simboloo);
                    ultimo_jogador.setText(jogador);
                }
                
                if(msg.equals("Sair")){

                }
               }else{
                 //texto.append(msg+"\r\n");
               }
        
        }
         
    }
    
    public void sair() throws IOException{

        enviarMensagem("Sair", 0);
        bfw.close();
        ouw.close();
        ou.close();
        socket.close();
    }

    private void limpar() {
        for(int i = 0; i < 9; i++){
            buttons[i].setText("");            
        }
    }
    
}
