package dev.leximon.schizophrenia.core;

import dev.leximon.schizophrenia.SchizophreniaPlugin;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class SchizoHandler {

    public static final NamespacedKey SCHIZO_KEY = SchizophreniaPlugin.key("schizo");

    private static final Random RANDOM = new Random();

    private final List<SchizoMessageSequence> registeredMessageSequences;
    private final Object2ObjectArrayMap<UUID, SchizoPlayer> schizoPlayers = new Object2ObjectArrayMap<>();

    public SchizoHandler(List<SchizoMessageSequence> registeredMessageSequences) {
        this.registeredMessageSequences = registeredMessageSequences;
    }

    public void tick() {
        for (SchizoPlayer schizoPlayer : schizoPlayers.values()) {
            schizoPlayer.tick();
        }
    }

    public void infect(Player player, int initialConversionDelay) {
        SchizoPlayer schizoPlayer = new SchizoPlayer(this, player.getUniqueId(), initialConversionDelay);
        schizoPlayers.put(player.getUniqueId(), schizoPlayer);
        player.getPersistentDataContainer()
                .set(SCHIZO_KEY, PersistentDataType.BYTE, (byte) 1);
    }

    public void cure(Player player) {
        schizoPlayers.remove(player.getUniqueId());
        player.getPersistentDataContainer()
                .remove(SCHIZO_KEY);
    }

    public int randomConversionDelay() {
        return RANDOM.nextInt(20 * 60 * 30, 20 * 60 * 60 * 2); // 30 minutes to 2 hours
    }

    public List<SchizoMessageSequence> getRegisteredMessageSequences() {
        return registeredMessageSequences;
    }

    public boolean isSchizo(Player player) {
        if (schizoPlayers.containsKey(player.getUniqueId()))
            return true;
        return player.getPersistentDataContainer().getOrDefault(SCHIZO_KEY, PersistentDataType.BYTE, (byte) 0) == 1;
    }

    public static SchizoHandler create(SchizoMessageSequence... sequences) {
        return new SchizoHandler(Arrays.asList(sequences));
    }

    public static void showMessageToPermittedPlayers(Component text) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("schizophrenia.view")) {
                player.sendMessage(text);
            }
        }
    }

    public void playerLeft(Player targetPlayer) {

        for (SchizoPlayer value : schizoPlayers.values()) {
            SchizoConversation currentConversation = value.getCurrentConversation();
            if (currentConversation == null)
                continue;

            List<String> actors = currentConversation.getActors();
            if (actors.contains(targetPlayer.getName())) {
                currentConversation.cancel();
            }
        }
    }
}
