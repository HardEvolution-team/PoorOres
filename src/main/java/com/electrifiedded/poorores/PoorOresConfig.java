package com.electrifiedded.poorores;

import net.minecraft.block.Block;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/** Same config layout as the 1.7.10 original: general switches plus one "ores.block_NAME" category per poor ore. */
public final class PoorOresConfig {
    private static final String PREFIX = "ores.block_";

    public static final LinkedList<PoorOre> ORES = new LinkedList<>();
    public static final LinkedList<Nugget> NUGGETS = new LinkedList<>();
    public static boolean worldgenCoal, worldgenDiamond, worldgenEmerald, worldgenGold, worldgenIron, worldgenLapis,
            worldgenRedstone, worldgenQuartz;
    public static boolean addCrafting = true, addSmelting = true;

    private PoorOresConfig() {}

    private static void ore(Configuration c, String name, String base, Integer burn, Integer height, Integer rate, Integer size,
                            Integer renderType, String underlying) {
        String cat = PREFIX + name;
        c.get(cat, "modID", "minecraft");
        c.get(cat, "baseBlock", base);
        if (burn != null) c.get(cat, "burnTime", burn);
        if (height != null) c.get(cat, "veinHeight", height);
        if (rate != null) c.get(cat, "veinRate", rate);
        if (size != null) c.get(cat, "veinSize", size);
        if (renderType != null) c.get(cat, "oreRenderType", renderType);
        if (underlying != null) c.get(cat, "underlyingBlock", underlying);
    }

    public static void load(File file) {
        Configuration c = new Configuration(file);
        worldgenCoal = c.getBoolean("baseWorldgenCoal", "general", false, "Enable/Disable worldgen for coal");
        worldgenDiamond = c.getBoolean("baseWorldgenDiamond", "general", false, "Enable/Disable worldgen for diamond");
        worldgenEmerald = c.getBoolean("baseWorldgenEmerald", "general", false, "Enable/Disable worldgen for emerald");
        worldgenGold = c.getBoolean("baseWorldgenGold", "general", false, "Enable/Disable worldgen for gold");
        worldgenIron = c.getBoolean("baseWorldgenIron", "general", false, "Enable/Disable worldgen for iron");
        worldgenLapis = c.getBoolean("baseWorldgenLapis", "general", false, "Enable/Disable worldgen for lapis");
        worldgenRedstone = c.getBoolean("baseWorldgenRedstone", "general", false, "Enable/Disable worldgen for redstone");
        worldgenQuartz = c.getBoolean("baseWorldgenQuartz", "general", false, "Enable/Disable worldgen for quartz");
        addCrafting = c.getBoolean("add_crafting", "general", true, "Enable/Disable recipes for crafting");
        addSmelting = c.getBoolean("add_smelting", "general", true, "Enable/Disable recipes for smelting");

        ore(c, "coal", "coal_ore", 200, 120, 16, 48, null, null);
        ore(c, "diamond", "diamond_ore", null, 16, 4, 8, null, null);
        ore(c, "emerald", "emerald_ore", null, null, null, null, 1, null);
        ore(c, "gold", "gold_ore", null, 32, 3, 32, null, null);
        ore(c, "iron", "iron_ore", null, 64, 16, 32, null, null);
        ore(c, "lapis", "lapis_ore", null, 32, 8, 32, null, null);
        ore(c, "redstone", "redstone_ore", null, 16, 4, 32, null, null);
        ore(c, "quartz", "quartz_ore", null, 128, 10, 64, 2, "minecraft:netherrack");

        for (String category : c.getCategoryNames()) {
            if (!category.startsWith(PREFIX)) continue;
            String name = category.substring(PREFIX.length());
            String modID = c.get(category, "modID", "minecraft").getString();
            if (!modID.equals("minecraft") && !Loader.isModLoaded(modID)) {
                PoorOresMod.LOG.warn("{}: corresponding mod \"{}\" is missing, ore will not be added", name, modID);
                continue;
            }
            String baseBlockName = c.get(category, "baseBlock", "").getString();
            int baseMeta = c.get(category, "baseBlockMeta", 0).getInt();
            String baseTexture = c.get(category, "baseBlockTexture", modID + ":" + baseBlockName).getString();
            String underlyingName = c.get(category, "underlyingBlock", "minecraft:stone").getString();
            Block underlying = Block.getBlockFromName(underlyingName);
            if (underlying == null) {
                PoorOresMod.LOG.warn("{}: underlying block \"{}\" not found, ore will not be added", name, underlyingName);
                continue;
            }
            int renderType = c.get(category, "oreRenderType", 0).getInt();
            int nuggetRenderType = c.get(category, "nuggetRenderType", 0).getInt();
            int burnTime = c.get(category, "burnTime", 0).getInt();
            int rate = c.get(category, "veinRate", 0).getInt();
            int size = c.get(category, "veinSize", 0).getInt();
            int height = c.get(category, "veinHeight", 0).getInt();
            List<Integer> white = parseList(name, c.get(category, "dimWhiteList", "").getString());
            List<Integer> black = parseList(name, c.get(category, "dimBlackList", "").getString());

            PoorOre ore = new PoorOre(name, modID, baseBlockName, baseMeta, baseTexture, underlying, underlyingName, renderType,
                    rate, size, height, white, black);
            Nugget nugget = new Nugget(name, ore, burnTime, baseMeta, nuggetRenderType);
            ore.setNugget(nugget);
            ORES.add(ore);
            NUGGETS.add(nugget);
        }
        if (c.hasChanged()) c.save();
    }

    private static List<Integer> parseList(String name, String s) {
        List<Integer> out = new LinkedList<>();
        if (s.isEmpty()) return out;
        for (String e : s.split(",")) {
            try {
                out.add(Integer.parseInt(e.trim()));
            } catch (NumberFormatException ex) {
                PoorOresMod.LOG.warn("{}: could not parse \"{}\" to an integer, entry ignored", name, e);
            }
        }
        return out;
    }
}
