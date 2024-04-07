package dev.leximon.schizophrenia.core;

import dev.leximon.schizophrenia.SchizophreniaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinLeaveListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        SchizoHandler schizoHandler = SchizophreniaPlugin.PLUGIN.getSchizoHandler();
        if (schizoHandler.isSchizo(player)) {
            schizoHandler.infect(player, schizoHandler.randomConversionDelay());
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        SchizoHandler schizoHandler = SchizophreniaPlugin.PLUGIN.getSchizoHandler();
        schizoHandler.playerLeft(player);
    }

}
