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
    private TextureRegion[] sprites = new TextureRegion[4];

    public Player(Body body){

        super(body);

        Texture tex = Game.res.getTexture("bunny");
        sprites[0] = new TextureRegion(tex,0,0,32,32);
        sprites[1] = new TextureRegion(tex,32,32,32,32);
        sprites[2] = new TextureRegion(tex,64,64,32,32);
        sprites[3] = new TextureRegion(tex,96,96,32,32);
        setAnimation(sprites, 1/12f);

    }

    public void collectCrystal(){
        numCrystals++;
    }
    public int getNumCrystals(){return numCrystals;}
    public void getTotalCrystals(int i){totalCrystals = i;}
    public int getTotalCrystals(){return totalCrystals;}

}
