package fr.ul.theascendofpluton;

import com.badlogic.gdx.Game;
import fr.ul.theascendofpluton.view.GameView;

public class Pluton extends Game {
    GameView gameView;

    @Override
    public void create() {
        gameView = new GameView();

        setScreen(gameView);
    }

    @Override
    public void dispose() {
        gameView.dispose();
    }

}
