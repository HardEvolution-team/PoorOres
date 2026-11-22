package com.electrifiedded.poorores;

import com.electrifiedded.archaiclib.legacy.registry.LegacyRecipes;
import com.electrifiedded.archaiclib.v1710.api.item.ArchaicItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

@SuppressWarnings("deprecation")
public class Nugget extends ArchaicItem {
    private final String name, oreDictName;
    private final PoorOre poorOre;
    private final int burnTime, meta, nuggetRenderType;

    public Nugget(String name, PoorOre poorOre, int burnTime, int meta, int nuggetRenderType) {
        this.name = name + "_nugget";
        this.poorOre = poorOre;
        this.burnTime = burnTime;
        this.meta = meta;
        this.nuggetRenderType = nuggetRenderType;
        this.oreDictName = "nugget" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
        setTranslationKey(this.name);
        setCreativeTab(PoorOresTab.INSTANCE);
        setTextureName(Reference.MOD_ID + ":" + this.name);
    }

    public String getName() { return name; }
    public PoorOre getPoorOre() { return poorOre; }
    public int getBurnTime() { return burnTime; }
    public int getNuggetRenderType() { return nuggetRenderType; }

    /** Smelting result of the base ore (the ingot this nugget is a ninth of). */
    public ItemStack getIngot() {
        if (poorOre.getBaseBlock() == null) return ItemStack.EMPTY;
        return FurnaceRecipes.instance().getSmeltingResult(new ItemStack(poorOre.getBaseBlock(), 1, meta));
    }

    public void registerSmelting() {
        if (PoorOresConfig.addSmelting) GameRegistry.addSmelting(poorOre, new ItemStack(this), 0.1F);
    }

    public void registerCrafting() {
        if (!PoorOresConfig.addCrafting) return;
        ItemStack ingot = getIngot();
        if (ingot.isEmpty()) {
            PoorOresMod.LOG.error("{}: could not get ingot, recipes will not be added", name);
            return;
        }
        LegacyRecipes.addShapelessRecipe(new ItemStack(this, 9), ingot);
        LegacyRecipes.addRecipe(ingot.copy(), "nnn", "nnn", "nnn", 'n', oreDictName);
    }

    public void registerOreDict() {
        OreDictionary.registerOre(oreDictName, this);
        OreDictionary.registerOre("nuggetAll", this);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String prefix = I18n.translateToLocal("poorores.nuggets.prefix").trim();
        String key = "poorores.material." + poorOre.getName();
        String material = I18n.canTranslate(key) ? I18n.translateToLocal(key).trim() : "";
        return prefix.replaceFirst("NUGGETNAME", material.isEmpty() ? poorOre.baseName() : material);
    }
}
