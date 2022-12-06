package fr.ul.theascendofpluton.listener;

import com.badlogic.gdx.physics.box2d.*;
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
                    joueur.receiveDamage(zombie.getDamage());
                    break;
                case "boss":
                    Boss boss = (Boss) otherFixture.getBody().getUserData();
                    joueur.receiveDamage(boss.getDamage());
                    break;
                case "apple":
                    Apple apple = (Apple) otherFixture.getBody().getUserData();
                    joueur.receiveLife(apple.getHeal());
                    break;
                case "portal":
                    if(!joueur.touchPortal()){
                        joueur.setTouchPortal(true);
                    }
                    break;
            }
            System.out.println("Vie restante : " + joueur.getLife());
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

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
