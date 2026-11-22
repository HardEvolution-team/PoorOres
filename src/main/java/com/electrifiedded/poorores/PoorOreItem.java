package com.electrifiedded.poorores;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;

@SuppressWarnings("deprecation")
public class PoorOreItem extends ItemBlock {
    private final PoorOre poorOre;

    public PoorOreItem(PoorOre block) {
        super(block);
        this.poorOre = block;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String prefix = I18n.translateToLocal("poorores.prefix").trim();
        return prefix.replaceFirst("ORENAME", poorOre.baseName());
    }
}
