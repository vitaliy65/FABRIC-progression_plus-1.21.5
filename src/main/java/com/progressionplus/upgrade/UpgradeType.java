package com.progressionplus.upgrade;

public enum UpgradeType {
    STRENGTH("strength", "Сила", 0.25f),
    ENDURANCE("endurance", "Выносливость", 0.01f),
    AGILITY("agility", "Ловкость", 0.02f),
    LUCK("luck", "Удача", 0.05f),
    MINING_SPEED("mining_speed", "Скорость добычи", 0.02f);

    private final String id;
    private final String displayName;
    private final float bonusPerLevel;

    UpgradeType(String id, String displayName, float bonusPerLevel) {
        this.id = id;
        this.displayName = displayName;
        this.bonusPerLevel = bonusPerLevel;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public float getBonusPerLevel() { return bonusPerLevel; }
}