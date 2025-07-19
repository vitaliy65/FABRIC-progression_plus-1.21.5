package com.progressionplus.network;

import Id;
import com.progressionplus.Progressionplus;
import com.progressionplus.upgrade.UpgradeType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.UUID;

public record UpgradePayload(UpgradeType upgradeType, int level, UUID playerUuid) implements CustomPayload {

    public static final CustomPayload.Id<UpgradePayload> ID = new CustomPayload.Id<>(Identifier.of(Progressionplus.MOD_ID, "sync_upgrades"));

    public static final PacketCodec<RegistryByteBuf, UpgradePayload> CODEC = PacketCodec.of(
            (payload, buf) -> {
                buf.writeString(payload.upgradeType.getId());
                buf.writeInt(payload.level);
                buf.writeUuid(payload.playerUuid);
            },
            (buf) -> new UpgradePayload(
                    UpgradeType.valueOf(buf.readString().toUpperCase()),
                    buf.readInt(),
                    buf.readUuid()
            )
    );



    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}