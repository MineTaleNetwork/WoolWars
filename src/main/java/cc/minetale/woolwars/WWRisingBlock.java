package cc.minetale.woolwars;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;

public class WWRisingBlock extends Entity {
    public WWRisingBlock(double velocity) {
        super(EntityType.FALLING_BLOCK);
        this.hasPhysics = false;

        setNoGravity(true);
        setVelocity(new Vec(0, velocity, 0));
    }
}
