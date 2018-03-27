package my.game.states;

import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

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

    int row_height,col_width;
    Stage stage;
    Image logo;
    ExtendViewport viewport;

    public Menu(final GameStateManager gsm) {
        super(gsm);

        //background
        Texture tex = Game.res.getTexture("menubg");
        bg = new Background(new TextureRegion(tex),hudCam,5 );
        bg.setVector(0, 0);

        cam.setToOrtho(false, Game.V_WIDTH, Game.V_HEIGHT);

        //viewport = new ExtendViewport(1920, 1080, cam);

        logo = new Image(new Texture("res/UI_final/menu_logo.png"));;

        row_height = 1080 / 12;
        col_width = 1920 / 12;

        //skin and stage
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        Skin mySkin = new Skin(Gdx.files.internal("res/skin/glassy-ui.json"));

        ImageButton playButton = new ImageButton(mySkin);
        playButton.setStyle(gsm.playButtonStyle);
        playButton.setSize(col_width*2,row_height*2);
        playButton.setScale(2f,2f);
        playButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
              game.snap.play(1f);
                dispose();
                gsm.setState(GameStateManager.LEVEL_SELECT);
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        ImageButton optionsButton = new ImageButton(mySkin);
        optionsButton.setStyle(gsm.optionButtonStyle);
        optionsButton.setSize(col_width,row_height);
        optionsButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                game.snap.play(1f);
/*                dispose(); todo Options menu not working atm might be about preferences
                gsm.setState(GameStateManager.OPTIONS);*/
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        ImageButton exitButton = new ImageButton(mySkin);
        exitButton.setStyle(gsm.exitButtonStyle);
        exitButton.setSize(col_width,row_height);
        exitButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {

                Gdx.app.exit();
                }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        tableLayout(optionsButton,playButton,exitButton);

    }

    private void tableLayout(ImageButton optionsButton, ImageButton playButton, ImageButton exitButton) {
        Table table = new Table();
        Table table1 = new Table();
        table.center();
        table.row();//first row
        table.add(optionsButton).width(col_width*2);
        table.row();//second row
        table.add();
        table.add(logo).colspan(2).height(750);
        table.add().width(col_width);
        table.add(table1); //nested table
        table1.add(playButton);
        table1.row();
        table1.add().height(row_height);
        table1.row();
        table1.add(exitButton);
        table.row();//third row
        table.add().colspan(5).height(row_height*3);
        table.setFillParent(true);
        stage.addActor(table);
        //table.debug();      // Turn on all debug lines (table, cell, and widget).
    }


    public void handleInput() {

    }

    public void update(float dt) {
        handleInput();
        bg.update(dt);
    }

    public void render() {
        sb.setProjectionMatrix(cam.combined);
        // draw background
        bg.render(sb);
        sb.begin();
        sb.end();
        //stage for menubutton layout
        stage.act();
        stage.draw();
    }

    public void dispose() {
        //stage.dispose();
    }


}


