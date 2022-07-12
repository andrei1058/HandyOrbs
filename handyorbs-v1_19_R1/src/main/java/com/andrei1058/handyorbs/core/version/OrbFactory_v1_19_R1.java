package com.andrei1058.handyorbs.core.version;

import com.mojang.datafixers.util.Pair;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@SuppressWarnings("unused")
public class OrbFactory_v1_19_R1 implements WrappedFactory {
    @Override
    @Nullable
    public OrbEntity spawnOrbEntity(@NotNull Location location, @NotNull ItemStack head) {
        if (location.getWorld() == null) return null;
        return new VersionedOrbEntity(location, head);
    }

    private static class VersionedOrbEntity extends EntityArmorStand implements OrbEntity {

        private Function<Player, Void> rightClickListener;
        private boolean animationUp = true;
        private double maxY;
        private double minY;
        private OrbActivity activity = () -> {
        };

        public VersionedOrbEntity(EntityTypes<? extends EntityArmorStand> entities, World world) {
            super(entities, world);
            setCustomNameVisible(true);
            j(true);
            a(true);
            s(false);
            d(true);
            persist = false;
            bf = 0;
            e(true);
            //no clip
            Q = true;
        }

        public VersionedOrbEntity(@NotNull Location location, ItemStack head) {
            this(EntityTypes.d, ((CraftWorld) Objects.requireNonNull(location.getWorld())).getHandle());
            this.a(location.getBlockX() + 0.5, location.getY(), location.getBlockZ() + 0.5);
            this.a(EnumItemSlot.f, CraftItemStack.asNMSCopy(head));
            this.maxY = location.getY() + 1;
            this.minY = location.getY();
            ((CraftWorld) location.getWorld()).getHandle().addFreshEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
        }

        @Override
        public boolean canCollideWithBukkit(Entity entity) {
            return false;
        }

        @Override
        public EnumInteractionResult a(EntityHuman entityhuman, Vec3D vec3d, EnumHand enumhand) {
            if (rightClickListener != null) {
                rightClickListener.apply((Player) entityhuman.getBukkitEntity());
            }
            return EnumInteractionResult.e;
        }

        @Override
        public void inactiveTick() {
        }

        @Override
        protected boolean damageEntity0(DamageSource damagesource, float f) {
            return false;
        }

        @Override
        public void a(DamageSource damagesource){

        }

        @Override
        public void k() {
            tickAnimation();
            activity.doTick();

            if (isAnimationUp()) {
                getBukkitEntity().getWorld().spawnParticle(org.bukkit.Particle.FIREWORKS_SPARK
                        , getLocX(), getLocY() + 1.3, getLocZ(), 1, 0.5, 0, 0.5, 0);
            }
        }

        @Override
        public void an() {

        }

        @Override
        public void setDisplayName(String string) {
            this.getBukkitEntity().setCustomName(string == null ? " "
                    : ChatColor.translateAlternateColorCodes('&', string));
        }

        @Override
        public void setIcon(ItemStack itemStack) {
            this.setItemSlot(EnumItemSlot.f, CraftItemStack.asNMSCopy(itemStack), true);
            List<Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> equipment = new ArrayList<>();
            equipment.add(new Pair<>(EnumItemSlot.f, this.c(EnumItemSlot.f)));
            PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(this.at(), equipment);
            this.getBukkitEntity().getWorld().getPlayers().forEach(
                    player -> ((CraftPlayer)player).getHandle().b.a(packet)
            );
        }

        @Contract(" -> new")
        @Override
        public @NotNull ItemStack getIcon() {
            return CraftItemStack.asBukkitCopy(this.c(EnumItemSlot.f));
        }

        @Override
        public void destroy() {
            super.a(DamageSource.a().sweep());
        }

        @Override
        public double getLocY() {
            return super.db().v();
        }

        @Override
        public double getLocX() {
            return super.db().u();
        }

        @Override
        public double getLocZ() {
            return super.db().w();
        }

        @Override
        public int getChunkX() {
            return super.getBukkitEntity().getLocation().getChunk().getX();
        }

        @Override
        public int getChunkZ() {
            return  super.getBukkitEntity().getLocation().getChunk().getZ();
        }

        @Override
        public void floatY(boolean animationUp) {
            setYaw(getBukkitYaw() + 7);
            b(getLocX(), getLocY() + (animationUp ? 0.07 : -0.07), getLocZ());
        }

        @Override
        public void setRightClickListener(@Nullable Function<Player, Void> rightClickListener) {
            this.rightClickListener = rightClickListener;
        }

        @Override
        public boolean getCustomNameVisible() {
            return cu();
        }

        @Override
        public void setCustomNameVisible(boolean toggle) {
            n(toggle);
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
            this.o(newYaw % 360.0F);
        }

        @Contract(pure = true)
        protected @Nullable SoundEffect aG() {
            return null;
        }

        @Contract(pure = true)
        protected @Nullable SoundEffect aH() {
            return null;
        }

        @Override
        protected @javax.annotation.Nullable SoundEffect x_() {
            return null;
        }

        @Override
        protected @javax.annotation.Nullable SoundEffect c(DamageSource damagesource) {
            return null;
        }

        @Override
        public String getName() {
            return getBukkitEntity().getCustomName();
        }

        @Override
        public OrbActivity getOrbActivity() {
            return activity;
        }

        @Override
        public void setOrbActivity(OrbActivity orbActivity) {
            this.activity = orbActivity;
        }
    }
}
