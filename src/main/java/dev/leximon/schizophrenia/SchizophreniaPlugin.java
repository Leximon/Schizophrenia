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
            SchizoMessageSequence.create(1)
                    .message(0, "frage $0?", "uhm $0?", "$0?")
                    .wait(15, 0)
                    .message(0, "ne doch nicht", "nvm", "vergiss es"),
            SchizoMessageSequence.create(1)
                    .message(1, "ich geh erstmal für heute", "ich geh jetzt", "ich geh mal", "ich grh mal")
                    .wait(3, 0)
                    .message(0, "bye")
                    .wait(5)
                    .leave(1)
                    .wait(0, 5)
                    .join(1)
                    .wait(11)
                    .message(0, "bin doch estmal wieder da", "nvm hi"),
            SchizoMessageSequence.create(2)
                    .message(1, "fuck", "bruh", "fr", "neee")
                    .wait(10)
                    .message(2, "?", "was", "brauche mending btw")
                    .wait(22)
                    .message(1, "der piglin ht meine hoe aufgehoben", "bin raus")
                    .wait(2)
                    .leave(1),
            SchizoMessageSequence.create(3)
                    .message(2, "hat wer lava?", "mending irgendwer")
                    .wait(3)
                    .message(3, "jo", "jup")
                    .wait(1)
                    .message(1, "nein", "nope", "wie viel?")
                    .wait(7)
                    .message(2, "also 1 2 oder so idk"),
            SchizoMessageSequence.create(1)
                    .message(1, "$0 hast du mein villager umgebracht?", "nicht im ernst $0")
                    .wait(30)
                    .message(1, "why $1?", "ok"),
            SchizoMessageSequence.create(2)
                    .leave(1)
                    .wait(2)
                    .leave(2)
                    .wait(30, 1)
                    .join(2)
                    .waitTicks(52)
                    .message(2, "Internet gecrashed"),
            SchizoMessageSequence.create(1)
                    .message(1, "will wer frost walker?", "hab nen lodestone", "EY")
                    .wait(40)
                    .message(2, "ne", "bre"),
            SchizoMessageSequence.create(2)
                    .message(1, "NETHERITE! ENDLICH lmao", "OMG gold bläcke", "h")
                    .wait(5)
                    .message(2, "cool $1", "meins $1", "$1 darf ich?"),
            SchizoMessageSequence.create(1)
                    .leave(1)
                    .waitTicks(10)
                    .join(1),
            SchizoMessageSequence.create(2)
                    .message(1, "hi", "hallo", "moin"),
            SchizoMessageSequence.create(2)
                    .message(1, "alter fick dich $2", "DIGGa $2", "JUNGE $2")
                    .waitTicks(10)
                    .leave(1)
                    .wait(6)
                    .message(2, "...", "heh", "verdient", "ok"),
            SchizoMessageSequence.create(1)
                    .message(1, "n")
                    .wait(2)
                    .message(1, "ni")
                    .waitTicks(50)
                    .message(1, "nichts ;)")
                    .waitTicks(10)
                    .leave(1)

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
