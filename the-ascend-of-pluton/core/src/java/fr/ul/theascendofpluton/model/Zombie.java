package fr.ul.theascendofpluton.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import fr.ul.theascendofpluton.LevelLoader;

public class Zombie extends DamageableObject {

    //x,y postion de l'ennemi dans le monde
    public Zombie(World world, Vector2 coords, float[] verticies, float life, float damage, float money){
        super(coords, life, damage, money);

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.set(verticies);

        fixtureDef.shape = shape;
        fixtureDef.density = .5f;
        fixtureDef.restitution = .1f;
        fixtureDef.friction = .5f;

        getBodyDef().type = BodyDef.BodyType.DynamicBody;
        getBodyDef().position.set(coords);
        setBody(world.createBody(getBodyDef()));

        getBody().setFixedRotation(true);
        getBody().createFixture(fixtureDef).setUserData("zombie");
        getBody().setUserData(this);
        shape.dispose();

    }

    //x,y coordonnées à atteindre
    @Override
    public void update(GameWorld gameWorld) {
        if(isTargetRange()){
            float target_x = getBody().getPosition().x - LevelLoader.getInstance().getPluton().getPosition().x;
            float target_y = getBody().getPosition().y - LevelLoader.getInstance().getPluton().getPosition().y;
            float force_x = 0;
            float force_y = 0;

            if (target_x < 0) force_x = 15f;
            if (target_x > 0) force_x = -15f;
            if (target_y < 0) force_y = 15f;
            if (target_y > 0) force_y = -15f;

            getBody().setLinearVelocity(force_x, force_y);
        }

        if (getLife() <= 0f) {
            LevelLoader.getInstance().getPluton().receiveMoney(getMoney());
            gameWorld.remove(this);
        }
    }

    /**
     *
     * @return Vrai si le joueur est assez proche du zombie pour qu'il le pourchasse, faux sinon.
     */
    private boolean isTargetRange() {
        return LevelLoader.getInstance().getPluton().getPosition().dst(getPosition()) <= 80;
    }

    @Override
    public void render(float delta) {
        super.render();
    }

}
