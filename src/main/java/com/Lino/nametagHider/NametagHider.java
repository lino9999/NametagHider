package com.Lino.nametagHider;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class NametagHider extends JavaPlugin implements Listener {

    private static final String TEAM_NAME = "nametag_hidden";
    private Team hiddenTeam;
    private final Set<UUID> trackedPlayers = new HashSet<>();

    @Override
    public void onEnable() {
        setupTeam();
        getServer().getPluginManager().registerEvents(this, this);

        for (Player player : Bukkit.getOnlinePlayers()) {
            hideNametagForPlayer(player);
        }

        getLogger().info("NametagHider plugin enabled successfully!");
    }

    @Override
    public void onDisable() {
        cleanup();
        getLogger().info("NametagHider plugin disabled!");
    }

    private void setupTeam() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        hiddenTeam = scoreboard.getTeam(TEAM_NAME);
        if (hiddenTeam == null) {
            hiddenTeam = scoreboard.registerNewTeam(TEAM_NAME);
        }

        hiddenTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskLater(this, () -> {
            hideNametagForPlayer(player);
        }, 1L);
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskLater(this, () -> {
            hideNametagForPlayer(player);
        }, 1L);
    }

    private void hideNametagForPlayer(Player player) {
        if (hiddenTeam != null && !hiddenTeam.hasEntry(player.getName())) {
            hiddenTeam.addEntry(player.getName());
            trackedPlayers.add(player.getUniqueId());
        }
    }

    private void cleanup() {
        if (hiddenTeam != null) {
            for (UUID playerId : trackedPlayers) {
                Player player = Bukkit.getPlayer(playerId);
                if (player != null) {
                    hiddenTeam.removeEntry(player.getName());
                }
            }
            trackedPlayers.clear();
        }
    }
}