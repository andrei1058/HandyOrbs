package com.andrei1058.handyorbs.registry;

public class ChunkedOrbs {

    private final int x;
    private final int z;

    //todo track when this chunk was unloaded and implement a timeout which will destroy it from memory after a few minutes
    // this will prevent memory loads if a chunk is unloaded and loaded frequently

    private ChunkedOrbs(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public int getZ() {
        return z;
    }

    public int getX() {
        return x;
    }

    public void unloadOrbs() {

    }
}
