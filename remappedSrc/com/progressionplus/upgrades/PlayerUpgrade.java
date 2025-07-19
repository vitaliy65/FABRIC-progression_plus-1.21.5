package com.progressionplus.upgrades;

import com.progressionplus.config.UpgradeConfig;
import com.progressionplus.upgrade.UpgradeType;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class PlayerUpgrade {
    private final Map<UpgradeType, Integer> upgrades = new EnumMap<>(UpgradeType.class);
    private static final int MAX_LEVEL = 20;

    public void addLevel(UpgradeType upgradeType) {
        upgrades.put(upgradeType, upgrades.getOrDefault(upgradeType, 0) + 1);
    }

    public int getLevel(UpgradeType upgradeType) {
        return upgrades.getOrDefault(upgradeType, 0);
    }

    public float getUpgradeBonus(UpgradeType upgradeType) {
        return UpgradeConfig.getSettings(upgradeType).bonusPerLevel * getLevel(upgradeType);
    }

    public float getUpgradeResistanceBonus(UpgradeType upgradeType) {
        return UpgradeConfig.getSettings(upgradeType).resistancePerLevel;
    }

    public static int getMaxLevel(){
        return MAX_LEVEL;
    }

    public int getTotalLevels() {
        return upgrades.values().stream().mapToInt(Integer::intValue).sum();
    }

    public int getRequiredExp(int totalLevel) {
        return UpgradeConfig.calculateRequiredExp(totalLevel);
    }

    public boolean canUpgrade(UpgradeType upgradeType, ServerPlayerEntity player) {
        int currentLevel = getLevel(upgradeType);
        if (currentLevel >= MAX_LEVEL) {
            return false;
        }

        // Use total level for cost calculation
        int totalLevel = getTotalLevels();
        int requiredExp = getRequiredExp(totalLevel);
        return player.totalExperience >= requiredExp;
    }

    public boolean tryUpgrade(UpgradeType upgradeType, ServerPlayerEntity player) {
        if (!canUpgrade(upgradeType, player)) {
            return false;
        }

        int totalLevel = getTotalLevels();
        int requiredExp = getRequiredExp(totalLevel);

        player.addExperience(-requiredExp);
        addLevel(upgradeType);

        // Recalculate attributes
        player.calculateDimensions();
        return true;
    }

    public Map<String, Integer> getUpgrades() {
        Map<String, Integer> result = new HashMap<>();
        for (Map.Entry<UpgradeType, Integer> entry : upgrades.entrySet()) {
            result.put(entry.getKey().name(), entry.getValue());
        }
        return result;
    }

    public void loadUpgrades(Map<String, Integer> savedUpgrades) {
        upgrades.clear();
        for (Map.Entry<String, Integer> entry : savedUpgrades.entrySet()) {
            try {
                UpgradeType type = UpgradeType.valueOf(entry.getKey().toUpperCase());
                upgrades.put(type, entry.getValue());
            } catch (IllegalArgumentException ignored) {}
        }
    }
}