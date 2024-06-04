package com.polyplugins.AutoVardorvis;

import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.InteractionApi.InventoryInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.piggyplugins.PiggyUtils.API.PrayerUtil;
import com.piggyplugins.PiggyUtils.API.SpellUtil;
import com.polyplugins.AutoVardorvis.state.StateHandler;
import com.polyplugins.AutoVardorvis.state.StateHandler.State;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Prayer;
import net.runelite.api.Projectile;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(
        name = "<html><font color=\"#00D8FF\">[Auto Vardovis]</font></font></html>",
        description = "Automated vardorvis killer"
)
public class AutoVardorvisPlugin extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(AutoVardorvisPlugin.class);
    private static final int RANGE_PROJECTILE = 330432512 >>> 4769 >>> (793 << 14116);
    private Projectile rangeProjectile;
    private int rangeTicks = 0;
    private int rangeCooldown = 0;
    int totalKills = 0;
    long startTime = System.currentTimeMillis();
    long elapsedTime = (long)578813952 >>> 13463 >>> (21390 >>> (char)13505);
    boolean running = false;
    int tickDelay = 0;
    StateHandler.State botState = null;
    private boolean drankSuperCombat = false;
    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AutoVardorvisOverlay overlay;
    @Inject
    private AutoVardorvisConfig config;

    public AutoVardorvisPlugin() {
    }

    @Provides
    private AutoVardorvisConfig getConfig(ConfigManager configManager) {
        return (AutoVardorvisConfig)configManager.getConfig(AutoVardorvisConfig.class);
    }

    protected void startUp() throws Exception {
        this.startTime = System.currentTimeMillis();
        this.overlayManager.add(this.overlay);
        this.running = this.client.getGameState() == GameState.LOGGED_IN;
        this.botState = State.GO_TO_BANK;
    }

    protected void shutDown() throws Exception {
        this.totalKills = 0;
        this.overlayManager.remove(this.overlay);
        this.drankSuperCombat = false;
        this.running = false;
        this.botState = null;
    }

    private void handleBotState(StateHandler.State passedBotState, int passedTickDelay) {
        if (passedBotState == null) {
            System.out.println("Null state...");
        } else {
            MainClassContext context = new MainClassContext(this.client, this.config, passedBotState, passedTickDelay, this.drankSuperCombat);
            StateHandler stateHandler = new StateHandler();
            stateHandler.handleState(this.botState, context);
            this.tickDelay = context.getContextTickDelay();
            this.botState = context.getContextBotState();
        }
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        long currentTime = System.currentTimeMillis();
        this.elapsedTime = currentTime - this.startTime;
        this.overlay.updateKillsPerHour();
        if (this.running) {
            if (this.tickDelay > 0) {
                --this.tickDelay;
                return;
            }

            if (EthanApiPlugin.getClient().getVarpValue((5530368 >>> 11944) + (-11089 ^ (char)30949)) == 0) {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetActionPacket(1, ("098l".hashCode() ^ -984920425) + -1688276549 - (806250757 << 7521), -1 << 3680 << (char)3161 + -2489, -1 >>> 3808 >>> 6686 + -126);
            }

            this.handleBotState(this.botState, this.tickDelay);
            if (this.isInFight()) {
                this.autoPray();
            }
        }

    }

    @Subscribe
    private void onVarbitChanged(VarbitChanged event) {
        if (event.getVarbitId() == 1798747455 - 1798713542 + (-17891 - 2359)) {
            this.drankSuperCombat = true;
            if (event.getValue() <= 20 << 2514 >>> -9228 - -11167) {
                Inventory.search().nameContains("Divine super combat").first().ifPresent((potion) -> {
                    InventoryInteraction.useItem(potion, new String[]{"Drink"});
                });
            }
        }

        if (event.getVarbitId() == 24826 << 14113 >>> (6545 << 12737) && event.getValue() == 0) {
            Widget thrallSpellWidget = SpellUtil.getSpellWidget(this.client, "Resurrect Greater Ghost");
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(thrallSpellWidget, new String[]{"Cast"});
        }

    }

    @Subscribe
    private void onProjectileMoved(ProjectileMoved event) {
        if (this.client.getGameState() == GameState.LOGGED_IN) {
            Projectile projectile = event.getProjectile();
            if (projectile.getId() == (23330816 >>> 15307 ^ 19122 >>> 14817) && this.rangeProjectile == null && this.rangeCooldown == 0) {
                this.rangeTicks = 262144 >>> 14177 >>> 7209 + -2618;
                this.rangeProjectile = projectile;
            }

        }
    }

    private void autoPray() {
        if (this.rangeTicks > 0) {
            --this.rangeTicks;
            if (this.rangeTicks == 0) {
                this.rangeCooldown = (-582697670 ^ 491044154) >>> 1201 + 5197;
            }
        }

        if (this.rangeTicks == 0) {
            this.rangeProjectile = null;
            if (this.rangeCooldown > 0) {
                --this.rangeCooldown;
            }
        }

        this.handleRangeFirstGameTick();
    }

    private void handleRangeFirstGameTick() {
        if (this.rangeTicks > 0) {
            if (!PrayerUtil.isPrayerActive(Prayer.PROTECT_FROM_MISSILES)) {
                PrayerUtil.togglePrayer(Prayer.PROTECT_FROM_MISSILES);
            }
        } else if (!PrayerUtil.isPrayerActive(Prayer.PROTECT_FROM_MELEE)) {
            PrayerUtil.togglePrayer(Prayer.PROTECT_FROM_MELEE);
        }

    }

    @Subscribe
    private void onChatMessage(ChatMessage e) {
        if (e.getMessage().contains("Tu Vardorvis kill count es:")) {
            ++this.totalKills;
        }

    }

    private boolean isInFight() {
        return this.client.isInInstancedRegion() && NPCs.search().nameContains("Vardorvis").nearestToPlayer().isPresent();
    }

    public static class MainClassContext {
        private Client client;
        private AutoVardorvisConfig config;
        private StateHandler.State contextBotState;
        private int contextTickDelay;
        private boolean drankSuperCombat;

        public MainClassContext(Client client, AutoVardorvisConfig config, StateHandler.State passedBotState, int passedTickDelay, boolean drankSuperCombat) {
            this.client = client;
            this.config = config;
            this.contextBotState = passedBotState;
            this.contextTickDelay = passedTickDelay;
            this.drankSuperCombat = drankSuperCombat;
        }

        public Client getClient() {
            return this.client;
        }

        public AutoVardorvisConfig getConfig() {
            return this.config;
        }

        public StateHandler.State getContextBotState() {
            return this.contextBotState;
        }

        public int getContextTickDelay() {
            return this.contextTickDelay;
        }

        public boolean isDrankSuperCombat() {
            return this.drankSuperCombat;
        }

        public void setClient(Client client) {
            this.client = client;
        }

        public void setConfig(AutoVardorvisConfig config) {
            this.config = config;
        }

        public void setContextBotState(StateHandler.State contextBotState) {
            this.contextBotState = contextBotState;
        }

        public void setContextTickDelay(int contextTickDelay) {
            this.contextTickDelay = contextTickDelay;
        }

        public void setDrankSuperCombat(boolean drankSuperCombat) {
            this.drankSuperCombat = drankSuperCombat;
        }
    }
}
