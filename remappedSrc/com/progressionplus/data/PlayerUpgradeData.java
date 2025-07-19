package com.progressionplus.data;

import com.progressionplus.Progressionplus;
import com.progressionplus.upgrade.UpgradeType;
import com.progressionplus.upgrades.PlayerUpgrade;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.entity.RespawnableComponent;

import java.util.HashMap;
import java.util.Map;

import static com.progressionplus.Progressionplus.LOGGER;

public class PlayerUpgradeData implements Component, RespawnableComponent {
    private static final Identifier UPGRADE_KEY = Identifier.of(Progressionplus.MOD_ID, "upgrades");
    private final PlayerUpgrade playerUpgrade;
    private final PlayerEntity player;

    public PlayerUpgradeData(PlayerEntity player) {
        this.playerUpgrade = new PlayerUpgrade();
        this.player = player;
        // Safely get player name, fallback to UUID if name is not available
        String playerIdentifier = player.getGameProfile() != null ?
                player.getGameProfile().getName() :
                player.getUuid().toString();
        LOGGER.info("Created PlayerUpgradeData for player: {}", playerIdentifier);
    }

    public PlayerUpgrade getPlayerUpgrade() {
        return playerUpgrade;
    }

    public void logUpgrades(PlayerEntity player) {
        String playerName = player.getGameProfile().getName();
        LOGGER.info("Player {} upgrades:", playerName);
        for (UpgradeType type : UpgradeType.values()) {
            int level = playerUpgrade.getLevel(type);
            LOGGER.info(" - {}: Level {}", type.getDisplayName(), level);
        }
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        if (nbtCompound.contains(UPGRADE_KEY.toString())) {
            Map<String, Integer> upgrades = new HashMap<>();

            for (String key : nbtCompound.getKeys()) {
                if (!key.equals(UPGRADE_KEY.toString())) {
                    int value = nbtCompound.getInt(key).orElse(0); // This returns a primitive int
                    upgrades.put(key, value);
                }
            }

            playerUpgrade.loadUpgrades(upgrades);

            LOGGER.info("LOADING upgrades FROM NBT");
            logUpgrades(player);
        }
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        Map<String, Integer> upgrades = playerUpgrade.getUpgrades();

        for (Map.Entry<String, Integer> entry : upgrades.entrySet()) {
            nbtCompound.putInt(entry.getKey(), entry.getValue());
        }

        nbtCompound.putString(UPGRADE_KEY.toString(), "upgrades");

        LOGGER.info("Saving upgrades TO NBT");
        logUpgrades(player);
    }
}