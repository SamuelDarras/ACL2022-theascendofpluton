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
            joueur.shouldGoRight = true;
            r = true;
        }
        if (keycode == Input.Keys.Q || keycode == Input.Keys.LEFT) {
            joueur.shouldGoLeft = true;
            r = true;
        }
        if (keycode == Input.Keys.Z || keycode == Input.Keys.UP) {
            joueur.shouldGoUp = true;
            r = true;
        }
        if (keycode == Input.Keys.S || keycode == Input.Keys.DOWN) {
            joueur.shouldGoDown = true;
            r = true;
        }

        return r;
    }

    @Override
    public boolean keyUp(int keycode) {
        boolean r = false;
        if (keycode == Input.Keys.D || keycode == Input.Keys.RIGHT) {
            joueur.shouldGoRight = false;
            r = true;
        }
        if (keycode == Input.Keys.Q || keycode == Input.Keys.LEFT) {
            joueur.shouldGoLeft = false;
            r = true;
        }
        if (keycode == Input.Keys.Z || keycode == Input.Keys.UP) {
            joueur.shouldGoUp = false;
            r = true;
        }
        if (keycode == Input.Keys.S || keycode == Input.Keys.DOWN) {
            joueur.shouldGoDown = false;
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
