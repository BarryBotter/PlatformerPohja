package my.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import my.game.Game;
import my.game.handlers.B2DVars;

/**
 * Created by Katriina on 22.3.2018.
 */

public class HUD {

    private Player player;
    private TextureRegion[] blocks;

    public HUD(Player player){
        this.player = player;

        Texture tex = Game.res.getTexture("hud");

        blocks = new TextureRegion[3];
        for(int i = 0; i < blocks.length; i++){
          blocks[i] = new TextureRegion(tex,32 + i * 16, 0, 16,16);
        }
    }

    public void render(SpriteBatch sb){

        sb.begin();
        short bits =
                player.getBody().getFixtureList().first().getFilterData().maskBits;
        if((bits & B2DVars.BIT_RED) != 0){
            sb.draw(blocks[0],40,200);
        }
        if((bits & B2DVars.BIT_GREEN) != 0){
            sb.draw(blocks[1],40,200);
        }
        if((bits & B2DVars.BIT_BLUE) != 0){
            sb.draw(blocks[2],40,200);
        }
        sb.end();
    }

}
