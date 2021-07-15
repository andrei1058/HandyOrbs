package com.andrei1058.handyorbs.listener;

import com.andrei1058.handyorbs.HandyOrbsPlugin;
import com.andrei1058.handyorbs.api.OrbCategory;
import com.andrei1058.handyorbs.core.HandyOrbsCore;
import com.andrei1058.handyorbs.core.OrbBase;
import com.andrei1058.handyorbs.core.model.Farmable;
import com.andrei1058.handyorbs.core.model.Ownable;
import com.andrei1058.handyorbs.database.OrbRepository;
import com.andrei1058.handyorbs.registry.OrbCategoryRegistry;
import com.andrei1058.handyorbs.registry.OrbRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class OrbListener implements Listener {

    /**
     * Listen if crops or soil are removed.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent event) {
        final Block block = event.getBlock();
        for (OrbBase orb : OrbRegistry.getInstance().getActiveOrbsByBlock(
                block.getWorld().getName(), block.getX(), block.getY(), block.getZ())) {
            if (orb instanceof Farmable) {
                final Block brokenBlock = event.getBlock();
                if (orb.getRegion().isInRegion(brokenBlock.getLocation())) {
                    // if soil block was removed
                    if (brokenBlock.getType() == ((Farmable) orb).getSoilMaterial()) {
                        Block seedsBlock = brokenBlock.getRelative(BlockFace.UP);
                        ((Farmable) orb).removeSoil(seedsBlock.getLocation());
                    } else {
                        // if seeds or grown plant was broken
                        Block soilBlock = brokenBlock.getRelative(BlockFace.DOWN);
                        if (soilBlock.getType() == ((Farmable) orb).getSoilMaterial()) {
                            ((Farmable) orb).addSoil(brokenBlock.getLocation());
                        }
                    }
                    // end loop
                    return;
                }
            }
        }
    }

    /**
     * Listen if a block was placed on soil.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        final BlockState blockState = event.getBlockReplacedState();
        final Block placedBlock = event.getBlockPlaced();
        for (OrbBase orb : OrbRegistry.getInstance().getActiveOrbsByBlock(
                blockState.getWorld().getName(), blockState.getX(), blockState.getY(), blockState.getZ())) {
            if (orb instanceof Farmable) {
                if (orb.getRegion().isInRegion(placedBlock.getLocation())) {
                    Bukkit.getScheduler().runTaskLater(HandyOrbsPlugin.getInstance(), () -> {
                        // if placed on a soil block
                        if (blockState.getType() == ((Farmable) orb).getSoilMaterial()) {
                            ((Farmable) orb).removeSoil(placedBlock.getLocation());
                        }
                        // if placed a soil block
                        Block upperBlock = ((Farmable) orb).canPlant(placedBlock);
                        if (upperBlock != null) {
                            ((Farmable) orb).addSoil(upperBlock.getLocation());
                        }
                    }, 1L);

                    // end cycle
                    return;
                }
            }
        }
    }

    /**
     * Listen for orb placement.
     */
    @EventHandler(ignoreCancelled = true)
    public void onOrbPlace(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getItem() == null) return;
        if (event.getClickedBlock() == null) return;

        if (HandyOrbsCore.getInstance().getItemStackSupport().hasTag(event.getItem(), HandyOrbsPlugin.ORB_CHECKER_TAG)) {
            event.setCancelled(true);
            // todo, spawn by id, replace owner if needed, handle region and disabled categories
            final Block clickedBlock = event.getClickedBlock();
            final ItemStack item = event.getItem();
            final Player player = event.getPlayer();
            String id = HandyOrbsCore.getInstance().getItemStackSupport().getTag(item, HandyOrbsPlugin.ORB_ID_TAG);
            String type = HandyOrbsCore.getInstance().getItemStackSupport().getTag(item, HandyOrbsPlugin.ORB_TYPE_TAG);
            String category = HandyOrbsCore.getInstance().getItemStackSupport().getTag(item, HandyOrbsPlugin.ORB_CATEGORY_TAG);

            // take item
            final ItemStack icon = item.clone();
            HandyOrbsCore.getInstance().getItemStackSupport().minusAmount(player, item, 1);

            Bukkit.getScheduler().runTask(HandyOrbsPlugin.getInstance(), () -> {
                final OrbCategory orbCat = OrbCategory.valueOf(category);
                OrbBase orb = OrbRegistry.getInstance().spawnOrb(type, orbCat, clickedBlock.getLocation().clone().add(0, 2, 0),
                        "internal;cuboid;10", 10 * 20);
                if (orb != null) {
                    if (id != null && !id.isEmpty()){
                        orb.setOrbId(Integer.parseInt(id));
                    }
                    if (orb instanceof Ownable){
                        ((Ownable) orb).setOwner(player.getUniqueId());
                    }
                    orb.getOrbEntity().setIcon(icon);
                    Bukkit.getScheduler().runTaskAsynchronously(HandyOrbsPlugin.getInstance(), () -> {
                        OrbRepository.getInstance().saveUpdate(orb, OrbCategory.FARMING);
                        int orbId = orb.getOrbId();
//                        player.sendMessage("Spawned orb with ID: " + orbId);

                        OrbCategoryRegistry registry = OrbRegistry.getInstance().getCategoryRegistry(orbCat);
                        registry.addActiveOrb(orb.getOrbId(), orb);
                    });
                }
            });
        }
    }

    /**
     * Listen for hoe and orb item interaction.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHoe(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getItem() == null) return;
        if (event.getClickedBlock() == null) return;
        if (!isHoe(event.getItem().getType())) return;
        final Block clickedBlock = event.getClickedBlock();
        // needs to run later to make sure the block state is changed
        Bukkit.getScheduler().runTaskLaterAsynchronously(HandyOrbsPlugin.getInstance(), () -> {

            for (OrbBase orb : OrbRegistry.getInstance().getActiveOrbsByBlock(
                    clickedBlock.getWorld().getName(), clickedBlock.getX(), clickedBlock.getY(), clickedBlock.getZ())) {
                if (orb instanceof Farmable) {
                    if (((Farmable) orb).getSoilMaterial() == clickedBlock.getType() && orb.getRegion().isInRegion(clickedBlock.getLocation())) {
                        Block upperBlock = ((Farmable) orb).canPlant(clickedBlock);
                        if (upperBlock != null) {
                            ((Farmable) orb).addSoil(upperBlock.getLocation());
                            // end cycle
                            return;
                        }
                    }
                }
            }
        }, 1L);
    }

    private static boolean isHoe(Material material) {
        switch (material.toString()) {
            case "DIAMOND_HOE":
            case "GOLDEN_HOE":
            case "IRON_HOE":
            case "NETHERITE_HOE":
            case "WOODEN_HOE":
            case "STONE_HOE":
                return true;
        }
        return false;
    }
}
