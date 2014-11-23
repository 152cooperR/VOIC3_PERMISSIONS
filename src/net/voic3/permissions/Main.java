package net.voic3.permissions;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Nathan Rhodes
 */
public class Main extends JavaPlugin implements Listener{

    public void onEnable(){


    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabal, String[] args) {
        return false;
    }
}
