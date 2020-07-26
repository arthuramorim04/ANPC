package com.arthuramorim.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import sun.misc.FloatingDecimal;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
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

        try {
            Object minecraftServer = getCraftBukkitClass("CraftServer").getMethod("getServer").invoke(Bukkit.getServer());
            Object worldServer = getCraftBukkitClass("CraftWorld").getMethod("getHandle").invoke(npcLocation.getWorld());

            this.gameProfile = new GameProfile(UUID.randomUUID(), ChatColor.translateAlternateColorCodes('&', this.name));
            this.gameProfile.getProperties().put("textures", new Property("textures", texture, signature));
            //cria construtores usando reflect, assim podendo usar em versoes acima da 1.8
            Constructor<?> entityPlayerConstructor = getNMSClass("EntityPlayer").getDeclaredConstructors()[0];
            Constructor<?> interactManagerConstructor = getNMSClass("PlayerInteractManager").getDeclaredConstructors()[0];

            //insntancia as entitys usando os contrutores acima
            this.entityPlayer = (EntityPlayer) entityPlayerConstructor.newInstance(minecraftServer, worldServer, gameProfile, interactManagerConstructor.newInstance(worldServer));
            this.entityPlayer.getClass().getMethod("setLocation", double.class, double.class, double.class, float.class, float.class).invoke(entityPlayer
                    , npcLocation.getX()
                    , npcLocation.getY()
                    , npcLocation.getZ()
                    , npcLocation.getYaw()
                    , npcLocation.getPitch());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showNPC(Player p) {

        //forma com reflect
        try {

            //PacketPlayOutPlayerInfo - Definindo packets de informacao das entidades
            Object addPlayerEnum = getNMSClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction").getField("ADD_PLAYER").get(null);
            Constructor<?> packetPlayOutPlayerInfoConstructor = getNMSClass("PacketPlayOutPlayerInfo").getConstructor(getNMSClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction")
                    , Class.forName("[Lnet.minecraft.server." + getVersion() + ".EntityPlayer;"));

            Object array = Array.newInstance(getNMSClass("EntityPlayer"), 1);
            Array.set(array, 0, this.entityPlayer);

            Object packetPlayOutPlayerInfo = packetPlayOutPlayerInfoConstructor.newInstance(addPlayerEnum, array);
            sendPacket(p, packetPlayOutPlayerInfo);

            // PacketPlayOutEntitySpawn - Spawner entidade no mundo
            // PacketPlayOutEntitySpawn - Spawner entidade no mundo
            Constructor<?> packetPlayOutNAmedEntitySpawnConstructor = getNMSClass("PacketPlayOutNamedEntitySpawn").getConstructor(getNMSClass("EntityHuman"));
            Object packetPlayOutNameEntitySpawn = packetPlayOutNAmedEntitySpawnConstructor.newInstance(this.entityPlayer);
            sendPacket(p,packetPlayOutNameEntitySpawn);

            //PacketPlayOutEntityHeadRotation -- Rotacao da cabeca do NPC
            Constructor<?> packetPlayOutEntityHeadRotationConstructor = getNMSClass("PacketPlayOutEntityHeadRotation").getConstructor(getNMSClass("Entity"), byte.class);
            Float yawRaw = (Float) this.entityPlayer.getClass().getField("yaw").get(this.entityPlayer);
            Double yaw = getFloatAsDouble(yawRaw);
            Object packetPlayOutEntityHeadRotation = packetPlayOutEntityHeadRotationConstructor.newInstance(this.entityPlayer, (byte) (yaw * 256 / 360));
            sendPacket(p,packetPlayOutEntityHeadRotation);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendPacket(Player p, Object packet) {
        try {
            Object handle = p.getClass().getMethod("getHandle").invoke(p);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);

            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
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

    private void updateNPC(Player player, Packet<?> packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    private String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    public static Double getFloatAsDouble(Float fValue) {
        return Double.valueOf(fValue.toString());
    }
}
