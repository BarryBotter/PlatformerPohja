package my.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;

import jdk.nashorn.internal.runtime.Debug;
import my.game.Game;
import my.game.entities.Player;
import my.game.handlers.B2DVars;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import my.game.handlers.GameStateManager;
import my.game.handlers.MyContacListener;
import my.game.handlers.MyInput;

/**
 * Created by Katriina on 20.3.2018.
 */

public class Play extends GameState{

    private World world;
    private Box2DDebugRenderer b2dr;

    private OrthographicCamera b2dCam;

    private Rectangle screenRightSide;
    private Rectangle screenLeftSide;

    private TiledMap tiledMap;
    private int tileSize;
    private OrthogonalTiledMapRenderer tmr;

    private MyContacListener cl;

    private Player player;

    private Vector3 touchPoint;

    public Play(GameStateManager gsm){
        super(gsm);

        world = new World(new Vector2(0,-9.81f),true);
        cl = new MyContacListener();
        world.setContactListener(cl);
        b2dr = new Box2DDebugRenderer();

        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();

        // create player
        cretePlayer();

        //create tile
        createTiles();

        //setup box2dcam
        b2dCam = new OrthographicCamera();
        b2dCam.setToOrtho(false, Game.V_WIDTH / B2DVars.PPM, Game.V_HEIGHT / B2DVars.PPM);

        setupTouchControlAreas();
    }
    @Override
    public void handleInput() {
    }

    @Override
    public void update(float dt) {

        handleInput();
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown (int x, int y, int pointer, int button) {
                translateScreenToWorldCoordinates(x, y);

                if (rightSideTouched(touchPoint.x, touchPoint.y)) {
                    Gdx.app.log("Puoli:", "oikea");
                    if(cl.isPlayerOnGround()){
                        player.getBody().applyLinearImpulse(0,10,1,1,true);
                    }

                } else if (leftSideTouched(touchPoint.x, touchPoint.y)) {
                    Gdx.app.log("Puoli:", "vasen");
                }

                return super.touchDown(x, y, pointer, button);
            }

            @Override
            public boolean touchUp (int x, int y, int pointer, int button) {
                return true;
            }
        });

        world.step(dt, 6, 2);
    }

        private boolean rightSideTouched(float x, float y) {
        return screenRightSide.contains(x, y);
         }

         private boolean leftSideTouched(float x, float y) {
        return screenLeftSide.contains(x, y);
    }

         private void translateScreenToWorldCoordinates(int x, int y) {
         game.getHUDCamera().unproject(touchPoint.set(x, y, 0));
         }
         private void setupTouchControlAreas() {
         touchPoint = new Vector3();
         screenRightSide = new Rectangle(game.getHUDCamera().viewportWidth / 2, 0, game.getHUDCamera().viewportWidth / 2,
                game.getHUDCamera().viewportHeight);
         screenLeftSide = new Rectangle(0, 0,game.getHUDCamera().viewportWidth / 2, game.getHUDCamera().viewportHeight);
         }

    @Override
    public void render() {

        //clear screen
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //draw tile map
        tmr.setView(cam);
        tmr.render();

        //draw box2d world
        b2dr.render(world, b2dCam.combined);

    }
    @Override
    public void dispose() {}

    private void cretePlayer(){

        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();

        bdef.position.set(160 / B2DVars.PPM,400 / B2DVars.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        Body body = world.createBody(bdef);
        body.setGravityScale(3);

        shape.setAsBox(5 / B2DVars.PPM,5 / B2DVars.PPM);
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fdef.filter.maskBits = B2DVars.BIT_RED;
        body.createFixture(fdef).setUserData("player");

        //create foot sensor
        shape.setAsBox(2/ B2DVars.PPM, 2/ B2DVars.PPM,new Vector2(0,-5/B2DVars.PPM),0);
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fdef.filter.maskBits = B2DVars.BIT_RED;
        fdef.isSensor = true;
        body.createFixture(fdef).setUserData("foot");

        //create player
        player = new Player(body);
    }
    private void createTiles(){


        //load tilemap
        tiledMap = new TmxMapLoader().load("res/maps/level1.tmx");
        tmr = new OrthogonalTiledMapRenderer(tiledMap);

        TiledMapTileLayer layer;

        tileSize = tiledMap.getProperties().get("tilewidth",Integer.class);

        layer =  (TiledMapTileLayer) tiledMap.getLayers().get("red");
        createLayer(layer, B2DVars.BIT_RED);
        layer =  (TiledMapTileLayer) tiledMap.getLayers().get("green");
        createLayer(layer, B2DVars.BIT_GREEN);
        layer =  (TiledMapTileLayer) tiledMap.getLayers().get("blue");
        createLayer(layer, B2DVars.BIT_BLUE);

    }

    private void createLayer(TiledMapTileLayer layer, short bits) {


        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();

        for (int row = 0; row < layer.getHeight(); row++) {
            for (int col = 0; col < layer.getWidth(); col++) {

                //get cell
                TiledMapTileLayer.Cell cell = layer.getCell(col, row);

                // check if cell exist
                if (cell == null) continue;
                if (cell.getTile() == null) continue;

                //create a body and fixture from cell
                bdef.type = BodyDef.BodyType.StaticBody;
                bdef.position.set((col + 0.5f) * tileSize / B2DVars.PPM, (row + 0.5f) * tileSize / B2DVars.PPM);

                ChainShape cs = new ChainShape();
                Vector2[] v = new Vector2[3];
                v[0] = new Vector2(-tileSize / 2 / B2DVars.PPM, -tileSize / 2 / B2DVars.PPM);
                v[1] = new Vector2(-tileSize / 2 / B2DVars.PPM, tileSize / 2 / B2DVars.PPM);
                v[2] = new Vector2(tileSize / 2 / B2DVars.PPM, tileSize / 2 / B2DVars.PPM);
                cs.createChain(v);
                fdef.friction = 0;
                fdef.shape = cs;
                fdef.filter.categoryBits = bits;
                fdef.filter.maskBits = B2DVars.BIT_PLAYER;
                fdef.isSensor = false;
                world.createBody(bdef).createFixture(fdef);
            }
        }

    }}
