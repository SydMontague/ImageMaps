/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.craftlancer.imagemaps;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.CompletableFuture;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author gbl
 */
public class ImageDownloadTask implements Runnable {
    private JavaPlugin plugin;
    private String filename;
    private String downloadUrl;
    private CommandSender sender;
    private CompletableFuture future;
    
    ImageDownloadTask(ImageMaps plugin, String url, String filename, CommandSender sender) {
        this.plugin=plugin;
        this.sender=sender;
        this.downloadUrl=url;
        this.filename=filename;
        
        future=CompletableFuture.runAsync(this);
    }
    
    public CommandSender getSender() {
        return sender;
    }
    
    public boolean isDone() {
        return future.isDone();
    }
    
    public String getResult() {
        try {
            return (future.isDone() ? (String) future.get() : null);
        } catch (Exception ex) {
            return "Exception when getting result";
        }
    }
    
    public String getURL() {
        return this.downloadUrl;
    }

    @Override
    public void run() {
        ReadableByteChannel in = null;
        FileChannel out = null;
        InputStream is = null;
        try {
            URL url=new URL(downloadUrl);
            URLConnection connection=url.openConnection();
            if (!(connection instanceof HttpURLConnection)) {
                future.complete("Not a http(s) URL");
                return;
            }
            int responseCode = ((HttpURLConnection) connection).getResponseCode();
            if (responseCode != 200) {
                future.complete("HTTP Status "+responseCode);
                return;
            }
            String mimeType = ((HttpURLConnection) connection).getHeaderField("Content-type");
            if (!(mimeType.startsWith("image/"))) {
                future.complete("That is a "+mimeType+", not an image");
                return;
            }
            in = Channels.newChannel(is=connection.getInputStream());
            out = new FileOutputStream(new File(plugin.getDataFolder()+"/images", filename)).getChannel();
            out.transferFrom(in, 0, Long.MAX_VALUE);
            future.complete("Download to "+filename+" finished");
        } catch (MalformedURLException ex) {
            future.complete("URL invalid");
        } catch (IOException ex) {
            future.complete("IO Exception");
        } finally {
            close(out);
            close(in);
            close(is);
        }
    }
    
    public void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException ex) {
            }
        }
    }
}
