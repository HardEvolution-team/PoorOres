package com.electrifiedded.poorores;

import com.electrifiedded.archaiclib.v1710.api.block.ArchaicBlock;
import com.electrifiedded.archaiclib.v1710.api.client.IIcon;
import com.electrifiedded.archaiclib.v1710.api.client.IIconRegister;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

/** A "poor" ore: looks like a sparse version of the base ore, drops nuggets of what the base ore drops. */
public class PoorOre extends ArchaicBlock {
    private final String name, modID, baseBlockName, baseBlockTexture, underlyingBlockName, oreDictName;
    private final int meta, oreRenderType, veinRate, veinSize, veinHeight;
    private final List<Integer> dimWhiteList, dimBlackList;
    private final Block underlyingBlock;
    private Block baseBlock;
    private Nugget nugget;
    private IIcon icon;

    public PoorOre(String name, String modID, String baseBlockName, int meta, String baseBlockTexture, Block underlyingBlock,
                   String underlyingBlockName, int oreRenderType, int veinRate, int veinSize, int veinHeight,
                   List<Integer> dimWhiteList, List<Integer> dimBlackList) {
        super(Material.ROCK);
        this.name = name;
        this.modID = modID;
        this.baseBlockName = baseBlockName;
        this.meta = meta;
        this.baseBlockTexture = baseBlockTexture;
        this.underlyingBlock = underlyingBlock;
        this.underlyingBlockName = underlyingBlockName;
        this.oreRenderType = oreRenderType;
        this.veinRate = veinRate;
        this.veinSize = veinSize;
        this.veinHeight = veinHeight;
        this.dimWhiteList = dimWhiteList;
        this.dimBlackList = dimBlackList;
        this.oreDictName = "orePoor" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
        setTranslationKey("poor_" + name + "_ore");
        setCreativeTab(PoorOresTab.INSTANCE);
    }

    public String getName() { return name; }
    public Nugget getNugget() { return nugget; }
    public void setNugget(Nugget nugget) { this.nugget = nugget; }
    public int getVeinRate() { return veinRate; }
    public int getVeinSize() { return veinSize; }
    public int getVeinHeight() { return veinHeight; }
    public List<Integer> getDimWhiteList() { return dimWhiteList; }
    public List<Integer> getDimBlackList() { return dimBlackList; }
    public int getMeta() { return meta; }
    public String getBaseBlockTexture() { return baseBlockTexture; }
    public String getUnderlyingBlockName() { return underlyingBlockName; }
    public Block getUnderlyingBlock() { return underlyingBlock; }
    public int getOreRenderType() { return oreRenderType; }

    public Block getBaseBlock() {
        if (baseBlock == null) {
            baseBlock = Block.getBlockFromName(modID + ":" + baseBlockName);
            if (baseBlock == null) PoorOresMod.LOG.error("{}: could not get base block {}:{}", name, modID, baseBlockName);
        }
        return baseBlock;
    }

    private IBlockState baseState() {
        Block b = getBaseBlock();
        if (b == null) return null;
        try {
            return b.getStateFromMeta(meta);
        } catch (RuntimeException e) {
            return b.getDefaultState();
        }
    }

    /** Display name of the base ore (for the "Poor ORENAME" pattern). */
    public String baseName() {
        Block b = getBaseBlock();
        return b == null ? name : new ItemStack(b, 1, meta).getDisplayName();
    }

    public void registerOreDict() {
        OreDictionary.registerOre(oreDictName, this);
        OreDictionary.registerOre("orePoorAll", this);
        OreDictionary.registerOre("poorOreAll", this);
    }

    @Override
    public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
        IBlockState b = baseState();
        return b == null ? 3.0F : b.getBlock().getBlockHardness(b, world, pos);
    }

    @Override
    public int getHarvestLevel(IBlockState state) {
        IBlockState b = baseState();
        return b == null ? 0 : b.getBlock().getHarvestLevel(b);
    }

    @Override
    public String getHarvestTool(IBlockState state) {
        IBlockState b = baseState();
        return b == null ? null : b.getBlock().getHarvestTool(b);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        IBlockState b = baseState();
        if (b == null) return;
        NonNullList<ItemStack> base = NonNullList.create();
        b.getBlock().getDrops(base, world, pos, b, fortune);
        Item baseItem = Item.getItemFromBlock(b.getBlock());
        for (ItemStack s : base) {
            if (s.getItem() == baseItem) drops.add(new ItemStack(this, s.getCount()));
            else drops.add(new ItemStack(nugget, s.getCount()));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        icon = reg.registerIcon(Reference.MOD_ID + ":poor_" + name + "_ore");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return icon;
    }
}
