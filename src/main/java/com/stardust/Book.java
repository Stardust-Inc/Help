package com.stardust;

import java.util.*;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;

/** A Book */
public class Book {
    /**
     * AF() = 
     * 
     */

    /** Representation */
    private String title = "";
    private String[][][] book = {};
    private String description = "";
    private ChatColor headerColor = ChatColor.GOLD;
    private ChatColor[] colors = {ChatColor.AQUA, ChatColor.GREEN};

    public Book(String[][][] book, String title) {
        this.book = book;
        this.description = book[0][0][1];
        this.title = title;
    }

    /**
     * Converts a page of the book to a TextComponent to be displayed as a message to the user
     * 
     * @param pageNumber the number of the page to display
     * @param prevCommand if showNavigation is true, this is the command to execute when the prev button is clicked 
     * @param nextCommand if showNavigation is true, this is the command to execute when the next button is clicked 
     * @param showNavigation show prev and next buttons or not, default is no
     * @return a TextComponent rendering the current book page
     */
    public ArrayList<TextComponent> getPage(Integer pageNumber, String prevCommand, String nextCommand, Boolean... showNavigation) {
        String[][] page = this.book[pageNumber];
        ArrayList<TextComponent> pageText = new ArrayList<>();
        pageText.add(this.getTitle());

        for (Integer i = 0; i < page.length; i++) {
            String[] pluginCommand = page[i];
            String pluginCommandName = pluginCommand[0];
            if (pluginCommandName == "?") continue;

            String pluginCommandDescription = pluginCommand[1];
            
            TextComponent commandMessage = new TextComponent("/" + pluginCommandName);
            // commandMessage.setUnderlined(true);
            
            TextComponent commandDescription =  new TextComponent(" " + pluginCommandDescription + "\n");
            commandDescription.setItalic(true);
            commandDescription.setColor(this.colors[i % this.colors.length]);

            pageText.add(commandMessage);
            pageText.add(commandDescription);
        }   

        if (showNavigation.length > 0) {
            pageText.add(getNavigation(pageNumber, prevCommand, nextCommand));
        }
        
        return pageText;
    }

    /**
     * @return the title of the book as a TextComponent
     */
    public TextComponent getTitle() {
        TextComponent title = new TextComponent(this.title + "\n");
        title.setBold(true);
        title.setColor(this.headerColor);
        return title;
    }
    /**
     * Converts the navigation of the book into a TextComponent to be displayed as a message
     * The navigation should be similar to < page x of n >
     * 
     * @param pageNumber the number of the currently displayed page
     * @param prevCommand the command to execute when the prev button is clicked 
     * @param nextCommand the command to execute when the next button is clicked 
     * @return the navigation of the book as a TextComponent
     */
    private TextComponent getNavigation(Integer pageNumber, String prevCommand, String nextCommand) {
        TextComponent navigateMessage = new TextComponent();
        if (pageNumber > 0) {
            TextComponent navigatePrevMessage = new TextComponent("" + ChatColor.GOLD + ChatColor.BOLD + "< ");
            navigatePrevMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, prevCommand));
            navigatePrevMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Prev"))); 
            navigateMessage.addExtra(navigatePrevMessage);                    
        } 

        navigateMessage.addExtra(
            ChatColor.YELLOW + "Page " + 
            ChatColor.GOLD + ChatColor.BOLD + (pageNumber + 1) + 
            ChatColor.YELLOW + " of " + 
            ChatColor.GOLD + ChatColor.BOLD + this.size()
        );

        if (pageNumber < this.size() - 1) {
            TextComponent navigateNextMessage = new TextComponent("" + ChatColor.GOLD + ChatColor.BOLD + " >");
            navigateNextMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, nextCommand));
            navigateNextMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Next"))); 
            navigateMessage.addExtra(navigateNextMessage);
        }
        return navigateMessage;
    }
    
    public Integer size() {return this.book.length;}
    public String getDescription() {return this.description;}
}
