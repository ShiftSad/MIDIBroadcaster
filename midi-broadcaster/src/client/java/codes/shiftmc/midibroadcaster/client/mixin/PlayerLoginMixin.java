package codes.shiftmc.midibroadcaster.client.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLoginNetworkHandler.class)
public class PlayerLoginMixin {

    @Shadow @Final MinecraftServer server;

    @Inject(method = "sendSuccessPacket", at = @At("HEAD"))
    private void onPlayerLogin(CallbackInfo ci) {
        System.out.println("A player has logged into the server!");

        server.sendMessage(Text.of("Penis!"));
    }
}