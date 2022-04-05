package com.andrei1058.handyorbs.core.version;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class OrbEntityFactory {

    private static OrbEntityFactory instance;

    private final WrappedFactory wrappedFactory;

    private OrbEntityFactory() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        String version = Bukkit.getServer().getClass().getName().split("\\.")[3];
        Class<?> wrappedFactoryClass = Class.forName("com.andrei1058.handyorbs.core.version.OrbFactory_" + version);
        Constructor<?> constructor = wrappedFactoryClass.getConstructor();
        this.wrappedFactory = (WrappedFactory) constructor.newInstance();
    }

    /**
     * Initialize factory.
     *
     * @return false if server version is not supported.
     */
    public static boolean init() {
        if (instance == null) {
            try {
                instance = new OrbEntityFactory();
            } catch (Exception ignored) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get factory instance. Make sure to initialize it first.
     */
    public static OrbEntityFactory getInstance() {
        return instance;
    }

    /**
     * Spawn a orb entity.
     */
    public OrbEntity spawnOrbEntity(Location location, ItemStack head) {
        return getWrappedFactory().spawnOrbEntity(location, head);
    }

    private WrappedFactory getWrappedFactory() {
        return wrappedFactory;
    }
}
