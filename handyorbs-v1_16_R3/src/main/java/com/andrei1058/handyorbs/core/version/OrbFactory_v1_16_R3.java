package com.andrei1058.handyorbs.core.version;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftParticle;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

@SuppressWarnings("unused")
public class OrbFactory_v1_16_R3 implements WrappedFactory {

    @Override
    @Nullable
    public OrbEntity spawnOrbEntity(@NotNull org.bukkit.Location location, @NotNull ItemStack head) {
        if (location.getWorld() == null) return null;
        return new VersionedOrbEntity(location, head);
    }

    private static class VersionedOrbEntity extends EntityArmorStand implements OrbEntity {

        private Function<Player, Void> rightClickListener;
        private boolean animationUp = true;
        private double maxY;
        private double minY;
        private int countdown;
        private boolean hyperActivity = false;
        private int delay = 20;
        private OrbActivity activity = () -> {
        };

        public VersionedOrbEntity(EntityTypes<? extends EntityArmorStand> entities, World world) {
            super(entities, world);
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

        public VersionedOrbEntity(Location location, ItemStack head) {
            this(EntityTypes.ARMOR_STAND, ((CraftWorld) Objects.requireNonNull(location.getWorld())).getHandle());
            this.setPosition(location.getBlockX() + 0.5, location.getY(), location.getBlockZ() + 0.5);
            this.setSlot(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(head));
            this.maxY = location.getY() + 1;
            this.minY = location.getY();
            world.addEntity(this);
        }

        @Override
        protected void collideNearby() {
        }

        @Override
        public EnumInteractionResult a(EntityHuman entityhuman, Vec3D vec3d, EnumHand enumhand) {
            if (rightClickListener != null) {
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
            if (countdown == 0) {
                countdown = delay;
                activity.doTick();
            } else {
                countdown--;
            }
            if (isAnimationUp()) {
                world.getWorld().spawnParticle(org.bukkit.Particle.FIREWORKS_SPARK
                        , getLocX(), getLocY() + 1.3, getLocZ(), 1, 0.5, 0, 0.5, 0);
            }
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
            this.getBukkitEntity().setCustomName(string == null ? " "
                    : ChatColor.translateAlternateColorCodes('&', string));
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
            return super.chunkX;
        }

        @Override
        public int getChunkZ() {
            return super.chunkZ;
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
        protected @javax.annotation.Nullable
        SoundEffect getSoundDeath() {
            return null;
        }

        @Override
        protected @javax.annotation.Nullable
        SoundEffect getSoundHurt(DamageSource damagesource) {
            return null;
        }

        @Override
        public String getName() {
            if (getCustomName() == null) {
                return " ";
            }
            return getCustomName().getText();
        }

        public void setActivity(OrbActivity activity) {
            this.activity = activity;
        }

        public void setDelay(int delay) {
            this.delay = delay;
        }

        public int getDelay() {
            return delay;
        }

        @Override
        public OrbActivity getOrbActivity() {
            return activity;
        }

        @Override
        public void setOrbActivity(OrbActivity orbActivity) {
            this.activity = orbActivity;
        }

        public void setCountdown(int countdown) {
            this.countdown = countdown;
        }

        public int getCountdown() {
            return countdown;
        }

        public void setHyperActivity(boolean hyperActivity) {
            this.hyperActivity = hyperActivity;
        }

        public boolean isHyperActivity() {
            return hyperActivity;
        }
    }
}
