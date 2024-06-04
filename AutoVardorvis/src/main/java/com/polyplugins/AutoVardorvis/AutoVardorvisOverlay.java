package com.polyplugins.AutoVardorvis;

import com.google.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.concurrent.TimeUnit;
import net.runelite.api.Client;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;

public class AutoVardorvisOverlay extends OverlayPanel {
    private final Client client;
    private final SpriteManager spriteManager;
    private final AutoVardorvisPlugin plugin;
    private double killsPerHour = 0.0;

    @Inject
    private AutoVardorvisOverlay(Client client, SpriteManager spriteManager, AutoVardorvisPlugin plugin) {
        super(plugin);
        this.client = client;
        this.spriteManager = spriteManager;
        this.plugin = plugin;
        this.setPosition(OverlayPosition.BOTTOM_RIGHT);
        this.setLayer(OverlayLayer.ABOVE_WIDGETS);
        this.setPriority(OverlayPriority.HIGH);
    }

    public Dimension render(Graphics2D graphics2D) {
        String killsText;
        if (this.killsPerHour != 0.0) {
            killsText = String.format("%.1f", this.killsPerHour);
        } else {
            killsText = "0.0";
        }

        this.panelComponent.getChildren().clear();
        LineComponent botState = this.buildLine("Estado: ", this.plugin.botState.toString());
        LineComponent elapsedTime = this.buildLine("Tiempo: ", this.formatTime(this.plugin.elapsedTime));
        LineComponent kills = this.buildLine("Kills: ", this.plugin.totalKills + " (" + killsText + " p/h)");
        LineComponent tickDelay = this.buildLine("Ticks: ", Integer.toString(this.plugin.tickDelay));
        this.panelComponent.getChildren().add(botState);
        this.panelComponent.getChildren().add(tickDelay);
        this.panelComponent.getChildren().add(elapsedTime);
        this.panelComponent.getChildren().add(kills);
        return super.render(graphics2D);
    }

    private LineComponent buildLine(String left, String right) {
        return LineComponent.builder().left(left).right(right).leftColor(Color.WHITE).rightColor(Color.cyan).build();
    }

    private String formatTime(Long timeInMillis) {
        long hours = TimeUnit.MILLISECONDS.toHours(timeInMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis) % ((long)15 << (char)24448 << (15863808 >>> (char)8235));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeInMillis) % ((4102712532L ^ (long)76180692) >>> (2006581248 >>> 9328));
        Object[] var10001 = new Object[3 << (char)14433 >>> (31620 >>> 3874)];
        var10001[0] = hours;
        var10001[1] = minutes;
        var10001[16 << 15691 >>> '鲮' - 25888] = seconds;
        return String.format("%02d:%02d:%02d", var10001);
    }

    public void updateKillsPerHour() {
        if (this.plugin.elapsedTime > 8444249301319680L >>> 22895 >>> (19165 ^ 15195)) {
            this.killsPerHour = (double)this.plugin.totalKills / ((double)this.plugin.elapsedTime / Double.longBitsToDouble((4704985352480201097L << 22400) + (Long.parseLong("-lr", 30) ^ (long)-25832)));
        } else {
            this.killsPerHour = 0.0;
        }

    }
}
