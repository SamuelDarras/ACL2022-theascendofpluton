package fr.ul.theascendofpluton.model;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import fr.ul.theascendofpluton.LevelLoader;
import fr.ul.theascendofpluton.Pluton;

public class Zombie extends GameObject {
    public final String name = "zombie";
    public float life;
    public float damage;
    public float money;

    //x,y postion de l'ennemi dans le monde
    public Zombie(World world, Vector2 coords, float[] verticies, float life, float damage, float money){
        super(coords, world);
        this.life = life;
        this.damage = damage;
        this.money = money;

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
        // System.out.println(getBody().getPosition());
        return;
        // float target_x = getBody().getPosition().x - gameWorld.getJoueur().getPosition().x;
        // float target_y = getBody().getPosition().y - gameWorld.getJoueur().getPosition().y;
        // float force_x = 0;
        // float force_y = 0;

        // if (target_x < 0) force_x = 10f;
        // if (target_x > 0) force_x = -10f;
        // if (target_y < 0) force_y = 10f;
        // if (target_y > 0) force_y = -10f;

        // getBody().setLinearVelocity(force_x, force_y);

        // if (life <= 0f) {
        //     gameWorld.remove(this);
        // }
    }

    public void receiveDamage(float damage) {
        life -= damage;
    }

    public float getDistance(Vector2 position) {
        return position.dst(getBody().getPosition());
    }

    public Vector2 getPosition() {
        return getBody().getPosition();
    }

    public float getMoney() {
        return money;
    }

    @Override
    public void render(float delta) {
        Sprite s = LevelLoader.getInstance().spriteHashMap.get("zombie");
        s.setPosition(getPosition().x, getPosition().y);
        s.draw(Pluton.batch);
    }
}
