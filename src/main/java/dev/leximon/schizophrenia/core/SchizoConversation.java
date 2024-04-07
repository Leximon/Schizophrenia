package dev.leximon.schizophrenia.core;

import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public class SchizoConversation {

    private final SchizoPlayer schizoPlayer;
    private final List<String> actors;
    private final LinkedList<SchizoMessageSequence.SequenceTask> tasks;

    private int nextTaskDelay = 0;
    private boolean ended = false;

    protected SchizoConversation(SchizoPlayer schizoPlayer, List<String> actors, List<SchizoMessageSequence.SequenceTask> tasks) {
        this.schizoPlayer = schizoPlayer;
        this.actors = actors;
        this.tasks = new LinkedList<>(tasks);
    }

    public void tick() {
        if (ended)
            return;

        if (nextTaskDelay > 0) {
            nextTaskDelay--;
            return;
        }

        if (tasks.isEmpty()) {
            ended = true;
            return;
        }

        SchizoMessageSequence.SequenceTask task = tasks.poll();
        if (task instanceof SchizoMessageSequence.WaitSequenceTask waitTask) {
            nextTaskDelay = waitTask.ticks();
        } else if (task instanceof SchizoMessageSequence.ExecutableSequenceTask executableTask) {
            Player player = schizoPlayer.getPlayer();
            if (player == null) {
                cancel();
                return;
            }

            executableTask.run(schizoPlayer.getHandler(), player, actors);
        }
    }

    public List<String> getActors() {
        return actors;
    }

    public void cancel() {
        ended = true;
    }

    public boolean isEnded() {
        return ended;
    }
}
