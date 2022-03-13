package cc.minetale.woolwars.vanilla.inventory;

import cc.minetale.woolwars.vanilla.VanillaUtils;
import cc.minetale.woolwars.vanilla.blocks.ChestBlockHandler;
import cc.minetale.woolwars.vanilla.blocks.ContainerHandler;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.NBTType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static cc.minetale.woolwars.vanilla.blocks.ChestBlockHandler.TAG_ITEMS;

public class ChestInventory extends Inventory {
    protected final Instance instance;
    protected final Point pos;
    protected final List<NBTCompound> items;

    public ChestInventory(@NotNull Instance instance, @NotNull Point pos, @NotNull List<NBTCompound> items) {
        super(InventoryType.CHEST_3_ROW, Component.text("chest"));
        this.instance = instance;
        this.pos = pos;
        this.items = new ArrayList<>(Collections.nCopies(InventoryType.CHEST_3_ROW.getSize(), NBTCompound.EMPTY));
        Collections.copy(this.items, items);
    }

    public boolean removeViewer(@NotNull Player player) {
        if (getViewers().size() <= 1)
            VanillaUtils.playChestAction(this.instance, this.pos, false);

        return super.removeViewer(player);
    }

    @NotNull
    public ItemStack getItemStack(int slot) {
        var item = this.items.get(slot);
        if (item.isEmpty())
            return ItemStack.AIR;

        var itemStack = ItemStackUtils.fromNBTCompound(item);
        if (itemStack == null || itemStack.isAir() || itemStack.getAmount() == 0)
            return ItemStack.AIR;

        return itemStack;
    }

    public void setItemStack(int slot, @NotNull ItemStack itemStack) {
        var compound = ItemStackUtils.toNBTCompound(itemStack);
        if (itemStack.isAir() || itemStack.getAmount() == 0)
            compound = NBTCompound.EMPTY;

        this.items.set(slot, compound);

        var prevBlock = this.instance.getBlock(this.pos);
        var block = prevBlock
                .withTag(ChestBlockHandler.TAG_ITEMS, NBT.List(NBTType.TAG_Compound, this.items))
                .withHandler(prevBlock.handler());

        var chunk = this.instance.getChunkAt(this.pos);
        if (chunk != null)
            chunk.setBlock(this.pos, block);
    }

    @NotNull
    public ItemStack[] getItemStacks() {
        List<ItemStack> list = new LinkedList<>();
        for (var item : this.items) {
            if (item.isEmpty()) {
                list.add(ItemStack.AIR);
                continue;
            }
            var itemStack = ItemStackUtils.fromNBTCompound(item);
            if (itemStack != null)
                list.add(itemStack);
        }
        return list.toArray(new ItemStack[0]);
    }
}