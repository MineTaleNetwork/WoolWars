package cc.minetale.woolwars.vanilla.blocks;

import cc.minetale.woolwars.vanilla.inventory.ItemStackUtils;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.util.List;
import java.util.Random;

public abstract class ContainerHandler extends VanillaBlockHandler {

    protected static final Random rng = new Random();

    public ContainerHandler(Block baseBlock) {
        super(baseBlock);
    }

    public abstract boolean dropContentsOnDestroy();
    public abstract void setDropContentsOnDestroy(boolean drop);

    public void onDestroy(@NotNull BlockHandler.Destroy destroy) {
        if (!dropContentsOnDestroy()) { return; }

        var block = destroy.getBlock();
        var pos = destroy.getBlockPosition();

        List<NBTCompound> items = getItems(destroy.getInstance(), pos, null);
        for (var item : items) {
            var itemStack = ItemStackUtils.fromNBTCompound(item);
            if (itemStack == null) { continue; }

            var entity = new ItemEntity(itemStack);
            entity.setInstance(destroy.getInstance());
            entity.teleport(new Pos(pos.x() + rng.nextDouble(), pos.y() + 0.5D, pos.z() + rng.nextDouble()));
        }
    }

    public abstract List<NBTCompound> getAllItems(Instance instance, Point pos, Player opener);

    protected abstract List<NBTCompound> getItems(Instance instance, Point pos, Player opener);

}