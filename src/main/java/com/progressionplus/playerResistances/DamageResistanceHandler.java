package com.progressionplus.playerResistances;

import com.progressionplus.config.UpgradeConfig;
import com.progressionplus.data.PlayerComponents;
import com.progressionplus.upgrade.UpgradeType;
import net.minecraft.entity.damage.DamageEffects;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;

public class DamageResistanceHandler {

    /**
     * Вычисляет сопротивление урону на основе прокачек игрока
     * @param player игрок
     * @param damageSource источник урона
     * @param originalDamage изначальный урон
     * @return уменьшенный урон
     */
    public static float calculateDamageReduction(ServerPlayerEntity player, DamageSource damageSource, float originalDamage) {
        var upgradeData = PlayerComponents.PLAYER_UPGRADES.get(player);
        var playerUpgrade = upgradeData.getPlayerUpgrade();

        float damageReduction = 0.0f;

        // Проверяем каждый тип прокачки и применяем соответствующее сопротивление
        for (UpgradeType upgradeType : UpgradeType.values()) {
            int level = playerUpgrade.getLevel(upgradeType);
            if (level > 0) {
                damageReduction += calculateResistanceForUpgrade(upgradeType, level, damageSource);
            }
        }

        // Ограничиваем максимальное сопротивление (например, 80%)
        damageReduction = Math.min(damageReduction, 0.8f);

        // Применяем сопротивление
        float reducedDamage = originalDamage * (1.0f - damageReduction);

        return Math.max(reducedDamage, 0.0f);
    }

    /**
     * Вычисляет сопротивление для конкретной прокачки
     */
    private static float calculateResistanceForUpgrade(UpgradeType upgradeType, int level, DamageSource damageSource) {
        float resistancePerLevel = UpgradeConfig.getSettings(upgradeType).resistancePerLevel;
        float baseResistance = resistancePerLevel * level;

        switch (upgradeType) {
            case ENDURANCE -> {
                // Сопротивление физическому урону и урону от мобов
                if (damageSource.isOf(DamageTypes.MOB_ATTACK) ||
                        damageSource.isOf(DamageTypes.PLAYER_ATTACK) ||
                        damageSource.isOf(DamageTypes.GENERIC) ||
                        damageSource.isOf(DamageTypes.ARROW) ||
                        damageSource.isOf(DamageTypes.TRIDENT) ||
                        damageSource.isOf(DamageTypes.FALLING_BLOCK) ||
                        damageSource.isOf(DamageTypes.FALL) ||
                        damageSource.isOf(DamageTypes.FLY_INTO_WALL) ||
                        damageSource.isOf(DamageTypes.CRAMMING) ||
                        damageSource.isOf(DamageTypes.DROWN) ||
                        damageSource.isOf(DamageTypes.STARVE)||
                        damageSource.isOf(DamageTypes.MACE_SMASH) ||
                        damageSource.isOf(DamageTypes.MOB_PROJECTILE) ||
                        damageSource.isOf(DamageTypes.THORNS) ){
                    return baseResistance;
                }
            }
            case STRENGTH -> {
                // Сопротивление урону от взрывов
                if (damageSource.isOf(DamageTypes.EXPLOSION) ||
                        damageSource.isOf(DamageTypes.PLAYER_EXPLOSION) ||
                        damageSource.isOf(DamageTypes.SONIC_BOOM)) {
                    return baseResistance;
                }
            }
            case AGILITY -> {
                // Сопротивление урону от отравления
                if (damageSource.isOf(DamageTypes.STARVE)) {
                    return baseResistance;
                }
            }
            case LUCK -> {
                // Сопротивление магическому урону и эффектам
                if (damageSource.isOf(DamageTypes.MAGIC) ||
                        damageSource.isOf(DamageTypes.INDIRECT_MAGIC) ||
                        damageSource.isOf(DamageTypes.WITHER) ||
                        damageSource.isOf(DamageTypes.DRAGON_BREATH)||
                        damageSource.isOf(DamageTypes.ENDER_PEARL)) {
                    return baseResistance;
                }
            }
            case MINING_SPEED -> {
                // Сопротивление урону от окружающей среды
                if (damageSource.isOf(DamageTypes.LAVA) ||
                        damageSource.isOf(DamageTypes.IN_FIRE) ||
                        damageSource.isOf(DamageTypes.ON_FIRE) ||
                        damageSource.isOf(DamageTypes.CACTUS) ||
                        damageSource.isOf(DamageTypes.SWEET_BERRY_BUSH) ||
                        damageSource.isOf(DamageTypes.CAMPFIRE) ||
                        damageSource.isOf(DamageTypes.FIREBALL)) {
                    return baseResistance;
                }
            }
        }

        return 0.0f;
    }

    /**
     * Проверяет, может ли данная прокачка защитить от конкретного типа урона
     */
    public static boolean canResistDamage(UpgradeType upgradeType, DamageSource damageSource) {
        return calculateResistanceForUpgrade(upgradeType, 1, damageSource) > 0.0f;
    }
}