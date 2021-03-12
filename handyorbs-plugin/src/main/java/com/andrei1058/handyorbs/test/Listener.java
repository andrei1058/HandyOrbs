package com.andrei1058.handyorbs.test;

import com.andrei1058.handyorbs.HandyOrbsPlugin;
import com.andrei1058.handyorbs.api.OrbCategory;
import com.andrei1058.handyorbs.core.HandyOrbsCore;
import com.andrei1058.handyorbs.core.OrbBase;
import com.andrei1058.handyorbs.core.model.Farmable;
import com.andrei1058.handyorbs.database.OrbRepository;
import com.andrei1058.handyorbs.registry.OrbRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Listener implements org.bukkit.event.Listener {

    @EventHandler
    public void onItem(PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        if (event.getItem().getType() == Material.DIAMOND) {
            if (event.getPlayer().hasCooldown(event.getItem().getType())) return;
            event.getPlayer().setCooldown(event.getItem().getType(), 100);
            OrbBase orb = OrbRegistry.getInstance().spawnOrb("wheat", OrbCategory.FARMING, event.getPlayer().getLocation().clone().add(0, 2, 0),
                    "internal;cuboid;10", 10 * 20);
            if (orb != null) {
                Bukkit.getScheduler().runTaskAsynchronously(HandyOrbsPlugin.getInstance(), () -> {
                    OrbRepository.getInstance().saveUpdate(orb, OrbCategory.FARMING);
                    int orbId = orb.getOrbId();
                    event.getPlayer().sendMessage("Spawned orb with ID: " + orbId);
                });
            }
        }
    }

    // not required
    //@EventHandler(priority = EventPriority.MONITOR)
    public void onSoil(PlayerHarvestBlockEvent event) {
        event.getPlayer().sendMessage("PlayerHarvestBlockEvent 1");
        for (OrbBase orbBase :
                OrbRegistry.getInstance().getActiveOrbsInChunk(event.getPlayer().getWorld().getName(),
                        event.getHarvestedBlock().getChunk().getX(),
                        event.getHarvestedBlock().getChunk().getZ())) {
            if (orbBase.getRegion().isInRegion(event.getHarvestedBlock().getLocation())) {
                event.getPlayer().sendMessage("PlayerHarvestBlockEvent 2");
                Block blockForSeeds = event.getHarvestedBlock().getRelative(BlockFace.UP);
                if (orbBase instanceof Farmable && blockForSeeds.getType() == ((Farmable) orbBase).getUpperMaterial()) {
                    ((Farmable) orbBase).getSoil().add(blockForSeeds.getLocation());
                    event.getPlayer().sendMessage("PlayerHarvestBlockEvent A");
                }
                blockForSeeds = event.getHarvestedBlock();
                if (orbBase instanceof Farmable && blockForSeeds.getType() == ((Farmable) orbBase).getUpperMaterial()) {
                    ((Farmable) orbBase).getSoil().add(blockForSeeds.getLocation());
                    event.getPlayer().sendMessage("PlayerHarvestBlockEvent B");
                }
                if (orbBase instanceof Farmable) {
                    ((Farmable) orbBase).getSoil().add(blockForSeeds.getLocation());
                    event.getPlayer().sendMessage("PlayerHarvestBlockEvent C");
                }
            }
        }
    }

    //todo da skip la block daca pune seminte manual pe block

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent event) {
        event.getPlayer().sendMessage("BlockBreakEvent 1");
        for (OrbBase orbBase : OrbRegistry.getInstance().getActiveOrbsInChunk(event.getBlock().getWorld().getName(), event.getBlock().getChunk().getX(), event.getBlock().getChunk().getZ())) {
            event.getPlayer().sendMessage("BlockBreakEvent a");
            if (orbBase instanceof Farmable) {
                event.getPlayer().sendMessage("BlockBreakEvent b");
                final Block brokenBlock = event.getBlock();
                if (orbBase.getRegion().isInRegion(brokenBlock.getLocation())) {
                    // if you broke soil block
                    if (event.getBlock().getType() == ((Farmable) orbBase).getSoilMaterial()) {
                        Block blockForSeeds = brokenBlock.getRelative(BlockFace.UP);
                        ((Farmable) orbBase).getSoil().remove(blockForSeeds.getLocation());
                        event.getPlayer().sendMessage("BlockBreakEvent c");
                    } else {
                        // if you broke seeds or harvest
                        event.getPlayer().sendMessage("BlockBreakEvent d");
                        Block soilBlock = event.getBlock().getRelative(BlockFace.DOWN);
                        if (soilBlock.getType() == ((Farmable) orbBase).getSoilMaterial()) {
                            event.getPlayer().sendMessage("BlockBreakEvent e");
                            ((Farmable) orbBase).getSoil().add(brokenBlock.getLocation());
                        }
                    }
                    break;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        final Block placedBlock = event.getBlockPlaced();
        final BlockState newBlockState = event.getBlockReplacedState();
        Bukkit.getScheduler().runTaskAsynchronously(HandyOrbsCore.getInstance().getOwner(), () -> {
            for (OrbBase orbBase : OrbRegistry.getInstance().getActiveOrbsInChunk(event.getBlock().getWorld().getName(), event.getBlock().getChunk().getX(), event.getBlock().getChunk().getZ())) {
                if (orbBase instanceof Farmable) {
                    event.getPlayer().sendMessage("BlockPlaceEvent a");
                    if (orbBase.getRegion().isInRegion(placedBlock.getLocation())) {
                        event.getPlayer().sendMessage("BlockPlaceEvent b");
                        Bukkit.getScheduler().runTaskLater(HandyOrbsCore.getInstance().getOwner(), () -> {
                            // if placed on a soil block
                            event.getPlayer().sendMessage("BlockPlaceEvent c");
                            if (newBlockState.getType() == ((Farmable) orbBase).getSoilMaterial()) {
                                ((Farmable) orbBase).getSoil().remove(placedBlock.getLocation());
                                event.getPlayer().sendMessage("BlockPlaceEvent d: " + newBlockState.getType().toString());
                            }
                            // if placed a soil block
                            Block upperBlock = newBlockState.getBlock().getRelative(BlockFace.UP);
                            if (upperBlock.getType() == ((Farmable) orbBase).getSoilMaterial() && upperBlock.getRelative(BlockFace.UP).getType() == ((Farmable) orbBase).getUpperMaterial()) {
                                event.getPlayer().sendMessage("BlockPlaceEvent e: " + upperBlock.getType());
                                ((Farmable) orbBase).getSoil().add(upperBlock.getRelative(BlockFace.UP).getLocation());
                            }
                        }, 1L);
                        break;
                    }
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlock(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getItem() == null) return;
        if (event.getClickedBlock() == null) return;
        final Block clickedBlock = event.getClickedBlock();
        if (isHoe(event.getItem().getType())) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(HandyOrbsCore.getInstance().getOwner(), () -> {
                for (OrbBase orbBase : OrbRegistry.getInstance().getActiveOrbsInChunk(clickedBlock.getWorld().getName(), clickedBlock.getChunk().getX(), clickedBlock.getChunk().getZ())) {
                    if (orbBase instanceof Farmable) {
                        if (((Farmable) orbBase).getSoilMaterial() == clickedBlock.getType()) {
                            Bukkit.getScheduler().runTask(HandyOrbsCore.getInstance().getOwner(), () -> {
                                Block upperBlock = clickedBlock.getRelative(BlockFace.UP);
                                if (((Farmable) orbBase).getUpperMaterial() == upperBlock.getType()) {
                                    ((Farmable) orbBase).getSoil().add(upperBlock.getLocation());
                                }
                            });
                            break;
                        }
                    }
                }
            }, 1L);
        }
    }

    public boolean isHoe(Material material) {
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
