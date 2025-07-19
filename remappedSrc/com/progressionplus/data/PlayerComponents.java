package com.progressionplus.data;

import com.progressionplus.Progressionplus;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;

import static com.progressionplus.Progressionplus.LOGGER;

public final class PlayerComponents implements EntityComponentInitializer {
    public static final ComponentKey<PlayerUpgradeData> PLAYER_UPGRADES =
            ComponentRegistry.getOrCreate(Identifier.of(Progressionplus.MOD_ID, "upgrades"), PlayerUpgradeData.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        LOGGER.info("Registering Player Upgrade Data component for Progression+");
        registry.beginRegistration(PlayerEntity.class, PLAYER_UPGRADES)
                .impl(PlayerUpgradeData.class)
                .end(PlayerUpgradeData::new);
    }
}