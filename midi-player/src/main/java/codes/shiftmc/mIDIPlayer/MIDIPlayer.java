package codes.shiftmc.mIDIPlayer;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.concurrent.ThreadLocalRandom;

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
        if (!player.hasPermission("midi.play")) return;

        int command = buffer.getInt();
        int midiNote = buffer.getInt();
        int velocity = buffer.getInt();

        if (command == 128) return; // When stop pressing
        var location = player.getLocation();
        var mapped = MidiMapper.midiToNoteBlock(midiNote);
        if (mapped == null) return;

        System.out.println(command + ":" + midiNote + ":" + velocity);

        var packet = MidiMapper.createSoundPacket(mapped.instrument(), mapped.pitch(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (player == p) continue;
            ((CraftPlayer) p).getHandle().connection.sendPacket(packet);
        }

        player.getWorld().spawnParticle(Particle.NOTE,
                player.getLocation().clone().add(new Vector(0, 2, 0)),
                0,
                ThreadLocalRandom.current().nextFloat() * 2.0,
                0, 0
        );
    }
}
