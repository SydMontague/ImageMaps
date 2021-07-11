package net.craftcitizen.imagemaps;

import de.craftlancer.core.command.SubCommand;

public abstract class ImageMapSubCommand extends SubCommand {

    public ImageMapSubCommand(String permission, ImageMaps plugin, boolean console) {
        super(permission, plugin, console);
    }

    @Override
    public ImageMaps getPlugin() {
        return (ImageMaps) super.getPlugin();
    }
}
