package dev.leximon.schizophrenia.core;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class SchizoPlayer {

    private static final Random RANDOM = new Random();

    private final SchizoHandler handler;
    private final UUID playerUUID;

    private final LinkedList<SchizoMessageSequence> messageSequencePool;
    private SchizoConversation currentConversation;

    private int nextConversationDelay;

    public SchizoPlayer(SchizoHandler handler, UUID playerUUID, int initialConversionDelay) {
        this.handler = handler;
        this.playerUUID = playerUUID;
        this.messageSequencePool = new LinkedList<>(handler.getRegisteredMessageSequences());
        this.nextConversationDelay = initialConversionDelay;
    }

    public void tick() {
        if (isConversationActive()) {
            currentConversation.tick();
            return;
        }

        if (nextConversationDelay > 0) {
            nextConversationDelay--;
            return;
        }

        SchizoMessageSequence messageSequence = messageSequencePool.remove(RANDOM.nextInt(messageSequencePool.size()));
        currentConversation = messageSequence.tryCreateConversation(this, this::findRandomlyChosenActors);
        if (currentConversation != null) {
            nextConversationDelay = handler.randomConversionDelay();
        } else {
            nextConversationDelay = 20 * 60; // try again after a minute
        }

        if (messageSequencePool.isEmpty()) { // repeat all sequences if all have been used
            messageSequencePool.addAll(handler.getRegisteredMessageSequences());
        }
    }

    public boolean isConversationActive() {
        return currentConversation != null && !currentConversation.isEnded();
    }
    private List<String> findRandomlyChosenActors(int count) {
        List<Player> candidates = new ArrayList<>(Bukkit.getOnlinePlayers());
        Collections.shuffle(candidates);

        return candidates.stream()
                .filter(player -> !player.getUniqueId().equals(playerUUID))
                .limit(count)
                .map(Player::getName)
                .collect(Collectors.toList());
    }

    public @Nullable Player getPlayer() {
        return Bukkit.getPlayer(playerUUID);
    }
}
