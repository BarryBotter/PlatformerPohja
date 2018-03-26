package my.game.states;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import my.game.Game;
import my.game.entities.B2DSprite;
import my.game.entities.Background;
import my.game.handlers.Animation;
import my.game.handlers.B2DVars;
import my.game.handlers.GameButton;
import my.game.handlers.GameStateManager;

import static my.game.handlers.B2DVars.PPM;

/**
 * Created by Katriina on 23.3.2018.
 */

public class Menu extends GameState{

    private boolean debug = false;

    private Background bg;
    private Animation animation;
    private GameButton playButton;

    private World world;
    private Box2DDebugRenderer b2dRenderer;

    private Array<B2DSprite> blocks;

    public Menu(GameStateManager gsm) {

        super(gsm);

        Texture tex = Game.res.getTexture("menubg");
        bg = new Background(new TextureRegion(tex),hudCam,5 );
        bg.setVector(0, 0);

        tex = Game.res.getTexture("bunny");
        TextureRegion[] reg = new TextureRegion[4];
        for(int i = 0; i < reg.length; i++) {
            reg[i] = new TextureRegion(tex, i * 32, 0, 32, 32);
        }
        animation = new Animation(reg, 1 / 12f);


        tex = Game.res.getTexture("play");
        playButton = new GameButton(new TextureRegion(tex, 0, 34, 58, 27), 160, 100, cam);

        cam.setToOrtho(false, Game.V_WIDTH, Game.V_HEIGHT);

        world = new World(new Vector2(0, -9.8f * 5), true);
        //world = new World(new Vector2(0, 0), true);
        b2dRenderer = new Box2DDebugRenderer();

        createTitleBodies();

    }

    private void createTitleBodies() {

        // top platform
        BodyDef tpbdef = new BodyDef();
        tpbdef.type = BodyDef.BodyType.StaticBody;
        tpbdef.position.set(160 / PPM, 180 / PPM);
        Body tpbody = world.createBody(tpbdef);
        PolygonShape tpshape = new PolygonShape();
        tpshape.setAsBox(120 / PPM, 1 / PPM);
        FixtureDef tpfdef = new FixtureDef();
        tpfdef.shape = tpshape;
        tpfdef.filter.categoryBits = B2DVars.BIT_TOP_PLATFORM;
        tpfdef.filter.maskBits = B2DVars.BIT_TOP_BLOCK;
        tpbody.createFixture(tpfdef);
        tpshape.dispose();

    }






    public void handleInput() {

        if(playButton.isClicked()) {
            gsm.setState(GameStateManager.PLAY);
        }

    }

    public void update(float dt) {

        handleInput();

        world.step(dt / 5, 8, 3);

        bg.update(dt);
        animation.update(dt);

        playButton.update(dt);

    }

    public void render() {

        sb.setProjectionMatrix(cam.combined);

        // draw background
        bg.render(sb);

        // draw button
        playButton.render(sb);

        // draw bunny
        sb.begin();
        sb.draw(animation.getFrame(), 146, 31);
        sb.end();

        // debug draw box2d
        if(debug) {
            cam.setToOrtho(false, Game.V_WIDTH / PPM, Game.V_HEIGHT / PPM);
            b2dRenderer.render(world, cam.combined);
            cam.setToOrtho(false, Game.V_WIDTH, Game.V_HEIGHT);
        }

    }

    public void dispose() {

    }

}


