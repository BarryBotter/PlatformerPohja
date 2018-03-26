package my.game.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Katriina on 21.3.2018.
 */



public class MyContacListener implements ContactListener{

    private int numFootContacts;
    private Array<Body> bodiesToRemove;

    public  MyContacListener(){
        bodiesToRemove = new Array<Body>();
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if(fa.getUserData() != null && fa.getUserData().equals("foot")){
            numFootContacts++;
        }
        if(fb.getUserData() != null && fb.getUserData().equals("foot")){
            numFootContacts++;
        }
        if(fa.getUserData() != null && fa.getUserData().equals("crystal")){
            //remove pickup
            bodiesToRemove.add(fa.getBody());
        }
        if(fb.getUserData() != null && fb.getUserData().equals("crystal")){
            bodiesToRemove.add(fb.getBody());
        }
        if(fa.getUserData() != null && fa.getUserData().equals("win")){
            //playerWin();
        }
        if(fb.getUserData() != null && fb.getUserData().equals("win")){
            //playerWin();
        }
    }

    @Override
    public void endContact(Contact contact) {

        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if(fa.getUserData() != null && fa.getUserData().equals("foot")){
            numFootContacts--;
        }
        if(fb.getUserData() != null && fb.getUserData().equals("foot")){
            numFootContacts--;
        }

    }

    public boolean isPlayerOnGround(){
        return numFootContacts > 0;
    }
    public Array<Body> getBodiesToRemove(){return bodiesToRemove;}


    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
