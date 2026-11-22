package com.electrifiedded.poorores;

import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DisableVanillaOres {
    @SubscribeEvent
    public void onGenerateMinable(OreGenEvent.GenerateMinable event) {
        Boolean allow = null;
        switch (event.getType()) {
            case COAL: allow = PoorOresConfig.worldgenCoal; break;
            case DIAMOND: allow = PoorOresConfig.worldgenDiamond; break;
            case GOLD: allow = PoorOresConfig.worldgenGold; break;
            case IRON: allow = PoorOresConfig.worldgenIron; break;
            case LAPIS: allow = PoorOresConfig.worldgenLapis; break;
            case REDSTONE: allow = PoorOresConfig.worldgenRedstone; break;
            case QUARTZ: allow = PoorOresConfig.worldgenQuartz; break;
            default: break;
        }
        if (allow != null) event.setResult(allow ? Result.ALLOW : Result.DENY);
    }
}
