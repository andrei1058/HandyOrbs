package com.andrei1058.handyorbs.core.model;

import java.util.UUID;

public interface Ownable {

    UUID getOwner();
    
    void setOwner(UUID uuid);
}
