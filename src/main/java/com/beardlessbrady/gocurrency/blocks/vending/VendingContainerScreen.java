package com.beardlessbrady.gocurrency.blocks.vending;

import com.beardlessbrady.gocurrency.CustomButton;
import com.beardlessbrady.gocurrency.GOCurrency;
import com.beardlessbrady.gocurrency.network.MessageVendingCashButton;
import com.beardlessbrady.gocurrency.network.MessageVendingStateData;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import org.lwjgl.opengl.GL12;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by BeardlessBrady on 2021-03-01 for Currency-Mod
 * All Rights Reserved
 * https://github.com/Beardlessbrady/Currency-Mod
 */
public class VendingContainerScreen extends ContainerScreen<VendingContainer> {
    private static final ResourceLocation PLAYER = new ResourceLocation("gocurrency", "textures/gui/player.png");
    private static final ResourceLocation TEXTURE = new ResourceLocation("gocurrency", "textures/gui/vending.png");
    private static final ResourceLocation TEXTURE2 = new ResourceLocation("gocurrency", "textures/gui/vending2.png");

    final static int FONT_Y_SPACING = 10;
    final static int PLAYER_INV_LABEL_XPOS = VendingContainer.PLAYER_INVENTORY_XPOS;
    final static int PLAYER_INV_LABEL_YPOS = VendingContainer.PLAYER_INVENTORY_YPOS - FONT_Y_SPACING;

    final static byte BUTTONID_MODE = 0;
    final static byte BUTTONID_PRICE = 1;
    final static byte BUTTONID_CASH = 2;
    final static byte BUTTONID_GUISWITCH = 3;

