package fr.ul.theascendofpluton.listener;

import com.badlogic.gdx.physics.box2d.*;

import fr.ul.theascendofpluton.LevelLoader;
import fr.ul.theascendofpluton.model.*;

public class PlayerContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture maybePlayerFixture;
        Fixture otherFixture = contact.getFixtureB();
        if (otherFixture.getUserData() != null && (otherFixture.getUserData().equals("player"))) {
            maybePlayerFixture = contact.getFixtureB();
            otherFixture = contact.getFixtureA();
        } else {
            maybePlayerFixture = contact.getFixtureA();
        }

        if(maybePlayerFixture.getUserData().equals("player")){
            Joueur joueur = (Joueur) maybePlayerFixture.getBody().getUserData();
            switch ((String)otherFixture.getUserData()){
                case "acid":
                    AcidPuddle acidPuddle =  (AcidPuddle) otherFixture.getBody().getUserData() ;
                    joueur.receiveContinuousDamage(acidPuddle.getDamage());
                    break;
                case "zombie":
                    Zombie zombie = (Zombie) otherFixture.getBody().getUserData();
                    zombie.inflictDamage(joueur);
                    break;
                case "bat":
                    Bat bat = (Bat) otherFixture.getBody().getUserData();
                    bat.inflictDamage(joueur);
                    break;
                case "boss":
                    Boss boss = (Boss) otherFixture.getBody().getUserData();
                    joueur.receiveDamage(boss.getDamage());
                    break;
                case "apple":
                    Apple apple = (Apple) otherFixture.getBody().getUserData();
                    joueur.receiveLife(apple.use());
                    break;
                case "portal":
                    if(!joueur.touchPortal()){
                        joueur.setTouchPortal(true);
                    }
                    break;
            }
            //System.out.println("Vie restante : " + joueur.getLife());
        }
        if (contact.getFixtureA().getUserData().equals("attack") && !(contact.getFixtureB().getUserData().equals("player"))) {
            ((DamageableObject) contact.getFixtureB().getBody().getUserData()).receiveDamage(LevelLoader.getInstance().getPluton().getDamage());
        }
        if (contact.getFixtureB().getUserData().equals("attack") && !(contact.getFixtureA().getUserData().equals("player"))) {
            ((DamageableObject) contact.getFixtureA().getBody().getUserData()).receiveDamage(LevelLoader.getInstance().getPluton().getDamage());
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture maybePlayerFixture;
        Fixture otherFixture = contact.getFixtureB();
        if (otherFixture.getUserData() != null && (otherFixture.getUserData().equals("player"))) {
            maybePlayerFixture = contact.getFixtureB();
            otherFixture = contact.getFixtureA();
        } else {
            maybePlayerFixture = contact.getFixtureA();
        }

        if (otherFixture.getUserData().equals("acid") && maybePlayerFixture.getUserData().equals("player")) {
            Joueur joueur = (Joueur) maybePlayerFixture.getBody().getUserData();
            joueur.stopContinuousDamage();
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        String fixtureAData = (String) contact.getFixtureA().getUserData();
        String fixtureBData = (String) contact.getFixtureB().getUserData();
        if (fixtureAData == "bat" && fixtureBData != "player" && fixtureBData != "attack" || fixtureBData == "bat" && fixtureAData != "player" && fixtureAData != "attack" )
            contact.setEnabled(false);
        contact.setEnabled(true);
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
