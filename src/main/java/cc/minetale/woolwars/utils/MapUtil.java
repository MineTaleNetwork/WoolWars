package cc.minetale.woolwars.utils;

import cc.minetale.commonlib.util.CollectionsUtil;
import cc.minetale.slime.game.GameInstance;
import cc.minetale.slime.loot.Loot;
import cc.minetale.slime.loot.LootRegistry;
import cc.minetale.slime.loot.LootTable;
import cc.minetale.slime.loot.TableType;
import cc.minetale.slime.loot.context.LootContext;
import cc.minetale.slime.map.AbstractMap;
import cc.minetale.woolwars.ChestLoot;
import cc.minetale.woolwars.vanilla.blocks.ChestBlockHandler;
import cc.minetale.woolwars.vanilla.blocks.LootChestBlockHandler;
import lombok.experimental.UtilityClass;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@UtilityClass
public final class MapUtil {
    private static final Random random = new Random();
    private static final List<String> facings = List.of("north", "east", "south", "west");

    public static void generateChests(GameInstance instance, float chance) {
        AbstractMap map = instance.getMap();
        Vec minPos = map.getMinPos().add(0.0, 1.0, 0.0);
        Vec maxPos = map.getMaxPos().sub(1.0, 1.0, 1.0);
        for(int x = minPos.blockX(); x < maxPos.blockX(); x++) {
            for (int y = minPos.blockY(); y < maxPos.blockY(); y++) {
                for (int z = minPos.blockZ(); z < maxPos.blockZ(); z++) {
                    if (!(random.nextFloat() <= chance)) continue;
                    String facing = facings.get(random.nextInt(facings.size()));

                    Block block = LootChestBlockHandler.applyFor(Block.CHEST.withProperty("facing", facing), ChestLoot.loot);

                    instance.setBlock(x, y, z, block);
                }
            }
        }
    }
}