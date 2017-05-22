package gunn.modcurrency.mod.client.gui;

import gunn.modcurrency.mod.client.gui.util.TabButton;
import gunn.modcurrency.mod.container.ContainerVending;
import gunn.modcurrency.mod.network.PacketHandler;
import gunn.modcurrency.mod.network.PacketSetFieldToServer;
import gunn.modcurrency.mod.network.PacketSetItemCostToServer;
import gunn.modcurrency.mod.tileentity.TileVending;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-05-09
 */
public class GuiVending extends GuiContainer {
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("modcurrency", "textures/gui/guivendortexture.png");
    private static final ResourceLocation TAB_TEXTURE = new ResourceLocation("modcurrency", "textures/gui/guivendortabtexture.png");
    private TileVending tile;
    private GuiTextField nameField;
    private boolean creativeExtended;
    private EntityPlayer player;

    private static final int MODE_ID = 0;
    private static final int LOCK_ID = 1;
    private static final int GEAR_ID = 2;
    private static final int CREATIVE_ID = 3;

    public GuiVending(EntityPlayer entityPlayer, TileVending te) {
        super(new ContainerVending(entityPlayer.inventory, te));
        tile = te;
        xSize = 176;
        ySize = 235;
        creativeExtended = false;
        player = entityPlayer;
    }

    @Override
    public void initGui() {
        super.initGui();
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;

       this.buttonList.add(new TabButton("Mode", MODE_ID, i - 20, j + 20,0, 88, 20, 21, "", TAB_TEXTURE));
       this.buttonList.add(new TabButton("Lock", LOCK_ID, i - 20, 22 + ((TabButton)this.buttonList.get(MODE_ID)).getButtonY(),0, 22, 20, 21, "", TAB_TEXTURE));
       this.buttonList.add(new TabButton("Gear", GEAR_ID, i - 20, 22 + ((TabButton)this.buttonList.get(LOCK_ID)).getButtonY(),0, 0, 20, 21, "", TAB_TEXTURE));
       this.buttonList.add(new TabButton("Creative", CREATIVE_ID, i - 20, 22 + ((TabButton)this.buttonList.get(GEAR_ID)).getButtonY(),0, 44, 20, 21, "", TAB_TEXTURE));

       this.buttonList.get(MODE_ID).visible = false;
       this.buttonList.get(LOCK_ID).visible = false;
       this.buttonList.get(GEAR_ID).visible = false;
       this.buttonList.get(CREATIVE_ID).visible = false;
    }

    private void setCost() {
        if (this.nameField.getText().length() > 0) {
            int newCost = Integer.valueOf(this.nameField.getText());

            PacketSetItemCostToServer pack = new PacketSetItemCostToServer();
            pack.setData(newCost, tile.getPos());
            PacketHandler.INSTANCE.sendToServer(pack);

            tile.getWorld().notifyBlockUpdate(tile.getPos(), tile.getBlockType().getDefaultState(), tile.getBlockType().getDefaultState(), 3);
        }
    }

    @Override
    public void onResize(Minecraft mcIn, int w, int h) {
        super.onResize(mcIn, w, h);
        PacketSetFieldToServer pack = new PacketSetFieldToServer();
        pack.setData(0, 8, tile.getPos());
        PacketHandler.INSTANCE.sendToServer(pack);

        creativeExtended = false;
    }

    //<editor-fold desc="Draw and Renders">
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        if(tile.isTwoBlock()){
            drawTexturedModalRect(guiLeft + 43, guiTop + 31, 7, 210, 90, 18);
            drawTexturedModalRect(guiLeft + 43, guiTop + 103, 7, 210, 90, 18);
            drawTexturedModalRect(guiLeft + 43, guiTop + 121, 7, 210, 90, 18);
        }
        drawTexturedModalRect(guiLeft + 43, guiTop + 49, 7, 210, 90, 18);
        drawTexturedModalRect(guiLeft + 43, guiTop + 67, 7, 210, 90, 18);
        drawTexturedModalRect(guiLeft + 43, guiTop + 85, 7, 210, 90, 18);

