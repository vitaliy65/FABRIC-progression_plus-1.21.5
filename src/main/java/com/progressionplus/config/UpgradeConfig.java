package com.progressionplus.config;


import com.progressionplus.upgrade.UpgradeType;

import java.util.EnumMap;
import java.util.Map;

public class UpgradeConfig {
    private static final Map<UpgradeType, UpgradeSettings> UPGRADE_SETTINGS = new EnumMap<>(UpgradeType.class);
    private static final int BASE_COST = 100;
    private static final int LEVEL_MULTIPLIER = 50;

    public static void init() {
        // Only need to store bonus values now
        UPGRADE_SETTINGS.put(UpgradeType.STRENGTH, new UpgradeSettings(0.15f, 0.03f));     // 15% damage and 3% resistance
        UPGRADE_SETTINGS.put(UpgradeType.ENDURANCE, new UpgradeSettings(1f, 0.03f));       // 0.5 heart and 3% resistance
        UPGRADE_SETTINGS.put(UpgradeType.AGILITY, new UpgradeSettings(0.03f, 0.03f));      // 3% and 3% resistance
        UPGRADE_SETTINGS.put(UpgradeType.LUCK, new UpgradeSettings(0.05f, 0.03f));         // 5% and 3% resistance
        UPGRADE_SETTINGS.put(UpgradeType.MINING_SPEED, new UpgradeSettings(0.1f, 0.03f));  // 10% and 3% resistance
    }

    public static UpgradeSettings getSettings(UpgradeType type) {
        return UPGRADE_SETTINGS.getOrDefault(type, new UpgradeSettings( 0.02f, 2));
    }

    public static int calculateRequiredExp(int totalLevel) {
        return BASE_COST + (totalLevel * LEVEL_MULTIPLIER);
    }

    public static class UpgradeSettings {
        public final float bonusPerLevel;
        public final float resistancePerLevel;

        public UpgradeSettings(float bonusPerLevel, float resistancePerLevel) {
            this.bonusPerLevel = bonusPerLevel;
            this.resistancePerLevel = resistancePerLevel;
        }
    }
}