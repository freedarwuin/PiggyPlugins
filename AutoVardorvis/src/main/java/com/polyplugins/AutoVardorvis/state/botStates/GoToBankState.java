package com.polyplugins.AutoVardorvis.state.botStates;

import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.InteractionApi.InventoryInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.piggyplugins.PiggyUtils.API.PrayerUtil;
import com.polyplugins.AutoVardorvis.AutoVardorvisPlugin;
import com.polyplugins.AutoVardorvis.state.StateHandler.State;
import java.util.Optional;
import net.runelite.api.Client;
import net.runelite.api.Prayer;
import net.runelite.api.Skill;

public class GoToBankState {
    public Client client;

    public GoToBankState() {
    }

    public void execute(AutoVardorvisPlugin.MainClassContext context) {
        this.client = context.getClient();
        if (PrayerUtil.isPrayerActive(Prayer.PIETY)) {
            PrayerUtil.togglePrayer(Prayer.PIETY);
        }

        if (PrayerUtil.isPrayerActive(Prayer.PROTECT_FROM_MELEE)) {
            PrayerUtil.togglePrayer(Prayer.PROTECT_FROM_MELEE);
        }

        if (NPCs.search().nameContains("Jack").nearestToPlayer().isPresent()) {
            context.setContextBotState(State.BANKING);
        } else if (!this.inHouse()) {
            this.teleToHouse();
        } else {
            Optional portal;
            if (this.client.getBoostedSkillLevel(Skill.HITPOINTS) >= (-851678707 ^ -751015411) >>> (716701696 >>> 10323) && this.client.getBoostedSkillLevel(Skill.PRAYER) >= 1966080 >>> 2609 << (-4267 ^ -12169)) {
                if (this.inHouse()) {
                    portal = TileObjects.search().nameContains("Portal Nexus").first();
                    portal.ifPresent((portalObject) -> {
                        TileObjectInteraction.interact(portalObject, new String[]{"Lunar Isle"});
                    });
                }

            } else {
                portal = TileObjects.search().nameContains("pool of").withAction("Drink").first();
                portal.ifPresent((poolObject) -> {
                    TileObjectInteraction.interact(poolObject, new String[]{"Drink"});
                });
                context.setContextTickDelay(98304 >>> 8975 << (238551040 >>> 3246));
            }
        }
    }

    private void teleToHouse() {
        InventoryInteraction.useItem("Teleport to house", new String[]{"Break"});
    }

    private boolean inHouse() {
        return !TileObjects.search().nameContains("pool of").result().isEmpty();
    }
}
