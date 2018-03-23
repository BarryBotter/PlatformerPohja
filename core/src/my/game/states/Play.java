package my.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;

import jdk.nashorn.internal.runtime.Debug;
import my.game.Game;
import my.game.entities.Background;
import my.game.entities.HUD;
import my.game.entities.PickUp;
import my.game.entities.Player;
import my.game.entities.WinBlock;
import my.game.handlers.B2DVars;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
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
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import my.game.handlers.GameStateManager;
import my.game.handlers.MyContacListener;
import my.game.handlers.MyInput;

/**
 * Created by Katriina on 20.3.2018.
 */

public class Play extends GameState {

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
    private Array<PickUp> crystals;
    private WinBlock win;
    private Vector3 touchPoint;

    private Background[] backgrounds;

    private HUD hud;

    public Play(GameStateManager gsm) {
        super(gsm);

        world = new World(new Vector2(0, -9.81f), true);
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

        //create crystals
        createCrystals();

        //create winblock
        createWin();

        //create background
        // create backgrounds
        Texture bgs = Game.res.getTexture("bg");
        TextureRegion sky = new TextureRegion(bgs, 0, 0, 320, 240);
        TextureRegion clouds = new TextureRegion(bgs, 0, 240, 320, 240);
        TextureRegion mountains = new TextureRegion(bgs, 0, 480, 320, 240);
        backgrounds = new Background[3];
        backgrounds[0] = new Background(sky, cam, 0f);
        backgrounds[1] = new Background(clouds, cam, 0.1f);
        backgrounds[2] = new Background(mountains, cam, 0.2f);

        //setup box2dcam
        b2dCam = new OrthographicCamera();
        b2dCam.setToOrtho(false, Game.V_WIDTH / B2DVars.PPM, Game.V_HEIGHT / B2DVars.PPM);

        // set up hud
        hud = new HUD(player);

        //setup touch areas
        setupTouchControlAreas();
    }

    @Override
    public void handleInput() {
    }

    @Override
    public void update(float dt) {

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int x, int y, int pointer, int button) {
                translateScreenToWorldCoordinates(x, y);

                if (rightSideTouched(touchPoint.x, touchPoint.y)) {
                    switchBlocks();


                } else if (leftSideTouched(touchPoint.x, touchPoint.y)) {
                    Gdx.app.log("Puoli:", "vasen");
                    if (cl.isPlayerOnGround()) {
                        player.getBody().applyLinearImpulse(0.3f, 6, 0, 0, true);
                        
                    }

                }

                return super.touchDown(x, y, pointer, button);
            }

