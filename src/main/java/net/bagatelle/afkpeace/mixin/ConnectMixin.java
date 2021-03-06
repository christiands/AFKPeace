package net.bagatelle.afkpeace.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.bagatelle.afkpeace.AFKPeace;
import net.bagatelle.afkpeace.util.DisconnectRetryScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ConnectMixin {

    public ServerInfo currentServer;

    // Sets the server data so that we know what to reconnect to.
    @Inject(method="onGameJoin", at=@At("HEAD"))
    private void onConnectedToServerEvent(GameJoinS2CPacket packet, CallbackInfo cbi) {
        MinecraftClient mc = MinecraftClient.getInstance();
        ServerInfo serverData = mc.getCurrentServerEntry();
        if(serverData == null) {
            currentServer = null;
        } else {
            currentServer = serverData;
        }
    }

    // Checks if we should try to automatically reconnect, and if not opens a custom screen with a reconnect button
    @Inject(method="onDisconnected", at=@At("HEAD"), cancellable=true)
    public void setAFKPeaceDisconnectScreen(Text reason, CallbackInfo cbi) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if(reason.getString().contains("Internal Exception: java.io.IOException: An existing connection was forcibly closed by the remote host") || reason.getString().contains("Timed out") && currentServer != null) {
            mc.disconnect();
            if(AFKPeace.activeStates.isReconnectOnTimeoutActive) {
                AFKPeace.connectUtil.autoReconnectToServer(currentServer);
            } else {
                mc.openScreen(new DisconnectRetryScreen(new MultiplayerScreen(new TitleScreen()), "disconnect.lost", reason, currentServer));
            }
            cbi.cancel();
        }
    }

    // Gets when the player's health changes, and logs the player out if it has taken damage
    @Inject(method="onHealthUpdate", at=@At("TAIL"))
    public void onPlayerHealthUpdate(HealthUpdateS2CPacket packet, CallbackInfo cbi) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if(packet.getHealth() != mc.player.getMaximumHealth() && AFKPeace.activeStates.isDamageProtectActive) {
            mc.getNetworkHandler().getConnection().disconnect(new TranslatableText("Logged out on damage"));
            mc.openScreen(new DisconnectRetryScreen(new MultiplayerScreen(new TitleScreen()), "disconnect.lost", new TranslatableText("Saved ya"), currentServer));
        }
    }
}