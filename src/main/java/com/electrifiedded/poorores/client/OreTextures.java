package com.electrifiedded.poorores.client;

import com.electrifiedded.archaiclib.client.texture.GeneratedTexturePack;
import com.electrifiedded.poorores.Nugget;
import com.electrifiedded.poorores.PoorOre;
import com.electrifiedded.poorores.PoorOresMod;
import com.electrifiedded.poorores.Reference;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;

/** The two painters of the original (PoorOreTexture / NuggetTexture), as plain image generators. */
final class OreTextures {
    private OreTextures() {}

    private static ResourceLocation loc(String id, String dir) {
        int i = id.indexOf(':');
        String domain = i < 0 ? "minecraft" : id.substring(0, i);
        String path = i < 0 ? id : id.substring(i + 1);
        return new ResourceLocation(domain, "textures/" + dir + "/" + path + ".png");
    }

    /** Resamples the first square frame (nearest neighbour) to size x size. */
    private static int[] square(BufferedImage img, int size) {
        int fw = img.getWidth();
        int[] out = new int[size * size];
        for (int y = 0; y < size; y++)
            for (int x = 0; x < size; x++)
                out[y * size + x] = img.getRGB(x * fw / size, y * fw / size);
        return out;
    }

    static BufferedImage paintOre(PoorOre ore) {
        BufferedImage base = GeneratedTexturePack.read(loc(ore.getBaseBlockTexture(), "blocks"));
        BufferedImage under = GeneratedTexturePack.read(loc(ore.getUnderlyingBlockName(), "blocks"));
        BufferedImage mask = GeneratedTexturePack.read(new ResourceLocation(Reference.MOD_ID, "textures/blocks/ore_poor_alpha_" + ore.getOreRenderType() + ".png"));
        if (base == null || under == null || mask == null) {
            PoorOresMod.LOG.error("{}: cannot paint ore texture (base={}, underlying={}, mask={})", ore.getName(), base != null, under != null, mask != null);
            return null;
        }
        int w = base.getWidth();
        int[] b = square(base, w), u = square(under, w), m = square(mask, w);
        BufferedImage out = new BufferedImage(w, w, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < w; y++)
            for (int x = 0; x < w; x++) {
                int i = y * w + x;
                // the original used the lowest bit of the mask pixel as "show ore here"
                out.setRGB(x, y, 0xFF000000 | (((m[i] & 1) != 0 ? b[i] : u[i]) & 0xFFFFFF));
            }
        return out;
    }

    /** Texture name of an ingot stack (1.12 item sheet names), or null. */
    private static ResourceLocation ingotTexture(ItemStack ingot) {
        if (ingot.getItem() == Items.DYE)
            return new ResourceLocation("minecraft", "textures/items/dye_powder_" + EnumDyeColor.byDyeDamage(ingot.getMetadata()).getTranslationKey() + ".png");
        if (ingot.getItem() == Items.COAL && ingot.getMetadata() == 1)
            return new ResourceLocation("minecraft", "textures/items/charcoal.png");
        ResourceLocation reg = ingot.getItem().getRegistryName();
        return reg == null ? null : new ResourceLocation(reg.getNamespace(), "textures/items/" + reg.getPath() + ".png");
    }

    static BufferedImage paintNugget(Nugget nugget) {
        ItemStack ingot = nugget.getIngot();
        if (ingot.isEmpty()) {
            PoorOresMod.LOG.error("Could not get corresponding ingot for {}", nugget.getName());
            return null;
        }
        ResourceLocation tex = ingotTexture(ingot);
        BufferedImage ing = tex == null ? null : GeneratedTexturePack.read(tex);
        BufferedImage shape = GeneratedTexturePack.read(new ResourceLocation(Reference.MOD_ID, "textures/items/nugget_" + nugget.getNuggetRenderType() + ".png"));
        if (ing == null || shape == null) {
            PoorOresMod.LOG.error("{}: cannot paint nugget texture (ingot texture {} found={}, shape found={})", nugget.getName(), tex, ing != null, shape != null);
            return null;
        }
        int wn = shape.getWidth();
        int[] ingot16 = square(ing, wn);
        long r = 0, g = 0, bl = 0;
        int div = 0;
        for (int c : ingot16) {
            if ((c >>> 24) != 0) {
                r += (c >> 16) & 255;
                g += (c >> 8) & 255;
                bl += c & 255;
                div++;
            }
        }
        if (div == 0) div = 1;
        int ar = (int) (r / div), ag = (int) (g / div), ab = (int) (bl / div);
        BufferedImage out = new BufferedImage(wn, wn, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < wn; y++)
            for (int x = 0; x < wn; x++) {
                int c = shape.getRGB(x, y);
                if ((c >>> 24) == 0) continue;
                int mult = (c >> 16) & 255;
                out.setRGB(x, y, 0xFF000000 | (ar * mult / 255) << 16 | (ag * mult / 255) << 8 | (ab * mult / 255));
            }
        return out;
    }
}
