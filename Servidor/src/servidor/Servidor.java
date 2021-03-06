/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import java.awt.Color;
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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
/**
 *
 * @author MateusR
 */
public class Servidor extends Thread{

    /**
     * @param args the command line arguments
     */
   
    
    //Declarando streams usados pelo servidor
    private static ArrayList<BufferedWriter> clients;
    private static ServerSocket server;
    private Socket connection;
    private InputStream in;
    private InputStreamReader inreader;
    private BufferedReader bfreader;

    private static boolean X_on = false;
    private static boolean O_on = false;
    
    private static final boolean[] clicks_positions = new boolean[9];
    private static final String[] jogadas = new String[9];
    private static final String[] simbolos = new String[9];
    private static final String[] jogadores = new String[9];
    
    private static String last_player = "";
    
    private int vitoria_X = 0;
    private int vitoria_O = 0;
    
    public Servidor(Socket s){
        this.connection = s;
        try{
           
            in = s.getInputStream();
            inreader = new InputStreamReader(in);
            bfreader = new BufferedReader(inreader);           
            
        }catch (IOException e){
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        
        try {
            
            //Cria um campo solicitando a porta para criar o servidor
            JLabel lblMessage = new JLabel("Insira a porta do servidor");      
            JTextField txt_porta = new JTextField("");
            Object[] texts = {lblMessage, txt_porta}; // Armazena os componentes da tela inicial
            JOptionPane.showMessageDialog(null, texts);            // Insere os componentes na tela e mostra eles
            int porta = Integer.parseInt(txt_porta.getText().toString());
            
            /*Cria um novo socket com a porta, depois instancia um arraylist de BufferedWriter
            isso serve para guardar os BufferedWriters de cada cliente que se conectar
            para depois mandar alguma mensagem para eles*/
            server = new ServerSocket(porta);
            clients = new ArrayList<>(2);  
            
            //Aqui seta false para todas as posi????es dos bot??es no Cliente
            clearClicks();
            
            //jogadas = new ArrayList<>();
            
            //Loop para escutar os clientes que v??o se conectando
            while(true){
                
                /*Verifica se os 2 clientes (X e O) entraram, se ambos entraram, n??o permite mais
                nenhum jogador se conectar*/
                if(!X_on && !O_on){
                    Socket client = server.accept();
                    System.out.println("Server is listening");
                    
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
    esse m??todo ?? da classe Thread, quando executamos a Thread esse m??todo ?? chamado
    ent??o sobrescrevendo ele, estamos fazendo tudo que est?? dentro em uma thread, sem interferir no prog. principal*/
    @Override
    public void run(){
        
        try{
            
            String msg;
            String[] args;
            String msg_enviar;
            
            //Instancia todos os outputs do cliente que se conecta e mandar mensagem para o servidor
            OutputStream ou =  this.connection.getOutputStream();
            Writer ouw = new OutputStreamWriter(ou);
            BufferedWriter bfw = new BufferedWriter(ouw);

            //Adiciona o BufferedWriter do cleinte na lista
            clients.add(bfw);
            
            //L?? a mensagem recebiad
            msg = bfreader.readLine();
            
            /*Essas duas verifica????es ?? para quando o cliente se conecta. Ele envia uma mensagem
            dizendo qual o simbolo escolheu. Se o simbolo for "X", por exemplo
            ele diz que o X j?? est?? online e verifica se j?? possui um "O" online*/
            if(msg.equals("X") && !X_on){
                System.out.println("X entrou");
                X_on = true;
                
                /*Se n??o exitir um O, mas j?? tiver um X, significa que o segundo jogador ainda n??o se conectou*/
                if(O_on == false){
                    System.out.println("Aguardando jogador 2");
                    bfw.write("Aguarde" + "\r");
                    bfw.flush();
                    
                /*Se ambos se conectaram, o jogo j?? pode come??ar*/    
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
                // L??gica para caso o 2?? jogador escolhe o s??mbolo que j?? foi escolhido anteriormente
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
                
                /*A mensagem que o cliente envia quando clica no bot??o do jogo
                ?? no formato Jogador_Simbolo_Posi????o. L?? a mensagem e divide cada um*/
                msg = bfreader.readLine();  
                args = msg.split("_");

                //System.out.println(msg);

                int positionButton = Integer.parseInt(args[2]);               
                String jogador = args[0];                
                String simboloo = args[1];
                /*Aqui verifica se a posi????o clicada j?? foi clicada antes, se for falso
                ele entra e dentro ele seta como true, sinalizando que aquele bot??o j?? foi pressionado
                e n??o permite pressionar de novo*/                           
                if(clicks_positions[positionButton] == false){                                      
                    
                    /*E aqui verifica se o jogador est?? tentando jogar mais de 1x
                    sinalizando que n??o ?? a vez dele, e sim do advers??rio.*/
                    if(!simboloo.equals(last_player)){
                        
                        /*Se n??o for o mesmo jogador ele envia para todos
                        quem foi o jogador, o simbolo e a posi????o. Assim no cliente
                        da para setar o simbolo no bot??o certo*/
                        msg_enviar = jogador + "_" + simboloo + "_" + positionButton;                                     
                        sendToAll(bfw, msg_enviar);
                        
                        //Seta que o bot??o j?? foi pressionado
                        clicks_positions[positionButton] = true;
                        jogadas[positionButton] = jogador;
                        simbolos[positionButton] = simboloo;   
                        
                        //Seta o novo ultimo jogador, para controlar de quem ?? a vez
                        last_player = simboloo;

                        //Veritica se algu??m ganhou com a jogada
                        checkWinner();
                        
                        
                        
                }else{
                    //Sinaliza que o jogador est?? tentando jogar de novo e n??o faz nada
                    System.out.println("Jogador iguais");
                    bfw.write("N??o ?? sua vez!" + "\r");
                    bfw.flush();
                    
                }
                    
                }
               
            }

        }catch (IOException | NumberFormatException e) {
        }
        
    }
    
    // Manda mensagem pro ??ltimo jogador conectado
    public void sendToLast(BufferedWriter bwExit, String msg) throws  IOException{
        BufferedWriter bw = new BufferedWriter(clients.get(clients.size() - 1));
        
        bw.write(msg + "\r");
        bw.flush();
    }
    
    //Manda mensagem para todos os jogadores
    public void sendToAll(BufferedWriter bwExit, String msg) throws  IOException{
       
        //BufferedWriter bwE;

        //Percorre todos os BufferedWriters dos clientes e envia uma mensagem para todos eles
        for(BufferedWriter bw : clients){
            //bwE = (BufferedWriter)bw;
            bw.write(msg + "\r");
            bw.flush();
        }
}
    
    //M??todo que verifica se existe algum ganhador
    private void checkWinner() throws IOException{
        
        
        /*Aqui vai contar se todos os bot??es foram pressionados
        isso significa que n??o possui nenhum jogador, logo count = 9*/
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
            
            //Incrementa a v??t??ria para o 'X'
            vitoria_X ++;        
            System.out.println("X ganhou" + "_" + vitoria_X);
            
            //Diz que o ??ltimo jogador foi o 'O' para assim ser a vez do 'X' novamente, que foi o ganhador
            last_player = "O";
            
            //Se o X ganhou, reseta as posi????es dos bot??es para permitir ser pressionado novamente
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
            
            //Incrementa a v??t??ria para o 'O'
            vitoria_O++;
            System.out.println("O ganhou" + "_" + vitoria_O);
            
            //Diz que o ??ltimo jogador foi o 'X' para assim ser a vez do 'O' novamente, que foi o ganhador
            last_player = "X";
            
            clearClicks();
            sendToAll(null, "Oganhou" + "_" + vitoria_O);
        }
        
        //Se todos os bot??es foram pressionados, houve um empate. Envia a mensagem de empate para os clientes
        else if(count == 9){
            clearClicks();
            sendToAll(null, "Empate");
        }
        
    }
    
    //Reseta os bot??es para poder serem pressionados novamente
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
