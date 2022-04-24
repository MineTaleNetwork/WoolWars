package cc.minetale.woolwars.utils;

import cc.minetale.slime.game.GameInstance;
import cc.minetale.slime.map.AbstractMap;
import cc.minetale.woolwars.ChestLoot;
import cc.minetale.woolwars.vanilla.blocks.LootChestBlockHandler;
import lombok.experimental.UtilityClass;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.batch.AbsoluteBlockBatch;
import net.minestom.server.instance.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@UtilityClass
public final class MapUtil {
    private static final Random random = new Random();
    private static final List<String> facings = List.of("north", "east", "south", "west");

    public static void generateChests(GameInstance instance, float chance) {
        AbstractMap map = instance.getMap();
        AbsoluteBlockBatch batch = new AbsoluteBlockBatch();
        Vec minPos = map.getMinPos().add(0.0, 1.0, 0.0);
        Vec maxPos = map.getMaxPos().sub(1.0, 1.0, 1.0);
        List<CompletableFuture<Chunk>> chunkFutures = new ArrayList<>();
        for(int x = minPos.blockX(); x < maxPos.blockX(); x++) {
            for (int z = minPos.blockZ(); z < maxPos.blockZ(); z++) {
                for (int y = minPos.blockY(); y < maxPos.blockY(); y++) {
                    if (!(random.nextFloat() <= chance)) continue;
                    String facing = facings.get(random.nextInt(facings.size()));

                    Block block = LootChestBlockHandler.applyFor(Block.CHEST.withProperty("facing", facing), ChestLoot.loot);

                    batch.setBlock(x, y, z, block);
                }

                if(!instance.isChunkLoaded(x, z)) {
                    chunkFutures.add(instance.loadChunk(x, z));
                }
            }
        }

        CompletableFuture.allOf(chunkFutures.toArray(new CompletableFuture[0])).thenRun(() -> {
            batch.apply(instance, null);
        });
    }
}