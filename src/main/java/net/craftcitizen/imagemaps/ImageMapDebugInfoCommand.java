package net.craftcitizen.imagemaps;

import javax.imageio.ImageIO;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.craftlancer.core.util.MessageLevel;
import de.craftlancer.core.util.MessageUtil;

public class ImageMapDebugInfoCommand extends ImageMapSubCommand {

    public ImageMapDebugInfoCommand(ImageMaps plugin) {
        super("imagemaps.admin", plugin, true);
    }

    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.NORMAL,
                                "ImageMaps Version " + getPlugin().getDescription().getVersion());
        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.NORMAL, "OS: " + System.getProperty("os.name"));
        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.NORMAL, "ImageIO Params:");
        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.NORMAL,
                                "Formats: " + String.join(", ", ImageIO.getReaderFormatNames()));
        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.NORMAL,
                                "Suffixes: " + String.join(", ", ImageIO.getReaderFileSuffixes()));
        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.NORMAL,
                                "MIME: " + String.join(", ", ImageIO.getReaderMIMETypes()));
        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.NORMAL,
                                "Uses Cache: " + Boolean.toString(ImageIO.getUseCache()));
        return null;
    }

    @Override
    public void help(CommandSender sender) {
        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.NORMAL, "Prints some debug output.");
    }

}
