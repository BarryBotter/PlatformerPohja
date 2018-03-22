package my.game.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * Created by Katriina on 21.3.2018.
 */



public class MyContacListener implements ContactListener{

    private int numFootContacts;

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


    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
