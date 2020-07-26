package com.arthuramorim;

import com.arthuramorim.commands.NPCSummon;
import com.arthuramorim.monitor.PacketMonitor;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.plugin.java.JavaPlugin;


public class ANpc extends JavaPlugin {


    private ProtocolManager protocol;
    private PacketMonitor packetMonitor;

    @Override
    public void onEnable() {
        protocol = ProtocolLibrary.getProtocolManager();
        super.onEnable();

        packetMonitor = new PacketMonitor(this);

        getCommand("npc").setExecutor(new NPCSummon(this));
        getCommand("snake").setExecutor(new NPCSummon(this));
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    public ProtocolManager getProtocol() {
        return protocol;
    }

    public PacketMonitor getPacketMonitor() {
        return packetMonitor;
    }
}
