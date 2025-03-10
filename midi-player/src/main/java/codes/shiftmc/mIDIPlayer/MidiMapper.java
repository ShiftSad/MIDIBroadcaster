package codes.shiftmc.mIDIPlayer;

import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

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

    public static ClientboundSoundPacket createSoundPacket(
            String instrument,
            float pitch,
            int x, int y, int z
    ) {
        Holder.Reference<SoundEvent> sound = switch (instrument.toLowerCase()) {
            case "piano" -> SoundEvents.NOTE_BLOCK_HARP;  // or "minecraft:block.note_block.harp"
            case "bass"  -> SoundEvents.NOTE_BLOCK_BASS;  // or "minecraft:block.note_block.bass"
            case "bell"  -> SoundEvents.NOTE_BLOCK_BELL;  // or "minecraft:block.note_block.bell"
            default      -> SoundEvents.NOTE_BLOCK_HARP;
        };

        var source = SoundSource.BLOCKS;

        return new ClientboundSoundPacket(
                sound,
                source,
                x, y, z,
                3.0f,
                pitch,
                69
        );
    }
}