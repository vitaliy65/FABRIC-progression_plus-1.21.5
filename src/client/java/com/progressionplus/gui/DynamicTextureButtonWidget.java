package com.progressionplus.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.util.Identifier;

public class DynamicTextureButtonWidget extends TexturedButtonWidget {

    private Identifier texture;
    private final int u, v, hoveredV;

    public DynamicTextureButtonWidget(int x, int y, int width, int height,
                                      int u, int v, int hoveredV,
                                      Identifier texture,
                                      PressAction onPress) {
        super(x, y, width, height, u, v, hoveredV, texture, width, height, onPress);
        this.texture = texture;
        this.u = u;
        this.v = v;
        this.hoveredV = hoveredV;
    }

    public void setTexture(Identifier texture) {
        this.texture = texture;
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        int vOffset = this.isHovered() ? this.hoveredV : this.v;
        context.drawTexture(this.texture, this.getX(), this.getY(), this.u, vOffset, this.width, this.height,
                this.width, this.height);
    }
}
