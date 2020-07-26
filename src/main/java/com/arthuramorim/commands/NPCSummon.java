package com.arthuramorim.commands;


import com.arthuramorim.ANpc;
import com.arthuramorim.entity.NPC;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class NPCSummon implements CommandExecutor {

    public HashSet<NPC> cacheNPC = new HashSet<>();

    private ANpc plugin;

    public NPCSummon(ANpc plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("npc")) {
            Player player = (Player) sender;

            if (sender instanceof Player) {

                try {
                    EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();


                    GameProfile profile = entityPlayer.getProfile();
                    Property property = profile.getProperties().get("textures").iterator().next();

                    String texture = property.getValue();
                    String signature = property.getSignature();


                    NPC npc = new NPC(args[0], player.getLocation(), texture, signature);
                    npc.spawnNPC();
                    showNPC(npc, player);
                    getCacheNPC().add(npc);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                sender.sendMessage("apoenas jogador pode executar esse comando");
            }
        }
        if(command.getName().equalsIgnoreCase("snake")){
            for (NPC npc : getCacheNPC()) {
                if(npc.getName().equalsIgnoreCase(args[1])){
                    if(args[0].equalsIgnoreCase("1")){
                        npc.getEntityPlayer().setSneaking(true);

                        npc.getEntityPlayer().move(0.5,0.0,0.5);
                    }else{
                        npc.getEntityPlayer().setSneaking(false);
                    }
                }
            }


        }

        return false;
    }


    public void showNPC(NPC npc, Player player) {
        npc.showNPC(player);
    }

    public HashSet<NPC> getCacheNPC() {
        return cacheNPC;
    }
}
