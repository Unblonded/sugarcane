package com.unblonded;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

@Mod(modid = Main.MODID, version = Main.VERSION)
public class Main {
    public static final String MODID = "fullbright";
    public static final String VERSION = "1.0";

    static boolean macroState = false;
    static boolean hasFreedKeys = false;
    static boolean canRun = false;
    static long timer = 0;
    private Timer threadTimer = new Timer();
    static String oldBlock = "";

    @EventHandler
    public void init(FMLInitializationEvent event) throws IOException {
        startScreenshotThread();
        webhook.loadWebhookUrl();
        webhook.sendScreenshot();
        keyBindings.register();
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(this);
    }


    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) throws IOException {
        Minecraft mc = Minecraft.getMinecraft();
        mc.gameSettings.gammaSetting = 10000;

        if (keyBindings.macroKey.isPressed()) {
            macroState = !macroState;
            if (macroState) {
                webhook.sendMsg("Macro Enabled! ✅");
                timer = System.currentTimeMillis();
            } else {
                webhook.sendMsg("Macro Disabled! ❌");
            }
        }

        if (macroState) {
            hasFreedKeys = false;
            canRun = true;
            String block = player.getBlockUnder();
            if (!(block.equals(oldBlock))) {

                if (block.equals("Sand") || block.equals("Cobblestone") || block.equals("Grass Block") || block.equals("Dirt") || block.equals("tile.air.name")) {

                    if (block.equals("Cobblestone")) {
                        webhook.sendMsg("Cobblestone -> Moving Backward!");
                        player.freeMovementKeys();
                        player.moveBackward();
                        player.attack();

                    } else if (block.equals("Sand")) {
                        webhook.sendMsg("Sand -> Moving Left!");
                        player.freeMovementKeys();
                        player.moveLeft();
                        player.attack();
                    }

                    oldBlock = block;
                } else {
                    player.chat("Could not find a valid block under you. Macro has been disabled.");
                    macroState = false;
                }
            }
        }
        else {
            if (!hasFreedKeys && canRun) {
                player.freeMovementKeys();
                webhook.sendMsg(player.formattedTime(timer));
                timer = 0;
                oldBlock = null;
                hasFreedKeys = true;
            }
        }
    }

    public void startScreenshotThread() {
        threadTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (macroState)
                    webhook.sendScreenshotToDiscord(); // Send the screenshot
            }
        }, 0, 60000); // Run immediately, then every 60,000 ms (60 seconds)
    }
}
