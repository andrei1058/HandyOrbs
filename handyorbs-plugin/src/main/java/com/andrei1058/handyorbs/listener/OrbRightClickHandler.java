package com.andrei1058.handyorbs.listener;

import com.andrei1058.handyorbs.HandyOrbsPlugin;
import com.andrei1058.handyorbs.api.OrbCategory;
import com.andrei1058.handyorbs.core.HandyOrbsCore;
import com.andrei1058.handyorbs.core.OrbBase;
import com.andrei1058.handyorbs.core.model.Ownable;
import com.andrei1058.handyorbs.database.OrbRepository;
import com.andrei1058.handyorbs.registry.OrbRegistry;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public class OrbRightClickHandler {

    private static OrbRightClickHandler instance;

    public static OrbRightClickHandler getInstance() {
        if (instance == null) {
            instance = new OrbRightClickHandler();
        }
        return instance;
    }

    public Function<Player, Void> getDefaultRightClickListener(OrbBase orbBase) {
        return (player) -> {
            if (orbBase instanceof Ownable) {
                if (((Ownable) orbBase).getOwner() != null) {
                    if (((Ownable) orbBase).getOwner().equals(player.getUniqueId())) {
                        player.sendMessage("You own this");
                    }
                    if (player.isSneaking()) {
                        if (player.getInventory().getItemInMainHand().getType() == Material.AIR || player.getInventory().getItemInOffHand().getType() == Material.AIR) {
                            ItemStack icon = orbBase.getOrbEntity().getIcon();
                            OrbCategory orbCategory = OrbRegistry.getInstance().getActiveOrbCategory(orbBase.getOrbId());
                            if (orbCategory != null) {
                                String orbType = OrbRegistry.getInstance().getActiveOrbIdentifier(orbBase);
                                if (orbType == null){
                                    throw new RuntimeException("Orb type should not be null!");
                                }
                                icon = HandyOrbsCore.getInstance().getItemStackSupport().addTag(icon, HandyOrbsPlugin.ORB_ID_TAG, String.valueOf(orbBase.getOrbId()));
                                icon = HandyOrbsCore.getInstance().getItemStackSupport().addTag(icon, HandyOrbsPlugin.ORB_CATEGORY_TAG, orbCategory.toString());
                                icon = HandyOrbsCore.getInstance().getItemStackSupport().addTag(icon, HandyOrbsPlugin.ORB_TYPE_TAG, orbType);
                                icon = HandyOrbsCore.getInstance().getItemStackSupport().addTag(icon, HandyOrbsPlugin.ORB_CHECKER_TAG, "hello");
                                OrbRegistry.getInstance().despawnOrb(orbBase, orbCategory);
                                if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
                                    player.getInventory().setItemInMainHand(icon);
                                } else {
                                    player.getInventory().setItemInOffHand(icon);
                                }
                            } else {
                                HandyOrbsPlugin.getInstance().getLogger().info("Orb despawned completely (player right click) because its category was not found! [ID:" + orbBase.getOrbId() + "]");
                                OrbRegistry.getInstance().despawnOrb(orbBase);
                            }
                        }
                    }
                }
                player.sendMessage("Orb id: " + orbBase.getOrbId());
            }
            return null;
        };
    }
}
