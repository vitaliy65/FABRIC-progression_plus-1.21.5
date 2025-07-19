package com.progressionplus.network;

import com.progressionplus.config.UpgradeConfig;
import com.progressionplus.data.PlayerComponents;
import com.progressionplus.upgrade.UpgradeType;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;

import static com.progressionplus.Progressionplus.LOGGER;

public class ModMessages {
    public static final int damage = 1;
    public static final double movement_speed = 0.1; // Базовая скорость игрока в Minecraft
    public static final int mining_speed = 1;

    public static void init() {
        // Register for both directions
        PayloadTypeRegistry.playC2S().register(UpgradePayload.ID, UpgradePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(UpgradePayload.ID, UpgradePayload.CODEC);

        // Register server-side handler
        ServerPlayNetworking.registerGlobalReceiver(UpgradePayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                var player = context.player();
                var upgradeType = payload.upgradeType();
                var playerUpgradeData = PlayerComponents.PLAYER_UPGRADES.get(player);

                if (playerUpgradeData.getPlayerUpgrade().tryUpgrade(upgradeType, player)) {
                    // Update max health attribute
                    int upgradeLevel = playerUpgradeData.getPlayerUpgrade().getLevel(upgradeType);

                    handleUpgradeTypeUpgrade(upgradeType, player, upgradeLevel);

                    // Send update to client
                    ServerPlayNetworking.send(player, new UpgradePayload(upgradeType, upgradeLevel, player.getUuid()));
                }
            });
        });
    }

    // Метод для отправки всех данных игрока
    public static void sendFullSync(ServerPlayerEntity player) {
        var upgradeData = PlayerComponents.PLAYER_UPGRADES.get(player);
        var playerUpgrade = upgradeData.getPlayerUpgrade();

        for (UpgradeType type : UpgradeType.values()) {
            int level = playerUpgrade.getLevel(type);
            if (level > 0) {
                ServerPlayNetworking.send(player, new UpgradePayload(type, level, player.getUuid()));
            }
        }
    }

    // Вызываем при входе игрока в мир
    public static void onPlayerJoin(ServerPlayerEntity player) {
        // Восстанавливаем атрибуты на сервере
        restorePlayerAttributes(player);

        // Отправляем синхронизацию клиенту
        sendFullSync(player);
    }

    // Восстанавливает атрибуты игрока на основе сохраненных данных
    private static void restorePlayerAttributes(ServerPlayerEntity player) {
        var upgradeData = PlayerComponents.PLAYER_UPGRADES.get(player);
        var playerUpgrade = upgradeData.getPlayerUpgrade();

        // Восстанавливаем все атрибуты
        for (UpgradeType type : UpgradeType.values()) {
            int level = playerUpgrade.getLevel(type);
            if (level > 0) {
                handleUpgradeTypeUpgrade(type, player, level);
            }
        }
    }

    static void handleUpgradeTypeUpgrade(UpgradeType upgrade, ServerPlayerEntity player, int level) {
        switch (upgrade) {
            case ENDURANCE -> {
                float bonusHealth = UpgradeConfig.getSettings(UpgradeType.ENDURANCE).bonusPerLevel * level;
                float newMaxHealth = 20 + bonusHealth;

                var healthAttribute = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
                if (healthAttribute != null) {
                    healthAttribute.setBaseValue(newMaxHealth);
                    // Восстанавливаем здоровье до максимума только если текущее здоровье меньше нового максимума
                    if (player.getHealth() < newMaxHealth) {
                        player.setHealth(newMaxHealth);
                    }
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
                // Handle LUCK upgrade
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
}