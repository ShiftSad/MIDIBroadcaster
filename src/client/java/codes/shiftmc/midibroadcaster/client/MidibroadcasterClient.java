package codes.shiftmc.midibroadcaster.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

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
    }

    public static class MidiInputReceiver implements Receiver {
        @Override
        public void send(MidiMessage message, long timeStamp) {
            if (message instanceof ShortMessage sm) {
                int command = sm.getCommand();
                int note = sm.getData1();
                int velocity = sm.getData2();

                if (channels != null && channels.length > 0) {
                    if (command == ShortMessage.NOTE_ON) {
                        channels[0].noteOn(note, velocity);
                        System.out.println("Playing Note: " + note + " Velocity: " + velocity);
                    } else if (command == ShortMessage.NOTE_OFF) {
                        channels[0].noteOff(note);
                        System.out.println("Stopping Note: " + note);
                    }
                }
            }
        }

        @Override
        public void close() {
            // Implement if necessary
        }
    }

}
