package com.progressionplus.gui;

import com.progressionplus.Progressionplus;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class CustomHudRenderer implements HudRenderCallback {
    private static final Identifier HUD_TEXTURE = Identifier.of(Progressionplus.MOD_ID, "textures/gui/exp_counter_background.png");
    private static final int HUD_TEXTURE_WIDTH = 75;
    private static final int HUD_TEXTURE_HEIGHT = 38;

    @Override
    public void onHudRender(DrawContext drawContext, float v) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) return;

        int screenWidth = drawContext.getScaledWindowWidth();
        int screenHeight = drawContext.getScaledWindowHeight();

        int HUD_X = screenWidth - HUD_TEXTURE_WIDTH;
        int HUD_Y = (int) (screenHeight / 1.5f); // Положение текстуры по Y

        // Отрисовка фоновой текстуры
        drawContext.drawTexture(
                HUD_TEXTURE,
                HUD_X,
                HUD_Y,
                0, 0,
                HUD_TEXTURE_WIDTH,
                HUD_TEXTURE_HEIGHT,
                HUD_TEXTURE_WIDTH,
                HUD_TEXTURE_HEIGHT
        );

        // Получение значений опыта и уровня
        String currentExp = client.player.totalExperience + "";
        String currentLevel = client.player.experienceLevel + " lvl";

        // Центр текстуры
        int centerX = HUD_X + (int)(HUD_TEXTURE_WIDTH / 1.6f);

        // Высоты строк (4 равные полосы)
        int lineHeight = HUD_TEXTURE_HEIGHT / 3;

        // Позиции текста
        int firstLineY = HUD_Y + lineHeight /2 ;
        int thirdLineY = HUD_Y + (lineHeight * 2);

        // Ширина текста
        int expWidth = client.textRenderer.getWidth(currentExp);
        int levelWidth = client.textRenderer.getWidth(currentLevel);

        // Отрисовка текста по центру
        drawContext.drawText(client.textRenderer, currentExp, centerX - expWidth / 2, firstLineY, 0xFFFFFF, true);
        drawContext.drawText(client.textRenderer, currentLevel, centerX - levelWidth / 2, thirdLineY, 0xFFFFFF, true);
    }
}