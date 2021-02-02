package com.andrei1058.handyorbs.registry;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ChunkOrbRegistry {

    private static final List<ChunkedOrbs> chunkedOrbs = new ArrayList<>();


    @Nullable
    public static ChunkedOrbs getChunkedOrbs(int x, int z){
        for (ChunkedOrbs chunkedOrbs : chunkedOrbs){
            if (chunkedOrbs.getX() == x && chunkedOrbs.getZ() == z){
                return chunkedOrbs;
            }
        }
        return null;
    }
}
