package beardlessbrady.modcurrency.block;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-02-10
 */

public class TileEconomyBase extends TileEntity implements ITickable {
    protected int cashReserve, cashRegister, selectedSlot;
    protected EnumDyeColor color;
    protected boolean mode, creative;
    protected UUID owner, playerUsing;
    protected String selectedName;

    public static UUID EMPTYID = new UUID(0L, 0L);

    //Used for Warning messages
    private String message= "";
    private byte messageTime = 0;

    public TileEconomyBase(){
        cashReserve = 0;
        cashRegister = 0;
        creative = false;
        mode = false;
        color = EnumDyeColor.GRAY;

        selectedName = "No Item Selected";

        owner = new UUID(0L, 0L);
        playerUsing = EMPTYID;
    }

    @Override
    public void update() {
        if (playerUsing != EMPTYID) { // If a player is NOT using the machine */
            if (messageTime > 0) { // Timer for warning messages */
                messageTime--;
            } else {
                message = "";
            }
        }
    }

    //<editor-fold desc="NBT Stuff">
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("color", color.getDyeDamage());
        compound.setInteger("cashReserve", cashReserve);
        compound.setInteger("cashRegister", cashRegister);
        compound.setBoolean("mode", mode);
        compound.setUniqueId("playerUsing", playerUsing);
        compound.setUniqueId("owner", owner);
        compound.setBoolean("creative", creative);
        compound.setInteger("selectedSlot", selectedSlot);
        compound.setString("selectedName", selectedName);

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("color")) color = EnumDyeColor.byDyeDamage(compound.getInteger("color"));
        if (compound.hasKey("cashReserve")) cashReserve = compound.getInteger("cashReserve");
        if(compound.hasKey("cashRegister")) cashRegister = compound.getInteger("cashRegister");
        if (compound.hasKey("mode")) mode = compound.getBoolean("mode");
        if (compound.hasKey("playerUsing")) playerUsing = compound.getUniqueId("playerUsing");
        if (compound.hasUniqueId("owner")) owner = compound.getUniqueId("owner");
        if(compound.hasKey("creative")) creative = compound.getBoolean("creative");
        if(compound.hasKey("selectedSlot")) selectedSlot = compound.getInteger("selectedSlot");
        if(compound.hasKey("selectedName")) selectedName = compound.getString("selectedName");

    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("color", color.getDyeDamage());
        compound.setInteger("cashReserve", cashReserve);
        compound.setInteger("cashRegister", cashRegister);
        compound.setBoolean("mode", mode);
        compound.setUniqueId("playerUsing", playerUsing);
        compound.setUniqueId("owner", owner);
        compound.setBoolean("creative", creative);
        compound.setInteger("selectedSlot", selectedSlot);
        compound.setString("selectedName", selectedName);


        return new SPacketUpdateTileEntity(pos, 1, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        NBTTagCompound compound = pkt.getNbtCompound();

        if(compound.hasKey("color")) color = EnumDyeColor.byDyeDamage(compound.getInteger("color"));
        if(compound.hasKey("mode")) mode = compound.getBoolean("mode");
        if(compound.hasKey("cashReserve")) cashReserve = compound.getInteger("cashReserve");
        if(compound.hasKey("cashRegister")) cashRegister = compound.getInteger("cashRegister");
        if(compound.hasKey("playerUsing")) playerUsing = compound.getUniqueId("playerUsing");
        if(compound.hasUniqueId("owner")) owner = compound.getUniqueId("owner");
        if(compound.hasKey("creative")) creative = compound.getBoolean("creative");
        if(compound.hasKey("selectedSlot")) selectedSlot = compound.getInteger("selectedSlot");
        if(compound.hasKey("selectedName")) selectedName = compound.getString("selectedName");

    }
    //</editor-fold>

    //Field Id's
    public static final int FIELD_MODE = 0;
    public static final int FIELD_CASHRESERVE = 1;
    public static final int FIELD_CASHREGISTER = 2;
    public static final byte FIELD_SELECTED = 6;
    public static final byte FIELD_CREATIVE = 7;

    public int getFieldCount(){
        return 4;
    }

    public void setField(int id, int value){
        switch(id){
            case FIELD_MODE:
                mode = (value == 1);
                break;
            case FIELD_CASHRESERVE:
                cashReserve = value;
                break;
            case FIELD_CASHREGISTER:
                cashRegister = value;
                break;
            case FIELD_SELECTED:
                selectedSlot = value;
                break;
            case FIELD_CREATIVE:
                creative = (value == 1);
                break;
        }
    }

    public int getField(int id){
        switch(id){
            case FIELD_MODE:
                return (mode)? 1 : 0;
            case FIELD_CASHRESERVE:
                return cashReserve;
            case FIELD_CASHREGISTER:
                return cashRegister;
            case FIELD_SELECTED:
                return selectedSlot;
            case FIELD_CREATIVE:
                return (creative)? 1 : 0;
        }
        return 0;
    }

    public UUID getOwner(){
        return owner;
    }

    public void setOwner(UUID uuid){
        owner = uuid;
    }

    public boolean isOwner(){
        return owner.equals(playerUsing);
    }

    public UUID getPlayerUsing(){
        return playerUsing;
    }

    public void setPlayerUsing(UUID uuid){
        playerUsing = uuid;
    }

    public void voidPlayerUsing(){
        playerUsing = EMPTYID;
    }

    public EnumDyeColor getColor(){
        return color;
    }

    public void setColor(EnumDyeColor newColor){
        color = newColor;
    }

    public String getSelectedName(){
        if(selectedName.equals("Air")) return "No Item";
        return selectedName;
    }

    public void setSelectedName(String name){
        selectedName = name;
    }

    /** Sets error message **/
    public void setMessage(String newMessage, byte time){
        message = newMessage;
        messageTime = time;
    }

    /** Gets error message **/
    public String getMessage(){
        return message;
    }
}