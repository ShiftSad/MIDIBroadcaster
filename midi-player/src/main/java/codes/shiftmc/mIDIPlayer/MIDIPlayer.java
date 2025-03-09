package codes.shiftmc.mIDIPlayer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public final class MIDIPlayer extends JavaPlugin implements PluginMessageListener {

    @Override
    public void onEnable() {
        getServer().getMessenger().registerIncomingPluginChannel(this, "sadness:midi", this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "sadness:midi");
    }

    @Override
    public void onDisable() {
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        System.out.println(channel);
    }
}
