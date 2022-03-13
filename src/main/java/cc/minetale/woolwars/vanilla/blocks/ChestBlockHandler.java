package cc.minetale.woolwars.vanilla.blocks;

import cc.minetale.woolwars.vanilla.VanillaUtils;
import cc.minetale.woolwars.vanilla.inventory.ChestInventory;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.Direction;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.NBTList;
import org.jglrxavpok.hephaistos.nbt.NBTType;

import java.util.List;
import java.util.function.Function;

public class ChestBlockHandler extends ContainerHandler {

    public static final Tag<NBTList<NBTCompound>> TAG_ITEMS = Tag.NBT("Items");

    private boolean dropItemsOnDestroy;

    public ChestBlockHandler() {
        super(Block.CHEST);
    }

    public static Block applyFor(Block baseBlock) {
        return baseBlock
                .withTag(TAG_ITEMS, NBT.List(NBTType.TAG_Compound))
                .withHandler(new ChestBlockHandler());
    }

    @Override
    public void onPlace(@NotNull BlockHandler.Placement placement) {
        var block = placement.getBlock();
        NBTList<NBTCompound> items = block.getTag(TAG_ITEMS);
        if (items != null)
            return; //TODO Something's not right?

        var instance = placement.getInstance();
        var pos = placement.getBlockPosition();
        NBTList<NBTCompound> nbtCompoundNBTList = new NBTList<>(NBTType.TAG_Compound);
        var blockToSet = block.withTag(TAG_ITEMS, nbtCompoundNBTList);

        //TODO Set the block?
    }

    @Override
    public boolean onInteract(@NotNull BlockHandler.Interaction interaction) {
        var instance = interaction.getInstance();
        var pos = interaction.getBlockPosition();
        var above = instance.getBlock(pos.blockX(), pos.blockY() + 1, pos.blockZ());
        if (above.isSolid()) { return false; }

        var block = interaction.getBlock();
        var player = interaction.getPlayer();

        var chestInventory = new ChestInventory(instance, pos, getItems(instance, pos, player));

        player.openInventory(chestInventory);
        VanillaUtils.playChestAction(instance, pos, true);

        return true;
    }

    @Override
    public List<NBTCompound> getAllItems(Instance instance, Point pos, Player player) {
        var block = instance.getBlock(pos);
        List<NBTCompound> items = getItems(instance, pos, player);

        var positionOfOtherChest = pos;
        var facing = Direction.valueOf(block.getProperty("facing").toUpperCase());

        var type = block.getProperty("type");
        switch (type) {
            case "single" -> { return items; }
            case "left" -> positionOfOtherChest = positionOfOtherChest.add(-facing.normalZ(), 0.0D, facing.normalX());
            case "right" -> positionOfOtherChest = positionOfOtherChest.add(facing.normalZ(), 0.0D, -facing.normalX());
            default -> throw new IllegalArgumentException("Invalid chest type: " + type);
        }

        var otherBlock = instance.getBlock(positionOfOtherChest);
        var handler = otherBlock.handler();
        if (handler instanceof ContainerHandler) {
            List<NBTCompound> existingItems = getItems(instance, pos, player);
            for (NBTCompound item : items) {
                existingItems.add(item);
            }
            return existingItems;
        }

        return items;
    }

    @Override
    protected List<NBTCompound> getItems(Instance instance, Point pos, Player opener) {
        return instance.getBlock(pos).getTag(TAG_ITEMS).asListView();
    }

    @Override
    public boolean dropContentsOnDestroy() {
        return this.dropItemsOnDestroy;
    }

    @Override
    public void setDropContentsOnDestroy(boolean drop) {
        this.dropItemsOnDestroy = drop;
    }

    public byte getBlockEntityAction() {
        return 1;
    }
}