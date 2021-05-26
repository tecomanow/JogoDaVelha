/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

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
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import javax.print.DocFlavor;
import client.Jogada;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author MateusR
 */
public class Servidor extends Thread{

    /**
     * @param args the command line arguments
     */
   
    
    //Declaring streams to use by server
    private static ArrayList<BufferedWriter> clients;
    private static ArrayList<Jogada> jogadas_obg;
    private static ServerSocket server;
    private String name;
    private Socket connection;
    private InputStream in;
    private InputStreamReader inreader;
    private BufferedReader bfreader;
    //private ObjectInputStream objectInputStream;
  
    
    private boolean X_ou_O = false;
    //private String jogador = "Jogador 2_X";
    //private String simbolo = "O";
    
    private static boolean[] clicks_positions = new boolean[9];
    private static String[] jogadas = new String[9];
    private static String[] simbolos = new String[9];
    private static String[] jogadores = new String[9];
    
    private String last_player = "";
    
    public Servidor(Socket s){
        this.connection = s;
        try{
           
            in = s.getInputStream();
            inreader = new InputStreamReader(in);
            bfreader = new BufferedReader(inreader);
            //objectInputStream = new ObjectInputStream(in);
            
        }catch (Exception e){
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
        
        try {
            
            server = new ServerSocket(1478);
            clients = new ArrayList<>(2);         
            clearClicks();
            
            //jogadas = new ArrayList<>();
            
            while(true){
                
                Socket client = server.accept();
                System.out.println("Server is listening on port 1234");
                Thread clients = new Servidor(client);
                clients.start();
                                
            }
            
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage().toString());
        }
        
    }
    
    public void run(){
        
        try{

            String msg;
            String[] args;
            String msg_enviar;
            
            OutputStream ou =  this.connection.getOutputStream();
            Writer ouw = new OutputStreamWriter(ou);
            BufferedWriter bfw = new BufferedWriter(ouw);
            //ObjectOutputStream objectOutputStream = new ObjectOutputStream(ou);

            clients.add(bfw);
            msg = bfreader.readLine();
            //Jogada jogada = (Jogada)objectInputStream.readObject();
                     
            
            /*while(jogada != null){
                
                System.out.println(jogada.getNome_jogador());
                System.out.println(jogada.getPosicao_click());
               
            }*/

            while(msg != null){
                
                msg = bfreader.readLine();  
                args = msg.split("_");

                //System.out.println(msg);

                int positionButton = Integer.parseInt(args[2]);               
                String jogador = args[0];                
                String simboloo = args[1];
                            
                if(clicks_positions[positionButton] == false){                                      
                    
                    msg_enviar = jogador + "_" + simboloo + "_" + positionButton;                        
                    System.out.println("Entrou no if do click");                    
                    sendToAll(bfw, msg_enviar);
                    clicks_positions[positionButton] = true;
                    jogadas[positionButton] = jogador;
                    simbolos[positionButton] = simboloo;
                                   
                                     
                    
                    checkWinner();
                    
                }
               
            }

        }catch (IOException | NumberFormatException e) {
        }
        
    }
    
    public void sendToAll(BufferedWriter bwExit, String msg) throws  IOException{
        
        //BufferedWriter bwE;

        for(BufferedWriter bw : clients){
            //bwE = (BufferedWriter)bw;
            bw.write(msg + "\r");
            bw.flush();
        }
}
    
    private void checkWinner() throws IOException{
        
        int count = 0;
        for (int i = 0; i < 9; i++){
            if(clicks_positions[i] == true){
                count++;
            }
        }
        
        if(simbolos[0].equals("X") && simbolos[3].equals("X") && simbolos[6].equals("X")
            || simbolos[0].equals("X") && simbolos[1].equals("X") && simbolos[2].equals("X")
            || simbolos[0].equals("X") && simbolos[4].equals("X") && simbolos[8].equals("X")
            || simbolos[1].equals("X") && simbolos[4].equals("X") && simbolos[7].equals("X")
            || simbolos[2].equals("X") && simbolos[5].equals("X") && simbolos[8].equals("X")
            || simbolos[3].equals("X") && simbolos[4].equals("X") && simbolos[5].equals("X")
            || simbolos[6].equals("X") && simbolos[7].equals("X") && simbolos[8].equals("X")
            || simbolos[2].equals("X") && simbolos[4].equals("X") && simbolos[6].equals("X")){
            
            System.out.println("X ganhou");
            clearClicks();
            sendToAll(null, "X ganhou");
        }
        
        else if(simbolos[0].equals("O") && simbolos[3].equals("O") && simbolos[6].equals("o")
            || simbolos[0].equals("O") && simbolos[1].equals("O") && simbolos[2].equals("O")
            || simbolos[0].equals("O") && simbolos[4].equals("O") && simbolos[8].equals("O")
            || simbolos[1].equals("O") && simbolos[4].equals("O") && simbolos[7].equals("O")
            || simbolos[2].equals("O") && simbolos[5].equals("O") && simbolos[8].equals("O")
            || simbolos[3].equals("O") && simbolos[4].equals("O") && simbolos[5].equals("O")
            || simbolos[6].equals("O") && simbolos[7].equals("O") && simbolos[8].equals("O")
            || simbolos[2].equals("O") && simbolos[4].equals("O") && simbolos[6].equals("O")){
            
            System.out.println("O ganhou");
            clearClicks();
            sendToAll(null, "O ganhou");
        }
        
        else if(count == 9){
            clearClicks();
            sendToAll(null, "Empate");
        }
        
    }
    
    private static void clearClicks(){
        
        for(int i = 0; i < 9; i++){
            clicks_positions[i] = false;
            jogadas[i] = "";
            simbolos[i] = "";    
            //System.out.println(jogadores[i]);
            jogadores[i] = "";
            
        }              
        
    }    
    
}
