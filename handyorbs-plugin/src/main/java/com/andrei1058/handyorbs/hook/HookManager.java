package com.andrei1058.handyorbs.hook;

import com.andrei1058.handyorbs.HandyOrbsPlugin;
import com.andrei1058.handyorbs.hook.papi.PAPIFallback;
import com.andrei1058.handyorbs.hook.papi.PAPISupport;
import com.andrei1058.handyorbs.hook.papi.PlaceholdersProvider;
import com.andrei1058.handyorbs.hook.vault.VaultChatHook;
import com.andrei1058.handyorbs.hook.vault.VaultEconHook;
import com.andrei1058.handyorbs.hook.vault.chat.NoChatSupport;
import com.andrei1058.handyorbs.hook.vault.chat.VaultChatSupport;
import com.andrei1058.handyorbs.hook.vault.economy.NoEconSupport;
import com.andrei1058.handyorbs.hook.vault.economy.VaultEconSupport;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.Nullable;

/**
 * Hooks are external plugins integration/ support.
 */
@SuppressWarnings("unused")
public class HookManager {

    private static HookManager instance;


    private VaultChatHook vaultChatHook = new NoChatSupport();
    private VaultEconHook vaultEconHook = new NoEconSupport();
    private PAPIHook papiHook = new PAPIFallback();

    private HookManager(boolean vault, @Nullable String papiIdentifier) {
        instance = this;

        // Vault support. Soft-depend.
        if (Bukkit.getPluginManager().isPluginEnabled("Vault") && vault) {
            RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
            if (chatProvider != null) {
                vaultChatHook = new VaultChatSupport(chatProvider.getProvider());
                HandyOrbsPlugin.getInstance().getLogger().info("Hook: Vault -> Chat.");
            }
            RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (economyProvider != null) {
                vaultEconHook = new VaultEconSupport(economyProvider.getProvider());
                HandyOrbsPlugin.getInstance().getLogger().info("Hook: Vault -> Economy.");
            }
        }

        // PlaceholderAPI support. Soft-depend.
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") && papiIdentifier != null) {
            papiHook = new PAPISupport();
            HandyOrbsPlugin.getInstance().getLogger().info("Hook: PlaceholderAPI -> placeholder parser.");
            // register expansion as well
            if (new PlaceholdersProvider(papiIdentifier).register()) {
                HandyOrbsPlugin.getInstance().getLogger().info("Hook: PlaceholderAPI -> registered extension.");
            }
        }
    }

    /**
     * Get vault chat support.
     * If vault was not installed will use an empty interface
     * so it won't break the plugin.
     */
    public VaultChatHook getVaultChatHook() {
        return vaultChatHook;
    }

    /**
     * Get vault economy support.
     * If no economy found it will use an empty interface
     * so it won't break the plugin.
     */
    public VaultEconHook getVaultEconHook() {
        return vaultEconHook;
    }

    /**
     * Get placeholder API hook.
     * If dependency is not loaded will use an empty interface
     * so it won't break the plugin.
     */
    public PAPIHook getPapiHook() {
        return papiHook;
    }

    /**
     * Initialize hooks manager in your plugin's onEnable.
     * <p>
     * Required soft-depends: Vault, PlaceholderAPI.
     *
     * @param vault            true if should check for vault hook.
     * @param papiIdentifier   null if you do not want to use PAPI support, placeholder root identifier otherwise.
     */
    public static void onEnable(boolean vault, @Nullable String papiIdentifier) {
        if (instance == null) {
            new HookManager(vault, papiIdentifier);
        }
    }

    /**
     * Get hooks manager.
     */
    public static HookManager getInstance() {
        return instance;
    }
}
