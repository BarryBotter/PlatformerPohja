package my.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;

import my.game.Game;
import my.game.entities.Background;
import my.game.entities.HUD;
import my.game.entities.PickUp;
import my.game.entities.Player;
import my.game.entities.TextureDraw;
import my.game.handlers.B2DVars;

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
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import my.game.handlers.BoundedCamera;
import my.game.handlers.GameStateManager;
import my.game.handlers.MyContacListener;

import static my.game.handlers.B2DVars.PPM;

/**
 * Created by Katriina on 20.3.2018.
 */

public class Play extends GameState {

    public static int level;
    private World world;
    private Box2DDebugRenderer b2dr;

    //private OrthographicCamera b2dCam;
    private BoundedCamera b2dCam;

    private Rectangle screenRightSide;
    private Rectangle screenLeftSide;

    private TiledMap tileMap;
    private int tileMapWidth;
    private int tileMapHeight;
    private int tileSize;
    private OrthogonalTiledMapRenderer tmRenderer;

    private MyContacListener cl;

    private Player player;
    private Array<PickUp> crystals;
    private TextureDraw win;
    private Vector3 touchPoint;

    private Background[] backgrounds;

    private HUD hud;

    public Play(GameStateManager gsm) {
        super(gsm);

        world = new World(new Vector2(0, -9.81f), true);
        cl = new MyContacListener();
        world.setContactListener(cl);
        b2dr = new Box2DDebugRenderer();

        // create player
        cretePlayer();

        //create tile
        createWalls();
        cam.setBounds(0, tileMapWidth * tileSize, 0, tileMapHeight * tileSize);

        //create crystals
        createCrystals();
        player.setTotalCrystals(crystals.size);

        //create winblock
        createWin();

        //create background
        Texture bgs = Game.res.getTexture("bg");
        TextureRegion sky = new TextureRegion(bgs, 0, 0, 320, 240);
        TextureRegion clouds = new TextureRegion(bgs, 0, 240, 320, 240);
        TextureRegion mountains = new TextureRegion(bgs, 0, 480, 320, 240);
        backgrounds = new Background[3];
        backgrounds[0] = new Background(sky, cam, 0f);
        backgrounds[1] = new Background(clouds, cam, 0.1f);
        backgrounds[2] = new Background(mountains, cam, 0.2f);

       /* setup box2dcam
        b2dCam = new BoundedCamera();
        b2dCam.setToOrtho(false, Game.V_WIDTH / PPM, Game.V_HEIGHT / PPM);
        b2dCam.setBounds(0,(tileMapWidth * tileSize) / PPM,0,(tileMapHeight * tileSize ) / PPM);*/

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
                    //switchBlocks();


                } else if (leftSideTouched(touchPoint.x, touchPoint.y)) {
                    Gdx.app.log("Puoli:", "vasen");
                    if (cl.isPlayerOnGround()) {
                        player.getBody().setLinearVelocity(player.getBody().getLinearVelocity().x, 0);
                        player.getBody().applyLinearImpulse(0.3f, 6, 0, 0, true);

                        if (player.getBody().getLinearVelocity().x < 0.5f) {
                            float posX = player.getBody().getPosition().x;
                            player.getBody().setTransform(posX - 1, player.getBody().getPosition().y, 0);
                            player.getBody().setLinearVelocity(1.5f, 0);
                        }

                    }

                }

