package net.voic3.permissions.Utils;

import net.voic3.permissions.Main;


import java.util.ArrayList;

/**
 * Created by Nathan Rhodes
 * All rights reserved. © 2014.
 */
public class Groups{
    public static ArrayList<String> allGroups = new ArrayList<String>();
    public static ArrayList<String> owner = new ArrayList<String>();
    public static ArrayList<String> developer = new ArrayList<String>();
    public static ArrayList<String> admin = new ArrayList<String>();
    public static ArrayList<String> jrdev = new ArrayList<String>();
    public static ArrayList<String> mod = new ArrayList<String>();
    public static ArrayList<String> member = new ArrayList();

    public static ArrayList<String> getAllGroups() {
        return allGroups;
    }

}
