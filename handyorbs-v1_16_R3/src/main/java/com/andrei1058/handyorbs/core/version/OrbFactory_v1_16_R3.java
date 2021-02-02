package com.andrei1058.handyorbs.core.version;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@SuppressWarnings("unused")
public class OrbFactory_v1_16_R3 implements WrappedFactory {

    @Override
    @Nullable
    public OrbEntity spawnOrbEntity(@NotNull org.bukkit.Location location, @NotNull ItemStack head) {
        if (location.getWorld() == null) return null;
        return new VersionedOrbEntity(((CraftWorld) location.getWorld()).getHandle(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), head);
    }

    private static class VersionedOrbEntity extends EntityArmorStand implements OrbEntity {

        private Function<Player, Void> rightClickListener;
        private boolean animationUp = true;
        private double maxY;
        private double minY;

        public VersionedOrbEntity(EntityTypes<? extends EntityArmorStand> entitytypes, World world) {
            super(entitytypes, world);
            setCustomNameVisible(true);
            setInvisible(true);
            setSmall(true);
            setBasePlate(false);
            setSilent(true);
            persist = false;
            ticksFarFromPlayer = 0;
            setNoGravity(true);
            noclip = true;
        }

        public VersionedOrbEntity(World world, double x, double y, double z, ItemStack head) {
            this(EntityTypes.ARMOR_STAND, world);
            this.setPosition(x, y, z);
            this.setSlot(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(head));
            this.maxY = y + 1;
            this.minY = y;
            world.addEntity(this);
        }

        @Override
        protected void collideNearby() {
        }

        @Override
        public EnumInteractionResult a(EntityHuman entityhuman, Vec3D vec3d, EnumHand enumhand) {
            if (rightClickListener != null){
                rightClickListener.apply((Player) entityhuman.getBukkitEntity());
            }
            return EnumInteractionResult.FAIL;
        }

        @Override
        protected void doTick() {
        }

        @Override
        public void tick() {
            tickAnimation();
        }

        @Override
        public boolean damageEntity(DamageSource damagesource, float f) {
            return false;
        }

        @Override
        public void killEntity() {
        }

        @Override
        public void die(DamageSource damagesource) {
        }

        @Override
        public void die() {
        }

        @Override
        public void setDisplayName(String string) {
            this.setCustomName(IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + (string == null ? " "
                    : ChatColor.translateAlternateColorCodes('&', string)) + "\"}"));
        }

        @Override
        public void setIcon(ItemStack itemStack) {
            setSlot(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(itemStack), true);
        }

        @Override
        public void destroy() {
            super.die();
        }

        @Override
        public double getLocY() {
            return locY();
        }

        @Override
        public double getLocX() {
            return locX();
        }

        @Override
        public double getLocZ() {
            return locZ();
        }

        @Override
        public int getChunkX() {
            return this.chunkX;
        }

        @Override
        public int getChunkZ() {
            return this.chunkZ;
        }

        @Override
        public void floatY(boolean animationUp) {
            setYaw(yaw + 7);
            enderTeleportTo(locX(), getLocY() + (animationUp ? 0.07 : -0.07), locZ());
        }

        @Override
        public void setRightClickListener(@Nullable Function<Player, Void> rightClickListener) {
            this.rightClickListener = rightClickListener;
        }

        @Override
        public boolean isAnimationUp() {
            return animationUp;
        }

        @Override
        public double getMaxY() {
            return maxY;
        }

        @Override
        public double getMinY() {
            return minY;
        }

        @Override
        public void setAnimationUp(boolean toggle) {
            this.animationUp = toggle;
        }

        private void setYaw(float newYaw) {
            if (Float.isNaN(newYaw)) {
                newYaw = 0.0F;
            }
            this.yaw = newYaw % 360.0F;
        }

        @Override
        protected @javax.annotation.Nullable SoundEffect getSoundDeath() {
            return null;
        }

        @Override
        protected @javax.annotation.Nullable SoundEffect getSoundHurt(DamageSource damagesource) {
            return null;
        }

        @Override
        public String getName() {
            if (getCustomName() == null){
                return " ";
            }
            return getCustomName().getText();
        }
    }
}
