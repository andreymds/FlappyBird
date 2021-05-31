package com.mygdx.game.fb_teste;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
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
	private Texture gameOver;

	private float larguraDispositivo;
	private float alturaDispositivo;
	private float posicaoInicialVerticalPassaro = 0;
	private float variacao = 0;
	private float posicaoHorizontalPassaro = 0;

	private int gravidade = 0;
	private int pontos = 0;
	private  int pontuacaoMaxima = 0;
	private int estadoJogo=0;

	private float posicaoCanoHorizontal;
	private float posicaoCanoVertical;
	private float espacoEntreCanos;

	BitmapFont textoPontuacao;
	BitmapFont textoReiniciar;
	BitmapFont textoMelhorPontuacao;

	Sound somVoando;
	Sound somColisao;
	Sound somPontuacao;

	Preferences preferencias;

	boolean toqueTela = Gdx.input.justTouched(); //controle do toque na tela

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
				somPontuacao.play();
			}
		}
		//animação pássaro
		variacao += Gdx.graphics.getDeltaTime() * 10;
		if(variacao > 3)
			variacao = 0;
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
				alturaDispositivo /2 - canoTopo.getHeight() + espacoEntreCanos/2 + posicaoCanoVertical,
				canoTopo.getWidth(), canoTopo.getHeight());

		//colisão entre o pássaro e o cano
		boolean bateuCanoCima = Intersector.overlaps(circuloPassaro,retanguloCanoCima);
		boolean bateuCanoBaixo = Intersector.overlaps(circuloPassaro,retanguloCanoBaixo);

		if(bateuCanoBaixo||bateuCanoCima){
			if(estadoJogo==1){
				somColisao.play();
				estadoJogo = 2;
			}

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
		gameOver = new Texture("game_over.png");
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

		// infos do texto de reiniciar
		textoReiniciar = new BitmapFont();//fonte
		textoReiniciar.setColor(Color.GREEN);//cor
		textoReiniciar.getData().setScale(3);//tamanho

		// infos do texto da Melhor pontuação
		textoMelhorPontuacao = new BitmapFont();//fonte
		textoMelhorPontuacao.setColor(Color.RED);//cor
		textoMelhorPontuacao.getData().setScale(3);//tamanho

		//definindo colisores para cada figura
		shapeRenderer = new ShapeRenderer();
		circuloPassaro = new Circle();
		retanguloCanoCima = new Rectangle();
		retanguloCanoBaixo = new Rectangle();

		//setando os arquivos de áudio às variáveis
		somVoando = Gdx.audio.newSound(Gdx.files.internal("som_asa.wav"));
		somColisao = Gdx.audio.newSound(Gdx.files.internal("som_batida.wav"));
		somPontuacao = Gdx.audio.newSound(Gdx.files.internal("som_pontos.wav"));

		//define pontuação máxima zerada e que será alterada com um int
		preferencias = Gdx.app.getPreferences("flappyBird");
		pontuacaoMaxima = preferencias.getInteger("pontuacaoMaxima",0);
	}

	private void desenharTexturas() { //desenha a imagem que está instaciada no create
		batch.begin(); //inicia a renderização da aplicação

		batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo); //fundo
		batch.draw(passaros[(int) variacao], 50 + posicaoHorizontalPassaro, posicaoInicialVerticalPassaro); //pássaro

		//canos
		batch.draw(canoBaixo,posicaoCanoHorizontal,
				alturaDispositivo/2-canoBaixo.getHeight() - espacoEntreCanos/2+posicaoCanoVertical);
		batch.draw(canoTopo,posicaoCanoHorizontal, alturaDispositivo/2 + espacoEntreCanos/2 + posicaoCanoVertical);

		textoPontuacao.draw(batch,String.valueOf(pontos),
				larguraDispositivo/2, alturaDispositivo-100); //texto da Pontuação

		if(estadoJogo == 2){ //fim do jogo
			//Tela de Game Over
			batch.draw(gameOver, larguraDispositivo/2 - gameOver.getWidth()/2, alturaDispositivo/2);
			textoReiniciar.draw(batch, "TOQUE NA TELA PARA REINICIAR",
					larguraDispositivo/2 - 350, alturaDispositivo/2-gameOver.getHeight()/2);
			textoMelhorPontuacao.draw(batch, "SUA MELHOR PONTUAÇÃO É : " + pontuacaoMaxima + "PONTOS",
					larguraDispositivo/2 - 400, alturaDispositivo/2 - gameOver.getHeight()*2);
		}

		batch.end(); //finaliza a renderização da aplicação
	}

	private void verificaEstadoJogo(){

		if(estadoJogo == 0){ //antes do início do jogo
			if(toqueTela) //inicia o jogo com o toque
			{
				gravidade = -25;
				estadoJogo = 1;
				somVoando.play();
			}
		} else if (estadoJogo == 1){ //em jogo

			if(toqueTela)//mantém a gravidade
			{
				gravidade = -25;
				somVoando.play(); //toca o som do bater das asas quando toca a tela
			}
			//configuração e movimentação dos canos
			posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 200;
			if(posicaoCanoHorizontal < -canoBaixo.getHeight()){
				posicaoCanoHorizontal = larguraDispositivo; //posicionamento dos canos
				posicaoCanoVertical = random.nextInt(400) -200; //randomiza a posição dos canos
				passouCano = false; //controle pontuação
			}
			if(posicaoInicialVerticalPassaro > 0 || toqueTela) //movimentação pássaro
				posicaoInicialVerticalPassaro= posicaoInicialVerticalPassaro-gravidade;

			gravidade++; //alteração das variáveis para movimentação do pássaro

		} else if(estadoJogo == 2){

			if(pontuacaoMaxima>pontos){ //salva a pontuação Máxima nas preferências
				pontuacaoMaxima = pontos;
				preferencias.putInteger("pontuacaoMaxima", pontuacaoMaxima);
			}

			posicaoHorizontalPassaro -= Gdx.graphics.getDeltaTime() * 500; //animação de morte

			if(toqueTela){
				estadoJogo = 0;
				pontos = 0;
				gravidade = 0;
				posicaoHorizontalPassaro = 0;
				posicaoInicialVerticalPassaro = alturaDispositivo/2;
				posicaoCanoHorizontal = larguraDispositivo;
			}
		}
	}

	@Override
	public void dispose () {

	}
}
