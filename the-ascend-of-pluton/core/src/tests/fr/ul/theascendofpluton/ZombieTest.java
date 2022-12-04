package fr.ul.theascendofpluton;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import fr.ul.theascendofpluton.model.Zombie;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ZombieTest {

    @BeforeAll
    public void setUp(){
        Zombie zombie = new Zombie(new World(new Vector2(), false), new Vector2(10f, 10f), new float[]{-3.75f, 3.5f, 3.75f, 3.5f, 3.75f, -3.5f, -3.75f, -3.5f}, 15, 5, 2);
    }

    @Test
    public void test(){

    }
}