    public VendingContainerScreen(VendingContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void init() {
        super.init();
        int i = (width - xSize) / 2;
        int j = (height - ySize) / 2;

        buttons.clear();
        // 0
        this.buttons.add(new Button(i + 115, j - 30, 20, 20,
                new TranslationTextComponent(""), (button) -> {handle(HANDLE_MODE);}));
        this.children.add(this.buttons.get(BUTTONID_MODE));

        this.buttons.add(new CustomButton(i - 1000, j - 1000 , 21,23, 232, 21, 232, 21,
                new TranslationTextComponent(""), (button) -> {handle(HANDLE_EDITPRICE);}));

        this.buttons.add(new CustomButton(i + 116, j + 29 , 20,13, 177, 218, 177, 231,
                new TranslationTextComponent(""), (button) -> {handle(HANDLE_CASH);})); //
        this.children.add(this.buttons.get(BUTTONID_CASH));

        this.buttons.add(new CustomButton(i - 1000, j - 1000 , 10,10, 217, 218, 217, 228,
                new TranslationTextComponent(""), (button) -> {      })); //TODO
    }

    private final static byte HANDLE_MODE = 0;
    private final static byte HANDLE_EDITPRICE = 1;
    private final static byte HANDLE_CASH = 2;
    private void handle(int k) {
        int i = (width - xSize) / 2;
        int j = (height - ySize) / 2;
        switch (k) {
            case HANDLE_MODE: //VendingStateData.MODE_INDEX:
                if (container.getVendingStateData(VendingStateData.MODE_INDEX) == 1) { // MODE SELL (Reverse since hase not changed yet)
                    // Hide Price Settings Tab
                    this.children.remove(this.buttons.get(BUTTONID_PRICE));
                    this.buttons.set(BUTTONID_PRICE, new CustomButton(i + 1000, j + 1000, 21, 23, 232, 21, 232, 21,
                            new TranslationTextComponent(""), (button) -> {
                        handle(HANDLE_EDITPRICE);
                    }));
                    this.children.add(this.buttons.get(BUTTONID_PRICE));

                    // Hide GUI_SWITCH BUTTON
                    this.children.remove(this.buttons.get(BUTTONID_GUISWITCH));
                    this.buttons.set(BUTTONID_GUISWITCH, new CustomButton(i + 1000, j + 1000, 10,10, 217, 218, 217, 228,
                            new TranslationTextComponent(""), (button) -> {      }));
                    this.children.add(this.buttons.get(BUTTONID_GUISWITCH));

                    this.buttons.set(BUTTONID_CASH, (new CustomButton(i + 116, j + 29 , 20,13, 177, 218, 177, 231,
                            new TranslationTextComponent(""), (button) -> {handle(HANDLE_EDITPRICE);})));
                } else { // Mode STOCK
                    // Show Price Settings Tab
                    this.children.remove(this.buttons.get(BUTTONID_PRICE));
                    this.buttons.set(BUTTONID_PRICE, new CustomButton(i + 11, j - 33, 21, 23, 232, 21, 232, 21,
                            new TranslationTextComponent(""), (button) -> {
                        handle(HANDLE_EDITPRICE);
                    }));
                    this.children.add(this.buttons.get(BUTTONID_PRICE));

                    // Show GUI_SWITCH Button
                    this.buttons.set(BUTTONID_GUISWITCH, new CustomButton(i + 125, j - 48 , 10,10, 217, 218, 217, 228,
                            new TranslationTextComponent(""), (button) -> {      })); //TODO
                    this.children.add(this.buttons.get(BUTTONID_GUISWITCH));

                    this.buttons.set(BUTTONID_CASH, (new CustomButton(i + 116, j + 29 , 20,13, 197, 218, 197, 231,
                            new TranslationTextComponent(""), (button) -> {handle(HANDLE_EDITPRICE);})));
                }
                container.updateModeSlots();
                GOCurrency.NETWORK_HANDLER.sendToServer(new MessageVendingStateData(container.getTile().getPos(),VendingStateData.MODE_INDEX));
                break;
            case HANDLE_EDITPRICE: //VendingStateData.EDITPRICE_INDEX:
                if (container.getVendingStateData(VendingStateData.EDITPRICE_INDEX) == 1) { // PRICE EDIT ON (Reverse since hase not changed yet)
                    this.children.remove(this.buttons.get(BUTTONID_PRICE));
                    this.buttons.set(BUTTONID_PRICE, new CustomButton(i + 11, j - 33, 21, 23, 232, 21, 232, 21,
                            new TranslationTextComponent(""), (button) -> {
                        handle(HANDLE_EDITPRICE);
                    }));
                    this.children.add(this.buttons.get(BUTTONID_PRICE));
                } else {
                    this.children.remove(this.buttons.get(BUTTONID_PRICE));
                    this.buttons.set(BUTTONID_PRICE, new CustomButton(i - 73, j - 33, 21, 23, 126, 0, 126, 0,
                            new TranslationTextComponent(""), (button) -> {
                        handle(HANDLE_EDITPRICE);
                    }));
                    this.children.add(this.buttons.get(BUTTONID_PRICE));
                }
                GOCurrency.NETWORK_HANDLER.sendToServer(new MessageVendingStateData(container.getTile().getPos(), VendingStateData.EDITPRICE_INDEX));
                break;
            case HANDLE_CASH: //CASH BUTTON
                GOCurrency.NETWORK_HANDLER.sendToServer(new MessageVendingCashButton(container.getTile().getPos(), container.getVendingStateData(VendingStateData.MODE_INDEX)));
                break;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            super.render(matrixStack, mouseX, mouseY, partialTicks);
            this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
            this.drawCustomTooltips(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderHoveredTooltip(MatrixStack matrixStack, int x, int y) {
        if (this.minecraft.player.inventory.getItemStack().isEmpty() && this.hoveredSlot != null) {
            int slot = this.hoveredSlot.slotNumber;
        }
        super.renderHoveredTooltip(matrixStack, x, y);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        // Draw Background
        int edgeSpacingX = (this.width - this.xSize) / 2;
        int edgeSpacingY = (this.height - this.ySize) / 2;

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        //Player Inventory
        this.minecraft.getTextureManager().bindTexture(PLAYER);
        this.blit(matrixStack, edgeSpacingX, edgeSpacingY + 111, 0, 157, 176, 99);

        if (container.getVendingStateData(VendingStateData.MODE_INDEX) == 0) { // Sell
            // CLOSED Vending Machine
            this.minecraft.getTextureManager().bindTexture(TEXTURE);
            this.blit(matrixStack, edgeSpacingX + 32, edgeSpacingY - 53, 0, 0, 125, 163);
        } else {
            // OPEN Vending Machine
            this.minecraft.getTextureManager().bindTexture(TEXTURE2);
            this.blit(matrixStack, edgeSpacingX - 78, edgeSpacingY - 53, 0, 0, 235, 163);
            this.minecraft.getTextureManager().bindTexture(TEXTURE);
        }



    }

    // Returns true if the given x,y coordinates are within the given rectangle
    public static boolean isInRect(int x, int y, int xSize, int ySize, int mouseX, int mouseY) {
        return ((mouseX >= x && mouseX <= x + xSize) && (mouseY >= y && mouseY <= y + ySize));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
        int i = (width - xSize) / 2;
        int j = (height - ySize) / 2;

        drawBufferSize(matrixStack);

        this.font.func_243248_b(matrixStack, this.title, 40, -48, 4210752); //Block Title
        this.font.func_243248_b(matrixStack, this.playerInventory.getDisplayName(), (float) this.playerInventoryTitleX, 117, 4210752); //Inventory Title

        if (container.getVendingStateData(VendingStateData.MODE_INDEX) == 0) { // Sell
            GL12.glDisable(GL12.GL_DEPTH_TEST);
            GL12.glPushMatrix();
            GL12.glScalef(0.7F, 0.7F, 0.8F);
            this.font.drawStringWithShadow(matrixStack, TextFormatting.WHITE + I18n.format("block.gocurrency.vending.cash"), 56, -55,0);
            this.font.drawString(matrixStack, TextFormatting.DARK_GREEN + container.currencyToString(container.getVendingStateData(VendingStateData.MODE_INDEX)), 94, -55, 0);
            GL12.glPopMatrix();
            GL12.glDisable(GL12.GL_DEPTH_TEST);

            this.minecraft.getTextureManager().bindTexture(TEXTURE);
            // Draw Top tips of outer machine to cover items inside
            this.blit(matrixStack, 98, -31, 176, 245, 11, 11);
            this.blit(matrixStack, 39, -31, 187, 245, 11, 11);

            // Sell Mode Button icon
            this.blit(matrixStack, 117, -28, 144, 67, 16, 16);
        } else {
            GL12.glDisable(GL12.GL_DEPTH_TEST);
            GL12.glPushMatrix();
            GL12.glScalef(0.7F, 0.7F, 0.8F);
            this.font.drawStringWithShadow(matrixStack, TextFormatting.WHITE + I18n.format("block.gocurrency.vending.income"), 56, -55,0);
            this.font.drawString(matrixStack, TextFormatting.AQUA + container.currencyToString(container.getVendingStateData(VendingStateData.MODE_INDEX)), 104, -55,0);
            GL12.glPopMatrix();
            GL12.glDisable(GL12.GL_DEPTH_TEST);

            this.minecraft.getTextureManager().bindTexture(TEXTURE);
            // Stock Mode Button icon
            this.blit(matrixStack, 117, -28, 127, 67, 16, 16);

            // Price Tag Button Icon
            this.blit(matrixStack, 7, 2, 178, 67, 16, 16);

            if(container.getVendingStateData(VendingStateData.EDITPRICE_INDEX) == 1) { // PRICE EDIT ON
                this.blit(matrixStack, -73, -33, 126, 0, 106, 48); // Big Tag

                this.font.drawStringWithShadow(matrixStack, I18n.format("block.gocurrency.vending.slotpricing"), -45, -27, Objects.requireNonNull(Color.fromHex("#cbd11d")).getColor()); //Inventory Title
            }
        }


    }

    protected void drawCustomTooltips(MatrixStack matrixStack, int x, int y){
        for (int i = 0; i < buttons.size(); i++) {
            Widget butt = buttons.get(i);
            if (x >= butt.x && x <= butt.x + butt.getWidth() && y >= butt.y && y <= butt.y + butt.getHeightRealms()) {
                String text = "NULL";
                int mode = container.getVendingStateData(VendingStateData.MODE_INDEX);
                List<ITextComponent> listText = new ArrayList<>();

                switch(i) {
                    case BUTTONID_MODE:
                        listText.add(ITextComponent.getTextComponentOrEmpty(I18n.format("block.gocurrency.vending.tooltip.mode" + mode + "_0", TextFormatting.DARK_PURPLE, TextFormatting.RESET)));
                        listText.add(ITextComponent.getTextComponentOrEmpty(I18n.format("block.gocurrency.vending.tooltip.mode" + mode + "_1", TextFormatting.GRAY)));
                        break;
                    case BUTTONID_CASH:
                        listText.add(ITextComponent.getTextComponentOrEmpty(I18n.format("block.gocurrency.vending.tooltip.cash" + mode, mode == 0? TextFormatting.GREEN : TextFormatting.AQUA)));
                        break;
                    case BUTTONID_GUISWITCH:
                        listText.add(ITextComponent.getTextComponentOrEmpty(I18n.format("block.gocurrency.vending.tooltip.guiswitch")));
                        break;
                    case BUTTONID_PRICE:
                        listText.add(ITextComponent.getTextComponentOrEmpty(I18n.format("block.gocurrency.vending.tooltip.pricesetting")));
                        break;
                }

                renderWrappedToolTip(matrixStack, listText, x, y, this.font);
            }
        }
    }

    private void drawBufferSize(MatrixStack matrixStack) {
        GL12.glDisable(GL12.GL_DEPTH_TEST);
        GL12.glPushMatrix();
        GL12.glScalef(0.7F, 0.7F, 0.8F);

        matrixStack.push();
        matrixStack.translate(0, 0, 350);

        String num;
        int startY = -30;
        int startX = 59;
        int columnCount = 4;
        int rowCount = 4;

        for (int j = 0; j < columnCount; j++) {
            for (int i = 0; i < rowCount; i++) {
                int index = (i + (rowCount * j));
                int count = container.getStockContents().getStackSize(index);

                if (count > 0) {
                    num = TextFormatting.WHITE + Integer.toString(count);
                } else if (count == 0) {
                    num = TextFormatting.RED + "Out";
                } else {
                    num = " ";
                }

                if (count < 10 && count > 0) num = "  " + num;
                if (count >= 10 && count < 100) num = " " + num;

                if (count != 1 && !container.getStockContents().getStackInSlot(index).isEmpty())
                    this.font.drawStringWithShadow(matrixStack, num, startX + (i * 26), startY + (j * 31), 1); //Inventory Title
            }
        }
        matrixStack.pop();
        GL12.glPopMatrix();
        GL12.glDisable(GL12.GL_DEPTH_TEST);
    }

}
