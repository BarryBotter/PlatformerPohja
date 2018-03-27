package my.game.handlers;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.Stack;
import my.game.states.GameState;
import my.game.states.LevelSelect;
import my.game.states.Menu;
import my.game.states.Play;
import my.game.states.options;

/**
 * Created by Katriina on 20.3.2018.
 */

public class GameStateManager {

    private my.game.Game game;

    private Stack<GameState> gameStates;

    public static final int PLAY = 2182301;
    public static final int MENU = 823183;
    public static final int LEVEL_SELECT = 323971;
    public static final int OPTIONS = 345678;

    public ImageButton.ImageButtonStyle playButtonStyle, optionButtonStyle,exitButtonStyle,toothStyle;
    Texture tex;
    TextureRegion menuButtons[];

    public GameStateManager(my.game.Game game){
        this.game = game;
        makeStyles();
        gameStates = new Stack<GameState>();
        pushState(MENU);

    }

    public my.game.Game game(){return game;}

    public void update(float dt){
        gameStates.peek().update(dt);
    }

    public void render(){
        gameStates.peek().render();
    }

    private GameState getState(int state){

        if (state == MENU){
            return new Menu(this);
        }
        if (state == PLAY)
        {
            return new Play(this);
        }
        if (state == LEVEL_SELECT)
        {
            return new LevelSelect(this);
        }
        if (state == OPTIONS)
        {
            return new options(this);
        }
        return null;
    }

    public void setState(int state){
        popState();
        pushState(state);
    }

    public void pushState(int state){
        gameStates.push(getState(state));
    }

    public void popState(){
        GameState g = gameStates.pop();
        g.dispose();
    }

    private void makeStyles() {
        //styles for button

        //todo load from a bigger texture
        tex = my.game.Game.res.getTexture("main");
        menuButtons = new TextureRegion[5];
        menuButtons[0] =  new TextureRegion(tex, 340, 40, 200, 100);
        menuButtons[1] =  new TextureRegion(tex, 340, 125, 200, 100);

        TextureRegion play = new TextureRegion(new Texture(Gdx.files.internal("res/UI_final/play.png")));
        TextureRegion options = new TextureRegion(new Texture(Gdx.files.internal("res/UI_final/settings.png")));
        TextureRegion exit = new TextureRegion(new Texture(Gdx.files.internal("res/UI_final/exit.png")));
        TextureRegion tooth = new TextureRegion(new Texture(Gdx.files.internal("res/UI_final/tooth_192.png")));
        playButtonStyle = new ImageButton.ImageButtonStyle();
        playButtonStyle.imageDown = new TextureRegionDrawable(play);
        playButtonStyle.imageUp = new TextureRegionDrawable(play);
        optionButtonStyle = new ImageButton.ImageButtonStyle();
        optionButtonStyle.imageDown = new TextureRegionDrawable(options);
        optionButtonStyle.imageUp = new TextureRegionDrawable(options);
        exitButtonStyle = new ImageButton.ImageButtonStyle();
        exitButtonStyle.imageDown = new TextureRegionDrawable(exit);
        exitButtonStyle.imageUp = new TextureRegionDrawable(exit);
        toothStyle = new ImageButton.ImageButtonStyle();
        toothStyle.imageDown = new TextureRegionDrawable(tooth);
        toothStyle.imageUp = new TextureRegionDrawable(tooth);
    }

}
