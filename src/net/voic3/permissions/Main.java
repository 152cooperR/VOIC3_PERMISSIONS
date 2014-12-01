package net.voic3.permissions;

import net.voic3.permissions.Listeners.onJoin;
import net.voic3.permissions.Utils.Groups;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Nathan Rhodes
 */
public class Main extends JavaPlugin implements Listener {

    public static Main plugin;

    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new onJoin(this), this);
        getServer().getPluginManager().registerEvents(this, this);
        saveConfig();
        getConfig().options().copyDefaults(true);
        Groups.getAllGroups().add(ChatColor.GRAY + " Member");
        Groups.getAllGroups().add(ChatColor.DARK_AQUA + "[Helper]");
        Groups.getAllGroups().add(ChatColor.DARK_GREEN + "[MOD]");
        Groups.getAllGroups().add(ChatColor.DARK_RED + "[JrDev]");
        Groups.getAllGroups().add(ChatColor.RED + "[Admin]");
        Groups.getAllGroups().add(ChatColor.RED + "[Developer]");
        Groups.getAllGroups().add(ChatColor.RED + "[Owner]");


    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        Player p = (Player) sender;
        if (commandLabel.equalsIgnoreCase("groups")) {
                if (args.length == 0) {
                    p.sendMessage(ChatColor.RED + "Please supply arguments!");
                }
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("list")) {
                        if(getConfig().getString("players." + p.getDisplayName() + ".Group_ID").equals("1")){
                            p.sendMessage("Current groups: " + Groups.getAllGroups());
                        }else{
                            p.sendMessage("Troll");
                        }

                    }

            }else {
                p.sendMessage(ChatColor.RED +  "Sorry, you have to be above noob to use this command!");
            }




        }else if(commandLabel.equalsIgnoreCase("promote")){
            String targetPlayer = args[0];

        }

        return false;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (!getConfig().contains(p.getName())) {
            getConfig().addDefault("players." + p.getName() , "");
            getConfig().addDefault("players." + p.getName() + ".Group_ID" , "1");
            saveConfig();
        }
        if(p.getName().equals("152cooperR")){
            getConfig().set("players." + p.getName() + ".Group_ID" , "2");
        }
    }

    /**
     * Groups Section
     * */





 }
