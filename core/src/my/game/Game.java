package my.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import my.game.handlers.Content;
import my.game.handlers.GameStateManager;
import my.game.handlers.MyInput;
import my.game.states.Menu;

public class Game implements ApplicationListener {

	public static final int V_WIDTH = 320;
	public static final int V_HEIGHT = 240;
	public static final int SCALE = 2;

	public static final float STEP = 1 / 120f;

	private SpriteBatch sb;
	private OrthographicCamera cam;
	private OrthographicCamera hudCam;

	private GameStateManager gsm;

	public static Content res;

	public SpriteBatch getSpriteBatch(){return sb;}
	public OrthographicCamera getCamera(){return cam;}
	public OrthographicCamera getHUDCamera(){return hudCam;}



	@Override
	public void create() {

		res = new Content();
		res.loadTexture("res/images/bunny.png","bunny");
		res.loadTexture("res/images/crystal.png", "Crystal");
		res.loadTexture("res/images/hud.png","hud");
		res.loadTexture("res/images/bgs.png","bg");
		res.loadTexture("res/images/menu.png","menu");
		res.loadTexture("kuva.png","olvi");


		sb = new SpriteBatch();
		cam = new OrthographicCamera();
		cam.setToOrtho(false, V_WIDTH,V_HEIGHT);
		hudCam = new OrthographicCamera();
		hudCam.setToOrtho(false, V_WIDTH,V_HEIGHT);

		gsm = new GameStateManager(this);

	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void render() {

		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.render();

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {

	}


}
