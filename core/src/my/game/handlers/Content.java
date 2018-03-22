package my.game.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;

/**
 * Created by Katriina on 22.3.2018.
 */

public class Content {

    private HashMap<String, Texture> textures;

    public Content(){

        textures = new HashMap<String, Texture>();
    }

    public void loadTexture(String path, String key){
        Texture text = new Texture(Gdx.files.internal(path));
        textures.put(key, text);
    }
    public Texture getTexture(String key){
        return textures.get(key);
    }


    public void disposeTexture(String key){
        Texture tex = textures.get(key);
        if(tex != null) tex.dispose();
    }
}
