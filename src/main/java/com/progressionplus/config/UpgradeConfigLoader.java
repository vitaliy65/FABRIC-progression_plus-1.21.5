package com.progressionplus.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.progressionplus.upgrade.UpgradeType;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.EnumMap;
import java.util.Map;

public class UpgradeConfigLoader {
    private static final File CONFIG_FILE = new File("config/progressionplus/upgrade_config.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void load() {
        if (!CONFIG_FILE.exists()) {
            saveDefault();
        }

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            ConfigData data = GSON.fromJson(reader, ConfigData.class);

            // Load upgrades
            UpgradeConfig.UPGRADE_SETTINGS.clear();
            for (Map.Entry<UpgradeType, UpgradeConfig.UpgradeSettings> entry : data.upgrades.entrySet()) {
                UpgradeType type = UpgradeType.valueOf(String.valueOf(entry.getKey()));
                UpgradeConfig.UPGRADE_SETTINGS.put(type, entry.getValue());
            }

            UpgradeConfig.setBaseCost(data.base_cost);
            UpgradeConfig.setLevelMultiplier(data.level_multiplier);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveDefault() {
        try {
            CONFIG_FILE.getParentFile().mkdirs();
            Map<UpgradeType, UpgradeConfig.UpgradeSettings> defaultMap = new EnumMap<>(UpgradeType.class);
            UpgradeConfig.init(); // заповнює UPGRADE_SETTINGS
            for (Map.Entry<UpgradeType, UpgradeConfig.UpgradeSettings> entry : UpgradeConfig.UPGRADE_SETTINGS.entrySet()) {
                defaultMap.put(UpgradeType.valueOf(entry.getKey().name()), entry.getValue());
            }

            ConfigData data = new ConfigData();
            data.base_cost = 100;
            data.level_multiplier = 50;
            data.upgrades = defaultMap;

            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(data, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ConfigData {
        int base_cost;
        int level_multiplier;
        Map<UpgradeType, UpgradeConfig.UpgradeSettings> upgrades;
    }
}
