package com.arthuramorim.monitor;

import com.arthuramorim.ANpc;
import io.netty.channel.*;
import net.minecraft.server.v1_8_R3.PacketPlayOutBed;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PacketMonitor implements Listener {

    private ANpc plugin;

    public PacketMonitor(ANpc plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this,plugin);
    }

    @EventHandler
    public void onJoiun(PlayerJoinEvent e){
        injectPlayer(e.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e){
        removePlayer(e.getPlayer());
    }

    public void removePlayer(Player p){
        Channel channel = ((CraftPlayer) p).getHandle().playerConnection.networkManager.channel;
        channel.eventLoop().submit(()->{
           channel.pipeline().remove(p.getName());
           return null;
        });
    }



    public void injectPlayer(Player player){

        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler(){

            @Override
            public void channelRead(ChannelHandlerContext channelHandlerContext,Object packet) throws Exception {
//                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "PacketR READ -> " + packet.toString() );
                super.channelRead(channelHandlerContext,packet);
            }


            @Override
            public void write(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise channelPromise) throws Exception {
//                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "PacketR WRITE -> " + packet.toString() );
                if(packet instanceof PacketPlayOutBed){
                    return;
                }
                super.write(channelHandlerContext,packet,channelPromise);
            }

        };



        ChannelPipeline pipeline = ((CraftPlayer)player).getHandle().playerConnection.networkManager.channel.pipeline();
        pipeline.addBefore("packet_handler",player.getName(),channelDuplexHandler);

    }
}