            @Override
            public boolean touchUp(int x, int y, int pointer, int button) {
                return true;
            }
        });

        world.step(Game.STEP, 6, 2);

        //remove pickups
        Array<Body> bodies = cl.getBodiesToRemove();
        for(int i = 0; i<bodies.size; i++){
            Body b = bodies.get(i);
            crystals.removeValue((PickUp) b.getUserData(), true );
            world.destroyBody(b);
            player.collectCrystal();
        }
        bodies.clear();

        player.update(dt);

        for(int i = 0; i < crystals.size; i++){
            crystals.get(i).update(dt);
        }
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
        screenLeftSide = new Rectangle(0, 0, game.getHUDCamera().viewportWidth / 2, game.getHUDCamera().viewportHeight);
    }

    @Override
    public void render() {

        //clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        //set cam to follow player
        cam.position.set(
                player.getposition().x * B2DVars.PPM + Game.V_WIDTH / 4,
                Game.V_HEIGHT / 2,0);
        cam.update();

        // draw bgs
        sb.setProjectionMatrix(hudCam.combined);
        for(int i = 0; i < backgrounds.length; i++) {
            backgrounds[i].render(sb);
        }

        //draw tile map
        tmr.setView(cam);
        tmr.render();

        //draw player
        sb.setProjectionMatrix(cam.combined);
        player.render(sb);

        //draw crystals
        for(int i = 0; i < crystals.size; i++){
            crystals.get(i).render(sb);
        }

        //draw win
        win.render(sb);

        //draw hud
        sb.setProjectionMatrix(hudCam.combined);
        hud.render(sb);

        //draw box2d world
        //b2dr.render(world, b2dCam.combined);

    }

    @Override
    public void dispose() {
    }

    private void cretePlayer() {

        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();

        bdef.position.set(70 / B2DVars.PPM, 250 / B2DVars.PPM);
        bdef.linearVelocity.set(0.9f, 0);
        bdef.type = BodyDef.BodyType.DynamicBody;
        Body body = world.createBody(bdef);
        body.setGravityScale(3);

        shape.setAsBox(15 / B2DVars.PPM, 15 / B2DVars.PPM);
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fdef.filter.maskBits = B2DVars.BIT_RED | B2DVars.BIT_CRYSTAL;
        body.createFixture(fdef).setUserData("player");
        shape.dispose();

        //create foot sensor
        shape.setAsBox(15 / B2DVars.PPM, 2 / B2DVars.PPM, new Vector2(0, -15 / B2DVars.PPM), 0);
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fdef.filter.maskBits = B2DVars.BIT_RED;
        fdef.isSensor = true;
        body.createFixture(fdef).setUserData("foot");
        shape.dispose();

        //create player
        player = new Player(body);
    }

    private void createTiles() {


        //load tilemap
        tiledMap = new TmxMapLoader().load("res/maps/level0.tmx");
        tmr = new OrthogonalTiledMapRenderer(tiledMap);

        TiledMapTileLayer layer;

        tileSize = tiledMap.getProperties().get("tilewidth", Integer.class);

        layer = (TiledMapTileLayer) tiledMap.getLayers().get("red");
        createLayer(layer, B2DVars.BIT_RED);
        layer = (TiledMapTileLayer) tiledMap.getLayers().get("green");
        createLayer(layer, B2DVars.BIT_GREEN);
        layer = (TiledMapTileLayer) tiledMap.getLayers().get("blue");
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
                cs.dispose();
            }
        }

    }

    private void createCrystals() {
        crystals = new Array<PickUp>();

        MapLayer layer = tiledMap.getLayers().get("crystals");

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();

        for (MapObject mo : layer.getObjects()) {

            bdef.type = BodyDef.BodyType.StaticBody;

            float x = mo.getProperties().get("x", float.class) / B2DVars.PPM;
            float y = mo.getProperties().get("y", float.class) / B2DVars.PPM;

            bdef.position.set(x, y);

            CircleShape cshape = new CircleShape();
            cshape.setRadius(8 / B2DVars.PPM);

            fdef.shape = cshape;
            fdef.isSensor = true;
            fdef.filter.categoryBits = B2DVars.BIT_CRYSTAL;
            fdef.filter.maskBits = B2DVars.BIT_PLAYER;

            Body body = world.createBody(bdef);
            body.createFixture(fdef).setUserData("crystal");
            cshape.dispose();

            PickUp c = new PickUp(body);
            crystals.add(c);

            body.setUserData(c);
        }
    }

    private void switchBlocks(){
        Filter filter =
                player.getBody().getFixtureList().first().getFilterData();

        short bits = filter.maskBits;

        //next color

        if ((bits & B2DVars.BIT_RED) != 0){
            bits &= ~B2DVars.BIT_RED;
            bits |= B2DVars.BIT_GREEN;
        }
        else if ((bits & B2DVars.BIT_GREEN) != 0){
            bits &= ~B2DVars.BIT_GREEN;
            bits |= B2DVars.BIT_BLUE;
        }
        else if ((bits & B2DVars.BIT_BLUE) != 0){
            bits &= ~B2DVars.BIT_BLUE;
            bits |= B2DVars.BIT_RED;
        }

        // set new mask bits
        filter.maskBits = bits;
        player.getBody().getFixtureList().first().setFilterData(filter);

        //set new mask bits for foot
        filter = player.getBody().getFixtureList().get(1).getFilterData();
        bits &= ~B2DVars.BIT_CRYSTAL;
        filter.maskBits = bits;
        player.getBody().getFixtureList().get(1).setFilterData(filter);
    }

    private void createWin() {

        MapLayer layer = tiledMap.getLayers().get("win");

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();

        for (MapObject mo : layer.getObjects()) {

            bdef.type = BodyDef.BodyType.StaticBody;

            float x = mo.getProperties().get("x", float.class) / B2DVars.PPM;
            float y = mo.getProperties().get("y", float.class) / B2DVars.PPM;

            bdef.position.set(x, y);

            CircleShape cshape = new CircleShape();
            cshape.setRadius(8 / B2DVars.PPM);

            fdef.shape = cshape;
            fdef.isSensor = true;
            fdef.filter.categoryBits = B2DVars.BIT_CRYSTAL;
            fdef.filter.maskBits = B2DVars.BIT_PLAYER;

            Body body = world.createBody(bdef);
            body.createFixture(fdef).setUserData("win");
            cshape.dispose();

            win = new WinBlock(body);

            body.setUserData(win);
        }
}
}

