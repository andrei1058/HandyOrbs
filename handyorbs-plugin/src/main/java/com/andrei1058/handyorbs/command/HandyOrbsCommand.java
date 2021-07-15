package com.andrei1058.handyorbs.command;

import com.andrei1058.handyorbs.config.types.WheatOrbConfig;
import com.andrei1058.handyorbs.gui.GUIManager;
import com.andrei1058.spigot.commandlib.fast.FastRootCommand;
import com.andrei1058.spigot.commandlib.fast.FastSubCommand;
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
            withExecutor((commandSender, strings) -> {
                if (commandSender instanceof Player){
                    Player player = (Player) commandSender;
                    if (!player.hasPermission("handyorbs.get")){
                        //todo send permission message
                        return;
                    }
                    player.getInventory().addItem(WheatOrbConfig.getGiveItem(null));
                }
            });
        }
    }
}
