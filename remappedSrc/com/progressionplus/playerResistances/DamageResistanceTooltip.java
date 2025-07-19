package com.progressionplus.playerResistances;

import com.progressionplus.config.UpgradeConfig;
import com.progressionplus.data.PlayerComponents;
import com.progressionplus.upgrade.UpgradeType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class DamageResistanceTooltip {

    /**
     * Получает список текстов с информацией о сопротивлении урону
     */
    public static List<Text> getResistanceTooltips(PlayerEntity player, UpgradeType upgradeType) {
        List<Text> tooltips = new ArrayList<>();

        var upgradeData = PlayerComponents.PLAYER_UPGRADES.get(player);
        int currentLevel = upgradeData.getPlayerUpgrade().getLevel(upgradeType);
        int nextLevel = currentLevel + 1;

        float currentResistance = UpgradeConfig.getSettings(upgradeType).resistancePerLevel * currentLevel;
        float nextResistance = UpgradeConfig.getSettings(upgradeType).resistancePerLevel * nextLevel;

        // Добавляем заголовок
        tooltips.add(Text.literal("Сопротивление урону:").formatted(Formatting.YELLOW));

        // Добавляем информацию о текущем сопротивлении
        if (currentLevel > 0) {
            tooltips.add(Text.literal("  Текущее: " + String.format("%.1f%%", currentResistance * 100))
                    .formatted(Formatting.GREEN));
        }

        // Добавляем информацию о следующем уровне
        tooltips.add(Text.literal("  Следующий уровень: " + String.format("%.1f%%", nextResistance * 100))
                .formatted(Formatting.GRAY));

        // Добавляем информацию о типах урона
        tooltips.add(Text.literal("Защищает от:").formatted(Formatting.AQUA));

        switch (upgradeType) {
            case ENDURANCE -> {
                tooltips.add(Text.literal("  • Урон от мобов и игроков").formatted(Formatting.WHITE));
                tooltips.add(Text.literal("  • Физический урон").formatted(Formatting.WHITE));
            }
            case STRENGTH -> {
                tooltips.add(Text.literal("  • Урон от взрывов").formatted(Formatting.WHITE));
                tooltips.add(Text.literal("  • Урон от стрел и трезубцев").formatted(Formatting.WHITE));
            }
            case AGILITY -> {
                tooltips.add(Text.literal("  • Урон от падения").formatted(Formatting.WHITE));
                tooltips.add(Text.literal("  • Урон от столкновений").formatted(Formatting.WHITE));
            }
            case LUCK -> {
                tooltips.add(Text.literal("  • Магический урон").formatted(Formatting.WHITE));
                tooltips.add(Text.literal("  • Урон от иссушения").formatted(Formatting.WHITE));
                tooltips.add(Text.literal("  • Урон от дыхания дракона").formatted(Formatting.WHITE));
            }
            case MINING_SPEED -> {
                tooltips.add(Text.literal("  • Урон от лавы и огня").formatted(Formatting.WHITE));
                tooltips.add(Text.literal("  • Урон от кактуса и ягод").formatted(Formatting.WHITE));
                tooltips.add(Text.literal("  • Урон от удушения и утопления").formatted(Formatting.WHITE));
            }
        }

        return tooltips;
    }

    /**
     * Получает краткую информацию о сопротивлении для отображения в UI
     */
    public static Text getShortResistanceInfo(PlayerEntity player, UpgradeType upgradeType) {
        var upgradeData = PlayerComponents.PLAYER_UPGRADES.get(player);
        int currentLevel = upgradeData.getPlayerUpgrade().getLevel(upgradeType);

        if (currentLevel <= 0) {
            return Text.literal("Нет защиты").formatted(Formatting.GRAY);
        }

        float resistance = UpgradeConfig.getSettings(upgradeType).resistancePerLevel * currentLevel;
        String damageType = getDamageTypeName(upgradeType);

        return Text.literal(String.format("%s: %.1f%%", damageType, resistance * 100))
                .formatted(Formatting.GREEN);
    }

    private static String getDamageTypeName(UpgradeType upgradeType) {
        return switch (upgradeType) {
            case ENDURANCE -> "Физ. урон";
            case STRENGTH -> "Взрывы";
            case AGILITY -> "Падение";
            case LUCK -> "Магия";
            case MINING_SPEED -> "Среда";
        };
    }
}