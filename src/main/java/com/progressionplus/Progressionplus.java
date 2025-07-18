package com.progressionplus;

import com.progressionplus.data.PlayerComponents;
import com.progressionplus.data.PlayerUpgradeData;
import com.progressionplus.network.ModMessages;
import com.progressionplus.network.ServerDimensionSwitch;
import com.progressionplus.playerResistances.DamageEventHandler;
import net.fabricmc.api.ModInitializer;

import com.progressionplus.config.UpgradeConfig;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Progressionplus implements ModInitializer {
	public static final String MOD_ID = "progression-plus";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Progression+");

		// Инициализация конфигурации
		UpgradeConfig.init();
		ModMessages.init();
//		DamageEventHandler.register();

		// Add player join/leave handlers
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayerEntity player = handler.player;
			var upgradeData = PlayerComponents.PLAYER_UPGRADES.get(player);
			ModMessages.onPlayerJoin(player);
			upgradeData.logUpgrades(player);
		});

		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			ServerPlayerEntity player = handler.player;
			var upgradeData = PlayerComponents.PLAYER_UPGRADES.get(player);
			upgradeData.logUpgrades(player);
		});

		ServerEntityEvents.ENTITY_LOAD.register(ServerDimensionSwitch::Register);

		LOGGER.info("Progression+ initialized successfully!");
	}
}