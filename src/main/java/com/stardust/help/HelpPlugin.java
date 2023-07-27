package com.stardust.help;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.io.FileWriter;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.bukkit.*;
import org.bukkit.help.HelpMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.help.HelpTopic;
import org.bukkit.plugin.PluginBase;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.plugin.PluginDescriptionFile;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;

import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Help Plugin
 */
public final class HelpPlugin extends JavaPlugin {
    private Map<String, Book> plugins = new HashMap<>();
    private ChatColor headerColor = ChatColor.GOLD;

    @Override
    public void onEnable() {
        // Plugin startup logic
        final File dataFolder = getDataFolder();
        final File configFile = new File(dataFolder + File.separator + "config.yml");
        final File pluginsFolder = new File(dataFolder + File.separator + "plugins");

        dataFolder.mkdir();
        if (!pluginsFolder.isFile()) {
            pluginsFolder.mkdir();
            final File demoPlugin = new File(pluginsFolder + File.separator + "Demo.json");
            
            try {
                demoPlugin.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(demoPlugin.getAbsoluteFile()));
                writer.write("{\n\t\"PluginName\": [\n\t\t[\n\t\t\t[\"?\", \"plugin description goes here\"],\n\t\t\t[\"/demo\", \"this is the first command on the first page\"],\n\t\t\t[\"/demo command2\", \"this is another possible command\"]\n\t\t],\n\t\t[\n\t\t\t[\"/demo page2\", \"this is the first command on the second page\"],\n\t\t\t[\"/demo page3\", \"this is the second command on the second page\"]\n\t\t]\n\t]\n}");
                writer.close();
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (File plugin : pluginsFolder.listFiles()) {
            getLogger().info("Plugin " + plugin.getName());
            JSONParser parser = new JSONParser();

            try {     
                Object obj = parser.parse(new FileReader(plugin.getAbsoluteFile()));
                JSONObject jsonObject =  (JSONObject) obj;
                Iterator<String> keys = jsonObject.keySet().iterator();
                
                
                while(keys.hasNext()) {
                    final String key = keys.next();
                    final JSONArray pages = (JSONArray) jsonObject.get(key);
                    final ArrayList<ArrayList<ArrayList<String>>> pluginList = new ArrayList<>();

                    for (int i = 0; i < pages.size(); i++) {
                        JSONArray page = (JSONArray) pages.get(i);
                        ArrayList<ArrayList<String>> pagesList = new ArrayList<>();
                        for (int j = 0; j < page.size(); j++) {
                            JSONArray line = (JSONArray) page.get(j);
                            ArrayList<String> lineList = new ArrayList<>();
                            for (int k = 0; k < line.size(); k++) {
                                String word = line.get(k).toString();
                                lineList.add(word);
                            }
                            pagesList.add(lineList);
                        }
                        pluginList.add(pagesList);
                    }
                    
                    plugins.put(key, new Book(pluginList, key));
                }


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
    
        }

        getLogger().info("Help enabled!");
        getCommand("help").setExecutor(this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Help disabled!");
    }

    /** PlayerClass Command Handler */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        final Player player = (Player) sender;
        
        this.clearChat(player);
        ArrayList<TextComponent> responseMessage = new ArrayList<>();

        HelpMap helpMap = Bukkit.getServer().getHelpMap();
        HelpTopic topic = helpMap.getHelpTopic("");
        getLogger().info("[topic] " + topic.getName());

        Integer pageNumber = null;

        switch (args.length) {
            case 0: {
                TextComponent header = new TextComponent("Available Plugins\n");
                header.setBold(true);
                header.setColor(this.headerColor);

                responseMessage.add(header);

                for (Map.Entry<String, Book> plugin : this.plugins.entrySet()) {
                    String pluginName = plugin.getKey();
                    Book pluginBook = plugin.getValue();

                    TextComponent message = new TextComponent(ChatColor.BOLD + "? [ " + pluginName + ChatColor.BOLD + " ]");
                    message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/help " + pluginName));
                    
                    Text hoveredMessage = new Text("Select");
                    message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoveredMessage));
                    responseMessage.add(message);
                    
                    TextComponent pluginDescription = new TextComponent(" " + pluginBook.getDescription() + "\n");
                    pluginDescription.setColor(ChatColor.GRAY);
                    responseMessage.add(pluginDescription);
                }
                break;
            }
            case 1: pageNumber = 0;
            case 2: 
            {   
                String pluginName = args[0];
                if (pageNumber == null) pageNumber = Integer.parseInt(args[1]);
                Book book = this.plugins.getOrDefault(pluginName, null);
                if (book != null) {
                    if (pageNumber < book.size()) {
                        responseMessage = this.combine(
                            responseMessage, 
                            book.getPage(
                                pageNumber, 
                                "/help " + pluginName + " " + (pageNumber - 1), 
                                "/help " + pluginName + " " + (pageNumber + 1),
                                true
                            )
                        );     
                    }  
                }
                break;
            }
            default: break;
        }
        
        this.sendResponseMessage(player, responseMessage);
        return true;
    }

    // Below: Helper Methods
    /** Clears the player's chat */
    private void clearChat(Player player) {
        for (Integer i = 0; i < 100; i++) player.sendMessage("\n");
    }

    /** Sends the player a response message */
    private void sendResponseMessage(Player player, ArrayList<TextComponent> responseMessage) {
        for (TextComponent message : responseMessage) {
            player.spigot().sendMessage(message);
        }
    }

    private <T> ArrayList<T> combine(ArrayList<T> A, ArrayList<T> B) {
        Integer i;
        ArrayList<T> combinedArray = new ArrayList<>();
        
        for (i = 0; i < A.size(); i++) combinedArray.add(A.get(i));
        for (i = 0; i < B.size(); i++) combinedArray.add(B.get(i));
        return combinedArray;
    }
}