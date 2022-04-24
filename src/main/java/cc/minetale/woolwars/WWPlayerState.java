package cc.minetale.woolwars;

import cc.minetale.slime.player.PlayerState;
import cc.minetale.slime.rule.PlayerRule;
import cc.minetale.slime.rule.RuleEntry;
import cc.minetale.slime.rule.RuleSet;
import net.minestom.server.entity.GameMode;

public class WWPlayerState {
    public static PlayerState STARTING;

    static {
        STARTING = new PlayerState()
                .setGamemode(GameMode.ADVENTURE)
                .setRuleSet(RuleSet.ofDefaultsWith(PlayerRule.ALL_RULES,
                        RuleEntry.of(PlayerRule.FROZEN, PlayerRule.FreezeType.POSITION)));
    }
}
