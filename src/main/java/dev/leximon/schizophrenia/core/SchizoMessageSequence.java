package dev.leximon.schizophrenia.core;

import dev.leximon.schizophrenia.SchizophreniaPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.IntFunction;

public class SchizoMessageSequence {

    private static final Random RANDOM = new Random();

    private final int actorCount;
    private final ArrayList<SequenceTask> tasks = new ArrayList<>();

    private SchizoMessageSequence(int actorCount) {
        this.actorCount = actorCount;
    }

    public SchizoMessageSequence waitTicks(int ticks) {
        tasks.add(new WaitSequenceTask(ticks));
        return this;
    }

    public SchizoMessageSequence wait(int seconds) {
        return waitTicks(seconds * 20);
    }

    public SchizoMessageSequence wait(int seconds, int minutes) {
        return waitTicks(seconds * 20 + minutes * 20 * 60);
    }

    public SchizoMessageSequence message(int actorId, String... messageVariants) {
        tasks.add(new MessageSequenceTask(actorId, messageVariants));
        return this;
    }

    public SchizoMessageSequence leave(int actorId) {
        tasks.add(new JoinOrLeaveSequenceTask(actorId, false));
        return this;
    }

    public SchizoMessageSequence join(int actorId) {
        tasks.add(new JoinOrLeaveSequenceTask(actorId, true));
        return this;
    }

    protected @Nullable SchizoConversation tryCreateConversation(SchizoPlayer schizoPlayer, IntFunction<List<String>> actorSupplier) {
        List<String> actors = actorSupplier.apply(actorCount);
        if (actors.size() < actorCount)
            return null;

        return new SchizoConversation(schizoPlayer, actors, tasks);
    }

    public static SchizoMessageSequence create(int actorCount) {
        return new SchizoMessageSequence(actorCount);
    }

    public interface SequenceTask {
    }

    public interface ExecutableSequenceTask extends SequenceTask {
        void run(Player to, List<String> actors);
    }

    public record WaitSequenceTask(int ticks) implements SequenceTask {
    }

    public record MessageSequenceTask(int actorId, String[] messageVariants) implements ExecutableSequenceTask {

        @Override
        public void run(Player to, List<String> actors) {
            String formattedMessage = messageVariants[RANDOM.nextInt(messageVariants.length)];
            for (int actorIndex = 0; actorIndex < actors.size(); actorIndex++) {
                String actor = actors.get(actorIndex);
                formattedMessage = formattedMessage.replace("$" + (actorIndex + 1), actor);
            }
            formattedMessage = formattedMessage.replace("$0", to.getName());

            String senderActor = actorId == 0
                    ? to.getName()
                    : actors.get(actorId - 1);
            formattedMessage = "<" + senderActor + "> " + formattedMessage;

            to.sendMessage(Component.text(formattedMessage));
        }

    }

    public record JoinOrLeaveSequenceTask(int actorId, boolean join) implements ExecutableSequenceTask {

        @Override
        public void run(Player to, List<String> actors) {
            String translationKey = join ? "multiplayer.player.joined" : "multiplayer.player.left";
            String targetActor = actorId == 0
                    ? to.getName()
                    : actors.get(actorId - 1);

            to.sendMessage(Component.translatable(translationKey, NamedTextColor.YELLOW, Component.text(targetActor)));

            Player targetPlayer = Bukkit.getPlayer(targetActor);
            if (targetPlayer == null)
                return;

            if (join) {
                to.listPlayer(targetPlayer);
                to.showPlayer(SchizophreniaPlugin.PLUGIN, targetPlayer);
            } else {
                to.unlistPlayer(targetPlayer);
                to.hidePlayer(SchizophreniaPlugin.PLUGIN, targetPlayer);
            }
        }

    }
}
