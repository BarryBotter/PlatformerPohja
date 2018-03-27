package my.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import my.game.handlers.GameStateManager;
import my.game.handlers.MyTextInputListener;

/**
 * Created by velij on 27.3.2018.
 */

public class options extends GameState {

    OrthographicCamera camera;
    ExtendViewport viewport;
    SpriteBatch batch;
    Texture logo;
    String Name = "", soundvalue = "",difficultyString = "",hintname = "Your Name";
    private Stage stage;
    private int row_height,col_width,fpsInt = 0;
    private Label nameLabel,soundLabel,difficultyLabel;


    public options(final GameStateManager gsm){
        super(gsm);
        //getSettings();

        row_height = 1080 / 12;
        col_width = 1920 / 12;

        logo = new Texture("res/UI_final/logo2.png");

        //skin and stage
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        Skin mySkin = new Skin(Gdx.files.internal("res/skin/glassy-ui.json"));

        //exitbutton (not in the table)
        Button exitButton = new TextButton("EXIT",mySkin,"default");
        exitButton.setSize(col_width*2, row_height*2);
        exitButton.setPosition(col_width,row_height);
        exitButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                dispose();
                gsm.setState(GameStateManager.MENU);
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {

                return true;
            }
        });
        stage.addActor(exitButton);

        //nameEditbutton, makes a textinputthing
        ImageButton nameEditButton = new ImageButton(mySkin);
        nameEditButton.setStyle(gsm.toothStyle);
        nameEditButton.setSize(col_width,col_width);
        nameEditButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                //creates textinputlistener and inputs the output to Preferences
                MyTextInputListener listener = new MyTextInputListener(game);
                Gdx.input.getTextInput(listener, "Enter your name:", "", hintname);            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        //name label
        Name = game.prefs.getString("name", "no name stored");  //getting name from preferences
        nameLabel = new Label(Name,mySkin); //labeltest
        nameLabel.setFontScale(5,5);

        //Button for setting the sound on and off, does not have functionality(yet)
        ImageButton soundButton = new ImageButton(mySkin);
        soundButton.setStyle(gsm.toothStyle);
        soundButton.setSize(col_width,col_width);
        soundButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                soundOption();//sound on/off
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        //sound label
        soundLabel = new Label(soundvalue, mySkin); //labeltest
        soundLabel.setFontScale(5,5);

        //chances the difficulty
        ImageButton difficultyButton = new ImageButton(mySkin);
        difficultyButton.setStyle(gsm.toothStyle);
        difficultyButton.setSize(col_width,col_width);
        difficultyButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                difficultyChange();
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        stage.addActor(difficultyButton);

        //difficultylabel
        difficultyLabel = new Label(difficultyString, mySkin); //labeltest
        difficultyLabel.setFontScale(5,5);

        //todo better layouting for options
        Table table = new Table();
        table.center();
        table.row();//first row
        table.add(nameEditButton);
        table.add(nameLabel).width(col_width*2);
        table.add(difficultyButton);
        table.add(difficultyLabel).width(col_width*2);
        table.row();//second row
        table.add(soundButton);
        table.add(soundLabel);
        table.add().width(col_width*2);
        table.setFillParent(true);
        stage.addActor(table);
        table.debug();      // Turn on all debug lines (table, cell, and widget).
    }

    private void difficultyChange(){
        //Chances the difficulty
        int difficulty = game.prefs.getInteger("difficulty");
        switch (difficulty) {
            case 0: game.prefs.putInteger("difficulty", 1);
                difficultyString = "normal";
                break;
            case 1: game.prefs.putInteger("difficulty", 2);
                difficultyString = "hard";
                break;
            case 2: game.prefs.putInteger("difficulty", 0);
                difficultyString = "easy";
                break;
            default: difficultyString = "Invalid ";
                break;
        }
        game.prefs.flush();
    }

    private void soundOption(){
        //chances the value of the sound boolean
        if (!game.prefs.getBoolean("sound")){
            game.prefs.putBoolean("sound",true);
            soundvalue = "true";
        }else
        {
            game.prefs.putBoolean("sound",false);
            soundvalue = "false";
        }
        game.prefs.flush();
    }

    private void getSettings(){
        //gets the characters name from Preferences
        Name = game.prefs.getString("name", "no name stored");  //getting name from preferences

        //checks the boolean from Preferences
        if(game.prefs.getBoolean("sound")){
            soundvalue = "true";
        }else {
            soundvalue = "false";
        }

        //difficulty is an integer(0-2) in the preferences
        int difficulty = game.prefs.getInteger("difficulty");
        switch (difficulty) {
            case 0:
                difficultyString = "easy";
                break;
            case 1:
                difficultyString = "normal";
                break;
            case 2:
                difficultyString = "hard";
                break;
            default: difficultyString = "Invalid ";
                break;
        }
    }


    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.57f, 0.95f, 0.45f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        getSettings();
        batch.begin();
        fpsInt = Gdx.graphics.getFramesPerSecond();
        //Name = game.prefs.getString("name", "no name stored");  //getting name from preferences
        batch.draw(logo, col_width*4, row_height*9, col_width*3, row_height*4); //logo
        batch.end();

        difficultyLabel.setText(difficultyString);
        nameLabel.setText(Name);
        soundLabel.setText(soundvalue);
        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {

    }
}
