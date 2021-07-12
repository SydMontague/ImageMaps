package net.craftcitizen.imagemaps;

import de.craftlancer.core.Utils;
import de.craftlancer.core.util.MessageLevel;
import de.craftlancer.core.util.MessageUtil;
import de.craftlancer.core.util.Tuple;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ImageMapPlaceCommand extends ImageMapSubCommand {

    public ImageMapPlaceCommand(ImageMaps plugin) {
        super("imagemaps.place", plugin, false);
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
        boolean isInvisible = false;
        boolean isFixed = false;
        boolean isGlowing = false;
        Tuple<Integer, Integer> scale;

        if (getPlugin().isInvisibilitySupported()) {
            isInvisible = args.length >= 3 && Boolean.parseBoolean(args[2]);
            isFixed = args.length >= 4 && Boolean.parseBoolean(args[3]);
            if (getPlugin().isGlowingSupported()) {
                isGlowing = args.length >= 5 && Boolean.parseBoolean(args[4]);
                scale = args.length >= 6 ? parseScale(args[5]) : new Tuple<>(-1, -1);
            } else {
                scale = args.length >= 5 ? parseScale(args[4]) : new Tuple<>(-1, -1);
            }
        } else {
            scale = args.length >= 3 ? parseScale(args[2]) : new Tuple<>(-1, -1);
        }

        if (filename.contains("/") || filename.contains("\\") || filename.contains(":")) {
            MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.WARNING, "Filename contains illegal character.");
            return null;
        }

        if (!getPlugin().hasImage(filename)) {
            MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.WARNING, "No image with this name exists.");
            return null;
        }

        Player player = (Player) sender;
        player.setMetadata(ImageMaps.PLACEMENT_METADATA, new FixedMetadataValue(getPlugin(), new PlacementData(filename, isInvisible, isFixed, isGlowing, scale)));

        Tuple<Integer, Integer> size = getPlugin().getImageSize(filename, scale);
        MessageUtil.sendMessage(getPlugin(),
                sender,
                MessageLevel.NORMAL,
                String.format("Started placing of %s. It needs a %d by %d area.", args[1], size.getKey(), size.getValue()));
        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.NORMAL, "Right click on the block, that should be the upper left corner.");
        return null;
    }

    @Override
    public void help(CommandSender sender) {
        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.NORMAL, "Starts placing an image.");

        if (getPlugin().isGlowingSupported()) {
            MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.INFO, "Usage: /imagemap place <filename> [frameInvisible] [frameFixed] [frameGlowing] [size]");
        } else if (getPlugin().isInvisibilitySupported()) {
            MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.INFO, "Usage: /imagemap place <filename> [frameInvisible] [frameFixed] [size]");
        } else {
            MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.INFO, "Usage: /imagemap place <filename> [size]");
        }

        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.NORMAL, "Size format: XxY -> 5x2, use -1 for default");
        MessageUtil.sendMessage(getPlugin(),
                sender,
                MessageLevel.NORMAL,
                "The plugin will scale the map to not be larger than the given size while maintaining the aspect ratio.");
        MessageUtil.sendMessage(getPlugin(),
                sender,
                MessageLevel.NORMAL,
                "It's recommended to avoid the size function in favor of using properly sized source images.");
    }

    private static Tuple<Integer, Integer> parseScale(String string) {
        String[] tmp = string.split("x");

        if (tmp.length < 2)
            return new Tuple<>(-1, -1);

        return new Tuple<>(Utils.parseIntegerOrDefault(tmp[0], -1), Utils.parseIntegerOrDefault(tmp[1], -1));
    }

    @Override
    protected List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length > 2 && !getPlugin().isInvisibilitySupported()
                || args.length > 4 && !getPlugin().isGlowingSupported()) {
            return Collections.emptyList();
        }

        switch (args.length) {
            case 2:
                return Utils.getMatches(args[1], new File(plugin.getDataFolder(), "images").list());
            case 3:
                return Utils.getMatches(args[2], Arrays.asList("true", "false"));
            case 4:
                return Utils.getMatches(args[3], Arrays.asList("true", "false"));
            case 5:
                return Utils.getMatches(args[4], Arrays.asList("true", "false"));
            default:
                return Collections.emptyList();
        }
    }
}
