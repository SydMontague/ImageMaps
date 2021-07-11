package net.craftcitizen.imagemaps;

import de.craftlancer.core.command.HelpCommand;
import de.craftlancer.core.command.SubCommand;
import de.craftlancer.core.util.MessageLevel;
import de.craftlancer.core.util.MessageUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public class ImageMapHelpCommand extends HelpCommand {

    public ImageMapHelpCommand(Plugin plugin, Map<String, SubCommand> map) {
        super("imagemaps.help", plugin, map);
    }

    @Override
    public void help(CommandSender sender) {
        if (((ImageMaps) getPlugin()).isGlowingSupported()) {
            MessageUtil.sendMessage(getPlugin(),
                    sender,
                    MessageLevel.NORMAL,
                    buildMessage("/imagemap place <filename> [frameInvisible] [frameFixed] [frameGlowing] [size]", " - starts image placement"));
        } else if (((ImageMaps) getPlugin()).isInvisibilitySupported()) {
            MessageUtil.sendMessage(getPlugin(),
                    sender,
                    MessageLevel.NORMAL,
                    buildMessage("/imagemap place <filename> [frameInvisible] [frameFixed] [size]", " - starts image placement"));
        } else {
            MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.NORMAL, buildMessage("/imagemap place <filename> [size]", " - starts image placement"));
        }

        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.NORMAL, buildMessage("/imagemap download <filename> <sourceURL>", " - downloads an image"));
        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.NORMAL, buildMessage("/imagemap delete <filename>", " - deletes an image"));
        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.NORMAL, buildMessage("/imagemap info <filename>", " - displays image info"));
        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.NORMAL, buildMessage("/imagemap reload <filename>", " - reloads an image from disk"));
        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.NORMAL, buildMessage("/imagemap list [page]", " - lists all files in the images folder"));
        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.NORMAL, buildMessage("/imagemap help [command]", " - shows help"));
    }

    private static BaseComponent buildMessage(String str1, String str2) {
        BaseComponent combined = new TextComponent();

        BaseComponent comp1 = new TextComponent(str1);
        comp1.setColor(ChatColor.WHITE);
        BaseComponent comp2 = new TextComponent(str2);
        comp2.setColor(ChatColor.GRAY);

        combined.addExtra(comp1);
        combined.addExtra(comp2);

        return combined;
    }
}
