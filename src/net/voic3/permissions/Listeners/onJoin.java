package net.voic3.permissions.Listeners;

import net.voic3.permissions.Main;
import net.voic3.permissions.PluginListener;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by Nathan Rhodes
 */
public class onJoin extends PluginListener implements Listener{

    public onJoin(Main pl) {
        super(pl);

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        if(p.isOp()){
            e.setJoinMessage(ChatColor.RED + "[ADMIN]" + p.getName() + " has join the lobby!");

        }
    }


}
