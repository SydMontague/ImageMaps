package net.craftcitizen.imagemaps;

import de.craftlancer.core.Utils;
import de.craftlancer.core.util.MessageLevel;
import de.craftlancer.core.util.MessageUtil;
import de.craftlancer.core.util.Tuple;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collections;
import java.util.List;

public class ImageMapInfoCommand extends ImageMapSubCommand {

    public ImageMapInfoCommand(ImageMaps plugin) {
        super("imagemaps.info", plugin, true);
    }

    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if (!checkSender(sender)) {
            MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.WARNING, "You can't run this command.");
            return null;
        }

        if (args.length < 2) {
            MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.WARNING, "You must specify a file name.");
            return null;
        }

        String filename = args[1];
        BufferedImage image = getPlugin().getImage(filename);

        if (image == null) {
            MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.WARNING, "No image with this name exists.");
            return null;
        }

        Tuple<Integer, Integer> size = getPlugin().getImageSize(filename, null);
        BaseComponent reloadAction = new TextComponent("[Reload]");
        reloadAction.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/imagemap reload \"%s\"", filename)));
        reloadAction.setColor(ChatColor.GOLD);
        BaseComponent placeAction = new TextComponent("[Place]");
        placeAction.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/imagemap place \"%s\"", filename)));
        placeAction.setColor(ChatColor.GOLD);
        BaseComponent deleteAction = new TextComponent("[Delete]");
        deleteAction.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/imagemap delete \"%s\"", filename)));
        deleteAction.setColor(ChatColor.RED);

        BaseComponent actions = new TextComponent("Action: ");
        actions.addExtra(reloadAction);
        actions.addExtra(" ");
        actions.addExtra(placeAction);
        actions.addExtra(" ");
        actions.addExtra(deleteAction);

        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.INFO, "Image Information: ");
        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.NORMAL, String.format("File Name: %s", filename));
        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.NORMAL, String.format("Resolution: %dx%d", image.getWidth(), image.getHeight()));
        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.NORMAL, String.format("Ingame Size: %dx%d", size.getKey(), size.getValue()));
        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.NORMAL, actions);
        return null;
    }

    @Override
    public void help(CommandSender sender) {
        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.NORMAL, "Displays information about an image.");
        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.INFO, "Usage: /imagemap info <filename>");
    }

    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Utils.getMatches(args[1], new File(plugin.getDataFolder(), "images").list());

        return Collections.emptyList();
    }
}
