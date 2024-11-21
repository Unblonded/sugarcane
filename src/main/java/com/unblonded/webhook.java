package com.unblonded;

import net.minecraft.client.Minecraft;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Scanner;

public class webhook {

    private static String webhookUrl = null;

    // Method to load the webhook URL from the .webhook file
    public static void loadWebhookUrl() throws IOException {
        // Get the mod's directory
        File modDirectory = null;
        File webhookFile = null;

        modDirectory = (Minecraft.getMinecraft().mcDataDir);
        webhookFile = new File(modDirectory, ".webhook");

        // Check if the .webhook file exists
        if (!webhookFile.exists()) {
            System.out.println(".webhook file not found in the Minecraft directory!");
            return;
        }

        // Read the webhook URL from the file
        Scanner scanner = new Scanner(Files.newInputStream(webhookFile.toPath()), StandardCharsets.UTF_8.name());
        webhookUrl = scanner.useDelimiter("\\A").next().trim();  // Read entire file and trim whitespace
        System.out.println("Loaded Webhook URL: " + webhookUrl);
        sendMsg("Client Game Loaded! ✅");

    }

    public static String getUrl() {
        return webhookUrl;
    }

    // Method to send a message to the Discord webhook
    public static void sendDiscordWebHookMsg(String message) throws IOException {
        if (webhookUrl == null) {
            System.out.println("Webhook URL is not loaded. Call loadWebhookUrl() first.");
            return;
        }

        URL url = new URL(webhookUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0"); // Add User-Agent header
        connection.setDoOutput(true);

        // Create JSON payload with escaped message content
        String jsonPayload = "{\"content\":\"" + message.replace("\"", "\\\"") + "\"}";

        OutputStream os = null;
        try {
            os = connection.getOutputStream();
            byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        // Check response code
        int responseCode = connection.getResponseCode();
        if (responseCode == 204) {
            System.out.println("Message sent successfully to Discord.");
        } else {
            System.out.println("Failed to send message. Response code: " + responseCode);
        }
    }

    public static void sendMsg(final String msg) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    sendDiscordWebHookMsg(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void sendScreenshotToDiscord() {
        try {
            // Step 1: Capture the screenshot
            BufferedImage screenshot = captureScreenshot();

            // Step 2: Resize the screenshot to 720p (1280x720)
            BufferedImage resizedScreenshot = resizeImage(screenshot, 1280, 720);

            // Step 3: Save the screenshot to a temporary file
            File tempFile = File.createTempFile("screenshot", ".png");
            ImageIO.write(resizedScreenshot, "PNG", tempFile);

            // Step 4: Send the image as an attachment to Discord
            sendDiscordMessage(tempFile);

            // Delete the temporary file after sending
            tempFile.delete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage captureScreenshot() throws AWTException, IOException {
        // Capture the screen (full screen)
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        Robot robot = new Robot();
        BufferedImage screenshot = robot.createScreenCapture(screenRect);
        return screenshot;
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        Image tmp = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resizedImage;
    }

    private static void sendDiscordMessage(File screenshotFile) throws IOException {
        if (webhookUrl == null) {
            System.out.println("Webhook URL is not loaded.");
            return;
        }

        // Create a URL object for the webhook URL
        URL url = new URL(webhookUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundary");

        connection.setDoOutput(true);

        // Prepare the multipart body
        try {
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            // Add the message content part
            outputStream.writeBytes("------WebKitFormBoundary\r\n");
            outputStream.writeBytes("Content-Disposition: form-data; name=\"content\"\r\n\r\n");
            outputStream.writeBytes("Here is a screenshot:\r\n");

            // Add the file attachment part
            outputStream.writeBytes("------WebKitFormBoundary\r\n");
            outputStream.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + screenshotFile.getName() + "\"\r\n");
            outputStream.writeBytes("Content-Type: image/png\r\n\r\n");

            try {
                FileInputStream fileInputStream = new FileInputStream(screenshotFile);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            outputStream.writeBytes("\r\n------WebKitFormBoundary--\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Check the response code
        int responseCode = connection.getResponseCode();
        if (responseCode == 204) {
            System.out.println("Screenshot sent successfully.");
        } else {
            System.out.println("Failed to send screenshot. Response code: " + responseCode);
        }
    }

    public static void sendScreenshot() {
        new Thread(new Runnable() {
            public void run() {
                sendScreenshotToDiscord();
            }
        }).start();
    }
}
