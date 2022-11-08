package fr.ul.theascendofpluton.listener;

import com.badlogic.gdx.physics.box2d.*;
import fr.ul.theascendofpluton.model.Joueur;
import fr.ul.theascendofpluton.model.Zombie;

public class PlayerContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixture_a;
        Fixture fixture_b = contact.getFixtureB();
        if(fixture_b.getUserData() != null && (fixture_b.getUserData().equals("player"))){
            fixture_a = contact.getFixtureB();
            fixture_b = contact.getFixtureA();
        }else {
            fixture_a = contact.getFixtureA();
        }

        Joueur entity_a = (Joueur) fixture_a.getBody().getUserData();
        Zombie entity_b = (Zombie) fixture_b.getBody().getUserData();

        entity_a.receiveDamage(entity_b.damage);
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
