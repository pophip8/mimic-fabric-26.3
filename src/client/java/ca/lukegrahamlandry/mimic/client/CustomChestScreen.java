package ca.lukegrahamlandry.mimic.client;

import ca.lukegrahamlandry.mimic.Constants;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class CustomChestScreen extends AbstractContainerScreen<MimicContainer> {
    private final Identifier background;
    public CustomChestScreen(MimicContainer menu, Inventory inventory, Component title, String type) {
        super(menu, inventory, title, 176, 114 + menu.getRowCount() * 18);

        inventoryLabelY = imageHeight - 94;
        background = Identifier.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/" + type + ".png");
    }
    public static CustomChestScreen createEvil(MimicContainer menu, Inventory inventory, Component title) {
        return new CustomChestScreen(menu, inventory, title, "evil_mimic");
    }
    public static CustomChestScreen createTame(MimicContainer menu, Inventory inventory, Component title) {
        return new CustomChestScreen(menu, inventory, title, "tame_mimic");
    }
    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(RenderPipelines.GUI_TEXTURED, background, leftPos, topPos, 0f, 0f,
                imageWidth, imageHeight, 256, 256);
    }
}
