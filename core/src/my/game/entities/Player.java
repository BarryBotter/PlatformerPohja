package my.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;

import my.game.Game;

/**
 * Created by Katriina on 22.3.2018.
 */

public class Player extends  B2DSprite{

    private int numCrystals;
    private int totalCrystals;

    public Player(Body body){

        super(body);

        Texture tex = Game.res.getTexture("bunny");
        TextureRegion[] sprites = TextureRegion.split(tex,32,32)[0];
        setAnimation(sprites, 1/12f);

    }

    public void collectCrystal(){numCrystals++;}
    public int getNumCrystals(){return numCrystals;}
    public void setTotalCrystals(int i){totalCrystals = i;}
    public int getTotalCrystals(){return totalCrystals;}

}
