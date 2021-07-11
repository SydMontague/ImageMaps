package net.craftcitizen.imagemaps;

import de.craftlancer.core.Utils;
import de.craftlancer.core.util.MessageLevel;
import de.craftlancer.core.util.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class ImageMapReloadCommand extends ImageMapSubCommand {

    public ImageMapReloadCommand(ImageMaps plugin) {
        super("imagemap.reload", plugin, true);
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

        if (getPlugin().reloadImage(filename))
            MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.NORMAL, "Image reloaded.");
        else
            MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.NORMAL, "Image couldn't be reloaded (does it exist?).");

        return null;
    }

    @Override
    public void help(CommandSender sender) {
        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.NORMAL, "Reloads an image from disk, to be used when the file changed.");
        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.NORMAL, "Avoid resolution changes, since they won't be scaled.");
        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.INFO, "Usage: /imagemap reload <filename>");
    }

    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Utils.getMatches(args[1], new File(plugin.getDataFolder(), "images").list());

        return Collections.emptyList();
    }

}
