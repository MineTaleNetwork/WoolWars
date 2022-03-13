package cc.minetale.woolwars;

import cc.minetale.slime.game.Stage;

public class WWStage extends Stage {
    public static final WWStage GRACE_PERIOD = new WWStage();

    static {
        Stage.IN_GAME.insertPrevious(GRACE_PERIOD);
    }
}
