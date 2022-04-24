package cc.minetale.woolwars;

import cc.minetale.slime.loot.Loot;
import cc.minetale.slime.loot.LootPool;
import cc.minetale.slime.loot.context.ContextType;
import cc.minetale.slime.loot.entry.LootEntry;
import cc.minetale.slime.loot.function.LootFunction;
import cc.minetale.slime.loot.util.NumberProvider;
import net.minestom.server.item.Material;

public final class ChestLoot {

    public static Loot loot = new Loot(ContextType.CHEST)
            //Wool Pool
            .addPool(LootPool.of(new NumberProvider.Uniform(0, 3), NumberProvider.ZERO,
                                 LootEntry.item(0, Material.WHITE_WOOL)
                                         .addFunction(LootFunction.setCount(new NumberProvider.Uniform(8, 16), false))))
            //Sword Pool
            .addPool(LootPool.of(new NumberProvider.Uniform(0, 2), NumberProvider.ZERO,
                                 LootEntry.empty(24),
                                 LootEntry.item(12, Material.WOODEN_SWORD)
                                          .addFunction(LootFunction.enchantRandomly()),
                                 LootEntry.item(8, Material.STONE_SWORD)
                                          .addFunction(LootFunction.enchantRandomly()),
                                 LootEntry.item(4, Material.IRON_SWORD)
                                          .addFunction(LootFunction.enchantRandomly()),
                                 LootEntry.item(3, Material.DIAMOND_SWORD)
                                          .addFunction(LootFunction.enchantRandomly()),
                                 LootEntry.item(1, Material.NETHERITE_SWORD)
                                          .addFunction(LootFunction.enchantRandomly())))
            //Helmet Pool
            .addPool(LootPool.of(new NumberProvider.Uniform(0, 2), NumberProvider.ZERO,
                                 LootEntry.empty(24),
                                 LootEntry.item(12, Material.LEATHER_HELMET)
                                          .addFunction(LootFunction.enchantRandomly()),
                                 LootEntry.item(8, Material.CHAINMAIL_HELMET)
                                          .addFunction(LootFunction.enchantRandomly()),
                                 LootEntry.item(4, Material.IRON_HELMET)
                                          .addFunction(LootFunction.enchantRandomly()),
                                 LootEntry.item(3, Material.DIAMOND_HELMET)
                                          .addFunction(LootFunction.enchantRandomly()),
                                 LootEntry.item(1, Material.NETHERITE_HELMET)
                                          .addFunction(LootFunction.enchantRandomly())))
            //Chestplate Pool
            .addPool(LootPool.of(new NumberProvider.Uniform(0, 2), NumberProvider.ZERO,
                                 LootEntry.empty(24),
                                 LootEntry.item(12, Material.LEATHER_CHESTPLATE)
                                          .addFunction(LootFunction.enchantRandomly()),
                                 LootEntry.item(8, Material.CHAINMAIL_CHESTPLATE)
                                          .addFunction(LootFunction.enchantRandomly()),
                                 LootEntry.item(4, Material.IRON_CHESTPLATE)
                                          .addFunction(LootFunction.enchantRandomly()),
                                 LootEntry.item(3, Material.DIAMOND_CHESTPLATE)
                                          .addFunction(LootFunction.enchantRandomly()),
                                 LootEntry.item(1, Material.NETHERITE_CHESTPLATE)
                                          .addFunction(LootFunction.enchantRandomly())))
            //Leggings Pool
            .addPool(LootPool.of(new NumberProvider.Uniform(0, 2), NumberProvider.ZERO,
                                 LootEntry.empty(24),
                                 LootEntry.item(12, Material.LEATHER_LEGGINGS)
                                          .addFunction(LootFunction.enchantRandomly()),
                                 LootEntry.item(8, Material.CHAINMAIL_LEGGINGS)
                                          .addFunction(LootFunction.enchantRandomly()),
                                 LootEntry.item(4, Material.IRON_LEGGINGS)
                                          .addFunction(LootFunction.enchantRandomly()),
                                 LootEntry.item(3, Material.DIAMOND_LEGGINGS)
                                          .addFunction(LootFunction.enchantRandomly()),
                                 LootEntry.item(1, Material.NETHERITE_LEGGINGS)
                                          .addFunction(LootFunction.enchantRandomly())))
            //Boots Pool
            .addPool(LootPool.of(new NumberProvider.Uniform(0, 2), NumberProvider.ZERO,
                                 LootEntry.empty(24),
                                 LootEntry.item(12, Material.LEATHER_BOOTS)
                                          .addFunction(LootFunction.enchantRandomly()),
                                 LootEntry.item(8, Material.CHAINMAIL_BOOTS)
                                          .addFunction(LootFunction.enchantRandomly()),
                                 LootEntry.item(4, Material.IRON_BOOTS)
                                          .addFunction(LootFunction.enchantRandomly()),
                                 LootEntry.item(3, Material.DIAMOND_BOOTS)
                                          .addFunction(LootFunction.enchantRandomly()),
                                 LootEntry.item(1, Material.NETHERITE_BOOTS)
                                          .addFunction(LootFunction.enchantRandomly())));

}
