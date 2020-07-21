package net.craftcitizen.imagemaps;

import java.io.File;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.craftlancer.core.Utils;
import de.craftlancer.core.util.MessageLevel;
import de.craftlancer.core.util.MessageUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ImageMapListCommand extends ImageMapSubCommand {
    
    public ImageMapListCommand(ImageMaps plugin) {
        super("imagemaps.list", plugin, true);
    }
    
    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if (!checkSender(sender)) {
            MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.WARNING, "You can't run this command.");
            return null;
        }
        
        long page = args.length >= 2 ? Utils.parseIntegerOrDefault(args[1], 0) - 1 : 0;
        
        String[] fileList = new File(plugin.getDataFolder(), "images").list();
        
        MessageUtil.sendMessage(plugin,
                                sender,
                                MessageLevel.INFO,
                                String.format("Image List %d/%d", page + 1, (int) Math.ceil((double) fileList.length / Utils.ELEMENTS_PER_PAGE)));
        
        // TODO alternating color
        Utils.paginate(fileList, page).forEach(filename -> {
            BaseComponent infoAction = new TextComponent("[Info]");
            infoAction.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/imagemap info " + filename));
            infoAction.setColor(ChatColor.GOLD);
            BaseComponent reloadAction = new TextComponent("[Reload]");
            reloadAction.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/imagemap reload " + filename));
            reloadAction.setColor(ChatColor.GOLD);
            BaseComponent placeAction = new TextComponent("[Place]");
            placeAction.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/imagemap place " + filename));
            placeAction.setColor(ChatColor.GOLD);
            
            BaseComponent message = new TextComponent(filename);
            message.addExtra(" ");
            message.addExtra(infoAction);
            message.addExtra(" ");
            message.addExtra(reloadAction);
            message.addExtra(" ");
            message.addExtra(placeAction);
            
            MessageUtil.sendMessage(plugin, sender, MessageLevel.NORMAL, message);
        });
        return null;
    }
    
    @Override
    public void help(CommandSender sender) {
        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.NORMAL, "Lists all files in the images folder.");
        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.INFO, "Usage: /imagemap list [page]");
    }
}
