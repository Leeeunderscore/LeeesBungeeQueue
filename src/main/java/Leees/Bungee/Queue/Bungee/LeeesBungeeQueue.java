package Leees.Bungee.Queue.Bungee;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 * LeeesBungeeQueue
 */
public class LeeesBungeeQueue extends Plugin {
    public LinkedHashMap<UUID, String> regularqueue = new LinkedHashMap<>();
    public LinkedHashMap<UUID, String> priorityqueue = new LinkedHashMap<>();
    public LinkedHashMap<UUID, String> getRegularqueue() {
        return regularqueue;
    }

    public LinkedHashMap<UUID, String> getPriorityqueue() {
        return priorityqueue;
    }


    public Configuration config;
    private static LeeesBungeeQueue instance;

    public static LeeesBungeeQueue getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        Logger logger = getLogger();

        logger.info("§9Loading config");
        processConfig();

        logger.info("§9Registering commands");
        getProxy().getPluginManager().registerCommand(this, new ReloadCommand(this));

        logger.info("§9Registering listeners");
        getProxy().getPluginManager().registerListener(this, new Events());
        getProxy().getPluginManager().registerListener(this, new PingEvent());
        logger.info("§9Scheduling tasks");
        //sends the position message and updates tab on an interval for non priority players and priority players in chat
        getProxy().getScheduler().schedule(this, () -> {
            if (!Lang.POSITIONMESSAGEHOTBAR.equals("true")) {

                int i = 0;

                Map<UUID, String> the_map = new LinkedHashMap<>(regularqueue);
                for (Entry<UUID, String> entry : the_map.entrySet()) {
                    try {
                        i++;

                        ProxiedPlayer player = getProxy().getPlayer(entry.getKey());
                        if (player == null) {
                            regularqueue.remove(entry.getKey());
                            continue;
                        }
                        player.sendMessage(ChatMessageType.CHAT,
                                TextComponent.fromLegacyText(Lang.QUEUEPOSITION.replace("&", "§")
                                        .replace("<position>", i + "").replace("<total>",
                                                regularqueue.size() + "").replace("<server>",
                                                entry.getValue())));
                    } catch (Exception e) {
                        regularqueue.remove(entry.getKey());
                        //TODO: handle exception
                    }
                }
            }
        }, 10000, 10000, TimeUnit.MILLISECONDS);

        getProxy().getScheduler().schedule(this, () -> {
            if (!Lang.POSITIONMESSAGEHOTBAR.equals("true")) {

                int i = 0;

                Map<UUID, String> the_map = new LinkedHashMap<>(priorityqueue);
                for (Entry<UUID, String> entry2 : the_map.entrySet()) {
                    try {
                        i++;

                        ProxiedPlayer player = getProxy().getPlayer(entry2.getKey());
                        if (player == null) {
                            priorityqueue.remove(entry2.getKey());
                            continue;
                        }
                        player.sendMessage(ChatMessageType.CHAT,
                                TextComponent.fromLegacyText(Lang.QUEUEPOSITION.replace("&", "§")
                                        .replace("<position>", i + "").replace("<total>",
                                                regularqueue.size() + "").replace("<server>",
                                                entry2.getValue())));

                    } catch (Exception e) {
                        priorityqueue.remove(entry2.getKey());
                        //TODO: handle exception
                    }
                }
            }
        }, 10000, 10000, TimeUnit.MILLISECONDS);

        //sends the position message and updates tab on an interval for non priority players and priority players on hotbar
        getProxy().getScheduler().schedule(this, () -> {
            if (Lang.POSITIONMESSAGEHOTBAR.equals("true")) {

                int i = 0;

                Map<UUID, String> the_map = new LinkedHashMap<>(regularqueue);
                for (Entry<UUID, String> entry : the_map.entrySet()) {
                    try {
                        i++;

                        ProxiedPlayer player = getProxy().getPlayer(entry.getKey());
                        if (player == null) {
                            regularqueue.remove(entry.getKey());
                            continue;
                        }

                        player.sendMessage(ChatMessageType.ACTION_BAR,
                                TextComponent.fromLegacyText(Lang.QUEUEPOSITION.replace("&", "§")
                                        .replace("<position>",
                                                i + "").replace("<total>",
                                                regularqueue.size() + "").replace("<server>",
                                                entry.getValue())));
                    } catch (Exception e) {
                        regularqueue.remove(entry.getKey());
                        //TODO: handle exception
                    }
                }
            }
        }, Lang.QUEUEMOVEDELAY, Lang.QUEUEMOVEDELAY, TimeUnit.MILLISECONDS);

        getProxy().getScheduler().schedule(this, () -> {
            if (Lang.POSITIONMESSAGEHOTBAR.equals("true")) {

                int i = 0;

                Map<UUID, String> the_map = new LinkedHashMap<>(priorityqueue);
                for (Entry<UUID, String> entry2 : the_map.entrySet()) {
                    try {
                        i++;

                        ProxiedPlayer player = getProxy().getPlayer(entry2.getKey());
                        if (player == null) {
                            priorityqueue.remove(entry2.getKey());
                            continue;
                        }
                        player.sendMessage(ChatMessageType.ACTION_BAR,
                                TextComponent.fromLegacyText(Lang.QUEUEPOSITION.replace("&", "§")
                                        .replace("<position>",
                                                i + "").replace("<total>",
                                                regularqueue.size() + "").replace("<server>",
                                                entry2.getValue())));
                    } catch (Exception e) {
                        priorityqueue.remove(entry2.getKey());
                        //TODO: handle exception
                    }
                }
            }
        }, Lang.QUEUEMOVEDELAY, Lang.QUEUEMOVEDELAY, TimeUnit.MILLISECONDS);

