package com.electrifiedded.poorores;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class PoorOresTab extends CreativeTabs {
    public static final PoorOresTab INSTANCE = new PoorOresTab();

    private PoorOresTab() {
        super(Reference.MOD_ID);
    }

    @Override
    public ItemStack createIcon() {
        return PoorOresConfig.ORES.isEmpty() ? new ItemStack(Blocks.STONE)
                : new ItemStack(Item.getItemFromBlock(PoorOresConfig.ORES.getFirst()));
    }
}
