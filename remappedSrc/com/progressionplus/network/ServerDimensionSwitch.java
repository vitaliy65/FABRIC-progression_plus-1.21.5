package com.progressionplus.network;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class ServerDimensionSwitch {

    public static void Register(Entity entity, ServerWorld world) {
        if (entity instanceof ServerPlayerEntity player) {
            ModMessages.sendFullSync(player);
        }
    }
}
