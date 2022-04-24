package cc.minetale.woolwars.vanilla.inventory;

import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.TagReadable;
import net.minestom.server.tag.TagSerializer;
import net.minestom.server.tag.TagWritable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;
import org.jglrxavpok.hephaistos.nbt.mutable.MutableNBTCompound;

public class ItemStackUtils {

    public static @Nullable ItemStack fromNBTCompound(@NotNull NBTCompound tag) {
        var id = tag.getString("id");
        if (id == null) { return null; }

        var material = Material.fromNamespaceId(id);
        if (material == null) { return null; }

//        if (material == Material.AIR)
//            material = Material.STONE;
        if (material == Material.AIR)
            return ItemStack.AIR;

        var count = tag.getByte("Count");
        var compound = tag.getCompound("tag");
        if (count == null || compound == null)
            return null;

        return ItemStack.fromNBT(material, compound, count);
    }

    public static @NotNull NBTCompound toNBTCompound(@NotNull ItemStack itemStack) {
        var compound = new MutableNBTCompound();
        compound.setString("id", itemStack.getMaterial().namespace().toString());
        compound.setByte("Count", (byte) itemStack.getAmount());
        compound.set("tag", itemStack.getMeta().toNBT());
        return compound.toCompound();
    }

    public static Tag<ItemStack> itemStackTag(@NotNull String key) {
        return Tag.Structure(key, new ItemStackSerializer(key));
    }

    private static class ItemStackSerializer implements TagSerializer<ItemStack> {
        private final Tag<NBTCompound> tag;

        public ItemStackSerializer(String key) {
            this.tag = Tag.NBT(key);
        }

        @Nullable
        public ItemStack read(@NotNull TagReadable reader) {
            var compound = reader.getTag(this.tag);
            return (compound != null) ? ItemStackUtils.fromNBTCompound(compound) : null;
        }

        public void write(@NotNull TagWritable writer, @Nullable ItemStack value) {
            if (value == null) {
                writer.setTag(this.tag, null);
                return;
            }
            writer.setTag(this.tag, ItemStackUtils.toNBTCompound(value));
        }
    }

}