package my.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;

import my.game.Game;

/**
 * Created by Katriina on 23.3.2018.
 */

public class WinBlock extends B2DSprite {

    public WinBlock(Body body) {
        super(body);

        Texture tex = Game.res.getTexture("olvi");
        TextureRegion[] sprites = TextureRegion.split(tex,1440,1850)[0];

        setAnimation(sprites, 1 / 12f);

    }
}
