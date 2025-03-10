package codes.shiftmc.midibroadcaster.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MidiMapper {

    public record NoteMapping(String instrument, int noteBlockNote, float pitch) {
    }

    /**
     * Returns a NoteMapping for the given MIDI note [30..102],
     * or null if the note should not be played.
     */
    public static NoteMapping midiToNoteBlock(int midiNote) {
        final String instrument;
        final int noteBlockNote;

        if (midiNote >= 30 && midiNote <= 53) {
            // Bass (wood)
            instrument = "bass";
            noteBlockNote = midiNote - 30;
        } else if (midiNote >= 54 && midiNote <= 78) {
            // Piano/harp
            instrument = "piano";
            noteBlockNote = midiNote - 54;
        } else if (midiNote >= 79 && midiNote <= 102) {
            // Bell
            instrument = "bell";
            noteBlockNote = midiNote - 78;
        } else return null;

        float pitch = (float) (0.5 * Math.pow(2.0, noteBlockNote / 12.0));
        return new NoteMapping(instrument, noteBlockNote, pitch);
    }

    @Environment(EnvType.CLIENT)
    public static void playNoteBlockSound(World world, String instrument, float pitch, BlockPos pos) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.world == null) return;

        client.execute(() -> {
            RegistryEntry<SoundEvent> sound = switch (instrument.toLowerCase()) {
                case "piano" -> SoundEvents.BLOCK_NOTE_BLOCK_HARP;
                case "bass"  -> SoundEvents.BLOCK_NOTE_BLOCK_BASS;
                case "bell"  -> SoundEvents.BLOCK_NOTE_BLOCK_BELL;
                default      -> SoundEvents.BLOCK_NOTE_BLOCK_HARP;
            };

            client.world.playSound(client.player, pos, sound.value(), SoundCategory.BLOCKS, 3.0f, pitch);
        });
    }
}