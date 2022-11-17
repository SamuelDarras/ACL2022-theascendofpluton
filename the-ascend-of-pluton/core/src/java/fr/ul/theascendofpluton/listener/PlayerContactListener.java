package fr.ul.theascendofpluton.listener;

import com.badlogic.gdx.physics.box2d.*;
import fr.ul.theascendofpluton.model.AcidPuddle;
import fr.ul.theascendofpluton.model.Joueur;
import fr.ul.theascendofpluton.model.Zombie;

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


        //Lorsque le Joueur rentre en collision avec l'acide
        System.out.println(maybePlayerFixture.getUserData());
        if (otherFixture.getUserData().equals("acid") && maybePlayerFixture.getUserData().equals("player")) {
            Joueur joueur = (Joueur) maybePlayerFixture.getBody().getUserData();
            AcidPuddle acidPuddle =  (AcidPuddle) otherFixture.getBody().getUserData() ;

            joueur.receiveContinuousDamage(acidPuddle.getDamage());
            System.out.println("Vie restante : " + joueur.getLife());
        } else if (otherFixture.getUserData().equals("zombie") && maybePlayerFixture.getUserData().equals("player")) {
            Joueur joueur = (Joueur) maybePlayerFixture.getBody().getUserData();
            Zombie zombie = (Zombie) otherFixture.getBody().getUserData();

            joueur.receiveDamage(zombie.damage);
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
