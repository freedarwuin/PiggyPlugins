package com.example.AutoThiever;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.ColorUtil;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
class AutoThieverOverlay extends OverlayPanel {
    private static final Logger log = LoggerFactory.getLogger(AutoThieverOverlay.class);
    private final AutoThieverPlugin plugin;
    private final AutoThieverConfiguration config;
    String timeFormat;
    public String infoStatus = "Starting...";

    @Inject
    private AutoThieverOverlay(Client client, AutoThieverPlugin plugin, AutoThieverConfiguration config) {
        super(plugin);
        this.setPosition(OverlayPosition.BOTTOM_LEFT);
        this.plugin = plugin;
        this.config = config;
        this.getMenuEntries().add(new OverlayMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, "Configure", "Auto Thiever overlay"));
    }

    public Dimension render(Graphics2D graphics) {
        if (this.plugin.botTimer != null && this.config.enableUI()) {
            if (!this.plugin.enablePlugin) {
                this.infoStatus = "Plugin apagado";
            }

            Duration duration = Duration.between(this.plugin.botTimer, Instant.now());
            this.timeFormat = duration.toHours() < 1L ? "mm:ss" : "HH:mm:ss";
            List var10000 = this.panelComponent.getChildren();
            TitleComponent.TitleComponentBuilder var10001 = TitleComponent.builder();
            AutoThieverConfiguration var10002 = this.config;
            var10000.add(var10001.text("Auto Thiever ElGuason " + "v1.1").color(ColorUtil.fromHex("#FFE247")).build());
            if (this.plugin.uiSetting != com.example.AutoThiever.UISettings.NONE) {
                if (this.plugin.enablePlugin) {
                    this.panelComponent.setPreferredSize(new Dimension(250, 200));
                    this.panelComponent.setBorder(new Rectangle(5, 5, 5, 5));
                    this.panelComponent.getChildren().add(TitleComponent.builder().text("Plugin encendido").color(Color.GREEN).build());
                    switch (this.plugin.uiSetting) {
                        case FULL:
                            this.panelComponent.getChildren().add(LineComponent.builder().left("Estado:").leftColor(Color.WHITE).right(this.infoStatus).rightColor(Color.WHITE).build());
                            this.panelComponent.getChildren().add(LineComponent.builder().left("Tiempo:").leftColor(Color.WHITE).right(DurationFormatUtils.formatDuration(duration.toMillis(), this.timeFormat)).rightColor(Color.WHITE).build());
                            var10000 = this.panelComponent.getChildren();
                            LineComponent.LineComponentBuilder var3 = LineComponent.builder().left("Exito %:").leftColor(Color.WHITE);
                            Object[] var10003 = new Object[]{this.plugin.successRate};
                            var10000.add(var3.right(String.format("%.2f", var10003) + "%").rightColor(Color.WHITE).build());
                            this.panelComponent.getChildren().add(LineComponent.builder().left("Exito picks:").leftColor(Color.WHITE).right(String.valueOf((int)this.plugin.successfulThieves)).rightColor(Color.WHITE).build());
                            this.panelComponent.getChildren().add(LineComponent.builder().left("Fallados picks:").leftColor(Color.WHITE).right(String.valueOf((int)this.plugin.failedThieves)).rightColor(Color.WHITE).build());
                            if (this.config.dodgyNecklace()) {
                                this.panelComponent.getChildren().add(LineComponent.builder().left("Tiene dodgy?:").leftColor(Color.WHITE).right(String.valueOf(this.plugin.hasDodgy())).rightColor(Color.WHITE).build());
                                this.panelComponent.getChildren().add(LineComponent.builder().left("No tiene necklaces?:").leftColor(Color.WHITE).right(String.valueOf(this.plugin.isOutOfDodgy)).rightColor(Color.WHITE).build());
                            }

                            this.panelComponent.getChildren().add(LineComponent.builder().left("Aprox. próximo pouch open:").leftColor(Color.WHITE).right(String.valueOf(this.plugin.nextPouchOpen)).rightColor(Color.WHITE).build());
                            if (this.config.shadowVeil()) {
                                this.panelComponent.getChildren().add(LineComponent.builder().left("Shadow Veil requerimientos:").leftColor(Color.WHITE).right(String.valueOf(this.plugin.hasShadowVeilRequirements())).rightColor(Color.WHITE).build());
                                this.panelComponent.getChildren().add(LineComponent.builder().left("Shadow Veil cooldown:").leftColor(Color.WHITE).right(String.valueOf(this.plugin.shadowVeilCooldown)).rightColor(Color.WHITE).build());
                            }

                            if (this.config.useRedemption()) {
                                this.panelComponent.getChildren().add(LineComponent.builder().left("Out of Ancient brews?:").leftColor(Color.WHITE).right(String.valueOf(this.plugin.isOutOfAncientBrews)).rightColor(Color.WHITE).build());
                            }

                            this.panelComponent.getChildren().add(LineComponent.builder().left("Espera:").leftColor(Color.WHITE).right(String.valueOf(this.plugin.timeout)).rightColor(Color.WHITE).build());
                            break;
                        case DEFAULT:
                            this.panelComponent.getChildren().add(LineComponent.builder().left("Estado:").leftColor(Color.WHITE).right(this.infoStatus).rightColor(Color.WHITE).build());
                            this.panelComponent.getChildren().add(LineComponent.builder().left("Tiempo:").leftColor(Color.WHITE).right(DurationFormatUtils.formatDuration(duration.toMillis(), this.timeFormat)).rightColor(Color.WHITE).build());
                            if (this.config.dodgyNecklace()) {
                                this.panelComponent.getChildren().add(LineComponent.builder().left("Tiene dodgy?:").leftColor(Color.WHITE).right(String.valueOf(this.plugin.hasDodgy())).rightColor(Color.WHITE).build());
                                this.panelComponent.getChildren().add(LineComponent.builder().left("no tiene necklaces?:").leftColor(Color.WHITE).right(String.valueOf(this.plugin.isOutOfDodgy)).rightColor(Color.WHITE).build());
                            }

                            if (this.config.shadowVeil()) {
                                this.panelComponent.getChildren().add(LineComponent.builder().left("Shadow Veil cooldown:").leftColor(Color.WHITE).right(String.valueOf(this.plugin.shadowVeilCooldown)).rightColor(Color.WHITE).build());
                            }
                        case SIMPLE:
                    }
                } else {
                    this.panelComponent.getChildren().add(TitleComponent.builder().text("Plugin apagado").color(Color.RED).build());
                }
            }

            return super.render(graphics);
        } else {
            log.debug("Overlay conditions not met, not starting overlay");
            return null;
        }
    }
}
