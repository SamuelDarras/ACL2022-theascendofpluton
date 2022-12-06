package fr.ul.theascendofpluton.listener;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Output;

import fr.ul.theascendofpluton.LevelLoader;
import fr.ul.theascendofpluton.Pluton;
import fr.ul.theascendofpluton.model.Joueur;
import fr.ul.theascendofpluton.view.GameView;
import fr.ul.theascendofpluton.view.MiniMap;
import fr.ul.theascendofpluton.view.Shop;

public class PlayerControlListener implements InputProcessor {
    private Pluton game;
    private Joueur joueur;
    private boolean debugMode;
    private MiniMap map;
    private Shop shop;
    private boolean isShopOpen = false;

    public PlayerControlListener(Joueur joueur, MiniMap map, Shop shop, Pluton game) {
        this.game = game;
        this.joueur = joueur;
        this.map = map;
        this.shop = shop;
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
            //System.out.println("ici");
            map.toggle();
            r = true;
        }
        if(keycode == Input.Keys.P){
            if(isShopOpen==false){
                isShopOpen=true;
            } else {
                isShopOpen=false;
            }
            shop.toggle();
            r = true;
        }
        if(isShopOpen){
            if(keycode == Input.Keys.I){
                joueur.buyShop("vie",5);
               // System.out.println("achat PV");
                r = true;
            }
            if(keycode == Input.Keys.O){
                joueur.buyShop("strength",10);
                //System.out.println("achat force");
                r = true;
            }
        }

        if (keycode == Input.Keys.S && !joueur.isDead() && !joueur.touchPortal()) {
            LevelLoader.getInstance().getGameWorld().save();
            r = true;
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
