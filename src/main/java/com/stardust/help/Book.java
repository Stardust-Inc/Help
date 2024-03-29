package com.stardust.help;

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
    private String description = "";
    private ChatColor headerColor = ChatColor.GOLD;
    private ChatColor[] colors = {ChatColor.AQUA, ChatColor.GREEN};
    private ArrayList<ArrayList<ArrayList<String>>> book = new ArrayList<>();

    public Book(ArrayList<ArrayList<ArrayList<String>>> book, String title) {
        this.book = book;
        this.description = book.get(0).get(0).get(1);
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
        ArrayList<ArrayList<String>> page = this.book.get(pageNumber);
        ArrayList<TextComponent> pageText = new ArrayList<>();

        TextComponent header = this.getBackArrow();
        header.addExtra(this.getTitle());

        pageText.add(header);
        // pageText.add(this.getTitle());

        for (Integer i = 0; i < page.size(); i++) {
            ArrayList<String> content = page.get(i);
            String subHeader = content.get(0);
            if (subHeader.equals("?")) continue;

            String contentHeader = content.get(1);
            
            TextComponent commandMessage = new TextComponent(subHeader);
            // commandMessage.setUnderlined(true);
            
            TextComponent description =  new TextComponent(" " + contentHeader + "\n");
            description.setItalic(true);
            description.setColor(this.colors[i % this.colors.length]);

            pageText.add(commandMessage);
            pageText.add(description);
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

    public TextComponent getBackArrow() {
        TextComponent backArrow = new TextComponent(ChatColor.BOLD + "<< ");
        backArrow.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/help"));
        
        Text hoveredMessage = new Text("Back");
        backArrow.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoveredMessage));

        backArrow.setColor(this.headerColor);
        return backArrow;
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
    
    public Integer size() {return this.book.size();}
    public String getDescription() {return this.description;}
}
