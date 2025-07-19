package com.progressionplus.mixin;

import com.progressionplus.data.PlayerComponents;
import com.progressionplus.network.ModMessages;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class syncUpgradesOnDeath {

    @Inject(method = "copyFrom", at = @At("TAIL"))
    private void copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        ServerPlayerEntity newPlayer = (ServerPlayerEntity) (Object) this;
        if (oldPlayer != null && oldPlayer.getUuid().equals(((ServerPlayerEntity) (Object) this).getUuid())) {
            // get old player's upgrades
            PlayerComponents.PLAYER_UPGRADES.get(newPlayer)
                .getPlayerUpgrade()
                .loadUpgrades(PlayerComponents.PLAYER_UPGRADES.get(oldPlayer).getPlayerUpgrade().getUpgrades());

            ModMessages.sendFullSync(newPlayer);
        }
    }
}
