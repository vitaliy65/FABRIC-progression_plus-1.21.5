package com.progressionplus.gui;

import com.progressionplus.Progressionplus;
import com.progressionplus.config.UpgradeConfig;
import com.progressionplus.data.PlayerComponents;
import com.progressionplus.network.ClientModMessages;
import com.progressionplus.upgrade.UpgradeType;
import com.progressionplus.upgrades.PlayerUpgrade;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class UpgradeMenu extends Screen {
    @FunctionalInterface
    private interface ScaledRenderer {
        void render(DrawContext context, int adjustedX, int adjustedY);
    }


    private static final Identifier BACKGROUND_TEXTURE = Identifier.of(Progressionplus.MOD_ID, "textures/gui/background.png");
    private static final Identifier CARD_BACKGROUND_TEXTURE = Identifier.of(Progressionplus.MOD_ID, "textures/gui/background_player.png");
    private static final ButtonTextures BUTTON_TEXTURES = new ButtonTextures(
            Identifier.of(Progressionplus.MOD_ID, "stats/button_level_up"),
            Identifier.of(Progressionplus.MOD_ID, "stats/button_level_up_disabled"),
            Identifier.of(Progressionplus.MOD_ID, "stats/button_level_up_focused")
    );

    private static final int PADDING = 13;
    private static final int TEXT_COLOR = 0xfff7cc;
    private static final int BUTTON_SIZE = 9;

    private int mainBgWidth;
    private int mainBgHeight;
    private int mainBgX;
    private int mainBgY;

    private int cardBgWidth;
    private int cardBgHeight;
    private int cardBgX;
    private int cardBgY;
    private float baseScale;

    private int levels;
    private int requiredTotalExp;

    private PlayerUpgrade playerUpgrades;

    private List<ButtonWidget> upgradeButtons = new ArrayList<>();



    public UpgradeMenu() {
        super(Text.translatable("gui.progression-plus.upgrade_menu"));
    }

    @Override
    protected void init() {
        this.mainBgWidth = (int) (this.width * 0.7f);
        this.mainBgHeight = (int) (mainBgWidth / (16f / 9f) + 4);
        this.mainBgX = (int)(this.width / 3.5f);
        this.mainBgY = this.height / 2 - mainBgHeight / 2;

        this.cardBgWidth = (int) (this.width * 0.2f);
        this.cardBgHeight = (int) (cardBgWidth / (1f / 2f) - 3);
        this.cardBgX = (int)(this.width / 40f);
        this.cardBgY = this.height / 2 - cardBgHeight / 2;

        this.baseScale = this.width / 1920f;

        this.playerUpgrades = PlayerComponents.PLAYER_UPGRADES.get(this.client.player).getPlayerUpgrade();
        this.levels = playerUpgrades.getTotalLevels();
        this.requiredTotalExp = playerUpgrades.getRequiredExp(levels);

        // Очищаем список кнопок перед инициализацией
        this.upgradeButtons.clear();

        initUpgradeButtons();
    }

    private void initUpgradeButtons() {

        // Используем фиксированные координаты относительно основного фона
        int baseX = mainBgX + (int)(460 * baseScale * 2.5f);
        int baseY = mainBgY + (int)(85 * baseScale * 2.5f);

        // Размер кнопки с учетом масштаба
        int scaledButtonSize = (int)(BUTTON_SIZE * baseScale * 2.5f);
        int scaledPadding = (int)(PADDING * baseScale * 2.5f);

        int currentY = baseY;

        for (UpgradeType type : UpgradeType.values()) {
            ButtonWidget button = createUpgradeButton(type, baseX, currentY, scaledButtonSize);
            this.upgradeButtons.add(button);
            currentY += scaledPadding;
        }
    }

    private ButtonWidget createUpgradeButton(UpgradeType type, int x, int y, int buttonSize) {
        ButtonWidget button = new TexturedButtonWidget(x, y, buttonSize, buttonSize, BUTTON_TEXTURES, btn -> {
            ClientModMessages.sendSyncPacketToServer(type, client.player);

            // Обновляем состояние всех кнопок
            updateButtonStates();
        });

        this.addDrawableChild(button);
        return button;
    }

    private void updateButtonStates() {
        float currentExp = client.player.totalExperience;

        for (ButtonWidget button : upgradeButtons) {
            if (currentExp >= requiredTotalExp) {
                button.active = true;
            } else {
                button.active = false;
            }
        }
    }



    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.levels = playerUpgrades.getTotalLevels();
        this.requiredTotalExp = playerUpgrades.getRequiredExp(levels);
        context.fill(0, 0, this.width, this.height, 0xC0000000);

        renderCustomBackground(context);
        renderCustomPlayerCard(context, mouseX, mouseY);

        // Обновляем состояние кнопок перед рендерингом
        updateButtonStates();

        // Add this line to render buttons and other child elements
        super.render(context, mouseX, mouseY, delta);
    }

    void renderCustomBackground(DrawContext context) {

        renderTitles(context, mainBgX, mainBgY);
        renderUpgrades(context);
        renderRequiredExpCounter(context);
        renderDefenses(context);
        renderBonusIndicators(context);

        context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND_TEXTURE,
                mainBgX, mainBgY, 0, 0,
                mainBgWidth, mainBgHeight,
                mainBgWidth, mainBgHeight);
    }


    //  ---------------------  render titles ---------------------
    private record TitleInfo(String translationKey, int xOffset, int yOffset, String suffix) {}
    private void renderTitle(DrawContext context, float adjustedX, float adjustedY, TitleInfo info) {
        MutableText text = Text.translatable(info.translationKey());
        if (info.suffix() != null) {
            text = text.append(info.suffix());
        }

        context.drawTextWithShadow(
                this.textRenderer,
                text,
                (int)adjustedX + info.xOffset(),
                (int)adjustedY + info.yOffset(),
                TEXT_COLOR
        );
    }
    private void renderTitles(DrawContext context, int x, int y) {
        renderWithScale(context, x, y, 3f, (ctx, adjustedX, adjustedY) -> {
            TitleInfo[] titles = {
                    new TitleInfo("upgrade.progression-plus.level", 68, 36, " " + levels),
                    new TitleInfo("upgrade.progression-plus.attributes", 275, 36, null),
                    new TitleInfo("upgrade.progression-plus.indicators", 68, 149, null),
                    new TitleInfo("upgrade.progression-plus.defense", 275, 149, null)
            };

            for (TitleInfo title : titles) {
                renderTitle(ctx, adjustedX, adjustedY, title);
            }
        });
    }



    //  ---------------------  render upgrades ---------------------
    private record UpgradeInfo(String translationKey, int xOffset, int yOffset, int suffix) {}
    private void renderUpgrade(DrawContext context, float adjustedX, float adjustedY, UpgradeInfo info) {
        MutableText translatedText = Text.translatable(info.translationKey()).styled(style ->
                style.withColor(TEXT_COLOR));

        if (info.suffix() != -1) {
            String prefix = info.suffix() < 10
                    ? "0" + info.suffix() + "   "
                    : info.suffix() + "   ";

            MutableText coloredPrefix = Text.literal(prefix).styled(style ->
                    style.withColor(Formatting.GREEN)  // Цвет префикса
            );

            translatedText = coloredPrefix.append(translatedText);  // Склеиваем цветной префикс и основной текст
        }

        context.drawTextWithShadow(
                this.textRenderer,
                translatedText,
                (int)adjustedX + info.xOffset(),
                (int)adjustedY + info.yOffset(),
                TEXT_COLOR
        );
    }
    private void renderUpgrades(DrawContext context) {
        int x = (int)(mainBgX * 1.35f);
        int y = (int)(mainBgY * 1.2f);

        renderWithScale(context, x, y, 2.5f, (ctx, adjustedX, adjustedY) -> {
            UpgradeInfo[] upgrades = {
                    new UpgradeInfo("upgrade.progression-plus.strength",    240, 60 + PADDING * 1,
                            playerUpgrades.getLevel(UpgradeType.STRENGTH)),
                    new UpgradeInfo("upgrade.progression-plus.endurance",   240, 60 + PADDING * 2,
                            playerUpgrades.getLevel(UpgradeType.ENDURANCE)),
                    new UpgradeInfo("upgrade.progression-plus.agility",     240, 60 + PADDING * 3,
                            playerUpgrades.getLevel(UpgradeType.AGILITY)),
                    new UpgradeInfo("upgrade.progression-plus.luck",        240, 60 + PADDING * 4,
                            playerUpgrades.getLevel(UpgradeType.LUCK)),
                    new UpgradeInfo("upgrade.progression-plus.mining_speed",240, 60 + PADDING * 5,
                            playerUpgrades.getLevel(UpgradeType.MINING_SPEED)),
            };

            for (UpgradeInfo upgrade : upgrades) {
                renderUpgrade(ctx, adjustedX, adjustedY, upgrade);
            }
        });
    }


    //  ---------------------  render defense stats  ---------------------
    private record DefenseInfo(String translationKey, int xOffset, int yOffset, float resistancePerLevel, int upgradeLevel) {}
    private void renderDefense(DrawContext context, float adjustedX, float adjustedY, DefenseInfo info) {
        boolean isMaxLevel = info.upgradeLevel() == PlayerUpgrade.getMaxLevel();

        int resistencePerLevel = (int)(info.resistancePerLevel() * 100);
        int pracentage = resistencePerLevel * info.upgradeLevel();

        MutableText translatedText = Text.translatable(info.translationKey())
                .styled(style -> style.withColor(TEXT_COLOR));

        MutableText coloredPrefix = Text.literal(pracentage + "")
                .styled(style -> style.withColor(Formatting.YELLOW));

        if (isMaxLevel) resistencePerLevel = 0;

        context.drawTextWithShadow(
                this.textRenderer,
                translatedText,
                (int) adjustedX + info.xOffset(),
                (int) adjustedY + info.yOffset(),
                TEXT_COLOR
        );

        coloredPrefix.append(Text.literal("% > " + (pracentage + resistencePerLevel) + "%")
                .styled(style -> style.withColor(TEXT_COLOR)));

        int textWidth = this.textRenderer.getWidth(coloredPrefix);

        context.drawTextWithShadow(
                this.textRenderer,
                coloredPrefix,
                (int) (adjustedX * 1.5f) - textWidth / 2 + info.xOffset(),
                (int) adjustedY + info.yOffset(),
                TEXT_COLOR
        );
    }
    private void renderDefenses(DrawContext context) {
        int x = (int)(mainBgX * 1.35f);
        int y = (int)(mainBgY * 3.1f);

        renderWithScale(context, x, y, 2.5f, (ctx, adjustedX, adjustedY) -> {
            DefenseInfo[] defenses = {
                    new DefenseInfo("defense.progression-plus.strength_damage_bonus",   240, 60 + PADDING * 1,
                            playerUpgrades.getUpgradeResistanceBonus(UpgradeType.STRENGTH),
                            playerUpgrades.getLevel(UpgradeType.STRENGTH)),
                    new DefenseInfo("defense.progression-plus.endurance_health_bonus",  240, 60 + PADDING * 2,
                            playerUpgrades.getUpgradeResistanceBonus(UpgradeType.ENDURANCE),
                            playerUpgrades.getLevel(UpgradeType.ENDURANCE)),
                    new DefenseInfo("defense.progression-plus.agility_deffense_bonus",  240, 60 + PADDING * 3,
                            playerUpgrades.getUpgradeResistanceBonus(UpgradeType.AGILITY),
                            playerUpgrades.getLevel(UpgradeType.AGILITY)),
                    new DefenseInfo("defense.progression-plus.luck_bonus",              240, 60 + PADDING * 4,
                            playerUpgrades.getUpgradeResistanceBonus(UpgradeType.LUCK),
                            playerUpgrades.getLevel(UpgradeType.LUCK)),
                    new DefenseInfo("defense.progression-plus.mining_speed_bonus",      240, 60 + PADDING * 5,
                            playerUpgrades.getUpgradeResistanceBonus(UpgradeType.MINING_SPEED),
                            playerUpgrades.getLevel(UpgradeType.MINING_SPEED)),
            };

            for (DefenseInfo defense : defenses) {
                renderDefense(ctx, adjustedX, adjustedY, defense);
            }
        });
    }


    //  ---------------------  render custom player card ---------------------
    private void renderCustomPlayerCard(DrawContext context, int mouseX, int mouseY) {
        // Draw card background
        context.drawTexture(RenderLayer::getGuiTextured, CARD_BACKGROUND_TEXTURE,
                cardBgX, cardBgY, 0, 0,
                cardBgWidth, cardBgHeight,
                cardBgWidth, cardBgHeight);

        // Отрисовка текста уровня с масштабированием
        String levelText = levels + " Lvl";

        // Используем renderWithScale для масштабирования текста
        // Координаты рассчитываются как смещение от центра карточки
        int centerX = cardBgX + cardBgWidth / 2;
        int textY = cardBgY + cardBgHeight - (int)(cardBgHeight / 14f);

        renderWithScale(context, centerX, textY, 2.3f, (ctx, adjustedX, adjustedY) -> {
            // Центрируем текст в масштабированном контексте
            int textWidth = this.textRenderer.getWidth(levelText);
            int centeredX = adjustedX - textWidth / 2;

            ctx.drawTextWithShadow(this.textRenderer, Text.of(levelText),
                    centeredX, adjustedY, TEXT_COLOR);
        });

        // Draw player entity
        int playerScale = (int)(cardBgHeight * 0.3f);
        myDrawEntity(context,
                cardBgX,
                cardBgY,
                (int)(cardBgWidth * 1.13f),
                (int)(cardBgHeight * 1.13f),
                playerScale,
                0.0625F,
                mouseX,
                mouseY,
                client.player);
    }


    //  ---------------------  render custom required exp counter  ---------------------
    private void renderRequiredExpCounter(DrawContext context) {
        int x = (int)(mainBgX * 1.65f);
        int y = (int)(mainBgY * 2.6);

        float myCurentExp = this.client.player.totalExperience;

        // Первая часть с масштабом 5f
        renderWithScale(context, x , y, 5f, (ctx, adjustedX, adjustedY) -> {
            String levelText = (int)myCurentExp + " / " + requiredTotalExp;
            int textWidth = this.textRenderer.getWidth(levelText);
            ctx.drawTextWithShadow(this.textRenderer,
                    Text.of(levelText),
                    adjustedX - textWidth / 2, adjustedY, TEXT_COLOR);
        });


        // Вторая часть с масштабом 2f
        renderWithScale(context, x, y, 2f, (ctx, adjustedX, adjustedY) -> {
            MutableText underLevelText = Text.translatable("upgrade.progression-plus.required_exp");
            int underTextWidth = this.textRenderer.getWidth(underLevelText);
            ctx.drawTextWithShadow(this.textRenderer, underLevelText,
                    adjustedX - underTextWidth / 2, adjustedY + PADDING + 8, TEXT_COLOR);
        });
    }


    // ---------------------  render bonus indicators  ---------------------

    private void renderBonusIndicators(DrawContext context) {
        int x = (int)(mainBgX * 1.25f);
        int y = (int)(mainBgY * 3.25f);

        renderWithScale(context, x, y, 2.5f, (ctx, adjustedX, adjustedY) -> {
            // Пример индикаторов
            MutableText[] indicatorsText = {
                    Text.translatable("indicator.progression-plus.hp_bonus"),
                    Text.translatable("indicator.progression-plus.damage_bonus"),
                    Text.translatable("indicator.progression-plus.speed_bonus"),
                    Text.translatable("indicator.progression-plus.mining_speed_bonus"),
            };

            String[] indicatorsValues = {
                    String.valueOf((int)(client.player.getMaxHealth())),
                    String.format("%.2f", ((playerUpgrades.getUpgradeBonus(UpgradeType.STRENGTH)))),
                    (int)(playerUpgrades.getUpgradeBonus(UpgradeType.AGILITY) * 100) + "%",
                    (int)(playerUpgrades.getUpgradeBonus(UpgradeType.MINING_SPEED) * 100) + "%",
            };

            int tempPadding = PADDING;
            for (var indicator : indicatorsText){
                ctx.drawTextWithShadow(this.textRenderer,
                        Text.of(indicator),
                        adjustedX, adjustedY + 60 + tempPadding, TEXT_COLOR);
                tempPadding += PADDING;
            }

            tempPadding = PADDING;
            for (var indicatorValue : indicatorsValues){
                int textWidth = this.textRenderer.getWidth(indicatorValue);
                ctx.drawTextWithShadow(this.textRenderer,
                        Text.of(indicatorValue),
                        adjustedX + 180 - textWidth, adjustedY + 60 + tempPadding, TEXT_COLOR);
                tempPadding += PADDING;
            }
        });
    }


    // Утилитарная функция для выполнения кода в масштабированном контексте
    private void renderWithScale(DrawContext context, int x, int y, float scaleMultiplier, ScaledRenderer renderer) {
        float scaleFactor = baseScale * scaleMultiplier;
        int adjustedX = (int)(x / scaleFactor);
        int adjustedY = (int)(y / scaleFactor);

        context.getMatrices().push();
        context.getMatrices().scale(scaleFactor, scaleFactor, 1.0f);

        renderer.render(context, adjustedX, adjustedY);

        context.getMatrices().pop();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {}

    public static void myDrawEntity(DrawContext context, int x1, int y1, int x2, int y2, int size, float f, float mouseX, float mouseY, LivingEntity entity) {
        float g = (float)(x1 + x2) / 2.0F;
        float h = (float)(y1 + y2) / 2.0F;
        context.enableScissor(x1, y1, x2, y2);
        float i = (float)Math.atan((double)((g - mouseX) / 40.0F));
        float j = (float)Math.atan((double)((h - mouseY) / 40.0F));
        Quaternionf quaternionf = (new Quaternionf()).rotateZ(3.1415927F);
        Quaternionf quaternionf2 = (new Quaternionf()).rotateX(j * 20.0F * 0.017453292F);
        quaternionf.mul(quaternionf2);
        float k = entity.bodyYaw;
        float l = entity.getYaw();
        float m = entity.getPitch();
        float n = entity.lastHeadYaw;
        float o = entity.headYaw;
        entity.bodyYaw = 180.0F + i * 20.0F;
        entity.setYaw(180.0F + i * 40.0F);
        entity.setPitch(-j * 20.0F);
        entity.headYaw = entity.getYaw();
        entity.lastHeadYaw = entity.getYaw();
        float p = entity.getScale();
        Vector3f vector3f = new Vector3f(0.0F, entity.getHeight() / 2.0F + f * p, -2.0F);
        float q = (float)size / p;
        InventoryScreen.drawEntity(context, g, h, q, vector3f, quaternionf, quaternionf2, entity);
        entity.bodyYaw = k;
        entity.setYaw(l);
        entity.setPitch(m);
        entity.lastHeadYaw = n;
        entity.headYaw = o;
        context.disableScissor();
    }
}