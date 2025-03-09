package codes.shiftmc.midibroadcaster.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;

import javax.sound.midi.*;

public class MidibroadcasterClient implements ClientModInitializer {

    private static Synthesizer synthesizer;
    private static MidiChannel[] channels;

    @Override
    public void onInitializeClient() {
        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        for (int i = 0; i < infos.length; i++) System.out.println(i + ": " + infos[i].getName() + ":" + infos[i].getDescription());

        int selectedDeviceIndex = 5;
        try {
            // Open a Synthesizer for playing MIDI sounds
            synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            channels = synthesizer.getChannels(); // Get available channels

            var device = MidiSystem.getMidiDevice(infos[selectedDeviceIndex]);
            device.open();

            var transmitter = device.getTransmitter();
            transmitter.setReceiver(new MidiInputReceiver());

            ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
                device.close();
                transmitter.close();
                synthesizer.close();
            });
        } catch (MidiUnavailableException e) {
            throw new RuntimeException(e);
        }

        // Register plugin messaging channels
        PayloadTypeRegistry.playC2S().register(FabricPluginMessage.CHANNEL_ID, FabricPluginMessage.CODEC);
        PayloadTypeRegistry.playS2C().register(FabricPluginMessage.CHANNEL_ID, FabricPluginMessage.CODEC);
    }

    public static class MidiInputReceiver implements Receiver {
        @Override
        public void send(MidiMessage message, long timeStamp) {
            if (message instanceof ShortMessage sm) {
                int command = sm.getCommand();
                int note = sm.getData1();
                int velocity = sm.getData2();

                if (isPlayerInServer()) {
                    System.out.println("Sent " + note);

                    var buf = PacketByteBufs.create();
                    buf.writeInt(command);
                    buf.writeInt(note);
                    buf.writeInt(velocity);

                    var client = MinecraftClient.getInstance();
                    client.getNetworkHandler().sendPacket(
                            new CustomPayloadC2SPacket(
                                    new FabricPluginMessage(buf)
                            )
                    );
                }
            }
        }

        @Override
        public void close() {
            // Implement if necessary
        }

        /**
         * Checks if the player is currently in a multiplayer server.
         */
        private boolean isPlayerInServer() {
            var client = MinecraftClient.getInstance();
            return client.getNetworkHandler() != null && !client.isInSingleplayer();
        }
    }

}
