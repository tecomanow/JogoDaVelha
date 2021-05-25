/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jogada;

import java.io.Serializable;

/**
 *
 * @author MateusR
 */
public class Jogada implements Serializable{
    
    private String nome_jogador;
    private int posicao_click;
    private String simbolo;

    public String getNome_jogador() {
        return nome_jogador;
    }

    public void setNome_jogador(String nome_jogador) {
        this.nome_jogador = nome_jogador;
    }

    public int getPosicao_click() {
        return posicao_click;
    }

    public void setPosicao_click(int posicao_click) {
        this.posicao_click = posicao_click;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }

    
    
}
