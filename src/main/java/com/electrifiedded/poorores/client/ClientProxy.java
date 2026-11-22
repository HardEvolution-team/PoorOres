package com.electrifiedded.poorores.client;

import com.electrifiedded.archaiclib.client.texture.GeneratedTexturePack;
import com.electrifiedded.poorores.CommonProxy;
import com.electrifiedded.poorores.Nugget;
import com.electrifiedded.poorores.PoorOre;
import com.electrifiedded.poorores.PoorOresConfig;
import com.electrifiedded.poorores.Reference;

/** Registers the runtime-painted textures (ore = base ore over stone through a mask, nugget = tinted ingot colour). */
public class ClientProxy extends CommonProxy {
    @Override
    public void preInit() {
        GeneratedTexturePack pack = GeneratedTexturePack.install(Reference.MOD_ID);
        for (PoorOre ore : PoorOresConfig.ORES) {
            pack.add("textures/blocks/poor_" + ore.getName() + "_ore.png", () -> OreTextures.paintOre(ore));
        }
        for (Nugget nugget : PoorOresConfig.NUGGETS) {
            pack.add("textures/items/" + nugget.getName() + ".png", () -> OreTextures.paintNugget(nugget));
        }
    }
}
