package cc.minetale.woolwars;

import cc.minetale.slime.core.GameState;
import cc.minetale.slime.game.Game;
import cc.minetale.slime.game.GameManager;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.metadata.other.FallingBlockMeta;
import net.minestom.server.instance.batch.AbsoluteBlockBatch;
import net.minestom.server.instance.block.Block;
import net.minestom.server.timer.ExecutionType;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class WWGame extends Game {
    private static final int blocksPerLevel = 7;
    private static final int ticksPerLevel = 20;

    private int floorLevel;
    private List<WWRisingBlock> risingBlocks;

    public WWGame(@NotNull GameManager gameManager, @NotNull GameState state) {
        super(gameManager, state);
    }

    public void startFloor() {
        this.risingBlocks = new LinkedList<>();

        MinecraftServer.getSchedulerManager().scheduleTask(this::increaseFloorLevel,
                                                           TaskSchedule.immediate(),
                                                           TaskSchedule.tick(ticksPerLevel),
                                                           ExecutionType.ASYNC);

//        MinecraftServer.getSchedulerManager().scheduleTask(() -> {
//            for(var fallingBlock : this.risingBlocks) {
//                var newVel = new Vec(0, 0.25, 0);
//                fallingBlock.setVelocity(newVel);
//            }
//        }, TaskSchedule.immediate(), TaskSchedule.tick(5), ExecutionType.ASYNC);
    }

//    public int getBlocksPerLevel() {
//        var map = this.mainInstance.getMap();
//        return (int) Math.floor((map.getMaxPos().blockX() - map.getMinPos().blockX()) / (double) blocksPerLevel);
//    }

    public void increaseFloorLevel() {
        var map = this.mainInstance.getMap();

//        var blocksPerLevel = getBlocksPerLevel();

        var minPos = map.getMinPos();
        var maxPos = map.getMaxPos();

        //Create batch
        var batch = new AbsoluteBlockBatch();

        var prevX = (this.floorLevel * blocksPerLevel) % maxPos.blockX();
        var prevY = (this.floorLevel * blocksPerLevel) / maxPos.blockX();

        var from = this.floorLevel * blocksPerLevel;

        //Increase floor level
        this.floorLevel++;

        var newX = (this.floorLevel * blocksPerLevel) % maxPos.blockX();
        var newY = (this.floorLevel * blocksPerLevel) / maxPos.blockX();

        var to = this.floorLevel * blocksPerLevel;

        //Fill batch
        for(int i = from; i < to; i++) {
            var x = i % maxPos.blockX();
            var y = i / maxPos.blockX();

            for(int z = minPos.blockZ(); z < maxPos.blockZ(); z++) {
                batch.setBlock(x, y, z, Block.RED_CONCRETE);
            }
        }

        for(var risingBlock : this.risingBlocks) {
            risingBlock.remove();
        }

        //Apply batch
        //After setting the blocks, create new rising blocks
        batch.apply(this.mainInstance, () -> {
            this.mainInstance.scheduler().scheduleTask(() -> {
                var nextTo = (this.floorLevel + 1) * blocksPerLevel;
                for(int i = to; i < nextTo; i++) {
                    var x = i % maxPos.blockX();
                    var y = i / maxPos.blockX();

                    for(int z = minPos.blockZ(); z < maxPos.blockZ(); z++) {
                        var risingBlock = new WWRisingBlock(22.5 / ticksPerLevel);
                        ((FallingBlockMeta) risingBlock.getEntityMeta()).setBlock(Block.RED_CONCRETE_POWDER);
                        risingBlock.setInstance(this.mainInstance, new Pos(x + .5, y - 1, z + .5));

                        this.risingBlocks.add(risingBlock);
                    }
                }
            }, TaskSchedule.tick(5), TaskSchedule.stop(), ExecutionType.ASYNC);
        });
    }
}
