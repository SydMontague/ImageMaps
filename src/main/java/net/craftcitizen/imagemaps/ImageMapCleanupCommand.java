package net.craftcitizen.imagemaps;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.craftlancer.core.util.MessageLevel;
import de.craftlancer.core.util.MessageUtil;

public class ImageMapCleanupCommand extends ImageMapSubCommand {

    public ImageMapCleanupCommand(ImageMaps plugin) {
        super("imagemaps.admin", plugin, true);
    }

    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if (!checkSender(sender)) {
            MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.WARNING, "You can't run this command.");
            return null;
        }

        int removedMaps = getPlugin().cleanupMaps();
        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.NORMAL,
                                "Removed " + removedMaps + " invalid images/maps.");
        return null;
    }

    @Override
    public void help(CommandSender sender) {
        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.NORMAL,
                                "Removes maps with invalid IDs or missing image files.");
        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.WARNING,
                                "This action is not reverseable. It is recommended to create a backup of your maps.yml first!");
        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.INFO, "Usage: /imagemap cleanup");
    }

}
