package cc.minetale.woolwars.vanilla;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.packet.server.ServerPacket;
import net.minestom.server.network.packet.server.play.BlockActionPacket;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.utils.PacketUtils;
import net.minestom.server.utils.time.TimeUnit;

@UtilityClass
public class VanillaUtils {

    public static void playChestAction(Instance instance, Point pos, boolean open) {
        PacketUtils.sendPacket(instance, new BlockActionPacket(pos, (byte) 1, (byte) (open ? 1 : 0), Block.CHEST));
        var type = open ? SoundEvent.BLOCK_CHEST_OPEN : SoundEvent.BLOCK_CHEST_CLOSE;
        var source = Sound.Source.BLOCK;
        instance.playSound(Sound.sound(type, source, 1.0F, 1.0F));
    }

    public static EventNode<Event> getDefaultEvents() {
        return EventNode.all("vanilla")
                .addListener(ItemDropEvent.class, event -> {
                    var player = event.getPlayer();
                    var instance = player.getInstance();
                    if (instance == null) { return; }

                    var playerPos = player.getPosition();
                    var itemPos = playerPos.add(0.0D, player.getEyeHeight(), 0.0D);
                    var itemVel = playerPos.direction().mul(7.0D);

                    var droppedItem = event.getItemStack();
                    var itemEntity = new ItemEntity(droppedItem);
                    itemEntity.setInstance(instance, itemPos);
                    itemEntity.setVelocity(itemVel);
                    itemEntity.setPickupDelay(500L, TimeUnit.MILLISECOND);
                })
                .addListener(EventListener.builder(PickupItemEvent.class)
                        .filter(event -> event.getEntity() instanceof Player)
                        .handler(event -> {
                            var player = (Player) event.getEntity();
                            var couldAdd = player.getInventory().addItemStack(event.getItemStack());
                            event.setCancelled(!couldAdd);
                        })
                        .build()
                );
    }

}
