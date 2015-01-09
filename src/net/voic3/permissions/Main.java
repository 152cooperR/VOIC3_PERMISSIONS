package net.voic3.permissions;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nathan Rhodes A.K.A. 152cooperR & NathanSDK
 */

public class Main extends JavaPlugin implements Listener{

    private static Connection connection;



    public static Main plugin;
    public static boolean  isAFK = false;
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        saveConfig();
    }


    public void onDisable() {
       try {
           if (connection != null && connection.isClosed())
               connection.close();
       }catch (SQLException e){
           e.printStackTrace();
       }
    }

    public synchronized static void openConnection(){
        try {
            connection.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public synchronized static void closeConnection(){
        try {
            connection = DriverManager.getConnection("jdbc:msql://192.168.3.164/Voice/playerData" , "root" , "JavaCoder152!");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public synchronized static boolean playerDataContainsPlayer(Player p){
        try{
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM `playerData` WHERE username=?;");
            sql.setString(1, p.getName());

            ResultSet resultSet = sql.executeQuery();

            boolean containsPlayer = resultSet.next();

            sql.close();
            resultSet.close();

            return containsPlayer;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        Player p = (Player) sender;
        if (commandLabel.equalsIgnoreCase("groups")) {
            if (args.length == 0) {
                p.sendMessage(ChatColor.RED + "Please supply arguments!");
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("prefix")) {
                    p.sendMessage(ChatColor.RED + "Please use the correct syntax. /groups prefix <group> <prefix>");
                }
            }
                if (args[0].equalsIgnoreCase("list")) {
                    if (!p.hasPermission("voic3.owner")) {
                        p.sendMessage(ChatColor.RED + "You do not have permission to view groups!");

                    } else {
                        p.sendMessage(ChatColor.RED + "Please use the correct syntax. /groups prefix <group> <prefix>");
                    }
                }

            } else if (commandLabel.equalsIgnoreCase("promote")) {
            Player targetP = p.getServer().getPlayer(args[0]);
            if(targetP.isOnline() || getConfig().contains(targetP.getName())){
                if(getGroup(targetP) == "OWNER" ){
                    p.sendMessage(ChatColor.RED + targetP.getName() + " is already the highest rank!");
                }else{
                    if(targetP.getName() == p.getName()){
                        promotePlayer(targetP);
                        targetP.sendMessage(ChatColor.AQUA + "[" + ChatColor.RED + "STAFF" + ChatColor.AQUA + "] "
                                + " You have been promoted to " + getGroup(p));
                        return false;

                    }
                    promotePlayer(targetP);
                    targetP.sendMessage(ChatColor.AQUA + "[" + ChatColor.RED + "STAFF" + ChatColor.AQUA + "] "
                            + " You have been promoted to " + getGroup(targetP));
                    p.sendMessage(ChatColor.AQUA + "[" + ChatColor.RED + "STAFF" + ChatColor.AQUA + "] " + ChatColor.AQUA +
                            "You promoted " + targetP.getName() + " to " + getGroup(targetP));
                }
            }else{
                p.sendMessage(ChatColor.RED + "ERROR! Target player must be online to be promoted!");

            }



            }else if(commandLabel.equalsIgnoreCase("demote")){
            Player targetP = p.getServer().getPlayer(args[0]);
            if(getGroup(targetP) == "MEMBER" ){
                p.sendMessage(ChatColor.RED + targetP.getName() + " is already the lowest rank!");
            }else{

                if(targetP.isOnline() || getConfig().contains(targetP.getName())){
                    if(targetP.getName() == p.getName()){
                        demotePlayer(targetP);
                        targetP.sendMessage(ChatColor.AQUA + "[" + ChatColor.RED + "STAFF" + ChatColor.AQUA + "] " + " You have been demoted to " + getGroup(p));
                        return false;
                    }
                    demotePlayer(targetP);
                    targetP.sendMessage(ChatColor.AQUA + "[" + ChatColor.RED + "STAFF" + ChatColor.AQUA + "] " + " You have been demoted to " + getGroup(targetP));
                    p.sendMessage(ChatColor.AQUA + "[" + ChatColor.RED + "STAFF" + ChatColor.AQUA + "] " +
                            ChatColor.AQUA + "You demoted " + targetP.getName() + " to " + getGroup(targetP));
                }else{
                    p.sendMessage(ChatColor.RED + "ERROR! Target player must be online to be demoted!");
                }

            }
        }else if (commandLabel.equalsIgnoreCase("ban")) {

            Player targetP = p.getServer().getPlayer(args[0]);
            if (args.length <= 2) {
                if (targetP.isOnline() || getConfig().contains(targetP.getName())) {
                    int bans = getConfig().getInt("players." + targetP.getName() + ".Bans");
                    getConfig().set("players." + targetP.getName() + ".Bans", bans++);
                    getConfig().set("players." + targetP.getName() + ".IsBanned" , true);

                    int i;
                    String reason = "";
                    for (i = 1; i < args.length; i++) {

                        reason = args[i];
                    }

                    setBanMessage(targetP, ChatColor.translateAlternateColorCodes('&',  reason));

                    targetP.kickPlayer(ChatColor.translateAlternateColorCodes('&',  reason));
                    saveConfig();


                } else {
                    if (Bukkit.getOnlinePlayers().contains(targetP.getName())) {
                        sender.sendMessage(ChatColor.RED + targetP.getName() + " has never joined this server!");
                    } else {
                        OfflinePlayer offlinePlayer = getServer().getOfflinePlayer(args[0]);

                    }
                }
            }else{
                p.sendMessage(ChatColor.RED + "The correct format is /ban <player> <reason>");
            }


        }else if (commandLabel.equalsIgnoreCase("prefix")){


                if (args[0].equalsIgnoreCase("owner")) {
                    String prefix = "";
                    for (int i  = 1; i < args.length; i++) {
                        prefix = prefix + "" + args[i];

                    }

                    getConfig().set("Groups." + ".Owner" + ".Prefix", prefix);
                    p.sendMessage(ChatColor.AQUA + "Updated prefix for group" + ChatColor.RED + " OWNER " + ChatColor.AQUA + "to " +
                            ChatColor.translateAlternateColorCodes('&',prefix ));
                    saveConfig();
                    p.setPlayerListName(getPrefix(p) + p.getDisplayName());

                } else if (args[0].equalsIgnoreCase("developer")) {

                    String prefix = "";
                    for (int i  = 1; i < args.length; i++) {
                        prefix = prefix + "" + args[i];

                    }

                    getConfig().set("Groups." + ".Developer" + ".Prefix", prefix);
                    p.sendMessage(ChatColor.AQUA + "Updated prefix for group" + ChatColor.RED + " DEVELOPER " + ChatColor.AQUA + "to " +
                            ChatColor.translateAlternateColorCodes('&', prefix));
                    saveConfig();
                    p.setPlayerListName(getPrefix(p) + p.getDisplayName());


                }else if (args[0].equalsIgnoreCase("admin")) {

                    String prefix = "";
                    for (int i  = 1; i < args.length; i++) {
                        prefix = prefix + " " + args[i];

                    }

                    getConfig().set("Groups." + ".Admin" + ".Prefix", prefix);
                    p.sendMessage(ChatColor.AQUA + "Updated prefix for group" + ChatColor.RED + " ADMIN " + ChatColor.AQUA + "to " +
                            ChatColor.translateAlternateColorCodes('&', prefix));
                    saveConfig();
                    p.setPlayerListName(getPrefix(p) + p.getDisplayName());


                }else if (args[0].equalsIgnoreCase("jrdev")) {

                    String prefix = "";
                    for (int i  = 1; i < args.length; i++) {
                        prefix = prefix + "" + args[i];

                    }
                    getConfig().set("Groups." + ".JrDev" + ".Prefix", prefix);
                    p.sendMessage(ChatColor.AQUA + "Updated prefix for group" + ChatColor.RED + " JR.DEV " + ChatColor.AQUA + "to " +
                            ChatColor.translateAlternateColorCodes('&', prefix));
                    saveConfig();
                    for(Player staff : Bukkit.getOnlinePlayers()){
                        if(getGroup(staff) == "JRDEV"){
                            staff.setPlayerListName(getPrefix(staff) + staff.getName());
                        }
                    }


                }else if (args[0].equalsIgnoreCase("Mod")) {


                    String prefix = "";
                    for (int i  = 1; i < args.length; i++) {
                        prefix = prefix + "" + args[i];

                    }

                    getConfig().set("Groups." + ".Mod" + ".Prefix", prefix);
                    p.sendMessage(ChatColor.AQUA + "Updated prefix for group" + ChatColor.RED + " MOD " + ChatColor.AQUA + "to " +
                            ChatColor.translateAlternateColorCodes('&', prefix));
                    saveConfig();
                    p.setPlayerListName(getPrefix(p) + p.getDisplayName());


                }else if (args[0].equalsIgnoreCase("helper")) {

                    String prefix = "";
                    for (int i  = 1; i < args.length; i++) {
                        prefix = prefix + "" + args[i];

                    }

                    getConfig().set("Groups." + ".Helper" + ".Prefix", prefix);
                    p.sendMessage(ChatColor.AQUA + "Updated prefix for group" + ChatColor.RED + " HELPER " + ChatColor.AQUA + "to " +
                            ChatColor.translateAlternateColorCodes('&', prefix));
                    saveConfig();
                    p.setPlayerListName(getPrefix(p) + p.getDisplayName());


                }else{
                    p.sendMessage(ChatColor.RED +   args[0] + " is not a valid group!");
                }
            }else if(commandLabel.equalsIgnoreCase("s")){
            String message = "";
            int i =0;

            if(isStaff(p)){

                if(args.length==0){
                    p.sendMessage(ChatColor.RED + "The correct syntax is /s <message>");
                }else{



                    for(Player staff : Bukkit.getOnlinePlayers()){
                        if(isStaff(staff)){
                            for(i=0; i < args.length; i++){
                                message = message + "" + args[i] + " ";
                            }
                            staff.sendMessage(ChatColor.AQUA + "[" + ChatColor.RED + "STAFF CHAT" + ChatColor.AQUA + "] "  + getPrefix(p) + p.getName() + ": " +ChatColor.RESET + "" +  ChatColor.translateAlternateColorCodes('&' , message));

                        }
                        message = "";
                        i = 0;

                    }


                }

            }else{
                p.sendMessage(ChatColor.RED + "You do not have permission to use staff chat!");
            }

        }else if (commandLabel.equalsIgnoreCase("nick")) {
            if (isStaff(p)){

                if (args.length == 0) {
                    p.sendMessage(ChatColor.RED + "The correct syntax is /nick <nickname>");
                } else if (args.length == 1) {
                    p.setDisplayName(ChatColor.translateAlternateColorCodes('&', args[0]) + ChatColor.RESET);
                    p.sendMessage(ChatColor.AQUA + "[" + ChatColor.RED + "STAFF" + ChatColor.AQUA + "] " + ChatColor.DARK_AQUA +
                            "Your nickname is now " + ChatColor.translateAlternateColorCodes('&', args[0]) + ChatColor.DARK_AQUA + "!");
                    p.setPlayerListName(getPrefix(p) + ChatColor.translateAlternateColorCodes('&', args[0]));


                    for (Player test : Bukkit.getOnlinePlayers()) {
                        if (isStaff(p)) {
                            test.sendMessage(ChatColor.AQUA + "[" + ChatColor.RED + "STAFF" + ChatColor.AQUA + "] " + p.getName() +
                                    " changed their name to " + ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', args[0]) + ChatColor.AQUA + ".");
                        }
                    }
                } else {
                    p.sendMessage(ChatColor.RED + "The correct syntax is /nick <nickname>");
                }
            } else {
                p.sendMessage(ChatColor.RED + "You do not have permission to change your nickname!");
            }

        } else if (commandLabel.equalsIgnoreCase("userinfo")){
            if(args.length == 1){
                Player targetP = p.getServer().getPlayer(args[0]);

                if(getConfig().contains("players." + args[0])){
                    p.sendMessage(ChatColor.GOLD + "Username: " + ChatColor.RESET + getPrefix(targetP) + targetP.getDisplayName());
                    p.sendMessage(ChatColor.GOLD + "Nickname: " + ChatColor.RESET + getNickname(p));
                    p.sendMessage(ChatColor.GOLD + "Bans: " + ChatColor.RED  + getBans(targetP));
                    p.sendMessage(ChatColor.GOLD + "Kicks: " + ChatColor.RED  + getKicks(targetP));
                    p.sendMessage(ChatColor.GOLD + "Mutes: " + ChatColor.RED  + getMutes(targetP));
                    p.sendMessage(ChatColor.GOLD + "Warnings: " + ChatColor.RED  + getWarnings(targetP));



                }else{
                    p.sendMessage(ChatColor.RED +  args[0] + " has never joined this server!");
            }

            }else{

            }
        }else if (commandLabel.equalsIgnoreCase("afk")){
        if(isStaff(p)){
            if(args.length  == 0){
                if(getConfig().get("players." + p.getName() + ".IsAFK").equals(true) ){
                    getConfig().set("players." + p.getName() + ".IsAFK" , false);
                    saveConfig();
                    sendStaffMessage(ChatColor.GREEN + "+ " + getPrefix(p) + p.getName() + ChatColor.DARK_AQUA + " Is no longer AFK!");
                }else  if(getConfig().get("players." + p.getName() + ".IsAFK").equals(false) ){
                    getConfig().set("players." + p.getName() + ".IsAFK" , true);
                    saveConfig();
                    sendStaffMessage(ChatColor.RED + "- " + getPrefix(p) + p.getName() + ChatColor.DARK_AQUA + " Is now AFK!");
                }

            } else{
                p.sendMessage(ChatColor.RED + "Incorrect syntax! Try /afk");
            }
        }else{
            p.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
        }

        }else if (commandLabel.equalsIgnoreCase("killall")){



        }else if(commandLabel.equalsIgnoreCase("shutup")){
            if(p.isOp()){
                if(args.length ==1){
                    if(getConfig().contains("players." + args[0])){

                        if(getConfig().get("players." + args[0] + ".IsMuted").equals(false)){
                            getConfig().set("players." + args[0] + ".IsMuted" ,  true);
                            saveConfig();
                        }else if(getConfig().get("players." + args[0] + ".IsMuted").equals(true)){
                            getConfig().set("players." + args[0] + ".IsMuted" ,  false);
                            saveConfig();
                        }




                    }else{
                        p.sendMessage(ChatColor.RED + args[0] + " has never joined this server!");
                    }
                }else{
                    p.sendMessage(ChatColor.RED + "The correct syntax is /shutup <player>");
                }

            }else{
                p.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            }
        }else if(commandLabel.equalsIgnoreCase("jurrado")){
            LivingEntity entity = (LivingEntity) Bukkit.getWorld("world").spawnEntity(p.getLocation(), EntityType.SNOWMAN);
            entity.setCustomName(ChatColor.DARK_PURPLE + "Jurrado");



        } else if (commandLabel.equalsIgnoreCase("kick")){
            if(isStaff(p)){
                if(args.length < 3){
                        p.sendMessage(ChatColor.RED + "The correct syntax is /kick <player> <reason>");
                }else{

                }
            }else{
                p.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            }
        }

            return false;
        }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String commandLabel, String[] args) {

        if(commandLabel.equalsIgnoreCase("prefix")){
            if(args.length ==1){
                ArrayList staff = new ArrayList();
                staff.add("OWNER");
                staff.add("DEVELOPER");
                staff.add("ADMIN");
                staff.add("JRDEV");
                staff.add("MOD");
                staff.add("HELPER");
                staff.add("MEMBER");
                return staff;
            }

        }

        return null;
    }

    private static ArrayList protectionList = new ArrayList();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {




        e.setJoinMessage(null);
        Player p = e.getPlayer();

        sendStaffMessage(getPrefix(p) + p.getName() + ChatColor.AQUA + " has joined!");





        if (!getConfig().isSet("players."+ p.getName()) ){
            getConfig().addDefault("players." + p.getName(), "");
            getConfig().addDefault("players." + p.getName() + ".Group_ID", 0);
            getConfig().addDefault("players." + p.getName() + ".Nickname", "");
            getConfig().addDefault("players." + p.getName() + ".IsAFK", false);
            getConfig().addDefault("players." + p.getName() + ".Bans", 0);
            getConfig().addDefault("players." + p.getName() + ".Kicks", 0);
            getConfig().addDefault("players." + p.getName() + ".Mutes", 0);

            getConfig().addDefault("players." + p.getName() + ".Warnings", 0);
            getConfig().addDefault("players." + p.getName() + ".IsBanned", false);
            getConfig().addDefault("players." + p.getName() + ".BanReason", "");
            getConfig().set("players." + p.getName() + ".IsAFK", false);
            getConfig().addDefault("players." + p.getName() + ".IsMuted" , false);
            saveConfig();
        }


        if (getGroup(p) == "OWNER") {

            p.setPlayerListName(ChatColor.RED + "[OWNER]" + p.getDisplayName());


        } else if (getGroup(p) == "DEVELOPER") {

            p.setPlayerListName(getPrefix(p) + p.getDisplayName());

        } else if (getGroup(p) == "ADMIN") {

            p.setPlayerListName(getPrefix(p) + p.getDisplayName());

        } else if (getGroup(p) == "JRDEV") {

            p.setPlayerListName(getPrefix(p) + p.getDisplayName());

        } else if (getGroup(p) == "MOD") {

            p.setPlayerListName(getPrefix(p) + p.getDisplayName());

        } else if (getGroup(p) == "HELPER") {

            p.setPlayerListName(getPrefix(p) + p.getDisplayName());

        } else if (getGroup(p) == "MEMBER") {

            p.setPlayerListName(getPrefix(p) + p.getDisplayName());

        }





    }
    /**
     * Chat Management
     */
    @EventHandler
        public void onChat(AsyncPlayerChatEvent e ) {

        String message = e.getMessage();
        Player p = e.getPlayer();



        if(isStaff(p)){
            e.setMessage(ChatColor.translateAlternateColorCodes('&', e.getMessage() ));
        }
        if (getGroup(p)== "OWNER") {

            e.setMessage(ChatColor.translateAlternateColorCodes('&', e.getMessage()));
            e.setFormat(getPrefix(p) + "%s" + ChatColor.WHITE + ": " + "%s");


        } else if (getGroup(p) =="DEVELOPER") {

            e.setMessage(ChatColor.translateAlternateColorCodes('&', e.getMessage()));
            e.setFormat(getPrefix(p) + "%s" + ChatColor.WHITE + ": " + "%s");


        } else if (getGroup(p) == "ADMIN") {

            e.setMessage(ChatColor.translateAlternateColorCodes('&', e.getMessage()));
            e.setFormat(getPrefix(p) + "%s" + ChatColor.WHITE + ": " + "%s");

        } else if (getGroup(p) == "JRDEV") {

            e.setFormat(getPrefix(p) + "%s" + ChatColor.WHITE + ": " + "%s");

        } else if (getGroup(p) == "MOD") {

            e.setFormat(getPrefix(p) +  "%s" + ChatColor.WHITE + ": " + "%s");

        } else if (getGroup(p)=="HELPER") {

            e.setFormat(getPrefix(p) + "%s" + ChatColor.WHITE + ": " + "%s");

        } else if (getGroup(p) == "MEMBER") {

            e.setFormat(ChatColor.GRAY + "%s" + ": " + "%s");



        }
        if(isMuted(p)){
            p.sendMessage(ChatColor.RED + "You are muted!");
            e.setCancelled(true);

        }


        if(getConfig().get("players." + p.getName() + ".IsAFK").equals(true) ){
            if(isStaff(p) == true){
                getConfig().set("players." + p.getName() + ".IsAFK" , false);
                saveConfig();
                sendStaffMessage(ChatColor.GREEN + "+ " + getPrefix(p) + p.getName() + ChatColor.DARK_AQUA + " Is no longer AFK!");
            }

        }

    }

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        Player p = e.getPlayer();
        if(getConfig().get("players." + p.getName() + ".IsAFK").equals(true) ){
            if(isStaff(p) == true){
                getConfig().set("players." + p.getName() + ".IsAFK" , false);
                saveConfig();
                sendStaffMessage(ChatColor.GREEN + "+ " + getPrefix(p) + p.getName() + ChatColor.DARK_AQUA  +" Is no longer AFK!");
            }

        }

    }

    /**
     *End of Chat Manager
     */

    public String getGroup(Player p){
        String group = "";
        if(getConfig().getInt("players." + p.getName() + ".Group_ID") == 6){
            group = "OWNER";
        }else if(getConfig().getInt("players." + p.getName() + ".Group_ID") == 5){
            group = "DEVELOPER";
        }else if(getConfig().getInt("players." + p.getName() + ".Group_ID") == 4){
            group = "ADMIN";
        }else if(getConfig().getInt("players." + p.getName() + ".Group_ID") == 3){
            group = "JRDEV";
        }else if(getConfig().getInt("players." + p.getName() + ".Group_ID") == 2){
            group = "MOD";
        }else if(getConfig().getInt("players." + p.getName() + ".Group_ID") == 1){
            group = "HELPER";
        }else if(getConfig().getInt("players." + p.getName() + ".Group_ID") == 0){
            group = "MEMBER";
        }
        return group;
    }

    public int getID(Player p){
        int ID = 0;
        if(getConfig().getInt("players." + p.getName() + ".Group_ID") == 6){
            ID = 6;
        }else if(getConfig().getInt("players." + p.getName() + ".Group_ID") == 5){
            ID = 5;
        }else if(getConfig().getInt("players." + p.getName() + ".Group_ID") == 4){
            ID = 4;
        }else if(getConfig().getInt("players." + p.getName() + ".Group_ID") == 3){
            ID = 3;
        }else if(getConfig().getInt("players." + p.getName() + ".Group_ID") == 2){
            ID = 2;
        }else if(getConfig().getInt("players." + p.getName() + ".Group_ID") == 1){
            ID = 1;
        }else if(getConfig().getInt("players." + p.getName() + ".Group_ID") == 0){
            ID = 0;
        }

        return ID;
    }


    public String getPrefix(Player p){

        String prefix ="";

        if(getConfig().getInt("players." + p.getName() + ".Group_ID") == 6){
            prefix = ChatColor.translateAlternateColorCodes('&',getConfig().getString("Groups." + ".Owner" + ".Prefix"));
        }else if(getConfig().getInt("players." + p.getName() + ".Group_ID") == 5){
            prefix = ChatColor.translateAlternateColorCodes('&',getConfig().getString("Groups." + ".Developer" + ".Prefix"));
        }else if(getConfig().getInt("players." + p.getName() + ".Group_ID") == 4){
            prefix = ChatColor.translateAlternateColorCodes('&',getConfig().getString("Groups." + ".Admin" + ".Prefix"));
        }else if(getConfig().getInt("players." + p.getName() + ".Group_ID") == 3){
            prefix = ChatColor.translateAlternateColorCodes('&',getConfig().getString("Groups." + ".JrDev" + ".Prefix"));
        }else if(getConfig().getInt("players." + p.getName() + ".Group_ID") == 2){
            prefix = ChatColor.translateAlternateColorCodes('&',getConfig().getString("Groups." + ".Mod" + ".Prefix"));
        }else if(getConfig().getInt("players." + p.getName() + ".Group_ID") == 1){
            prefix = ChatColor.translateAlternateColorCodes('&',getConfig().getString("Groups." + ".Helper" + ".Prefix"));
        }else if(getConfig().getInt("players." + p.getName() + ".Group_ID") == 0){
            prefix = "";
        }
        return prefix;
    }

    public void promotePlayer(Player p){
        if(getID(p) == 6){
            return;
        }else if(getID(p) == 5){
            getConfig().set("players." + p.getName() + ".Group_ID", 6);
            p.setPlayerListName(getPrefix(p) + p.getDisplayName());
            saveConfig();
        }else if(getID(p) == 4){
            getConfig().set("players." + p.getName() + ".Group_ID", 5);
            p.setPlayerListName(getPrefix(p) + p.getDisplayName());
            saveConfig();
        }else if(getID(p) == 3){
            getConfig().set("players." + p.getName() + ".Group_ID", 4);
            p.setPlayerListName(getPrefix(p) + p.getDisplayName());
            saveConfig();
        }else if(getID(p) == 2){
            getConfig().set("players." + p.getName() + ".Group_ID", 3);
            p.setPlayerListName(getPrefix(p) + p.getDisplayName());
            saveConfig();
        }else if(getID(p) == 1){
            getConfig().set("players." + p.getName() + ".Group_ID", 2);
            p.setPlayerListName(getPrefix(p) + p.getDisplayName());
            saveConfig();
        }else if(getID(p) == 0){
            getConfig().set("players." + p.getName() + ".Group_ID", 1);
            p.setPlayerListName(getPrefix(p) + p.getDisplayName());
            saveConfig();
        }
    }

    public void demotePlayer(Player p){
        if(getID(p) == 6){
            getConfig().set("players." + p.getName() + ".Group_ID", 5);
            p.setPlayerListName(getPrefix(p) + p.getDisplayName());
            saveConfig();
        }else if(getID(p) == 5){
            getConfig().set("players." + p.getName() + ".Group_ID", 4);
            p.setPlayerListName(getPrefix(p) + p.getDisplayName());
            saveConfig();
        }else if(getID(p) == 4){
            getConfig().set("players." + p.getName() + ".Group_ID", 3);
            p.setPlayerListName(getPrefix(p) + p.getDisplayName());
            saveConfig();
        }else if(getID(p) == 3){
            getConfig().set("players." + p.getName() + ".Group_ID", 2);
            p.setPlayerListName(getPrefix(p) + p.getDisplayName());
            saveConfig();
        }else if(getID(p) == 2){
            getConfig().set("players." + p.getName() + ".Group_ID", 1);
            p.setPlayerListName(getPrefix(p) + p.getDisplayName());
            saveConfig();
        }else if(getID(p) == 1){
            getConfig().set("players." + p.getName() + ".Group_ID", 0);
            p.setPlayerListName(getPrefix(p) + p.getDisplayName());
            saveConfig();
        }else if(getID(p) == 0){
            return;

        }
    }
    public boolean isBanned(Player p){
        boolean banned = false;
        if(getConfig().getBoolean("players." + p.getName() + "IsBanned") == true){
            banned = true;
        }else{
            banned = false;
        }
        return banned;
    }

    public void setBanMessage(Player p, String reason ){
        getConfig().set("players." + p.getName() + ".BanReason" , reason);
    }

    public String getBanMessage(Player p){
        String message = ChatColor.translateAlternateColorCodes('&' , getConfig().getString("players." + p.getName() + ".BanReason")) ;
        return message;
    }

    public boolean isStaff(Player p){
        boolean staff = false;
        if(getID(p) == 6){
            staff = true;
        }else if(getID(p) == 5){
            staff = true;
        }else if(getID(p) == 4){
            staff = true;
        }else if(getID(p) == 3){
            staff = true;
        }else if(getID(p) == 2){
            staff = true;
        }else if(getID(p) == 1){
            staff = true;
        }else{
            staff = false;
        }
            return staff;

        }
    public String getNickname(Player p){

        String nickname = getConfig().getString("players." + p.getName() + ".Nickname");
        return nickname;
    }

    public int getBans(Player p){
        int bans = getConfig().getInt("players." + p.getName() + ".Bans");
        return bans;
    }

    public int getKicks(Player p){
        int kicks = getConfig().getInt("players." + p.getName() + ".Kicks");
        return kicks;
    }

    public int getMutes(Player p){
        int mutes = getConfig().getInt("players." + p.getName() + ".Mutes");
        return mutes;
    }

    public int getWarnings(Player p){
        int warnings = getConfig().getInt("players." + p.getName() + ".Warnings");
        return warnings;
    }

    public static void sendStaffChat(String message){

    }

    public boolean isMuted(Player p){
        boolean muted = false;
        if(getConfig().get("players." + p.getName() + ".IsMuted").equals(true)){
            muted = true;
        }else{
            muted = false;
        }

        return muted;
    }


    public void sendStaffMessage(String m){
        for(Player staff : Bukkit.getOnlinePlayers()){
            if(isStaff(staff)){
                staff.sendMessage(ChatColor.AQUA + "[" + ChatColor.RED + "STAFF" + ChatColor.AQUA + "] " + ChatColor.translateAlternateColorCodes('&', m));
            }
        }
    }

    }