        //Draw Input Icons
        if (tile.getField(2) == 0) {
            drawTexturedModalRect(guiLeft + 152, guiTop + 9, 198, 0, 15, 15);
        } else {
            drawTexturedModalRect(guiLeft + 152, guiTop + 9, 215, 0, 15, 15);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TAB_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;

        if (tile.getOwner() == player.getUniqueID().toString() || player.isCreative()) {  //If owner or creative
            this.buttonList.get(MODE_ID).visible = true;

            if(tile.getField(2) == 1) { //If in EDIT mode
                this.buttonList.get(LOCK_ID).visible = true;
                this.buttonList.get(GEAR_ID).visible = true;

                ((TabButton)buttonList.get(GEAR_ID)).setOpenState(tile.getField(8) == 1, 26);
                if(((TabButton)buttonList.get(GEAR_ID)).openState()){
                    drawTexturedModalRect(-91, 64, 27, 0, 91, 47);
                    drawSelectionOverlay();
                    Minecraft.getMinecraft().getTextureManager().bindTexture(TAB_TEXTURE);
                    this.buttonList.set(CREATIVE_ID, new TabButton("Creative", CREATIVE_ID, i - 20, 22 + ((TabButton)this.buttonList.get(GEAR_ID)).getButtonY(),0, 44, 20, 21, "", TAB_TEXTURE));
                }else{
                    this.buttonList.set(CREATIVE_ID, new TabButton("Creative", CREATIVE_ID, i - 20, 22 + ((TabButton)this.buttonList.get(GEAR_ID)).getButtonY(),0, 44, 20, 21, "", TAB_TEXTURE));
                }
                if(player.isCreative()){    //If in Creative
                    this.buttonList.get(CREATIVE_ID).visible = true;

                    ((TabButton)buttonList.get(CREATIVE_ID)).setOpenState(creativeExtended, 26 + ((TabButton)this.buttonList.get(GEAR_ID)).openExtY());
                    if(((TabButton)buttonList.get(CREATIVE_ID)).openState()) drawTexturedModalRect(-91, 86 + ((TabButton)this.buttonList.get(GEAR_ID)).openExtY(), 27, 48, 91, 47);
                }else{  //If in Survival
                    this.buttonList.get(CREATIVE_ID).visible = false;
                }

            }else{  //In SELL mode
                this.buttonList.get(LOCK_ID).visible = false;
                this.buttonList.get(GEAR_ID).visible = false;
                this.buttonList.get(CREATIVE_ID).visible = false;
            }
            drawIcons();
        }
        drawText();











    }

    private void drawText(){
        fontRendererObj.drawString(I18n.format("tile.modcurrency:blockvending.name"), 5, 6, Color.darkGray.getRGB());
        fontRendererObj.drawString(I18n.format("tile.modcurrency:gui.playerinventory"), 4, 142, Color.darkGray.getRGB());
        fontRendererObj.drawString(I18n.format("Cash") + ": $" + tile.getField(0), 5, 15, Color.darkGray.getRGB());

        if (tile.getField(8) == 1) {
            fontRendererObj.drawString(I18n.format("tile.modcurrency:guivending.slotsettings"), -81, 71, Integer.parseInt("42401c", 16));
            fontRendererObj.drawString(I18n.format("tile.modcurrency:guivending.slotsettings"), -80, 70, Integer.parseInt("fff200", 16));
            fontRendererObj.drawString(I18n.format("tile.modcurrency:guivending.cost"), -84, 92, Integer.parseInt("211d1b", 16));
            fontRendererObj.drawString(I18n.format("tile.modcurrency:guivending.cost"), -83, 91, Color.lightGray.getRGB());
            fontRendererObj.drawString(I18n.format("$"), -57, 91, Integer.parseInt("0099ff", 16));

            GL11.glPushMatrix();
            GL11.glScaled(0.7, 0.7, 0.7);
            fontRendererObj.drawString(I18n.format("[" + tile.getSelectedName() + "]"), -117, 115, Integer.parseInt("001f33", 16));
            fontRendererObj.drawString(I18n.format("[" + tile.getSelectedName() + "]"), -118, 114, Integer.parseInt("0099ff", 16));
            GL11.glPopMatrix();
        }
        if (creativeExtended) {
            fontRendererObj.drawString(I18n.format("tile.modcurrency:guivending.infinitestock"), -86, 93 + ((TabButton)this.buttonList.get(GEAR_ID)).openExtY(), Integer.parseInt("42401c", 16));
            fontRendererObj.drawString(I18n.format("tile.modcurrency:guivending.infinitestock"), -85, 92 + ((TabButton)this.buttonList.get(GEAR_ID)).openExtY(), Integer.parseInt("fff200", 16));
        }
    }

    private void drawIcons() {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        int size = 1;
        if(tile.getField(2) == 1){
            size = 3;
            if(tile.getField(5) == 1) size = 4;
        }

        for (int k = 0; k < size; k++) {
            int tabLoc = 22 * (k + 1);
            int offSet2 = 0;
            if (tile.getField(8) == 1) offSet2 = 26;

            switch (k) {
                case 0:
                    drawTexturedModalRect(-19, tabLoc, 236, 73, 19, 16);
                    break;
                case 1:
                    if (tile.getField(1) == 0) {
                        drawTexturedModalRect(-19, tabLoc, 236, 1, 19, 16);
                    }else drawTexturedModalRect(-19, tabLoc, 216, 1, 19, 16);
                    break;
                case 2:
                    drawTexturedModalRect(-19, tabLoc, 236, 19, 19, 16); //Icon
                    break;
                case 3:
                    drawTexturedModalRect(-19, tabLoc + offSet2, 236, 37, 19, 16);
                    break;
            }
        }
    }



