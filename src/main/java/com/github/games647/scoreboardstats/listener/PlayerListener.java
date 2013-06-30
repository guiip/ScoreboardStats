package com.github.games647.scoreboardstats.listener;import static com.github.games647.scoreboardstats.ScoreboardStats.getInstance;import com.github.games647.scoreboardstats.Settings;import com.github.games647.scoreboardstats.pvpstats.Database;import com.github.games647.scoreboardstats.pvpstats.PlayerCache;import com.github.games647.scoreboardstats.pvpstats.SaveTask;import com.github.games647.variables.Other;import org.bukkit.Bukkit;import org.bukkit.Sound;import org.bukkit.entity.Player;import org.bukkit.event.EventHandler;import org.bukkit.event.Listener;import org.bukkit.event.entity.PlayerDeathEvent;import org.bukkit.event.player.PlayerChangedWorldEvent;import org.bukkit.scoreboard.DisplaySlot;public final class PlayerListener implements Listener {    @EventHandler    public static void onDeath(final PlayerDeathEvent death) {        final Player killed = death.getEntity();        final Player killer = killed.getKiller();        if (!Settings.isPvpStats()                || Settings.isDisabledWorld(killed.getWorld().getName())) {            return;        }        final PlayerCache killedcache = Database.getCache(killed.getName());        if (killedcache != null) {            killedcache.onDeath();        }        if (killer != null                && killer.isOnline()) {            final PlayerCache killercache = Database.getCache(killer.getName());            if (Settings.isSound()) {                killer.playSound(killer.getLocation(), Sound.ORB_PICKUP, Other.VOLUME, Other.PITCH);            }            if (killercache != null) {                killercache.onKill();            }        }    }    @EventHandler    public static void onJoin(final org.bukkit.event.player.PlayerJoinEvent join) {        final Player player = join.getPlayer();        Bukkit.getScheduler().runTaskAsynchronously(getInstance(), new Runnable() {            @Override            public void run() {                if (Settings.isPvpStats()) {                    Database.loadAccount(player.getName());                }            }        });    }    @EventHandler(ignoreCancelled = true)    public static void onChange(final PlayerChangedWorldEvent teleport) {        final Player player = teleport.getPlayer();        if (Settings.isDisabledWorld(player.getWorld().getName())) {            player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);        }    }    @EventHandler    public static void onKick(final org.bukkit.event.player.PlayerKickEvent kick) {        if (Settings.isPvpStats()) {            Bukkit.getScheduler().runTaskLaterAsynchronously(getInstance(), new SaveTask(kick.getPlayer()), Other.DELAYED_SAVE);        }    }    @EventHandler    public static void onQuit(final org.bukkit.event.player.PlayerQuitEvent quit) {        if (Settings.isPvpStats()) {            Bukkit.getScheduler().runTaskLaterAsynchronously(getInstance(), new SaveTask(quit.getPlayer()), Other.DELAYED_SAVE);        }    }}