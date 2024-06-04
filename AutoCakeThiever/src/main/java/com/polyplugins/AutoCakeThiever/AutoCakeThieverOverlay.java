package com.polyplugins.AutoCakeThiever;

import com.google.inject.Inject;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.time.Duration;
import java.time.Instant;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class AutoCakeThieverOverlay extends OverlayPanel {
    private final AutoCakeThiever plugin;

    @Inject
    public AutoCakeThieverOverlay(AutoCakeThiever plugin) {
        this.plugin = plugin;
        this.setPosition(OverlayPosition.TOP_LEFT);
    }

    public Dimension render(Graphics2D graphics) {
        UiLayoutOption selectedLayout = this.plugin.config.uiLayout();
        this.panelComponent.getChildren().clear();
        graphics.setStroke(new BasicStroke(1.0F));
        switch (SyntheticClass_1.$SwitchMap$net$runelite$client$plugins$AutoCakeThiever$UiLayoutOption[selectedLayout.ordinal()] ^ -1327903733 >>> 11680 >>> ("066 ".hashCode() ^ -1498124 ^ -21500)) {
            case -1327903736:
                this.addDebugOverlay(graphics);
                break;
            case -1327903735:
                this.addSimpleOverlay();
                break;
            case -1327903734:
                this.addFullOverlay();
            case -1327903729:
        }

        return super.render(graphics);
    }

    private void addFullOverlay() {
        this.panelComponent.setPreferredSize(new Dimension(512000 >>> 11948 << (4260352 >>> 8329), (-15889 ^ 12724) + (4205 << 3680)));
        this.panelComponent.getChildren().add(TitleComponent.builder().text("[B] Cake Thiever").color(new Color(32588 ^ 17017 ^ 5251 - -10544, 25690112 >>> 2514 << (952172544 >>> 2896), 63 << 4928 << 1804 - -12948)).build());
        this.panelComponent.getChildren().add(TitleComponent.builder().text("Encendido").color(Color.GREEN).build());
        Duration runtime = Duration.between(this.plugin.getStartTime(), Instant.now());
        double hoursElapsed = (double)runtime.getSeconds() / Double.longBitsToDouble((long)'艥' + Long.parseLong("-767", 33) ^ 4660134898793718997L - (long)-16124);
        int suppliesPerHour = false;
        if (hoursElapsed > 0.0) {
        }

        this.panelComponent.getChildren().add(LineComponent.builder().left("Version:").leftColor(Color.WHITE).right("1.1").rightColor(new Color(562036736 >>> 13897 >>> (179616 >>> 581), 822083584 >>> 3011 >>> (15751 ^ 11860), -573762902 + 573827414 >>> 8897 + 2025)).build());
        String runtimeStr = this.formatDuration(runtime);
        this.panelComponent.getChildren().add(LineComponent.builder().left("Tiempo:").leftColor(Color.WHITE).right(runtimeStr).rightColor(Color.WHITE).build());
        String state = this.plugin.getCurrentState().toString();
        this.panelComponent.getChildren().add(LineComponent.builder().left("Estado:").right(state).leftColor(Color.WHITE).build());
    }

    private void addSimpleOverlay() {
        this.panelComponent.setPreferredSize(new Dimension((-8739 ^ -11075) >>> (14408 >>> 4801), 15 << 15456 << -5790 + 15615));
        String title = "[B] Cake Thiever v1.1";
        String enabledText = "Plugin Enabled";
        this.panelComponent.getChildren().add(TitleComponent.builder().text(title).color(new Color(-17184 - 7235 + (24553 << 7776), -4391 + -1491 ^ 20654 + -26348, 16128 >>> 968 << (273 << 5893))).build());
        this.panelComponent.getChildren().add(TitleComponent.builder().text("Encendido").color(Color.GREEN).build());
    }

    private void addDebugOverlay(Graphics2D graphics) {
    }

    private void addDebugLine(String text) {
        this.panelComponent.getChildren().add(LineComponent.builder().left(text).leftColor(Color.ORANGE).build());
    }

    private String formatDuration(Duration duration) {
        Object[] var10001;
        if (duration.toHours() > 2738188573441261568L >>> 22519 << (-12039 ^ -14713)) {
            var10001 = new Object[(1002740058 ^ 1539610970) >>> (6205 << 4768)];
            var10001[0] = duration.toHours();
            var10001[1] = duration.toMinutesPart();
            var10001[(char)1 << 12864 << (140541952 >>> 3919)] = duration.toSecondsPart();
            return String.format("%02d:%02d:%02d", var10001);
        } else {
            var10001 = new Object[(char)1 << 7136 << (1611 ^ 13578)];
            var10001[0] = duration.toMinutes();
            var10001[1] = duration.toSecondsPart();
            return String.format("%02d:%02d", var10001);
        }
    }
}
