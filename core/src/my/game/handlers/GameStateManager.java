package my.game.handlers;

import com.badlogic.gdx.Game;

import java.util.Stack;
import my.game.states.GameState;
import my.game.states.Menu;
import my.game.states.Play;

/**
 * Created by Katriina on 20.3.2018.
 */

public class GameStateManager {

    private my.game.Game game;

    private Stack<GameState> gameStates;

    public static final int PLAY = 2182301;
    public static final int MENU = 823183;
    public static final int LEVEL_SELECT = 323971;


    public GameStateManager(my.game.Game game){
        this.game = game;
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
            return new Play(this);
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

}
