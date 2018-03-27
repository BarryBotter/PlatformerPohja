package my.game.handlers;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/**
 * Created by velij on 19.3.2018.
 */

public class MyTextInputListener implements Input.TextInputListener {

    private final my.game.Game game;
    String nameToSettings;

    public MyTextInputListener(final my.game.Game gam)
    {
        this.game = gam;
    }

    @Override
    public void input(String text) {
        if(!text.equals("")) { // this forbids entering empty strings
            nameToSettings = text;
            //game.prefs.putString("name", "this is not the string you're looking for");
            game.prefs.putString("name", nameToSettings);
            game.prefs.flush();
        }
    }

    @Override
    public void canceled() {
/*        nameToSettings = "EeroCancel";
        game.prefs.putString("name", nameToSettings);
        game.prefs.flush();*/

    }
}
