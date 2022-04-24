package cc.minetale.woolwars.vanilla.blocks;

import cc.minetale.commonlib.util.CollectionsUtil;
import cc.minetale.slime.loot.Loot;
import cc.minetale.slime.loot.context.LootContext;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.NamespaceID;
import org.jglrxavpok.hephaistos.nbt.NBT;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.NBTType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
public class LootChestBlockHandler extends ChestBlockHandler {
    private boolean hasGenerated;
    private Loot loot;

    public LootChestBlockHandler(Loot loot) {
        super();
        this.loot = loot;
    }

    public static Block applyFor(Block baseBlock, Loot loot) {
        return baseBlock
                .withTag(TAG_ITEMS, NBT.List(NBTType.TAG_Compound))
                .withHandler(new LootChestBlockHandler(loot));
    }

    @Override
    protected List<NBTCompound> getItems(Instance instance, Point pos, Player opener) {
        if(this.loot != null && !this.hasGenerated) {
            List<ItemStack> generated = new ArrayList<>();
            this.loot.generateLoot(generated, new LootContext.ChestCtx(NamespaceID.from("chest"), instance, new Vec(pos.x(), pos.y(), pos.z()), opener));

            var airNbt = ItemStack.AIR.toItemNBT();

            var size = InventoryType.CHEST_3_ROW.getSize();
            List<NBTCompound> items = CollectionsUtil.randomSpread(generated, size)
                    .stream()
                    .map(item -> {
                        if(item == null) { return airNbt; }
                        return item.toItemNBT();
                    })
                    .collect(Collectors.toList());

            var block = instance.getBlock(pos);
            if(block.handler() instanceof ContainerHandler handler) {
                handler.setDropContentsOnDestroy(false);
                instance.setBlock(pos, block
                        .withTag(TAG_ITEMS, NBT.List(NBTType.TAG_Compound, items))
                        .withHandler(handler));
                handler.setDropContentsOnDestroy(true);
                this.hasGenerated = true;
            };
        }

        return super.getItems(instance, pos, opener);
    }
}