    private void drawSelectionOverlay() {
        if (tile.getField(8) == 1) {
            int slotId = tile.getField(3) -37;
            int slotColumn, slotRow;

            if (slotId >= 0 && slotId <= 4) {
                slotColumn = 0;
                slotRow = slotId + 1;
            } else if (slotId >= 5 && slotId <= 9) {
                slotColumn = 1;
                slotRow = (slotId + 1) - 5;
            } else if (slotId >= 10 && slotId <= 14) {
                slotColumn = 2;
                slotRow = (slotId + 1) - 10;
            } else if (slotId >= 15 && slotId <= 19) {
                slotColumn = 3;
                slotRow = (slotId + 1) - 15;
            } else if (slotId >= 20 && slotId <= 24) {
                slotColumn = 4;
                slotRow = (slotId + 1) - 20;
            } else {
                slotColumn = 5;
                slotRow = (slotId + 1) - 25;
            }

            if(!tile.isTwoBlock()) slotColumn++;

            Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
            drawTexturedModalRect(24 + (18 * slotRow), 30 + (18 * slotColumn), 177, 0, 20, 20); //Selection Box
        }
    }

    @Override
    protected void renderToolTip(ItemStack stack, int x, int y) {
        int i = (x - (this.width - this.xSize) / 2);
        int j = (y - (this.height - this.ySize) / 2);

        if(j < 140 && j > 30 && i >= 43) {
            int startX = 43;
            int startY = 31;
            int row = ((i - startX) / 18);
            int column = ((j - startY) / 18);
            int slot = row + (column * 5);
            if(!tile.isTwoBlock())slot = slot -5;

            ItemStack currStack = tile.getStack(slot);

            List<String> list = new ArrayList<>();
            list.add(String.valueOf(currStack.getDisplayName()));

            if(tile.getField(2) == 0){
                if(!tile.canAfford(slot)){
                    list.add(TextFormatting.RED + "$" + (String.valueOf(tile.getItemCost(slot))));
                }else{
                    list.add("$" + (String.valueOf(tile.getItemCost(slot))));
                }
                if(tile.isGhostSlot(slot)) {
                    list.add(TextFormatting.RED + "OUT OF STOCK");
                }
            }else{
                list.add("$" + (String.valueOf(tile.getItemCost(slot))));
            }

            FontRenderer font = stack.getItem().getFontRenderer(stack);
            net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(stack);
            this.drawHoveringText(list, x, y, (font == null ? fontRendererObj : font));
            net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();

            tile.getWorld().notifyBlockUpdate(tile.getPos(), tile.getBlockType().getDefaultState(), tile.getBlockType().getDefaultState(), 3);
        }else{
            super.renderToolTip(stack, x, y);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Keyboard and Mouse Inputs">
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(tile.getField(2) == 1) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
//            nameField.mouseClicked(mouseX, mouseY, mouseButton);
           // if (tile.getField(8) == 1 && mouseButton == 0) updateTextField();
        }else {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        int numChar = Character.getNumericValue(typedChar);
        if ((tile.getField(2) == 1) && ((numChar >= 0 && numChar <= 9) || (keyCode == 14) || keyCode == 211 || (keyCode == 203) || (keyCode == 205))) { //Ensures keys input are only numbers or backspace type keys
            if (this.nameField.textboxKeyTyped(typedChar, keyCode)) setCost();
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    //Button Actions
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            /*case 0:  //Change Button
                PacketItemSpawnToServer pack0 = new PacketItemSpawnToServer();
                pack0.setBlockPos(tile.getPos());
                PacketHandler.INSTANCE.sendToServer(pack0);
                break;
            case 1: //Infinite? Button
                PacketSetFieldToServer pack1 = new PacketSetFieldToServer();
                pack1.setData((tile.getField(6) == 1) ? 0 : 1, 6, tile.getPos());
                PacketHandler.INSTANCE.sendToServer(pack1);
                break;*/
            case MODE_ID: //Mode Button
                PacketSetFieldToServer pack2 = new PacketSetFieldToServer();
                pack2.setData((tile.getField(2) == 1) ? 0 : 1, 2, tile.getPos());
                PacketHandler.INSTANCE.sendToServer(pack2);
                tile.getWorld().notifyBlockUpdate(tile.getPos(), tile.getBlockType().getDefaultState(), tile.getBlockType().getDefaultState(), 3);
                break;
            case LOCK_ID: //Lock Button
                PacketSetFieldToServer pack3 = new PacketSetFieldToServer();
                pack3.setData((tile.getField(1) == 1) ? 0 : 1, 1, tile.getPos());
                PacketHandler.INSTANCE.sendToServer(pack3);
                tile.getWorld().notifyBlockUpdate(tile.getPos(), tile.getBlockType().getDefaultState(), tile.getBlockType().getDefaultState(), 3);
                break;
            case GEAR_ID: //Gear Button
                int newGear = tile.getField(8) == 1 ? 0 : 1;
                tile.setField(8, newGear);
                PacketSetFieldToServer pack4 = new PacketSetFieldToServer();
                pack4.setData(newGear, 8, tile.getPos());
                PacketHandler.INSTANCE.sendToServer(pack4);
                break;
            case CREATIVE_ID: //Creative Button
                creativeExtended = !creativeExtended;
                break;
        }
    }
    //</editor-fold>





}