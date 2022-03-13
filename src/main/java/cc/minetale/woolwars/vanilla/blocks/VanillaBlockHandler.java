package cc.minetale.woolwars.vanilla.blocks;


import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

public abstract class VanillaBlockHandler implements BlockHandler {
    protected final @NotNull Block baseBlock;
    protected final @NotNull NamespaceID namespaceID;

    protected VanillaBlockHandler(@NotNull Block baseBlock) {
        this.baseBlock = baseBlock;
        this.namespaceID = baseBlock.namespace();
    }

    public @NotNull NamespaceID getNamespaceId() {
        return this.namespaceID;
    }

//    public void onBlockUpdate(BlockUpdate blockUpdate) {}

    public @NotNull Map<Tag<?>, ?> defaultTagValues() {
        return Collections.emptyMap();
    }
}

