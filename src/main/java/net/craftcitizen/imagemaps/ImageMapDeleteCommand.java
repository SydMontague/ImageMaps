package net.craftcitizen.imagemaps;

import de.craftlancer.core.Utils;
import de.craftlancer.core.util.MessageLevel;
import de.craftlancer.core.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class ImageMapDeleteCommand extends ImageMapSubCommand {

    public ImageMapDeleteCommand(ImageMaps plugin) {
        super("imagemaps.delete", plugin, true);
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

        if (filename.contains("/") || filename.contains("\\") || filename.contains(":")) {
            MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.WARNING, "Filename contains illegal character.");
            return null;
        }

        if (!getPlugin().hasImage(filename)) {
            MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.WARNING, "No image with this name exists.");
            return null;
        }

        if (getPlugin().deleteImage(filename)) {
            MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.NORMAL, "File deleted.");
        } else {
            MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.WARNING, "Failed to delete file.");
        }
        return null;
    }

    @Override
    public void help(CommandSender sender) {
        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.NORMAL, "Deletes an image.");
        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.INFO, "Usage: /imagemap delete <filename>");
    }

    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Utils.getMatches(args[1], new File(plugin.getDataFolder(), "images").list());

        return Collections.emptyList();
    }
}
