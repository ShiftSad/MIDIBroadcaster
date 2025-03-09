package codes.shiftmc.mIDIPlayer;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

public final class MIDIPlayer extends JavaPlugin implements PluginMessageListener, Listener {

    @Override
    public void onEnable() {
        getServer().getMessenger().registerIncomingPluginChannel(this, "sadness:midi", this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "sadness:midi");

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().setOp(true);
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        var buffer = ByteBuffer.wrap(message);

        int command = buffer.getInt();
        int midiNote = buffer.getInt();
        int velocity = buffer.getInt();

        var location = player.getLocation();
        var mapped = MidiMapper.midiToNoteBlock(midiNote);
        if (mapped == null) return;

        var packet = MidiMapper.createSoundPacket(mapped.instrument(), mapped.pitch(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        ((CraftPlayer) player).getHandle().connection.sendPacket(packet);
    }
}
