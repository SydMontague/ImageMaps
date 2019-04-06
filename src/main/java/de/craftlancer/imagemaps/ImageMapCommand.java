package de.craftlancer.imagemaps;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class ImageMapCommand implements TabExecutor
{
    private ImageMaps plugin;
    
    public ImageMapCommand(ImageMaps plugin)
    {
        this.plugin = plugin;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args)
    {
        switch (args.length)
        {
            case 1:
                return getMatches(args[0], new File(plugin.getDataFolder(), "images").list());
            case 2:
                return Arrays.asList("true", "false", "reload", "download", "scale", "info");
            case 3:
                if (args[2].equals("true") || args[2].equals("false"))
                    return Arrays.asList("scale");
                break;
            case 5:
                if (args[2].equals("scale")) {
                    return Arrays.asList("true", "false");
                }
                break;
        }
        return Collections.emptyList();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!sender.hasPermission("imagemaps.use"))
            return true;
        
        if (args.length < 1)
            return false;

        String filename=args[0];
        for (int i=0; i<filename.length(); i++) {
            if (filename.charAt(i) == '/'
            ||  filename.charAt(i) == '\\'
            ||  filename.charAt(i) == ':') {
                sender.sendMessage("Sorry, this filename isn't allowed");
                return true;
            }
        }        

        if(args.length >= 2 && args[1].equalsIgnoreCase("reload"))
        {
            plugin.reloadImage(args[0]);
            sender.sendMessage("Image " + args[0] + " reloaded!");
            return true;
        }
        
        if (args.length>=2 && args[1].equals("info")) {
            BufferedImage image=plugin.loadImage(args[0]);
            if (image == null) {
                sender.sendMessage("Error getting this image, please consult server logs");
                return true;
            }
            int tileWidth=(image.getWidth()+ImageMaps.MAP_WIDTH-1)/ImageMaps.MAP_WIDTH;
            int tileHeight=(image.getHeight()+ImageMaps.MAP_HEIGHT-1)/ImageMaps.MAP_HEIGHT;

            sender.sendMessage("This image is "+tileWidth+" tiles ("+image.getWidth()+" pixels) wide and "+tileHeight+" tiles ("+image.getHeight()+" pixels) high");
            return true;
        }
        
        if (args.length >= 2 && args[1].equals("download")) {
            if (sender.hasPermission("imagemaps.download")) {
                plugin.appendDownloadTask(new ImageDownloadTask(plugin, args[2], args[0], sender));
            } else {
                sender.sendMessage("You don't have download permission");
            }
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("You need to be a player to do that");
            return true;
        }
        
        BufferedImage image=plugin.loadImage(args[0]);
        if (image == null) {
            sender.sendMessage("Error getting this image, please consult server logs");
            return true;
        }
        boolean fastsend = false;
        int tilesx = 0, tilesy = 0;

        for (int i=1; i<args.length; i++) {
            if (args[i].equalsIgnoreCase("true")) {
                fastsend=true;
            } else if (args[i].equalsIgnoreCase("false")) {
                fastsend=false;
            } else if (args[i].equalsIgnoreCase("scale") && i+2<args.length) {
                try {
                    tilesx=Integer.parseInt(args[i+1]);
                    tilesy=Integer.parseInt(args[i+2]);
                } catch (NumberFormatException ex) {
                    tilesx = tilesy = 0;
                }
                if (tilesx < 0 || tilesy < 0) {
                    sender.sendMessage("Need to pass two integers to scale");
                    return true;
                }
                i+=2;
            } else {
                sender.sendMessage("ignoring unknown parameter "+args[i]+" (continuing)");
            }
        }
        
        double scalex=tilesx * 128.0 / image.getWidth();
        double scaley=tilesy * 128.0 / image.getHeight();
        
        if (scalex == 0 && scaley == 0) {
            scalex = scaley = 1.0;
        } else if (scalex == 0) {
            scalex = scaley;
        } else if (scaley == 0) {
            scaley = scalex;
        } else {
            if (scalex > scaley) {
                scalex = scaley;
            } else {
                scaley = scalex;
            }
        }

        plugin.startPlacing((Player) sender, args[0], fastsend, scalex);
        int tileWidth=(int)((image.getWidth()*scalex+0.001)+ImageMaps.MAP_WIDTH-1)/ImageMaps.MAP_WIDTH;
        int tileHeight=(int)((image.getHeight()*scaley+0.001)+ImageMaps.MAP_HEIGHT-1)/ImageMaps.MAP_HEIGHT;
        sender.sendMessage("Started placing of " + args[0] + " which needs "+image.getWidth()*scalex/ImageMaps.MAP_WIDTH+" width and "+image.getHeight()*scaley/ImageMaps.MAP_HEIGHT+" height. Rightclick on a block, that shall be the upper left corner.");
        
        return true;
    }
    
    /**
     * Get all values of a String array which start with a given String
     * 
     * @param value the given String
     * @param list the array
     * @return a List of all matches
     */
    public static List<String> getMatches(String value, String[] list)
    {
        List<String> result = new LinkedList<>();
        
        for (String str : list)
            if (str.startsWith(value))
                result.add(str);
        
        return result;
    }
    
}
