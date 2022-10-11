package fr.ul.theascendofpluton.listener;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import fr.ul.theascendofpluton.model.Joueur;

public class PlayerControlListener implements InputProcessor {
    private Joueur joueur;

    public PlayerControlListener(Joueur joueur) {
        this.joueur = joueur;
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean r = false;

        if (keycode == Input.Keys.D || keycode == Input.Keys.RIGHT) {
            joueur.setShouldGoRight(true);
            r = true;
        }
        if (keycode == Input.Keys.Q || keycode == Input.Keys.LEFT) {
            joueur.setShouldGoLeft(true);
            r = true;
        }
        if (keycode == Input.Keys.Z || keycode == Input.Keys.UP) {
            joueur.setShouldGoUp(true);
            r = true;
        }
        if (keycode == Input.Keys.S || keycode == Input.Keys.DOWN) {
            joueur.setShouldGoDown(true);
            r = true;
        }

        return r;
    }

    @Override
    public boolean keyUp(int keycode) {
        boolean r = false;
        if (keycode == Input.Keys.D || keycode == Input.Keys.RIGHT) {
            joueur.setShouldGoRight(false);
            r = true;
        }
        if (keycode == Input.Keys.Q || keycode == Input.Keys.LEFT) {
            joueur.setShouldGoLeft(false);
            r = true;
        }
        if (keycode == Input.Keys.Z || keycode == Input.Keys.UP) {
            joueur.setShouldGoUp(false);
            r = true;
        }
        if (keycode == Input.Keys.S || keycode == Input.Keys.DOWN) {
            joueur.setShouldGoDown(false);
            r = true;
        }
        return r;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
