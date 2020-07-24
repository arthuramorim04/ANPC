package com.arthuramorim.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.UUID;


/*
* Classe para criacao de NPC do tipo humano
* */

public class NPC {

    private String name;
    private GameProfile gameProfile;
    private Location npcLocation;
    private EntityPlayer entityPlayer;
    private String texture;
    private String signature;

    public NPC(String name, Location npcLocation, String texture, String signature) {
        this.name = name;
        this.npcLocation = npcLocation;
        this.texture = texture;
        this.signature = signature;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GameProfile getGameProfile() {
        return gameProfile;
    }

    public void setGameProfile(GameProfile gameProfile) {
        this.gameProfile = gameProfile;
    }

    public Location getNpcLocation() {
        return npcLocation;
    }

    public void setNpcLocation(Location npcLocation) {
        this.npcLocation = npcLocation;
    }

    public EntityPlayer getEntityPlayer() {
        return entityPlayer;
    }

    public void setEntityPlayer(EntityPlayer entityPlayer) {
        this.entityPlayer = entityPlayer;
    }

    public void spawnNPC() {

        //Forma com reclect

//        try {
//            Object minecraftServer = getCraftBukkitClass("CraftServer").getMethod("getServer").invoke(Bukkit.getServer());
//            Object worldServer = getCraftBukkitClass("CraftWorld").getMethod("getHandler").invoke(npcLocation.getWorld());
//
//            this.gameProfile = new GameProfile(UUID.randomUUID(), ChatColor.translateAlternateColorCodes('&', this.name));
//            this.gameProfile.getProperties().put("textures", new Property("textures", texture, signature));
//            //cria construtores usando reflect, assim podendo usar em versoes acima da 1.8
//            Constructor<?> entityPlayerConstructor = getNMSClass("EntityPlayer").getDeclaredConstructors()[0];
//            Constructor<?> interactManagerConstructor = getNMSClass("PlayerInteractManager").getDeclaredConstructors()[0];
//
//            //insntancia as entitys usando os contrutores acima
//            this.entityPlayer = (EntityPlayer) entityPlayerConstructor.newInstance(minecraftServer, worldServer, gameProfile, interactManagerConstructor.newInstance(worldServer));
//            this.entityPlayer.getClass().getMethod("setLocation", double.class, double.class, double.class, float.class, float.class).invoke(entityPlayer
//                    , npcLocation.getX()
//                    , npcLocation.getY()
//                    , npcLocation.getZ()
//                    , npcLocation.getYaw()
//                    , npcLocation.getPitch());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        //Forma sem reflect

        //Cria variaveis para o servidor e o mundo em que o npc foi criado
        MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer worldServer = ((CraftWorld) npcLocation.getWorld()).getHandle();
//
//        //cria o perfil do npc com uma UUID random, e depois cria uma entidade

        this.gameProfile = new GameProfile(UUID.randomUUID(), ChatColor.translateAlternateColorCodes('&', this.name));
        this.gameProfile.getProperties().put("textures", new Property("textures", texture, signature));
        this.entityPlayer = new EntityPlayer(minecraftServer, worldServer, gameProfile, new PlayerInteractManager(worldServer));
        this.entityPlayer.setLocation(npcLocation.getX(), npcLocation.getY(), npcLocation.getZ(), npcLocation.getYaw(), npcLocation.getPitch());

    }

    public void showNPC(Player p) {

        //forma com reflect
//        try {
//
//            //PacketPlayOutPlayerInfo - Definindo packets de informacao das entidades
//            Object addPlayerEnum = getNMSClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction").getField("ADD_PLAYER").get(null);
//            Constructor<?> packetPlatOutPlayerInfoConstructor = getNMSClass("PacketPlayOutPlayInfo").getConstructor(getNMSClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction")
//                    , Class.forName("[Lnet.minecraft.server." + getVersion() + ".EntityPlayer;"));
//
//            Object array = Array.newInstance(getNMSClass("EntityPlayer"),1);
//            Array.set(array,0,this.entityPlayer);
//            Object packetPlayOutPlayerInfo = packetPlatOutPlayerInfoConstructor.newInstance(addPlayerEnum,array);
//            sendPacket(p, packetPlayOutPlayerInfo);
//
//            // PacketPlayOutEntitySpawn - Spawner entidade no mundo
//            Constructor<?> packetPlayOutNAmedEntitySpawnConstructor = getNMSClass("PacketPlayOutNamedEntitySpawn").getConstructor(getNMSClass("EntityHuman"));
//            Object packetPlayOutNameEntitySpawn = packetPlayOutNAmedEntitySpawnConstructor.newInstance(this.entityPlayer);
//            sendPacket(p,packetPlayOutNameEntitySpawn);
//
//            //PacketPlayOutEntityHeadRotation -- Rotacao da cabeca do NPC
//            Constructor<?> packetPlayOutEntityHeadRotationConstructor = getNMSClass("PacketPlayOutEntityHeadRotation").getConstructor(getNMSClass("Entity"),byte.class);
//            Double yaw = (Double) this.entityPlayer.getClass().getField("yaw").get(this.entityPlayer);
//            Object packetPlayOutEntityHeadRotation = packetPlayOutEntityHeadRotationConstructor.newInstance(this.entityPlayer, (byte) (yaw * 256 / 360));
//            sendPacket(p,packetPlayOutEntityHeadRotation);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        //Forma sem reflect

        PlayerConnection playerConnection = ((CraftPlayer) p).getHandle().playerConnection;

        playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
        playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(entityPlayer));
        playerConnection.sendPacket(new PacketPlayOutEntityHeadRotation(entityPlayer, (byte) (entityPlayer.yaw * 256 / 360)));
    }

    public void sendPacket(Player p, Object packet) {
        try{
            Object handle = p.getClass().getMethod("getHandgetHandle").invoke(p);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);

            playerConnection.getClass().getMethod("sendPacket").invoke(playerConnection,packet);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Class<?> getNMSClass(String name) {

        try {
            return Class.forName("net.minecraft.server." + getVersion() + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Class<?> getCraftBukkitClass(String name) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + getVersion() + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }
}
