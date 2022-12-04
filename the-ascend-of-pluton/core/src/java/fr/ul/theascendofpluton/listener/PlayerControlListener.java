package fr.ul.theascendofpluton.listener;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Output;

import fr.ul.theascendofpluton.LevelLoader;
import fr.ul.theascendofpluton.model.Joueur;
import fr.ul.theascendofpluton.view.GameView;
import fr.ul.theascendofpluton.view.MiniMap;

public class PlayerControlListener implements InputProcessor {
    private Joueur joueur;
    private boolean debugMode;
    private MiniMap map;

    public PlayerControlListener(Joueur joueur, MiniMap map) {
        this.joueur = joueur;
        this.map = map;
        debugMode = false;
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean r = false;

        if (keycode == Input.Keys.D || keycode == Input.Keys.RIGHT) {
            joueur.addDirection(Joueur.RIGHT);
            r = true;
        }
        if (keycode == Input.Keys.Q || keycode == Input.Keys.LEFT) {
            joueur.addDirection(Joueur.LEFT);
            r = true;
        }
        if (keycode == Input.Keys.Z || keycode == Input.Keys.UP) {
            joueur.addDirection(Joueur.UP);
            r = true;
        }
        if (keycode == Input.Keys.S || keycode == Input.Keys.DOWN) {
            joueur.addDirection(Joueur.DOWN);
            r = true;
        }
        if (keycode == Input.Keys.SPACE) {
            joueur.setShouldAttack(true);
            r = true;
        }
        if(keycode == Input.Keys.B){
            debugMode = !debugMode;
            r = true;
        }
        if(keycode == Input.Keys.M){
            map.toggle();
            r = true;
        }

        if (keycode == Input.Keys.S) {
            try (Output output = new Output(new FileOutputStream("partie.bin"))) {
                LevelLoader.getInstance().getGameWorld().save(output);
            } catch (FileNotFoundException | KryoException e) {
                e.printStackTrace();
            }
        }

        return r;
    }

    @Override
    public boolean keyUp(int keycode) {
        boolean r = false;
        if (keycode == Input.Keys.D || keycode == Input.Keys.RIGHT) {
            joueur.removeDirection(Joueur.RIGHT);
            r = true;
        }
        if (keycode == Input.Keys.Q || keycode == Input.Keys.LEFT) {
            joueur.removeDirection(Joueur.LEFT);
            r = true;
        }
        if (keycode == Input.Keys.Z || keycode == Input.Keys.UP) {
            joueur.removeDirection(Joueur.UP);
            r = true;
        }
        if (keycode == Input.Keys.S || keycode == Input.Keys.DOWN) {
            joueur.removeDirection(Joueur.DOWN);
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

    public boolean isDebugMode(){
        return debugMode;
    }
}
