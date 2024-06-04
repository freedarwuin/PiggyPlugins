package com.polyplugins.AutoVardorvis.state.botStates;

import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.polyplugins.AutoVardorvis.AutoVardorvisPlugin;
import com.polyplugins.AutoVardorvis.state.StateHandler.State;
import java.util.Optional;
import net.runelite.api.Client;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;

public class GoToVardorvisState {
    AutoVardorvisPlugin.MainClassContext context;

    public GoToVardorvisState() {
    }

    public void execute(AutoVardorvisPlugin.MainClassContext context) {
        this.context = context;
        boolean inStrangleWood = TileObjects.search().withId(194892 << 8102 >>> (-17146 ^ "035t".hashCode() ^ -1497972)).first().isPresent();
        Optional<TileObject> vardorvisRock = TileObjects.search().withId((Integer.parseInt("-5ca9b8b", 23) >>> 4704) - (-820378983 << 4224)).first();
        Optional<TileObject> tunnel1 = TileObjects.search().withId((971978669 ^ "059w".hashCode() ^ 972630503) << (26176 >>> (char)7521)).first();
        Optional<TileObject> tunnel2 = TileObjects.search().withId((24775 ^ 16370) << (513 << Integer.parseInt("6lj", 27))).first();
        WorldPoint playerLocation = context.getClient().getLocalPlayer().getWorldLocation();
        if (this.isInFight(context.getClient())) {
            context.setContextBotState(State.FIGHTING);
        }

        if (tunnel1.isPresent() && ((TileObject)tunnel1.get()).getWorldLocation().distanceTo(playerLocation) <= (-507778421 ^ -507942261) >>> (2767 << 15328)) {
            tunnel1.ifPresent((t1) -> {
                TileObjectInteraction.interact(t1, new String[]{"Enter"});
            });
        } else {
            tunnel2.ifPresent((t2) -> {
                WorldPoint tunnel2Location = new WorldPoint(((TileObject)tunnel2.get()).getWorldLocation().getX() - ((1938788439 ^ 1938575447) >>> (-18003 ^ -29952)), ((TileObject)tunnel2.get()).getWorldLocation().getY() - (-1610612736 >>> 2081 >>> ((char)7374 << 2721)), 0);
                if (inStrangleWood && !this.isMoving() || playerLocation.getX() == tunnel2Location.getX() && playerLocation.getY() == tunnel2Location.getY()) {
                    if (tunnel1.isPresent() && ((TileObject)tunnel1.get()).getWorldLocation().distanceTo(playerLocation) <= 73759458 - 56982242 >>> -8647 + 12957) {
                        tunnel1.ifPresent((t1) -> {
                            TileObjectInteraction.interact(t1, new String[]{"Enter"});
                        });
                        return;
                    }

                    if (((TileObject)tunnel2.get()).getWorldLocation().distanceTo(playerLocation) <= 1342177280 >>> 1881 >>> -1201 + 3602) {
                        MousePackets.queueClickPacket();
                        MovementPackets.queueMovement(tunnel2Location);
                    } else if (inStrangleWood && !this.isMoving()) {
                        vardorvisRock.ifPresentOrElse((rock) -> {
                            TileObjectInteraction.interact(rock, new String[]{"Climb-over"});
                        }, () -> {
                            tunnel1.ifPresent((t1) -> {
                                TileObjectInteraction.interact(t1, new String[]{"Enter"});
                            });
                        });
                    }
                }

            });
        }
    }

    private boolean isMoving() {
        return EthanApiPlugin.isMoving() || this.context.getClient().getLocalPlayer().getAnimation() != -1 << 6112 << 14184 + -10568;
    }

    private boolean isInFight(Client client) {
        return client.isInInstancedRegion() && NPCs.search().nameContains("Vardorvis").nearestToPlayer().isPresent();
    }
}