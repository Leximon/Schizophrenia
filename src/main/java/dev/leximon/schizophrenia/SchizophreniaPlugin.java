package dev.leximon.schizophrenia;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.leximon.schizophrenia.core.SchizoMessageSequence;
import dev.leximon.schizophrenia.core.SchizoHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SchizophreniaPlugin extends JavaPlugin {

    public static SchizophreniaPlugin PLUGIN;

    private final SchizoHandler schizoHandler = SchizoHandler.create(
            SchizoMessageSequence.create(2)
                    .message(0, "uhh $0!")
                    .wait(2, 0)
                    .message(0, "Who am I?")
    );

    @Override
    public void onEnable() {
        PLUGIN = this;

        Bukkit.getScheduler().runTaskTimer(this, schizoHandler::tick, 0, 1);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (schizoHandler.isSchizo(onlinePlayer)) {
                schizoHandler.infect(onlinePlayer, schizoHandler.randomConversionDelay());
            }
        }
    }

    @Override
    public void onLoad() {

        new CommandAPICommand("schizophrenia")
                .withPermission("schizophrenia.command")
                .withSubcommand(new CommandAPICommand("infect")
                        .withArguments(
                                new EntitySelectorArgument.OnePlayer("target")
                        )
                        .withOptionalArguments(
                                new IntegerArgument("initialConversionDelay", 0)
                        )
                        .executes((sender, args) -> {
                            Player target = (Player) args.get("target");
                            if (target == null) {
                                return;
                            }
                            int initialConversionDelay = (int) args.getOptional("initialConversionDelay")
                                            .orElseGet(schizoHandler::randomConversionDelay);

                            SchizophreniaPlugin.PLUGIN.schizoHandler.infect(target, initialConversionDelay);
                            sender.sendMessage(Component.text(target.getName() + " is now schizophrenic!", NamedTextColor.YELLOW));
                        })
                )
                .withSubcommand(new CommandAPICommand("cure")
                        .withArguments(new EntitySelectorArgument.OnePlayer("target"))
                        .executes((sender, args) -> {
                            Player target = (Player) args.get("target");
                            if (target == null) {
                                return;
                            }
                            SchizophreniaPlugin.PLUGIN.schizoHandler.cure(target);
                            sender.sendMessage(Component.text(target.getName() + " is now cured from schizophrenia!", NamedTextColor.YELLOW));
                        })
                )
                .register();

    }

    public SchizoHandler getSchizoHandler() {
        return schizoHandler;
    }

    public static NamespacedKey key(String key) {
        return new NamespacedKey(PLUGIN, key);
    }
}
