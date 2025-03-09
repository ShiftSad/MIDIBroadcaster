package codes.shiftmc.midibroadcaster.client;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;


public class FabricPluginMessage implements CustomPayload {

    public static final PacketCodec<RegistryByteBuf, FabricPluginMessage> CODEC = PacketCodec.of(
            (value, buf) -> writeBytes(buf, value.getData()),
            FabricPluginMessage::new
    );
    public static final Id<FabricPluginMessage> CHANNEL_ID = new Id<>(
            Identifier.of("sadness", "midi")
    );

    private final byte[] data;

    public FabricPluginMessage(@NotNull PacketByteBuf buf) {
        this(getWrittenBytes(buf));
    }

    public FabricPluginMessage(byte[] data) {
        this.data = data;
    }

    private static byte[] getWrittenBytes(@NotNull PacketByteBuf buf) {
        byte[] bs = new byte[buf.readableBytes()];
        buf.readBytes(bs);
        return bs;
    }

    private static void writeBytes(@NotNull PacketByteBuf buf, byte[] v) {
        buf.writeBytes(v);
    }

    @Override
    public Id<FabricPluginMessage> getId() {
        return CHANNEL_ID;
    }

    public byte[] getData() {
        return data;
    }
}
