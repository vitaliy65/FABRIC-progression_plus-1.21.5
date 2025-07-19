package com.progressionplus.network;

import com.progressionplus.Progressionplus;
import com.progressionplus.upgrade.UpgradeType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class UpgradePayload {
    public static final Identifier ID = new Identifier(Progressionplus.MOD_ID, "sync_upgrades");

    private final UpgradeType upgradeType;
    private final int level;
    private final UUID playerUuid;

    public UpgradePayload(UpgradeType upgradeType, int level, UUID playerUuid) {
        this.upgradeType = upgradeType;
        this.level = level;
        this.playerUuid = playerUuid;
    }

    public UpgradeType getUpgradeType() {
        return upgradeType;
    }

    public int getLevel() {
        return level;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public void write(PacketByteBuf buf) {
        buf.writeString(upgradeType.name());
        buf.writeInt(level);
        buf.writeUuid(playerUuid);
    }

    public static UpgradePayload read(PacketByteBuf buf) {
        UpgradeType type = UpgradeType.valueOf(buf.readString().toUpperCase());
        int level = buf.readInt();
        UUID uuid = buf.readUuid();
        return new UpgradePayload(type, level, uuid);
    }
}