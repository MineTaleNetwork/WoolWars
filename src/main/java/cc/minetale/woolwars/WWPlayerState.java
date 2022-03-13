package cc.minetale.woolwars;

import cc.minetale.slime.player.IPlayerState;
import cc.minetale.slime.rule.PlayerRule;
import cc.minetale.slime.rule.RuleSet;
import cc.minetale.slime.utils.ApplyStrategy;
import lombok.Getter;
import net.kyori.adventure.util.TriState;
import net.minestom.server.entity.GameMode;
import org.jetbrains.annotations.NotNull;

public enum WWPlayerState implements IPlayerState {
    STARTING(null, TriState.TRUE,
            RuleSet.of(new RuleSet.Entry<>(PlayerRule.FROZEN, PlayerRule.FreezeType.POSITION)),
            ApplyStrategy.ALWAYS,
            false);

    @Getter private final GameMode gamemode;
    private final TriState showTeam;
    @Getter private final RuleSet ruleSet;
    @Getter private final ApplyStrategy rulesApplyStrategy;
    private final boolean rulesAffectChildren;

    WWPlayerState(GameMode gamemode, TriState showTeam, RuleSet ruleSet, ApplyStrategy rulesApplyStrategy, boolean rulesAffectChildren) {
        this.gamemode = gamemode;
        this.showTeam = showTeam;
        this.ruleSet = ruleSet;
        this.rulesApplyStrategy = rulesApplyStrategy;
        this.rulesAffectChildren = rulesAffectChildren;
    }

    @Override
    public @NotNull TriState showTeam() {
        return this.showTeam;
    }

    @Override
    public boolean getRulesAffectChildren() {
        return this.rulesAffectChildren;
    }
}
