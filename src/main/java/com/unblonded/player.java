package com.unblonded;

import net.minecraft.client.Minecraft;
import net.minecraft.block.Block;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MouseHelper;

public class player {


    public static String getBlockUnder()
    {
        Minecraft mc = Minecraft.getMinecraft();

        double playerX = mc.thePlayer.posX;
        double playerY = mc.thePlayer.posY;
        double playerZ = mc.thePlayer.posZ;

        Block blockUnderPlayer = mc.theWorld.getBlockState(new BlockPos(playerX, playerY - 1, playerZ)).getBlock();

        return blockUnderPlayer.getLocalizedName();
    }

    public static void freeMovementKeys()
    {
        Minecraft mc = Minecraft.getMinecraft();
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
    }

    public static void moveForward()
    {
        Minecraft mc = Minecraft.getMinecraft();
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
    }

    public static void moveBackward()
    {
        Minecraft mc = Minecraft.getMinecraft();
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), true);
    }

    public static void moveLeft()
    {
        Minecraft mc = Minecraft.getMinecraft();
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), true);
    }

    public static void moveRight()
    {
        Minecraft mc = Minecraft.getMinecraft();
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), true);
    }

    public static void attack()
    {
        Minecraft mc = Minecraft.getMinecraft();
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), true);
    }

    public static void chat(String msg)
    {
        Minecraft mc = Minecraft.getMinecraft();
        mc.thePlayer.addChatMessage(new ChatComponentText(msg));
    }

    public static void send(String msg)
    {
        Minecraft mc = Minecraft.getMinecraft();
        mc.thePlayer.sendChatMessage(msg);
    }

    public static String formattedTime(long startTime) {
        long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
        long hours = elapsedTime / 3600;
        long minutes = (elapsedTime % 3600) / 60;
        long seconds = elapsedTime % 60;
        return String.format("Elapsed time: %02d:%02d:%02d", hours, minutes, seconds);
    }
}
