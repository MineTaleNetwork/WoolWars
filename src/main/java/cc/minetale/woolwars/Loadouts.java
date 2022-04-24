package cc.minetale.woolwars;

import cc.minetale.slime.loadout.Loadout;
import cc.minetale.slime.loadout.LoadoutHandlers;
import cc.minetale.slime.player.GamePlayer;
import net.minestom.server.color.Color;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.*;
import net.minestom.server.item.firework.FireworkEffect;
import net.minestom.server.item.firework.FireworkEffectType;
import net.minestom.server.item.metadata.FireworkEffectMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.UnaryOperator;

public final class Loadouts {
    private Loadouts() {}

    public static final Loadout DEFAULT;

    static {
        List<ItemStack> items = new ArrayList<>(Collections.nCopies(PlayerInventory.INVENTORY_SIZE, ItemStack.AIR));

        items.set(3, ItemStack.of(Material.EMERALD));
        items.set(4, ItemStack.of(Material.NOTE_BLOCK));
        items.set(5, ItemStack.of(Material.FIREWORK_STAR).withMeta(FireworkEffectMeta.class, meta -> {
            List<Color> color = Collections.singletonList(new Color(0, 255, 0));
            meta.effect(new FireworkEffect(
                    false,
                    false,
                    FireworkEffectType.SMALL_BALL,
                    color,
                    color));
            meta.hideFlag(ItemHideFlag.HIDE_POTION_EFFECTS);
        }));
        items.set(8, ItemStack.of(Material.RED_DYE));

        DEFAULT = Loadout.builder()
                .id("default")
                .displayName("Default")
                .displayItem(ItemStack.of(Material.WHITE_WOOL))
                .items(items)
                .handlers(LoadoutHandlers.withDynamicSupplier((holder, holderItems) -> {
                    if(!(holder instanceof GamePlayer player)) {
                        throw new IllegalArgumentException("Provided holder is not of type GamePlayer!");
                    }

                    holderItems.set(0, ItemStack.of(Material.WHITE_WOOL, 32)); //TODO Color wool as cosmetic
                    holderItems.set(1, ItemStack.of(Material.WOODEN_SWORD));
                    holderItems.set(2, ItemStack.of(Material.DIAMOND_AXE));
                    holderItems.set(3, ItemStack.of(Material.DIAMOND_PICKAXE));
                    holderItems.set(4, ItemStack.of(Material.WOODEN_SHOVEL));

                    UnaryOperator<ItemMetaBuilder> applyEnchant = builder -> builder
                            .enchantment(Enchantment.FORTUNE, (short) 4)
                            .enchantment(Enchantment.EFFICIENCY, (short) 12);

                    holderItems.set(5, ItemStack.of(Material.DIAMOND_SWORD).withMeta(applyEnchant));
                    holderItems.set(6, ItemStack.of(Material.DIAMOND_AXE).withMeta(applyEnchant));
                    holderItems.set(7, ItemStack.of(Material.DIAMOND_PICKAXE).withMeta(applyEnchant));
                    holderItems.set(8, ItemStack.of(Material.DIAMOND_SHOVEL).withMeta(applyEnchant));

                    applyEnchant = builder -> builder
                            .enchantment(Enchantment.FORTUNE, (short) 4)
                            .enchantment(Enchantment.SILK_TOUCH, (short) 1);

                    holderItems.set(9, ItemStack.of(Material.DIAMOND_SWORD).withMeta(applyEnchant));
                    holderItems.set(10, ItemStack.of(Material.DIAMOND_AXE).withMeta(applyEnchant));
                    holderItems.set(11, ItemStack.of(Material.DIAMOND_PICKAXE).withMeta(applyEnchant));
                    holderItems.set(12, ItemStack.of(Material.DIAMOND_SHOVEL).withMeta(applyEnchant));

                    return holderItems;
                }))
                .build();

        DEFAULT.register();
    }
}
