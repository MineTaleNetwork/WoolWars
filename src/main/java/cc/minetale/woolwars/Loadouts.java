package cc.minetale.woolwars;

import cc.minetale.slime.loadout.Loadout;
import cc.minetale.slime.player.GamePlayer;
import cc.minetale.slime.team.GameTeam;
import cc.minetale.slime.team.ITeamType;
import net.minestom.server.item.*;
import net.minestom.server.utils.NamespaceID;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.UnaryOperator;

public final class Loadouts {
    private Loadouts() {}

    public static final Loadout DEFAULT;

    static {
        DEFAULT = new Loadout(
                "default", "Default",
                ItemStack.of(Material.WHITE_WOOL), Collections.emptyList(),
                null, null,
                holder -> {
                    if(!(holder instanceof GamePlayer player)) return Collections.emptyList();
                    var team = player.getGameTeam();
                    var type = team.getType();

                    ArrayList<ItemStack> items = new ArrayList<>(Collections.nCopies(46, ItemStack.AIR));
                    items.set(0, ItemStack.of(type.getWoolMaterial(), 32));
                    items.set(0, ItemStack.of(Material.WOODEN_SWORD));
                    items.set(1, ItemStack.of(Material.DIAMOND_AXE));
                    items.set(2, ItemStack.of(Material.DIAMOND_PICKAXE));
                    items.set(3, ItemStack.of(Material.WOODEN_SHOVEL));

                    UnaryOperator<ItemMetaBuilder> applyEnchant = builder -> builder
                            .enchantment(Enchantment.FORTUNE, (short) 4)
                            .enchantment(Enchantment.EFFICIENCY, (short) 12);

                    items.set(4, ItemStack.of(Material.DIAMOND_SWORD).withMeta(applyEnchant));
                    items.set(5, ItemStack.of(Material.DIAMOND_AXE).withMeta(applyEnchant));
                    items.set(6, ItemStack.of(Material.DIAMOND_PICKAXE).withMeta(applyEnchant));
                    items.set(7, ItemStack.of(Material.DIAMOND_SHOVEL).withMeta(applyEnchant));

                    applyEnchant = builder -> builder
                            .enchantment(Enchantment.FORTUNE, (short) 4)
                            .enchantment(Enchantment.SILK_TOUCH, (short) 1);

                    items.set(8, ItemStack.of(Material.DIAMOND_SWORD).withMeta(applyEnchant));
                    items.set(9, ItemStack.of(Material.DIAMOND_AXE).withMeta(applyEnchant));
                    items.set(10, ItemStack.of(Material.DIAMOND_PICKAXE).withMeta(applyEnchant));
                    items.set(11, ItemStack.of(Material.DIAMOND_SHOVEL).withMeta(applyEnchant));

                    return items;
                });
        DEFAULT.register();
    }
}
