package com.mygdx.game.fb_teste;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class Jogo extends ApplicationAdapter {
	private SpriteBatch batch;

	private Texture[] passaros;
	private Texture fundo;
	private Texture canoBaixo;
	private Texture canoTopo;

	private float larguraDispositivo;
	private float alturaDispositivo;
	private float posicaoInicialVerticalPassaro = 0;
	private float variacao = 0;

	private int gravidade = 0;
	private int pontos = 0;

	private float posicaoCanoHorizontal;
	private float posicaoCanoVertical;
	private float espacoEntreCanos;

	BitmapFont textoPontuacao;
	private boolean passouCano = false;
	private Random random;

	private ShapeRenderer shapeRenderer;
	private Circle circuloPassaro;
	private Rectangle retanguloCanoCima;
	private Rectangle retanguloCanoBaixo;

	@Override
	public void create () {
		inicializaTexturas();
		inicializaObjetos();
	}

	@Override
	public void render () {
		verificaEstadoJogo();
		desenharTexturas();
		detectarColisao();
		validarPontos();
	}

	private void validarPontos() {
		//define quando a pontuação deve ser adicionada
		if(posicaoCanoHorizontal<50 - passaros[0].getWidth()){
			if (!passouCano){ //se passou cano for verdadeiro
				pontos++; //soma pontos
				passouCano = true;
			}
		}
	}

	private void detectarColisao() {
		//colisor pássaro
		circuloPassaro.set(50 + passaros[0].getWidth() / 2,
				posicaoInicialVerticalPassaro+passaros[0].getHeight() / 2,
				passaros[0].getWidth() / 2);

		//colisor cano de baixo
		retanguloCanoBaixo.set(posicaoCanoHorizontal,
				alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos/2 + posicaoCanoVertical,
				canoBaixo.getWidth(), canoBaixo.getHeight());

		//colisor cano de cima
		retanguloCanoCima.set(posicaoCanoHorizontal,
				alturaDispositivo /2 - canoTopo.getHeight() - espacoEntreCanos/2 + posicaoCanoVertical,
				canoTopo.getWidth(), canoTopo.getHeight());

		//colisão entre o pássaro e o cano
		boolean bateuCanoCima = Intersector.overlaps(circuloPassaro,retanguloCanoCima);
		boolean bateuCanoBaixo = Intersector.overlaps(circuloPassaro,retanguloCanoBaixo);

		if(bateuCanoBaixo||bateuCanoCima){
			Gdx.app.log("Log", "Bateu"); //teste de colisão
		}
	}

	private void inicializaTexturas() {
		//animação do passaro
		passaros= new Texture[3];
		passaros[0] = new Texture("passaro1.png");
		passaros[1] = new Texture("passaro2.png");
		passaros[2] = new Texture("passaro3.png");

		//coloca a imagem de fundo e os canos
		fundo = new Texture("fundo.png");
		canoBaixo = new Texture("cano_baixo_maior.png");
		canoTopo = new Texture("cano_topo_maior.png");
	}

	private void inicializaObjetos() {

		random = new Random();
		batch = new SpriteBatch();

		//pega infos da biblioteca gdx a respeito do dispositivo
		larguraDispositivo = Gdx.graphics.getWidth();
		alturaDispositivo = Gdx.graphics.getHeight();
		posicaoInicialVerticalPassaro = alturaDispositivo/ 2;

		//info do posicionamento dos canos
		posicaoCanoHorizontal = larguraDispositivo;
		espacoEntreCanos = 350;

		//infos do texto de pontuação
		textoPontuacao = new BitmapFont();//fonte
		textoPontuacao.setColor(Color.WHITE);//cor
		textoPontuacao.getData().setScale(10);//tamanho

		//definindo colisores para cada figura
		shapeRenderer = new ShapeRenderer();
		circuloPassaro = new Circle();
		retanguloCanoCima = new Rectangle();
		retanguloCanoBaixo = new Rectangle();
	}

	private void desenharTexturas() { //desenha a imagem que está instaciada no create
		batch.begin(); //inicia a renderização da aplicação

		batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo); //fundo
		batch.draw(passaros[(int) variacao], 50, posicaoInicialVerticalPassaro); //pássaro

		//canos
		batch.draw(canoBaixo,posicaoCanoHorizontal,
				alturaDispositivo/2-canoBaixo.getHeight() - espacoEntreCanos/2+posicaoCanoVertical);
		batch.draw(canoTopo,posicaoCanoHorizontal, alturaDispositivo/2+espacoEntreCanos/2+posicaoCanoVertical);

		textoPontuacao.draw(batch,String.valueOf(pontos),
				larguraDispositivo/2, alturaDispositivo-100); //texto da Pontuação

		batch.end(); //finaliza a renderização da aplicação
	}

	private void verificaEstadoJogo() {

		//configuração dos canos
		posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 200;
		if(posicaoCanoHorizontal < -canoBaixo.getHeight()){
			posicaoCanoHorizontal = larguraDispositivo; //posicionamento dos canos
			posicaoCanoVertical = random.nextInt(400) -200; //randomiza a posição dos canos
			passouCano = false; //controle pontuação
		}

		//controle do toque na tela
		boolean toqueTela = Gdx.input.justTouched();
		if(Gdx.input.justTouched())
		{gravidade = -25;}

		if(posicaoInicialVerticalPassaro > 0 || toqueTela)
			posicaoInicialVerticalPassaro= posicaoInicialVerticalPassaro-gravidade;

		//animação pássaro
		variacao += Gdx.graphics.getDeltaTime() * 10;
		if(variacao > 3)
			variacao = 0;

		gravidade++; //alteração das variáveis para movimentação do pássaro
	}

	@Override
	public void dispose () {

	}
}