                return super.touchDown(x, y, pointer, button);
            }

            @Override
            public boolean touchUp(int x, int y, int pointer, int button) {
                return true;
            }
        });

        world.step(Game.STEP, 1, 1);

        //remove pickups
        Array<Body> bodies = cl.getBodiesToRemove();
        for (int i = 0; i < bodies.size; i++) {
            Body b = bodies.get(i);
            crystals.removeValue((PickUp) b.getUserData(), true);
            world.destroyBody(b);
            player.collectCrystal();
        }
        bodies.clear();

        player.update(dt);

        for (int i = 0; i < crystals.size; i++) {
            crystals.get(i).update(dt);
        }

        if (player.getBody().getPosition().y < 0) {
            gsm.setState(GameStateManager.MENU);
        }

        if (cl.isPlayerWin() == true) {
            if (level == 1) {
                gsm.setState(GameStateManager.LEVEL_SELECT);
            } else if (level == 3) {
                gsm.setState(GameStateManager.MENU);
            }
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
        //set cam to follow player
        cam.position.set(
                player.getposition().x * PPM + Game.V_WIDTH / 4,
                Game.V_HEIGHT / 2, 0);
        cam.update();

        // draw bgs
        sb.setProjectionMatrix(hudCam.combined);
        for (int i = 0; i < backgrounds.length; i++) {
            backgrounds[i].render(sb);
        }

        //draw tile map
        tmRenderer.setView(cam);
        tmRenderer.render();

        //draw player
        sb.setProjectionMatrix(cam.combined);
        player.render(sb);

        //draw crystals
        for (int i = 0; i < crystals.size; i++) {
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

        bdef.position.set(70 / PPM, 250 / PPM);
        bdef.linearVelocity.set(1.5f, 0);
        bdef.type = BodyDef.BodyType.DynamicBody;
        Body body = world.createBody(bdef);
        body.setGravityScale(3);

        shape.setAsBox(13 / PPM, 15 / PPM);
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fdef.filter.maskBits = B2DVars.BIT_GROUND | B2DVars.BIT_CRYSTAL;
        body.createFixture(fdef).setUserData("player");
        shape.dispose();

        //create foot sensor
        shape.setAsBox(15 / PPM, 2 / PPM, new Vector2(0, -15 / PPM), 0);
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fdef.filter.maskBits = B2DVars.BIT_GROUND;
        fdef.isSensor = true;
        body.createFixture(fdef).setUserData("foot");
        shape.dispose();

        //create player
        player = new Player(body);
    }

    private void createWalls() {

        // load tile map and map renderer
        try {
            tileMap = new TmxMapLoader().load("res/maps/level" + level + ".tmx");
        } catch (Exception e) {
            System.out.println("Cannot find file: res/maps/level" + level + ".tmx");
            Gdx.app.exit();
        }
        tileMapWidth = tileMap.getProperties().get("width", Integer.class);
        tileMapHeight = tileMap.getProperties().get("height", Integer.class);
        tileSize = tileMap.getProperties().get("tilewidth", Integer.class);
        tmRenderer = new OrthogonalTiledMapRenderer(tileMap);


        TiledMapTileLayer layer;
        layer = (TiledMapTileLayer) tileMap.getLayers().get("platforms");

        if (layer != null)
        createBlocks(layer, B2DVars.BIT_GROUND);
    }


    private void createBlocks(TiledMapTileLayer layer, short bits) {

        // tile size
        float ts = layer.getTileWidth();

        // go through all cells in layer
        for (int row = 0; row < layer.getHeight(); row++) {
            for (int col = 0; col < layer.getWidth(); col++) {

                // get cell
                TiledMapTileLayer.Cell cell = layer.getCell(col, row);

                // check that there is a cell
                if (cell == null) continue;
                if (cell.getTile() == null) continue;

                // create body from cell
                BodyDef bdef = new BodyDef();
                bdef.type = BodyDef.BodyType.StaticBody;
                bdef.position.set((col + 0.5f) * ts / PPM, (row + 0.5f) * ts / PPM);
                ChainShape cs = new ChainShape();
                Vector2[] v = new Vector2[3];
                v[0] = new Vector2(-ts / 2 / PPM, -ts / 2 / PPM);
                v[1] = new Vector2(-ts / 2 / PPM, ts / 2 / PPM);
                v[2] = new Vector2(ts / 2 / PPM, ts / 2 / PPM);
                cs.createChain(v);
                FixtureDef fd = new FixtureDef();
                fd.friction = 0;
                fd.shape = cs;
                fd.filter.categoryBits = bits;
                fd.filter.maskBits = B2DVars.BIT_PLAYER;
                world.createBody(bdef).createFixture(fd).setUserData("Ground");
                cs.dispose();

            }
        }

    }


    private void createCrystals() {
        crystals = new Array<PickUp>();

        MapLayer layer = tileMap.getLayers().get("crystals");

        if(layer != null){

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();

        for (MapObject mo : layer.getObjects()) {

            bdef.type = BodyDef.BodyType.StaticBody;

            float x = mo.getProperties().get("x", float.class) / PPM;
            float y = mo.getProperties().get("y", float.class) / PPM;

            bdef.position.set(x, y);

            CircleShape cshape = new CircleShape();
            cshape.setRadius(8 / PPM);

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
    }}

    private void createWin() {

        MapLayer layer = tileMap.getLayers().get("win");

        if (layer != null){

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();

        for (MapObject mo : layer.getObjects()) {

            bdef.type = BodyDef.BodyType.StaticBody;

            float x = mo.getProperties().get("x", float.class) / PPM;
            float y = mo.getProperties().get("y", float.class) / PPM;

            bdef.position.set(x, y);

            CircleShape cshape = new CircleShape();
            cshape.setRadius(8 / PPM);

            fdef.shape = cshape;
            fdef.isSensor = true;
            fdef.filter.categoryBits = B2DVars.BIT_CRYSTAL;
            fdef.filter.maskBits = B2DVars.BIT_PLAYER;

            Body body = world.createBody(bdef);
            body.createFixture(fdef).setUserData("win");
            cshape.dispose();

            win = new TextureDraw(body, "olvi");

            body.setUserData(win);
        }
    }
}}

