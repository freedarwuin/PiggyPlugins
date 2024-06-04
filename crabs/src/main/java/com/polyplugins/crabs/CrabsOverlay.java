package com.polyplugins.crabs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.List;
import java.util.Objects;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

public class CrabsOverlay extends Overlay {
    @Inject
    private Client client;
    @Inject
    private CrabsPlugin plugin;
    @Inject
    private CrabsConfig config;

    @Inject
    CrabsOverlay(Client clt, CrabsPlugin plg, CrabsConfig cfig) {
        this.plugin = plg;
        this.client = clt;
        this.config = cfig;
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setPriority(OverlayPriority.MED);
        this.setLayer(OverlayLayer.ABOVE_SCENE);
    }

    public Dimension render(Graphics2D graphics) {
        for(int i = 0; i < TilePelea.values().length; ++i) {
            List<WorldPoint> tiles = TilePelea.values()[i].getPuntos();

            for(int j = 0; j < tiles.size(); ++j) {
                if (((WorldPoint)tiles.get(j)).isInScene(this.client)) {
                    int var10000 = ((WorldPoint)tiles.get(j)).distanceTo(this.client.getLocalPlayer().getWorldLocation());
                    Objects.requireNonNull(this.plugin);
                    if (var10000 <= 14) {
                        LocalPoint lp = LocalPoint.fromWorld(this.client, (WorldPoint)tiles.get(j));
                        if (lp != null && lp.isInScene()) {
                            Polygon poly = Perspective.getCanvasTilePoly(this.client, lp);
                            OverlayUtil.renderPolygon(graphics, poly, Color.cyan);
                            graphics.setFont(new Font("Arial", 1, 11));
                            Point punto = Perspective.getCanvasTextLocation(this.client, graphics, lp, TilePelea.values()[i].toString(), 0);
                            OverlayUtil.renderTextLocation(graphics, punto, TilePelea.values()[i].toString(), Color.lightGray);
                        }
                    }
                }
            }
        }

        return null;
    }
}

