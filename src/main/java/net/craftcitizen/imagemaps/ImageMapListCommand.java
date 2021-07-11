package net.craftcitizen.imagemaps;

import de.craftlancer.core.Utils;
import de.craftlancer.core.util.MessageLevel;
import de.craftlancer.core.util.MessageUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.File;

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

        String[] fileList = new File(plugin.getDataFolder(), "images").list();
        long page = args.length >= 2 ? Utils.parseIntegerOrDefault(args[1], 0) - 1 : 0;
        int numPages = (int) Math.ceil((double) fileList.length / Utils.ELEMENTS_PER_PAGE);


        MessageUtil.sendMessage(plugin, sender, MessageLevel.INFO, String.format("## Image List Page %d of %d ##", page + 1, numPages));

        boolean even = false;
        for (String filename : Utils.paginate(fileList, page)) {
            BaseComponent infoAction = new TextComponent("[Info]");
            infoAction.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/imagemap info \"%s\"", filename)));
            infoAction.setColor(ChatColor.GOLD);
            BaseComponent reloadAction = new TextComponent("[Reload]");
            reloadAction.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/imagemap reload \"%s\"", filename)));
            reloadAction.setColor(ChatColor.GOLD);
            BaseComponent placeAction = new TextComponent("[Place]");
            placeAction.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/imagemap place \"%s\"", filename)));
            placeAction.setColor(ChatColor.GOLD);
            BaseComponent deleteAction = new TextComponent("[Delete]");
            deleteAction.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/imagemap delete \"%s\"", filename)));
            deleteAction.setColor(ChatColor.RED);

            BaseComponent message = new TextComponent(filename);
            message.setColor(even ? ChatColor.GRAY : ChatColor.WHITE);
            message.addExtra(" ");
            message.addExtra(infoAction);
            message.addExtra(" ");
            message.addExtra(reloadAction);
            message.addExtra(" ");
            message.addExtra(placeAction);
            message.addExtra(" ");
            message.addExtra(deleteAction);

            MessageUtil.sendMessage(plugin, sender, MessageLevel.NORMAL, message);
            even = !even;
        }

        BaseComponent navigation = new TextComponent();
        BaseComponent prevPage = new TextComponent(String.format("<< Page %d", Math.max(page, 1)));
        BaseComponent nextPage = new TextComponent(String.format("Page %d >>", Math.min(page + 1, numPages)));
        prevPage.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/imagemap list " + Math.max(page, 1)));
        nextPage.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/imagemap list " + Math.min(page + 2, numPages)));

        navigation.addExtra(prevPage);
        navigation.addExtra(" | ");
        navigation.addExtra(nextPage);
        MessageUtil.sendMessage(plugin, sender, MessageLevel.INFO, navigation);
        return null;
    }

    @Override
    public void help(CommandSender sender) {
        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.NORMAL, "Lists all files in the images folder.");
        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.INFO, "Usage: /imagemap list [page]");
    }
}
