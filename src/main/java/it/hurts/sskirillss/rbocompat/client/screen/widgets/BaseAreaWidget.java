package it.hurts.sskirillss.rbocompat.client.screen.widgets;

import it.hurts.sskirillss.rbocompat.client.IScrollingScreen;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class BaseAreaWidget extends AbstractButton implements IScrollingScreen {

    public BaseAreaWidget(int pX, int pY, int pWidth, int pHeight, Component pMessage) {
        super(pX, pY, pWidth, pHeight, pMessage);
    }

    @Override
    public String message() {
        return this.getMessage().toString().replace("literal", "")
                .replace("{", "")
                .replace("}", "");
    }

    @Override
    public void onPress() {

    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }

    @Override
    public int x() {
        return getX();
    }

    @Override
    public int y() {
        return getY();
    }

}
