package com.mygdx.game.fb_teste;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Jogo extends ApplicationAdapter {
	private int movimentaY = 0;
	private int movimentaX = 0;
	private SpriteBatch batch;
	private Texture[] passaros;
	private Texture fundo;

	private float larguraDispositivo;
	private float alturaDispositivo;
	private float variacao = 0;
	private float gravidade = 0;
	private float posicaoInicialVerticalPassaro = 0;

	@Override
	public void create () {
		batch = new SpriteBatch();

		//animação do passaro
		passaros= new Texture[3];
		passaros[0] = new Texture("passaro1.png");
		passaros[1] = new Texture("passaro2.png");
		passaros[2] = new Texture("passaro3.png");

		fundo = new Texture("fundo.png");

		//pega infos da biblioteca gdx a respeito do dispositivo
		larguraDispositivo = Gdx.graphics.getWidth();
		alturaDispositivo = Gdx.graphics.getHeight();
		posicaoInicialVerticalPassaro = alturaDispositivo/ 2;
	}

	@Override
	public void render () {

        batch.begin(); //inicia a renderização da aplicação

        if(variacao > 3)
            variacao = 0;

        //controle do toque na tela
        boolean toqueTela = Gdx.input.justTouched();
        if(Gdx.input.justTouched())
            {gravidade = -25;}

        if(posicaoInicialVerticalPassaro > 0 || toqueTela)
            posicaoInicialVerticalPassaro= posicaoInicialVerticalPassaro-gravidade;

        //desenha a imagem que está instaciada no create
        batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);
        batch.draw(passaros[(int) variacao], 30, posicaoInicialVerticalPassaro);

        variacao += Gdx.graphics.getDeltaTime() * 10;

        //alteração das variáveis para movimentação do passaro
        gravidade++;
        movimentaY++;
        movimentaX++;

        batch.end(); //finaliza a renderização da aplicação
	}
	
	@Override
	public void dispose () {

	}
}
