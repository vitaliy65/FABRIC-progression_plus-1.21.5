package com.progressionplus.network;

import com.progressionplus.config.UpgradeConfig;
import com.progressionplus.data.PlayerComponents;
import com.progressionplus.upgrade.UpgradeType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.attribute.EntityAttributes;

import java.util.UUID;

import static com.progressionplus.Progressionplus.LOGGER;

public class ClientModMessages {
    public static final int damage = 1;
    public static final double movement_speed = 0.1; // Базовая скорость игрока в Minecraft
    public static final int mining_speed = 1;

    public static void initClient() {
        System.out.println("Initializing client-side networking...");

        ClientPlayNetworking.registerGlobalReceiver(UpgradePayload.ID, (payload, context) -> {
            UpgradeType upgradeType = payload.upgradeType();
            int level = payload.level();
            UUID playerUuid = payload.playerUuid();

            context.client().execute(() -> {
                if (context.client().player != null && context.client().player.getUuid().equals(playerUuid)) {
                    var upgradeData = PlayerComponents.PLAYER_UPGRADES.get(context.client().player);
                    var upgrades = upgradeData.getPlayerUpgrade().getUpgrades();
                    upgrades.put(upgradeType.name(), level);
                    upgradeData.getPlayerUpgrade().loadUpgrades(upgrades);

                    syncUpgradeWithClient(upgradeType, context.client().player, level);
                }
            });
        });

        LOGGER.info("Client-side networking initialized successfully.");
    }

    public static void syncUpgradeWithClient(UpgradeType upgrade, ClientPlayerEntity player, int level){
        switch (upgrade) {
            case ENDURANCE -> {
                float bonusHealth = UpgradeConfig.getSettings(UpgradeType.ENDURANCE).bonusPerLevel * level;
                float newMaxHealth = 20 + bonusHealth;

                var healthAttribute = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
                if (healthAttribute != null) {
                    healthAttribute.setBaseValue(newMaxHealth);
                    // Синхронизируем здоровье с сервером
                    player.setHealth(player.getHealth());
                }
            }
            case STRENGTH -> {
                float damageBonusPerLevel = UpgradeConfig.getSettings(UpgradeType.STRENGTH).bonusPerLevel * level;
                double bonusDamage = damageBonusPerLevel * damage;

                var damageAttribute = player.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE);
                if (damageAttribute != null) {
                    damageAttribute.setBaseValue(damage + bonusDamage);
                }
            }
            case AGILITY -> {
                float speedBonusPerLevel = UpgradeConfig.getSettings(UpgradeType.AGILITY).bonusPerLevel * level;
                double bonusSpeed = speedBonusPerLevel * movement_speed;

                var speedAttribute = player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
                if (speedAttribute != null) {
                    speedAttribute.setBaseValue(movement_speed + bonusSpeed);
                }
            }
            case LUCK -> {
                double bonusLuck = UpgradeConfig.getSettings(UpgradeType.LUCK).bonusPerLevel * level;

                var luckAttribute = player.getAttributeInstance(EntityAttributes.LUCK);
                if (luckAttribute != null) {
                    luckAttribute.setBaseValue(bonusLuck);
                }
            }
            case MINING_SPEED -> {
                float miningSpeedBonusPerLevel = UpgradeConfig.getSettings(UpgradeType.MINING_SPEED).bonusPerLevel * level;
                double bonusMiningSpeed = miningSpeedBonusPerLevel * mining_speed;

                var miningSpeedAttribute = player.getAttributeInstance(EntityAttributes.BLOCK_BREAK_SPEED);
                if (miningSpeedAttribute != null) {
                    miningSpeedAttribute.setBaseValue(mining_speed + bonusMiningSpeed);
                }
            }
        }
    }

    public static void sendSyncPacketToServer(UpgradeType upgradeType, ClientPlayerEntity player) {
        if (player != null) {
            var upgradeData = PlayerComponents.PLAYER_UPGRADES.get(player);
            int level = upgradeData.getPlayerUpgrade().getLevel(upgradeType);
            ClientPlayNetworking.send(new UpgradePayload(upgradeType, level, player.getUuid()));
        }
    }
}