package com.polyplugins.AutoMiner;

import com.example.EthanApiPlugin.Collections.BankInventory;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.PacketUtils.PacketUtilsPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.WidgetPackets;
import com.google.inject.Inject;
import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.*;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@PluginDependencies({@PluginDependency(EthanApiPlugin.class), @PluginDependency(PacketUtilsPlugin.class)})
@PluginDescriptor(
        name = "<html><font color=\"#ff4d00\">[GS]</font> Auto Miner</html>",
        enabledByDefault = false,
        description = "Mining plugin..",
        tags = {"Oz", "Ethan"}
)
public class AutoMinerPlugin extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(AutoMinerPlugin.class);
    protected static final Random random = new Random();
    Instant botTimer;
    boolean enablePlugin;
    @Inject
    Client client;
    @Inject
    PluginManager pluginManager;
    @Inject
    AutoMinerConfiguration config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ConfigManager configManager;
    @Inject
    private AutoMinerOverlay overlay;
    @Inject
    private ClientThread clientThread;
    @Inject
    private KeyManager keyManager;
    @Inject
    private ChatMessageManager chatMessageManager;
    AutoMinerState state;
    int timeout = 0;
    UISettings uiSetting;
    private final HotkeyListener pluginToggle = new HotkeyListener(() -> {
        return this.config.toggle();
    }) {
        public void hotkeyPressed() {
            AutoMinerPlugin.this.togglePlugin();
        }
    };

    public AutoMinerPlugin() {
    }

    @Provides
    AutoMinerConfiguration provideConfig(ConfigManager configManager) {
        return (AutoMinerConfiguration)configManager.getConfig(AutoMinerConfiguration.class);
    }

    protected void startUp() {
        this.timeout = 0;
        this.enablePlugin = false;
        this.botTimer = Instant.now();
        this.state = null;
        this.uiSetting = this.config.UISettings();
        this.keyManager.registerKeyListener(this.pluginToggle);
        this.overlayManager.add(this.overlay);
    }

    protected void shutDown() {
        this.resetVals();
    }

    private void resetVals() {
        this.overlayManager.remove(this.overlay);
        this.state = null;
        this.timeout = 0;
        this.enablePlugin = false;
        this.keyManager.unregisterKeyListener(this.pluginToggle);
        this.uiSetting = null;
        this.botTimer = null;
    }

    public AutoMinerState getState() {
        Player player = this.client.getLocalPlayer();
        if (player == null) {
            return AutoMinerState.UNHANDLED_STATE;
        } else if (this.timeout > 0) {
            return AutoMinerState.TIMEOUT;
        } else if (EthanApiPlugin.isMoving()) {
            return AutoMinerState.MOVING;
        } else if (this.isBankPinOpen()) {
            this.overlay.infoStatus = "Bank Pin";
            return AutoMinerState.BANK_PIN;
        } else if (this.client.getLocalPlayer().getAnimation() != -1) {
            return AutoMinerState.ANIMATING;
        } else {
            switch (this.config.Mode()) {
                case MINING_GUILD:
                    if (this.isBankOpen() && Inventory.full()) {
                        return AutoMinerState.HANDLE_BANK;
                    } else if (!Inventory.full() || this.isBankOpen() && BankInventory.search().empty()) {
                        return AutoMinerState.MINE;
                    } else if (Inventory.full()) {
                        return AutoMinerState.FIND_BANK;
                    }
                case POWERMINE:
                default:
                    return AutoMinerState.UNHANDLED_STATE;
            }
        }
    }

    @Subscribe
    private void onGameTick(GameTick tick) {
        if (this.enablePlugin) {
            if (this.client.getGameState() == GameState.LOGGED_IN) {
                this.uiSetting = this.config.UISettings();
                this.state = this.getState();
                switch (this.state) {
                    case TIMEOUT:
                        --this.timeout;
                        break;
                    case HANDLE_BANK:
                        this.depositAll();
                        break;
                    case MINE:
                        this.handleMine();
                        break;
                    case FIND_BANK:
                        this.openNearestBank();
                        break;
                    case UNHANDLED_STATE:
                        this.overlay.infoStatus = "ded";
                        break;
                    case MOVING:
                    case ANIMATING:
                    case BANK_PIN:
                    case IDLE:
                        this.timeout = this.tickDelay();
                }

            }
        }
    }

    private void handleMine() {
        Optional<TileObject> rock = TileObjects.search().withName(this.config.RockToMine().getRockName() + " rocks").withAction("Mine").nearestToPoint(this.config.RockToMine().getMinePoint());
        WorldPoint playerLocation = this.client.getLocalPlayer().getWorldLocation();
        if (this.config.RockToMine().getRockName() == "Iron" && playerLocation.distanceTo(this.config.RockToMine().getMinePoint()) != 0) {
            this.overlay.infoStatus = "Moving to mine spot";
            MousePackets.queueClickPacket();
            MovementPackets.queueMovement(this.config.RockToMine().getMinePoint());
        } else {
            if (rock.isPresent()) {
                this.overlay.infoStatus = "Mining " + this.config.RockToMine().getRockName();
                TileObjectInteraction.interact((TileObject)rock.get(), new String[]{"Mine"});
            } else {
                this.overlay.infoStatus = "Waiting for " + this.config.RockToMine().getRockName();
                this.timeout = this.tickDelay();
            }

        }
    }

    private void depositAll() {
        this.overlay.infoStatus = "Depositing inventory";
        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetActionPacket(1, 786474, -1, -1);
    }

    public void togglePlugin() {
        this.enablePlugin = !this.enablePlugin;
        if (this.client.getGameState() == GameState.LOGGED_IN) {
            if (!this.enablePlugin) {
                this.sendGameMessage("Auto Miner apagado.");
            } else {
                this.sendGameMessage("Auto Miner encendido.");
            }

        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (this.enablePlugin) {
            ChatMessageType chatMessageType = event.getType();
            if (chatMessageType == ChatMessageType.GAMEMESSAGE || chatMessageType == ChatMessageType.SPAM) {
                ;
            }
        }
    }

    private int tickDelay() {
        int tickLength = (int)this.randomDelay(this.config.tickDelayWeightedDistribution(), this.config.tickDelayMin(), this.config.tickDelayMax(), this.config.tickDelayDeviation(), this.config.tickDelayTarget());
        log.debug("tick delay for {} ticks", tickLength);
        return tickLength;
    }

    public long randomDelay(boolean weightedDistribution, int min, int max, int deviation, int target) {
        return weightedDistribution ? (long)this.clamp(-Math.log(Math.abs(random.nextGaussian())) * (double)deviation + (double)target, min, max) : (long)this.clamp((double)Math.round(random.nextGaussian() * (double)deviation + (double)target), min, max);
    }

    private double clamp(double val, int min, int max) {
        return Math.max((double)min, Math.min((double)max, val));
    }

    public void openNearestBank() {
        Optional<TileObject> bank = TileObjects.search().withName("Bank chest").nearestToPlayer();
        if (!this.isBankOpen()) {
            if (bank.isPresent()) {
                this.overlay.infoStatus = "Banking";
                TileObjectInteraction.interact((TileObject)bank.get(), new String[]{"Use"});
            } else {
                this.overlay.infoStatus = "Bank not found";
            }
        }

        this.timeout = this.tickDelay();
    }

    public int getRandomIntBetweenRange(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public void sendGameMessage(String message) {
        String chatMessage = (new ChatMessageBuilder()).append(ChatColorType.HIGHLIGHT).append(message).build();
        this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.CONSOLE).runeLiteFormattedMessage(chatMessage).build());
    }

    public boolean isBankOpen() {
        return this.client.getWidget(WidgetInfo.BANK_CONTAINER) != null;
    }

    public boolean isBankPinOpen() {
        return this.client.getWidget(WidgetInfo.BANK_PIN_CONTAINER) != null;
    }
}
