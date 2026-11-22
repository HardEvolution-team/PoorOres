package com.electrifiedded.poorores;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Optimised poor-ore generator:
 *  - per-dimension list of active ores is computed once and cached (no list scans per chunk);
 *  - same vein shape as vanilla WorldGenMinable, but every block is clamped to the 2x2 chunk
 *    population area (x*16+8 .. x*16+23 +- 8), so a vein never loads/generates a neighbour
 *    chunk (no cascading worldgen);
 *  - reuses one MutableBlockPos, skips the block-update / observer flags (2|16).
 */
public class PoorOreGenerator implements IWorldGenerator {
    private final Int2ObjectOpenHashMap<PoorOre[]> byDim = new Int2ObjectOpenHashMap<>();

    private PoorOre[] activeFor(int dim) {
        PoorOre[] arr = byDim.get(dim);
        if (arr == null) {
            List<PoorOre> list = new ArrayList<>();
            for (PoorOre ore : PoorOresConfig.ORES) {
                if (ore.getVeinRate() == 0 || ore.getVeinHeight() <= 0 || ore.getVeinSize() <= 0) continue;
                boolean ok = ore.getDimWhiteList().contains(dim) || (ore.getDimWhiteList().isEmpty() && !ore.getDimBlackList().contains(dim));
                if (ok && ore.getUnderlyingBlock() != null) list.add(ore);
            }
            arr = list.toArray(new PoorOre[0]);
            byDim.put(dim, arr);
        }
        return arr;
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator gen, IChunkProvider provider) {
        PoorOre[] ores = activeFor(world.provider.getDimension());
        if (ores.length == 0) return;
        int baseX = chunkX * 16, baseZ = chunkZ * 16;
        // populated (loaded) area for this chunk: [base, base+32)
        int minX = baseX, maxX = baseX + 31, minZ = baseZ, maxZ = baseZ + 31;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int worldTop = world.getHeight() - 1;
        for (PoorOre ore : ores) {
            IBlockState state = ore.getDefaultState();
            Block target = ore.getUnderlyingBlock();
            int size = ore.getVeinSize();
            int height = Math.min(ore.getVeinHeight(), worldTop + 1);
            for (int i = 0; i <= ore.getVeinRate(); i++) {
                vein(world, random, pos, state, target, size,
                        baseX + random.nextInt(16), random.nextInt(height), baseZ + random.nextInt(16),
                        minX, maxX, minZ, maxZ, worldTop);
            }
        }
    }

    /** Port of WorldGenMinable#generate with bounds clamping. */
    private static void vein(World world, Random rand, BlockPos.MutableBlockPos p, IBlockState state, Block target, int size,
                             int px, int py, int pz, int minX, int maxX, int minZ, int maxZ, int worldTop) {
        float f = rand.nextFloat() * (float) Math.PI;
        double s8 = size / 8.0F;
        double x0 = px + 8 + MathHelper.sin(f) * s8, x1 = px + 8 - MathHelper.sin(f) * s8;
        double z0 = pz + 8 + MathHelper.cos(f) * s8, z1 = pz + 8 - MathHelper.cos(f) * s8;
        double y0 = py + rand.nextInt(3) - 2, y1 = py + rand.nextInt(3) - 2;
        for (int i = 0; i < size; ++i) {
            float t = (float) i / (float) size;
            double cx = x0 + (x1 - x0) * t, cy = y0 + (y1 - y0) * t, cz = z0 + (z1 - z0) * t;
            double r = rand.nextDouble() * size / 16.0D;
            double hr = (MathHelper.sin((float) Math.PI * t) + 1.0F) * r + 1.0D;
            double half = hr / 2.0D;
            int ax = Math.max(minX, MathHelper.floor(cx - half)), bx = Math.min(maxX, MathHelper.floor(cx + half));
            int ay = Math.max(0, MathHelper.floor(cy - half)), by = Math.min(worldTop, MathHelper.floor(cy + half));
            int az = Math.max(minZ, MathHelper.floor(cz - half)), bz = Math.min(maxZ, MathHelper.floor(cz + half));
            double inv = 1.0D / half;
            for (int x = ax; x <= bx; ++x) {
                double dx = (x + 0.5D - cx) * inv;
                double dx2 = dx * dx;
                if (dx2 >= 1.0D) continue;
                for (int y = ay; y <= by; ++y) {
                    double dy = (y + 0.5D - cy) * inv;
                    double dxy = dx2 + dy * dy;
                    if (dxy >= 1.0D) continue;
                    for (int z = az; z <= bz; ++z) {
                        double dz = (z + 0.5D - cz) * inv;
                        if (dxy + dz * dz >= 1.0D) continue;
                        p.setPos(x, y, z);
                        if (world.getBlockState(p).getBlock() == target) {
                            world.setBlockState(p, state, 2 | 16);
                        }
                    }
                }
            }
        }
    }
}
