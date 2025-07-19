package com.progressionplus.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.progressionplus.attributes.ModAttributes;
import com.progressionplus.config.UpgradeConfig;
import com.progressionplus.data.PlayerComponents;
import com.progressionplus.upgrade.UpgradeType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerEntity.class)
public abstract class MiningSpeedMixin {

    @ModifyReturnValue(
            method = "getBlockBreakingSpeed",
            at = @At("RETURN")
    )
    private float applyCustomMiningSpeed(float original, BlockState state) {
        PlayerEntity self = (PlayerEntity) (Object) this;

        if (self.getAttributeInstance(ModAttributes.GENERIC_MINING_SPEED) != null) {
            var upgradeData = PlayerComponents.PLAYER_UPGRADES.get(self);
            var playerUpgrade = upgradeData.getPlayerUpgrade();

            int level = playerUpgrade.getLevel(UpgradeType.MINING_SPEED);

            float miningSpeedBonusPerLevel = UpgradeConfig.getSettings(UpgradeType.MINING_SPEED).bonusPerLevel * level;

            double bonusMiningSpeed = miningSpeedBonusPerLevel * original;
            return (float) (original + bonusMiningSpeed);
        }

        return original;
    }
}
