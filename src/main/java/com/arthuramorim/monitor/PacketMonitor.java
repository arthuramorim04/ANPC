package com.arthuramorim.monitor;

import com.arthuramorim.ANpc;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.block.Block;

public class PacketMonitor {

    private ANpc plugin;

    public PacketMonitor(ANpc plugin) {
        this.plugin = plugin;
    }

    public void verifyPacket(){

        plugin.getProtocol().addPacketListener(new PacketAdapter(plugin,
                ListenerPriority.NORMAL,
                PacketType.Play.Client.BLOCK_PLACE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {



            }
        });
    }
}
