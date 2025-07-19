package com.progressionplus.network;

import com.progressionplus.config.UpgradeConfig;
import com.progressionplus.data.PlayerComponents;
import com.progressionplus.upgrade.UpgradeType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.network.PacketByteBuf;

import static com.progressionplus.Progressionplus.LOGGER;

public class ClientModMessages {
    public static final int damage = 1;
    public static final double movement_speed = 0.1;
    public static final int mining_speed = 1;

    public static void initClient() {
        ClientPlayNetworking.registerGlobalReceiver(UpgradePayload.ID, (client, handler, buf, responseSender) -> {
            UpgradePayload payload = UpgradePayload.read(buf);
            UpgradeType upgradeType = payload.getUpgradeType();
            int level = payload.getLevel();
            var playerUuid = payload.getPlayerUuid();

            client.execute(() -> {
                var player = client.player;
                if (player != null && player.getUuid().equals(playerUuid)) {
                    var upgradeData = PlayerComponents.PLAYER_UPGRADES.get(player);
                    var upgrades = upgradeData.getPlayerUpgrade().getUpgrades();
                    upgrades.put(upgradeType.name(), level);
                    upgradeData.getPlayerUpgrade().loadUpgrades(upgrades);

                    syncUpgradeWithClient(upgradeType, player, level);
                }
            });
        });

        LOGGER.info("Client-side networking initialized successfully.");
    }

    public static void syncUpgradeWithClient(UpgradeType upgrade, ClientPlayerEntity player, int level) {
        switch (upgrade) {
            case ENDURANCE -> {
                float bonusHealth = UpgradeConfig.getSettings(UpgradeType.ENDURANCE).bonusPerLevel * level;
                float newMaxHealth = 20 + bonusHealth;

                var healthAttribute = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
                if (healthAttribute != null) {
                    healthAttribute.setBaseValue(newMaxHealth);
                    player.setHealth(player.getHealth());
                }
            }
            case STRENGTH -> {
                float damageBonusPerLevel = UpgradeConfig.getSettings(UpgradeType.STRENGTH).bonusPerLevel * level;
                double bonusDamage = damageBonusPerLevel * damage;

                var damageAttribute = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                if (damageAttribute != null) {
                    damageAttribute.setBaseValue(damage + bonusDamage);
                }
            }
            case AGILITY -> {
                float speedBonusPerLevel = UpgradeConfig.getSettings(UpgradeType.AGILITY).bonusPerLevel * level;
                double bonusSpeed = speedBonusPerLevel * movement_speed;

                var speedAttribute = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
                if (speedAttribute != null) {
                    speedAttribute.setBaseValue(movement_speed + bonusSpeed);
                }
            }
            case LUCK -> {
                double bonusLuck = UpgradeConfig.getSettings(UpgradeType.LUCK).bonusPerLevel * level;

                var luckAttribute = player.getAttributeInstance(EntityAttributes.GENERIC_LUCK);
                if (luckAttribute != null) {
                    luckAttribute.setBaseValue(bonusLuck);
                }
            }
            case MINING_SPEED -> {
                float miningSpeedBonusPerLevel = UpgradeConfig.getSettings(UpgradeType.MINING_SPEED).bonusPerLevel * level;
                double bonusMiningSpeed = miningSpeedBonusPerLevel * mining_speed;

                // No vanilla attribute for mining speed
            }
        }
    }

    public static void sendSyncPacketToServer(UpgradeType upgradeType, ClientPlayerEntity player) {
        if (player != null) {
            var upgradeData = PlayerComponents.PLAYER_UPGRADES.get(player);
            int level = upgradeData.getPlayerUpgrade().getLevel(upgradeType);
            PacketByteBuf buf = PacketByteBufs.create();
            new UpgradePayload(upgradeType, level, player.getUuid()).write(buf);
            ClientPlayNetworking.send(UpgradePayload.ID, buf);
        }
    }
}