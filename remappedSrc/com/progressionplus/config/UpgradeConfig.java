package com.progressionplus.config;


import com.progressionplus.upgrade.UpgradeType;

import java.util.EnumMap;
import java.util.Map;

public class UpgradeConfig {
    public static final Map<UpgradeType, UpgradeSettings> UPGRADE_SETTINGS = new EnumMap<>(UpgradeType.class);
    private static int BASE_COST = 100;
    private static int LEVEL_MULTIPLIER = 50;

    public static void init() {
        // За замовчуванням
        UPGRADE_SETTINGS.put(UpgradeType.STRENGTH, new UpgradeSettings(0.15f, 0.03f));
        UPGRADE_SETTINGS.put(UpgradeType.ENDURANCE, new UpgradeSettings(1f, 0.03f));
        UPGRADE_SETTINGS.put(UpgradeType.AGILITY, new UpgradeSettings(0.03f, 0.03f));
        UPGRADE_SETTINGS.put(UpgradeType.LUCK, new UpgradeSettings(0.05f, 0.03f));
        UPGRADE_SETTINGS.put(UpgradeType.MINING_SPEED, new UpgradeSettings(0.1f, 0.03f));
    }

    public static UpgradeSettings getSettings(UpgradeType type) {
        return UPGRADE_SETTINGS.getOrDefault(type, new UpgradeSettings(0.02f, 2));
    }

    public static int calculateRequiredExp(int totalLevel) {
        return BASE_COST + (totalLevel * LEVEL_MULTIPLIER);
    }

    public static void setBaseCost(int baseCost) {
        BASE_COST = baseCost;
    }

    public static void setLevelMultiplier(int levelMultiplier) {
        LEVEL_MULTIPLIER = levelMultiplier;
    }

    public static class UpgradeSettings {
        public float bonusPerLevel;
        public float resistancePerLevel;

        public UpgradeSettings(float bonusPerLevel, float resistancePerLevel) {
            this.bonusPerLevel = bonusPerLevel;
            this.resistancePerLevel = resistancePerLevel;
        }
    }
}