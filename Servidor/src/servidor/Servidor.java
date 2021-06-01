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
  
    
    private static boolean X_on = false;
    private static boolean O_on = false;
    //private String jogador = "Jogador 2_X";
    //private String simbolo = "O";
    
    private static boolean[] clicks_positions = new boolean[9];
    private static String[] jogadas = new String[9];
    private static String[] simbolos = new String[9];
    private static String[] jogadores = new String[9];
    
    private static String last_player = "";
    
    private int vitoria_X = 0;
    private int vitoria_O = 0;
    
    public Servidor(Socket s){
        this.connection = s;
        try{
           
            in = s.getInputStream();
            inreader = new InputStreamReader(in);
            bfreader = new BufferedReader(inreader);
            //objectInputStream = new ObjectInputStream(in);
            
        }catch (IOException e){
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
        
        try {
            
            /*Cria um novo socket com a porta, depois instancia um arraylist de BufferedWriter
            isso serve para guardar os BufferedWriters de cada cliente que se conectar
            para depois mandar alguma mensagem para eles*/
            server = new ServerSocket(1478);
            clients = new ArrayList<>(2);  
            
            //Aqui seta false para todas as posições dos botões no Cliente
            clearClicks();
            
            //jogadas = new ArrayList<>();
            
            //Loop para escutar os clientes que vão se conectando
            while(true){
                
                /*Verifica se os 2 clientes (X e O) entraram, se ambos entraram, não permite mais
                nenhum jogador se conectar*/
                if(!X_on && !O_on){
                    Socket client = server.accept();
                    System.out.println("Server is listening on port 1234");
                    
                    //Instancia e cria uma Thread
                    Thread clientes = new Servidor(client);
                    //Executa a Thread
                    clientes.start();
                }
                                
            }
            
            
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
    }
    
    /*Como o programa principal vai ficar no loop recebendo os clientes
    esse método é da classe Thread, quando executamos a Thread esse método é chamado
    então sobrescrevendo ele, estamos fazendo tudo que está dentro em uma thread, sem interferir no prog. principal*/
    @Override
    public void run(){
        
        try{
            Boolean trocar = false;
            String msg;
            String[] args;
            String msg_enviar;
            
            //Instancia todos os outputs do cliente que se conecta e mandar mensagem para o servidor
            OutputStream ou =  this.connection.getOutputStream();
            Writer ouw = new OutputStreamWriter(ou);
            BufferedWriter bfw = new BufferedWriter(ouw);

            //Adiciona o BufferedWriter do cleinte na lista
            clients.add(bfw);
            
            //Lê a mensagem recebiad
            msg = bfreader.readLine();
            
            /*Essas duas verificações é para quando o cliente se conecta. Ele envia uma mensagem
            dizendo qual o simbolo escolheu. Se o simbolo for "X", por exemplo
            ele diz que o X já está online e verifica se já possui um "O" online*/
            if(msg.equals("X") && !X_on){
                System.out.println("X entrou");
                X_on = true;
                
                /*Se não exitir um O, mas já tiver um X, significa que o segundo jogador ainda não se conectou*/
                if(O_on == false){
                    System.out.println("Aguardando jogador 2");
                    bfw.write("Aguarde" + "\r");
                    bfw.flush();
                    
                /*Se ambos se conectaram, o jogo já pode começar*/    
                }else if(X_on && O_on){
                    sendToAll(null, "Pronto");
                }
                
            }else if(msg.equals("O") && !O_on){
                System.out.println("O entrou");
                O_on = true;
                
                if(X_on == false){
                    System.out.println("Aguardando jogador 2");
                    bfw.write("Aguarde" + "\r");
                    bfw.flush();
                }else if(X_on && O_on){
                    sendToAll(null, "Pronto");
                }
            }else{
                // Lógica para caso o 2º jogador escolhe o símbolo que já foi escolhido anteriormente
                if(X_on){
                    sendToLast(null, "Reinicio_x");
                    msg = "O";
                    O_on = true;
                    
                    sendToAll(null, "Pronto");
                }else{
                    sendToLast(null, "Reinicio_o");
                    msg = "X";
                    X_on = true;
                    
                    sendToAll(null, "Pronto");
                }
            }         

            while(msg != null){
                
                /*A mensagem que o cliente envia quando clica no botão do jogo
                é no formato Jogador_Simbolo_Posição. Lê a mensagem e divide cada um*/
                msg = bfreader.readLine();  
                args = msg.split("_");

                //System.out.println(msg);

                int positionButton = Integer.parseInt(args[2]);               
                String jogador = args[0];                
                String simboloo = args[1];
                /*Aqui verifica se a posição clicada já foi clicada antes, se for falso
                ele entra e dentro ele seta como true, sinalizando que aquele botão já foi pressionado
                e não permite pressionar de novo*/                           
                if(clicks_positions[positionButton] == false){                                      
                    
                    /*E aqui verifica se o jogador está tentando jogar mais de 1x
                    sinalizando que não é a vez dele, e sim do adversário.*/
                    if(!simboloo.equals(last_player)){
                        
                        /*Se não for o mesmo jogador ele envia para todos
                        quem foi o jogador, o simbolo e a posição. Assim no cliente
                        da para setar o simbolo no botão certo*/
                        msg_enviar = jogador + "_" + simboloo + "_" + positionButton;                                     
                        sendToAll(bfw, msg_enviar);
                        
                        //Seta que o botão já foi pressionado
                        clicks_positions[positionButton] = true;
                        jogadas[positionButton] = jogador;
                        simbolos[positionButton] = simboloo;               

                        //Veritica se alguém ganhou com a jogada
                        checkWinner();
                        
                        //Seta o novo ultimo jogador, para controlar de quem é a vez
                        last_player = simboloo;
                        
                }else{
                    //Sinaliza que o jogador está tentando jogar de novo e não faz nada
                    System.out.println("Jogador iguais");
                    bfw.write("Não é sua vez!" + "\r");
                    bfw.flush();
                    
                }
                    
                }
               
            }

        }catch (IOException | NumberFormatException e) {
        }
        
    }
    
    
    // Manda mensagem pro último jogador conectado
    public void sendToLast(BufferedWriter bwExit, String msg) throws  IOException{
        BufferedWriter bw = new BufferedWriter(clients.get(clients.size() - 1));
        
        bw.write(msg + "\r");
        bw.flush();
    }
    
    public void sendToAll(BufferedWriter bwExit, String msg) throws  IOException{
       
        //BufferedWriter bwE;

        //Percorre todos os BufferedWriters dos clientes e envia uma mensagem para todos eles
        for(BufferedWriter bw : clients){
            //bwE = (BufferedWriter)bw;
            bw.write(msg + "\r");
            bw.flush();
        }
}
    
    //Método que verifica se existe algum ganhador
    private void checkWinner() throws IOException{
        
        
        /*Aqui vai contar se todos os botões foram pressionados
        isso significa que não possui nenhum jogador, logo count = 9*/
        int count = 0;
        for (int i = 0; i < 9; i++){
            if(clicks_positions[i] == true){
                count++;
            }
        }
        
        //Verifica para cada possibilidade (Coluna, linha ou inclinado) o X ganhou
        if(simbolos[0].equals("X") && simbolos[3].equals("X") && simbolos[6].equals("X")
            || simbolos[0].equals("X") && simbolos[1].equals("X") && simbolos[2].equals("X")
            || simbolos[0].equals("X") && simbolos[4].equals("X") && simbolos[8].equals("X")
            || simbolos[1].equals("X") && simbolos[4].equals("X") && simbolos[7].equals("X")
            || simbolos[2].equals("X") && simbolos[5].equals("X") && simbolos[8].equals("X")
            || simbolos[3].equals("X") && simbolos[4].equals("X") && simbolos[5].equals("X")
            || simbolos[6].equals("X") && simbolos[7].equals("X") && simbolos[8].equals("X")
            || simbolos[2].equals("X") && simbolos[4].equals("X") && simbolos[6].equals("X")){
            
            //Conta quantas vezes o X ganhou
            vitoria_X ++;
            System.out.println("X ganhou" + "_" + vitoria_X);
            
            //Se o X ganhou, reseta as posições dos botões para permitir ser pressionado novamente
            clearClicks();
            sendToAll(null, "Xganhou" + "_" + vitoria_X);
        }
        
        //Verifica para cada possibilidade (Coluna, linha ou inclinado) o O ganhou
        else if(simbolos[0].equals("O") && simbolos[3].equals("O") && simbolos[6].equals("o")
            || simbolos[0].equals("O") && simbolos[1].equals("O") && simbolos[2].equals("O")
            || simbolos[0].equals("O") && simbolos[4].equals("O") && simbolos[8].equals("O")
            || simbolos[1].equals("O") && simbolos[4].equals("O") && simbolos[7].equals("O")
            || simbolos[2].equals("O") && simbolos[5].equals("O") && simbolos[8].equals("O")
            || simbolos[3].equals("O") && simbolos[4].equals("O") && simbolos[5].equals("O")
            || simbolos[6].equals("O") && simbolos[7].equals("O") && simbolos[8].equals("O")
            || simbolos[2].equals("O") && simbolos[4].equals("O") && simbolos[6].equals("O")){
            
            
            vitoria_O++;
            System.out.println("O ganhou" + "_" + vitoria_O);
            clearClicks();
            sendToAll(null, "Oganhou" + "_" + vitoria_O);
        }
        
        //Se todos os botões foram pressionados, houve um empate. Envia a mensagem de empate para os clientes
        else if(count == 9){
            clearClicks();
            sendToAll(null, "Empate");
        }
        
    }
    
    //Reseta os botões para poder serem pressionados novamente
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
