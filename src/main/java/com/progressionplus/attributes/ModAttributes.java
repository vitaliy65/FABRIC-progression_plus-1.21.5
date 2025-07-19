package com.progressionplus.attributes;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModAttributes {
    public static final EntityAttribute GENERIC_MINING_SPEED = new ClampedEntityAttribute(
            "attribute.name.generic.mining_speed", // переклад для lang
            0.0D, // значення за замовчуванням
            0.0D, // мінімум
            1024.0D // максимум
    ).setTracked(true);

    public static void registerAttributes() {
        Registry.register(Registries.ATTRIBUTE,
                new Identifier("progressionplus", "generic.mining_speed"),
                GENERIC_MINING_SPEED);
    }

    public static void register() {
        DefaultAttributeContainer.Builder builder = PlayerEntity.createPlayerAttributes()
                .add(ModAttributes.GENERIC_MINING_SPEED); // додаємо наш атрибут

        FabricDefaultAttributeRegistry.register(EntityType.PLAYER, builder);
    }
}
