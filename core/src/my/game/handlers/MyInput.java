package my.game.handlers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;

import my.game.Game;

/**
 * Created by Katriina on 21.3.2018.
 */

public class MyInput extends Stage{


        Game game;

        private Rectangle screenRightSide;
        private Rectangle screenLeftSide;

        private Vector3 touchPoint;

        public MyInput(){
                setupTouchControlAreas();
        }

        private boolean rightSideTouched(float x, float y) {
                return screenRightSide.contains(x, y);
        }

        private boolean leftSideTouched(float x, float y) {
                return screenLeftSide.contains(x, y);
        }

        private void translateScreenToWorldCoordinates(int x, int y) {
                game.getHUDCamera().unproject(touchPoint.set(x, y, 0));
        }
        private void setupTouchControlAreas() {
                touchPoint = new Vector3();
                screenRightSide = new Rectangle(game.getHUDCamera().viewportWidth / 2, 0, game.getHUDCamera().viewportWidth / 2,
                        game.getHUDCamera().viewportHeight);
                screenLeftSide = new Rectangle(0, 0,game.getHUDCamera().viewportWidth / 2, game.getHUDCamera().viewportHeight);
                Gdx.input.setInputProcessor(this);
        }

        @Override
        public boolean touchDown(int x, int y, int pointer, int button) {

                translateScreenToWorldCoordinates(x, y);

                if (rightSideTouched(touchPoint.x, touchPoint.y)) {
                        Gdx.app.debug("hei","miten mnee");

                } else if (leftSideTouched(touchPoint.x, touchPoint.y)) {

                }

                return super.touchDown(x, y, pointer, button);
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {


                return super.touchUp(screenX, screenY, pointer, button);
        }





}
