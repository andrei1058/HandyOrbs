package com.andrei1058.handyorbs.command;

import com.andrei1058.handyorbs.gui.GUIManager;
import com.andrei1058.handyorbs.registry.OrbRegistry;
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
            if (s instanceof Player player){
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
            setPermissions(new String[]{"handyorbs.get"});
            withExecutor((commandSender, strings) -> {

                if (strings.length == 0){
                    //todo send usage
                    return;
                }

                if (commandSender instanceof Player player){
                    if (OrbRegistry.getInstance().getOrbTypes().contains(strings[0].toLowerCase())){
                        player.getInventory().addItem(OrbRegistry.getInstance().getOrbItem(strings[0], player));
                    }
                }
            });

            withTabSuggestions(s -> OrbRegistry.getInstance().getOrbTypes());
        }
    }
}
