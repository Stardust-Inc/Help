package com.stardust;

import java.util.*;
import org.bukkit.*;
import org.bukkit.help.HelpMap;
import org.bukkit.help.HelpTopic;
import net.md_5.bungee.api.ChatColor;
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
public final class Help extends JavaPlugin {
    private String[][][] basesInfo = {
        {
            {"?", "3 word summary"},
            {"/base", "View base stats"},
            {"/base define", "Select base diagonal endpoints"},
            {"/base defend", "Spawn troops to protect base."},
        },
        {
            {"/base delete", "Deletes base"},
            {"/base save", "Saves base"},
            {"/base restore", "Restores base for 100 pieces of gold"}
        }
    };
    
    private Map<String, Book> plugins = Map.of(
        "Bases", new Book(basesInfo, "Bases")
    );

    private ChatColor headerColor = ChatColor.GOLD;

    @Override
    public void onEnable() {
        // Plugin startup logic
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