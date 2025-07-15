package com.progressionplus.playerResistances;

import com.progressionplus.Progressionplus;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class DamageEventHandler {

    public static void register() {
        // Регистрируем обработчик урона
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(DamageEventHandler::onDamage);
    }

    /**
     * Обработчик события получения урона
     */
    private static boolean onDamage(LivingEntity entity, DamageSource damageSource, float damageAmount) {
        // Проверяем, что это игрок
        if (!(entity instanceof ServerPlayerEntity player)) {
            return true; // Пропускаем урон для не-игроков
        }

        // Вычисляем сопротивление урону
        float reducedDamage = DamageResistanceHandler.calculateDamageReduction(player, damageSource, damageAmount);

        // Если урон был уменьшен, применяем его вручную
        if (reducedDamage != damageAmount) {
            // Отменяем оригинальный урон
            player.damage(player.getServerWorld() ,damageSource, reducedDamage);
            Progressionplus.LOGGER.info("Player {} took reduced damage: {} (original: {})", player.getName().getString(), reducedDamage, damageAmount);
            return false; // Блокируем оригинальный урон
        }

        return true; // Пропускаем урон без изменений
    }
}