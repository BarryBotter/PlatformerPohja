package my.game.states;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import my.game.Game;
import my.game.handlers.Content;
import my.game.handlers.GameButton;
import my.game.handlers.GameStateManager;


public class LevelSelect extends GameState {

    private TextureRegion reg;
    private GameButton[][] buttons;

    public LevelSelect(GameStateManager gsm) {

        super(gsm);

        reg = new TextureRegion(Game.res.getTexture("menubg"), 0, 0, 320, 240);

        TextureRegion buttonReg = new TextureRegion(Game.res.getTexture("hud"), 0, 0, 32, 32);
        buttons = new GameButton[3][2];
        for(int row = 0; row < buttons.length; row++) {
            for(int col = 0; col < buttons[0].length; col++) {
                buttons[row][col] = new GameButton(buttonReg, 150 + col *40, 100 - row * 40, cam);
                buttons[row][col].setText(row * buttons[0].length + col + 1 + "");
            }
        }

        cam.setToOrtho(false, Game.V_WIDTH, Game.V_HEIGHT);

    }

    public void handleInput() {
    }

    public void update(float dt) {

        for(int row = 0; row < buttons.length; row++) {
            for(int col = 0; col < buttons[0].length; col++) {
                buttons[row][col].update(dt);
                if(buttons[row][col].isClicked()) {
                    Play.level = row * buttons[0].length + col + 1;
                    Game.res.getSound("snap").play();
                    gsm.setState(GameStateManager.PLAY);
                }
            }
        }

    }

    public void render() {

        sb.setProjectionMatrix(cam.combined);

        sb.begin();
        sb.draw(reg, 0, 0);
        sb.end();

        for(int row = 0; row < buttons.length; row++) {
            for(int col = 0; col < buttons[0].length; col++) {
                buttons[row][col].render(sb);
            }
        }

    }

    public void dispose() {
    }

}

