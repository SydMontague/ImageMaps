package de.craftlancer.imagemaps;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.craftlancer.core.LambdaRunnable;
import de.craftlancer.core.util.MessageLevel;
import de.craftlancer.core.util.MessageUtil;

public class ImageMapDownloadCommand extends ImageMapSubCommand {
    
    public ImageMapDownloadCommand(ImageMaps plugin) {
        super("imagemaps.download", plugin, true);
    }
    
    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if (!checkSender(sender)) {
            MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.WARNING, "You can't run this command.");
            return null;
        }
        
        if (args.length < 3) {
            MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.WARNING, "You must specify a file name and a download link.");
            return null;
        }
        
        String filename = args[1];
        String url = args[2];
        
        if (filename.contains("/") || filename.contains("\\") || filename.contains(":")) {
            MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.WARNING, "Filename contains illegal character.");
            return null;
        }
        
        new LambdaRunnable(() -> download(sender, url, filename)).runTaskAsynchronously(plugin);
        return null;
    }
    
    private void download(CommandSender sender, String input, String filename) {
        try {
            URL srcURL = new URL(input);
            
            if (!srcURL.getProtocol().startsWith("http")) {
                MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.WARNING, "Download URL is not valid.");
                return;
            }
            
            URLConnection connection = srcURL.openConnection();
            
            if (!(connection instanceof HttpURLConnection)) {
                MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.WARNING, "Download URL is not valid.");
                return;
            }
            
            if (((HttpURLConnection) connection).getResponseCode() != 200) {
                MessageUtil.sendMessage(getPlugin(),
                                        sender,
                                        MessageLevel.WARNING,
                                        String.format("Download failed, HTTP Error code %d.", ((HttpURLConnection) connection).getResponseCode()));
                return;
            }
            
            String mimeType = ((HttpURLConnection) connection).getHeaderField("Content-type");
            if (!(mimeType.startsWith("image/"))) {
                MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.WARNING, String.format("Download is a %s file, not image.", mimeType));
                return;
            }
            
            try (InputStream str = connection.getInputStream()) {
                Files.copy(str, new File(plugin.getDataFolder(), "images" + File.separatorChar + filename).toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.NORMAL, "Download complete.");
        }
        catch (MalformedURLException ex) {
            MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.WARNING, "Malformatted URL");
        }
        catch (IOException ex) {
            MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.ERROR, "An IO Exception happened, see server log");
            ex.printStackTrace();
        }
    }
    
    @Override
    public void help(CommandSender sender) {
        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.NORMAL, "Downloads an image from an URL.");
        MessageUtil.sendMessage(getPlugin(), sender, MessageLevel.INFO, "Usage: /imagemap download <filename> <sourceURL>");
    }
}