        //updates the tablists for priority and regular queues
        getProxy().getScheduler().schedule(this, () -> {

            int i = 0;
            long waitTime;
            long waitTimeHour;
            long waitTimeMinute;

            Map<UUID, String> the_map = new LinkedHashMap<>(regularqueue);
            for (Entry<UUID, String> entry : the_map.entrySet()) {
                try {
                    i++;

                    ProxiedPlayer player = getProxy().getPlayer(entry.getKey());
                    if (player == null) {
                        regularqueue.remove(entry.getKey());
                        continue;
                    }

                    waitTime = i;

                    waitTimeHour = waitTime / 60;
                    waitTimeMinute = waitTime % 60;
                    if (waitTimeHour == 0) {
                        player.setTabHeader(
                                new ComponentBuilder(Lang.HEADER.replace("&", "§")
                                        .replace("<position>", i + "")
                                        .replace("<wait>", "" + String.format("%dm", waitTimeMinute) + "")).create(),
                                new ComponentBuilder(Lang.FOOTER.replace("&", "§")
                                        .replace("<position>", i + "")
                                        .replace("<wait>", "" + String.format("%dm", waitTimeMinute)) + "").create());
                    } else {
                        player.setTabHeader(
                                new ComponentBuilder(Lang.HEADER.replace("&", "§")
                                        .replace("<position>", i + "")
                                        .replace("<wait>", "" + String.format("%dh %dm", waitTimeHour, waitTimeMinute) + "")).create(),
                                new ComponentBuilder(Lang.FOOTER.replace("&", "§")
                                        .replace("<position>", i + "")
                                        .replace("<wait>", "" + String.format("%dh %dm", waitTimeHour, waitTimeMinute)) + "").create());
                    }

                } catch (Exception e) {
                    regularqueue.remove(entry.getKey());
                    //TODO: handle exception
                }
            }
        }, Lang.QUEUEMOVEDELAY, Lang.QUEUEMOVEDELAY, TimeUnit.MILLISECONDS);

        getProxy().getScheduler().schedule(this, () -> {

            int i = 0;
            long waitTime;
            long waitTimeHour;
            long waitTimeMinute;

            Map<UUID, String> the_map = new LinkedHashMap<>(priorityqueue);
            for (Entry<UUID, String> entry2 : the_map.entrySet()) {
                try {
                    i++;

                    ProxiedPlayer player = getProxy().getPlayer(entry2.getKey());
                    if (player == null) {
                        priorityqueue.remove(entry2.getKey());
                        continue;
                    }

                    waitTime = i;

                    waitTimeHour = waitTime / 60;
                    waitTimeMinute = waitTime % 60;
                    if (waitTimeHour == 0) {
                        player.setTabHeader(
                                new ComponentBuilder(Lang.HEADERPRIORITY.replace("&", "§")
                                        .replace("<position>", i + "")
                                        .replace("<wait>", "" + String.format("%dm", waitTimeMinute) + "")).create(),
                                new ComponentBuilder(Lang.FOOTERPRIORITY.replace("&", "§")
                                        .replace("<position>", i + "")
                                        .replace("<wait>", "" + String.format("%dm", waitTimeMinute)) + "").create());
                    } else {
                        player.setTabHeader(
                                new ComponentBuilder(Lang.HEADERPRIORITY.replace("&", "§")
                                        .replace("<position>", i + "")
                                        .replace("<wait>", "" + String.format("%dh %dm", waitTimeHour, waitTimeMinute) + "")).create(),
                                new ComponentBuilder(Lang.FOOTERPRIORITY.replace("&", "§")
                                        .replace("<position>", i + "")
                                        .replace("<wait>", "" + String.format("%dh %dm", waitTimeHour, waitTimeMinute)) + "").create());
                    }
                } catch (Exception e) {
                    priorityqueue.remove(entry2.getKey());
                    //TODO: handle exception
                }
            }
        }, Lang.QUEUEMOVEDELAY, Lang.QUEUEMOVEDELAY, TimeUnit.MILLISECONDS);
        //moves the queue when someone logs off the main server on an interval set in the bungeeconfig.yml

            try {
                getProxy().getScheduler().schedule(this, Events::moveQueue, Lang.QUEUEMOVEDELAY, Lang.QUEUEMOVEDELAY, TimeUnit.MILLISECONDS);
            } catch (NoSuchElementException ignored) {
            }
        //moves the queue when someone logs off the main server on an interval set in the bungeeconfig.yml
        try {
            getProxy().getScheduler().schedule(this, Events::CheckIfMainServerIsOnline,500, 500, TimeUnit.MILLISECONDS);
        }
        catch(NoSuchElementException ignored) {
        }
        try {
            getProxy().getScheduler().schedule(this, Events::CheckIfQueueServerIsOnline, 500, 500, TimeUnit.MILLISECONDS);
        }
        catch(NoSuchElementException ignored) {
        }
        try {
            getProxy().getScheduler().schedule(this, Events::CheckIfAuthServerIsOnline, 500, 500, TimeUnit.MILLISECONDS);
        }
        catch(NoSuchElementException ignored) {
        }
    }


    void processConfig() {
        try {
            loadConfig();
        } catch (IOException e) {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdir();
            }

            File file = new File(getDataFolder(), "config.yml");
            if (!file.exists()) {
                try (InputStream in = getResourceAsStream("bungeeconfig.yml")) {
                    Files.copy(in, file.toPath());
                    loadConfig();
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
            }
        }

    }

    void loadConfig() throws IOException {
        config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        Arrays.asList(Lang.class.getDeclaredFields()).forEach(it -> {
            try {
                it.setAccessible(true);
                it.set(Lang.class, config.get(it.getName()));
            } catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }
}
