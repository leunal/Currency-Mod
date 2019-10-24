package beardlessbrady.modcurrency.block.vending;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-10-19
 */

public class ItemVendor {
    private ItemStack itemStack;
    private int size, cost, amount;
    private int[] bundled;
    int itemMax, timeRaise, timeElapsed;

    public ItemVendor(ItemStack itemStack, int size){
        this.itemStack = itemStack;
        itemStack.setCount(1);

        this.size = size;
        this.cost = 0;

        amount = 1;
        itemMax = 0;
        timeRaise = 0;
        timeElapsed = 0;
    }

    public ItemVendor(ItemStack itemStack){
        this.itemStack = itemStack;
        this.size = itemStack.getCount();
        itemStack.setCount(1);

        this.cost = 0;

        amount = 1;
        itemMax = 0;
        timeRaise = 0;
        timeElapsed = 0;
    }

    public ItemVendor(NBTTagCompound compound){
        fromNBT(compound);
    }

    public ItemStack getStack(){
        return itemStack;
    }

    public int getSize(){
        return size;
    }

    public ItemVendor setSize(int i){
        size = i;

        return this;
    }

    public int getCost(){
        return cost;
    }

    public ItemVendor setCost(int i){
        cost = i;

        return this;
    }

    public int getAmount(){
        return amount;
    }

    public ItemVendor setAmount(int i){
        amount = i;

        return this;
    }

    /**
     * @return bundle array, 0 is main slot
     */
    public int[] getBundle(){
        return bundled;
    }

    /**
     * @param i = Array of bundle slots
     * main slot is 0
     */
    public ItemVendor setBundle(int[] i){
        bundled = i.clone();

        return this;
    }

    public int getBundleMainSlot(){
        if(bundled != null){
            return bundled[0];
        }
        return -1;
    }

    public int getItemMax(){
        return itemMax;
    }

    public ItemVendor setItemMax(int i){
        itemMax = i;

        return this;
    }

    public int getTimeRaise(){
        return timeRaise;
    }

    public ItemVendor setTimeRaise(int i){
        timeRaise = i;

        return this;
    }

    public int getTimeElapsed(){
        return timeElapsed;
    }

    public ItemVendor setTimeElapsed(int i){
        timeElapsed = i;

        return this;
    }

    public NBTTagCompound toNBT(){
        NBTTagCompound compound = new NBTTagCompound();
        compound.setTag("stack", itemStack.serializeNBT());
        if(size != 0) compound.setInteger("size", size);
        if(cost != 0) compound.setInteger("cost", cost);
        if(amount != 0) compound.setInteger("amount", amount);
        if(bundled != null) compound.setIntArray("bundled", bundled);
        if(itemMax != 0) compound.setInteger("itemMax", itemMax);
        if(timeRaise != 0) compound.setInteger("timeRaise", timeRaise);
        if(timeElapsed != 0) compound.setInteger("timeElapsed", timeElapsed);

        return compound;
    }

    public void fromNBT(NBTTagCompound nbt){
        if(nbt.hasKey("stack")){
            itemStack.deserializeNBT(nbt.getCompoundTag("stack"));
        }else itemStack = ItemStack.EMPTY;

        if(nbt.hasKey("size")) {
            size = nbt.getInteger("size");
        }else size = 0;

        if(nbt.hasKey("cost")){
            cost = nbt.getInteger("cost");
        }else cost = 0;

        if(nbt.hasKey("amount")){
            amount = nbt.getInteger("amount");
        }else amount = 0;

        if(nbt.hasKey("bundled")) bundled = nbt.getIntArray("bundled");

        if(nbt.hasKey("itemMax")){
            itemMax = nbt.getInteger("itemMax");
        }else itemMax = 0;

        if(nbt.hasKey("timeRaise")){
            timeRaise = nbt.getInteger("timeRaise");
        }else timeRaise = 0;

        if(nbt.hasKey("timeElapsed")){
            timeElapsed = nbt.getInteger("timeElapsed");
        }else timeElapsed = 0;
    }

    public ItemVendor shrinkSize(int amount){
        size =- amount;

        if(size < 0) size = 0;

        return this;
    }

    public ItemVendor growSize(int amount){
        size =+ amount;

        return this;
    }
}
