package com.electrifiedded.poorores;

import com.electrifiedded.archaiclib.legacy.registry.LegacyGameRegistry;
import com.electrifiedded.archaiclib.original.OriginalModAssets;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = "required-after:archaiclib@[1.25.0,)")
public class PoorOresMod {
    public static final Logger LOG = LogManager.getLogger(Reference.MOD_NAME);
    public static final String ORIGINAL_NAME = "PoorOres_1.7.10_1.4.2.jar - PoorOres 1.4.2 for 1.7.10 (by Xexanos)";
    public static final String ORIGINAL_URL = "https://www.curseforge.com/minecraft/mc-mods/poor-ores";

    @SidedProxy(clientSide = "com.electrifiedded.poorores.client.ClientProxy", serverSide = "com.electrifiedded.poorores.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        // only the texture masks come from the original jar (client side)
        OriginalModAssets.require(Reference.MOD_ID, Reference.MOD_ID, ORIGINAL_NAME, ORIGINAL_URL, Reference.MOD_ID);
        PoorOresConfig.load(e.getSuggestedConfigurationFile());
        for (PoorOre ore : PoorOresConfig.ORES) {
            LegacyGameRegistry.registerBlock(ore, new PoorOreItem(ore), "poor_" + ore.getName() + "_ore");
        }
        for (Nugget n : PoorOresConfig.NUGGETS) {
            LegacyGameRegistry.registerItem(n, n.getName());
        }
        MinecraftForge.ORE_GEN_BUS.register(new DisableVanillaOres());
        GameRegistry.registerWorldGenerator(new PoorOreGenerator(), 16);
        GameRegistry.registerFuelHandler(stack -> stack.getItem() instanceof Nugget ? ((Nugget) stack.getItem()).getBurnTime() : 0);
        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        for (PoorOre ore : PoorOresConfig.ORES) ore.registerOreDict();
        for (Nugget n : PoorOresConfig.NUGGETS) {
            n.registerOreDict();
            n.registerSmelting();
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        for (Nugget n : PoorOresConfig.NUGGETS) n.registerCrafting();
    }
}
