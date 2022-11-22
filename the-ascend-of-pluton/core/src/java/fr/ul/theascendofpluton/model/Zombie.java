package fr.ul.theascendofpluton.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import fr.ul.theascendofpluton.view.GameView;

public class Zombie {
    private BodyDef bodyDef;
    private Body body;

    public final String name = "zombie";
    public float life;
    public float damage;

    private GameView gv;
    private World world;

    //x,y postion de l'ennemi dans le monde
    public Zombie(World world, Vector2 coords, float[] verticies, float life, float damage, GameView gv){
        this.life = life;
        this.damage = damage;
        this.world = world;
        this.gv = gv;

        bodyDef  = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(coords);
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.set(verticies);

        fixtureDef.shape = shape;
        fixtureDef.density = .5f;
        fixtureDef.restitution = .1f;
        fixtureDef.friction = .5f;

        body.setFixedRotation(true);
        body.createFixture(fixtureDef).setUserData("zombie");
        body.setUserData(this);
        shape.dispose();
    }

    //x,y coordonnées à atteindre
    public void update(float x, float y){
        float target_x = body.getPosition().x - x;
        float target_y = body.getPosition().y - y;
        float force_x = 0;
        float force_y = 0;

        if (target_x < 0) force_x = 10f;
        if (target_x > 0) force_x = -10f;
        if (target_y < 0) force_y = 10f;
        if (target_y > 0) force_y = -10f;

        body.setLinearVelocity(force_x, force_y);
    }

    public void receiveDamage(float damage) {
        life -= damage;
        if (life <= 0f) {
            dispose();
            // world.destroyBody(this.body);
        }
        // System.out.println(life);
    }

    public float getDistance(Vector2 position) {
        return position.dst(body.getPosition());
    }

    public void dispose() {
        gv.setToDestroy(this);
        world.destroyBody(body);
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

}
