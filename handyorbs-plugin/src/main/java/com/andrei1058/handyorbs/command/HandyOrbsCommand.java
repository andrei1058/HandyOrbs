package com.andrei1058.handyorbs.command;

import com.andrei1058.handyorbs.HandyOrbsPlugin;
import com.andrei1058.handyorbs.api.OrbCategory;
import com.andrei1058.handyorbs.config.types.WheatOrbConfig;
import com.andrei1058.handyorbs.core.OrbBase;
import com.andrei1058.handyorbs.database.OrbRepository;
import com.andrei1058.handyorbs.gui.GUIManager;
import com.andrei1058.handyorbs.registry.OrbRegistry;
import com.andrei1058.spigot.commandlib.fast.FastRootCommand;
import com.andrei1058.spigot.commandlib.fast.FastSubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class HandyOrbsCommand extends FastRootCommand {

    private static boolean initialized = false;

    protected HandyOrbsCommand() {
        super("handyorbs");
        withSubNode(new GetOrbCommand());
    }

    @Override
    public void execute(@NotNull CommandSender s, @NotNull String[] args, @NotNull String st) {
        if (args.length == 0) {
            if (s instanceof Player){
                Player player = (Player) s;
                GUIManager.getInstance().openSelf(player, player);
            }
        } else {
            super.execute(s, args, st);
        }
    }

    /**
     * Initialize command.
     */
    public static void init() {
        if (initialized) return;
        new HandyOrbsCommand().register();
        initialized = true;
    }


    private static class GetOrbCommand extends FastSubCommand {

        public GetOrbCommand() {
            super("get");
            withExecutor(new FastExecutor() {
                @Override
                public void onExecute(CommandSender commandSender, @NotNull String[] strings) {
                    if (commandSender instanceof Player){
                        Player player = (Player) commandSender;
                        player.getInventory().addItem(WheatOrbConfig.getGiveItem(null));
                    }
                }
            });
        }
    }
}
