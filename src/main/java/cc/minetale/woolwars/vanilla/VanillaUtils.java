package cc.minetale.woolwars.vanilla;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.server.play.BlockActionPacket;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.utils.PacketUtils;

@UtilityClass
public class VanillaUtils {

    public static void playChestAction(Instance instance, Point pos, boolean open) {
        PacketUtils.sendPacket(instance, new BlockActionPacket(pos, (byte) 1, (byte) (open ? 1 : 0), Block.CHEST));
        var type = open ? SoundEvent.BLOCK_CHEST_OPEN : SoundEvent.BLOCK_CHEST_CLOSE;
        var source = Sound.Source.BLOCK;
        instance.playSound(Sound.sound(type, source, 1.0F, 1.0F));
    }

}
