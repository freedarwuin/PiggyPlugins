package com.polyplugins.AutoCakeThiever;

import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.EthanApiPlugin.Collections.Bank;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.InteractionApi.InventoryInteraction;
import com.example.InteractionApi.NPCInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.example.PathingTesting.PathingTesting;
import com.google.inject.Inject;
import com.google.inject.Provides;
import java.time.Instant;
import java.util.Optional;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.Skill;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
        name = "<html><font color=86C43F>[B]</font> Cake Thiever </html>",
        description = "",
        enabledByDefault = false,
        tags = {"bn", "plugins"}
)
public class AutoCakeThiever extends Plugin {
    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private AutoCakeThieverOverlay overlay;
    @Inject
    AutoCakeThieverConfig config;
    private Instant startTime;
    public int timeout;
    public State currentState;
    boolean startup;
    boolean shouldThieve;
    WorldPoint BankTile;
    WorldPoint StallTile;
    private static final int STALL_WITH_CAKE = 5865 << 1607 >>> (3507 << 12897);
    private static final int EMPTY_STALL = 317 << 9632 << (696320000 >>> 11088);

    public AutoCakeThiever() {
        this.currentState = State.STARTING;
        this.shouldThieve = false;
        this.BankTile = new WorldPoint(-6188 - 5846 ^ -28043 ^ 18644, 420224 >>> 4481 >>> (2563 << 11073), 0);
        this.StallTile = new WorldPoint((-21267 << 2112) + (784334848 >>> 5007), -343564303 + 343987983 >>> (1543 << (char)1248), 0);
    }

    protected void startUp() throws Exception {
        this.overlayManager.add(this.overlay);
        this.startTime = Instant.now();
        this.startup = true;
        this.currentState = State.IDLE;
    }

    protected void shutDown() throws Exception {
        this.overlayManager.remove(this.overlay);
        this.startTime = null;
        this.timeout = 0;
        this.startup = false;
        this.currentState = State.IDLE;
    }

    public State getCurrentState() {
        return this.currentState;
    }

    public Instant getStartTime() {
        return this.startTime;
    }

    @Provides
    AutoCakeThieverConfig provideConfig(ConfigManager configManager) {
        return (AutoCakeThieverConfig)configManager.getConfig(AutoCakeThieverConfig.class);
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (this.client.getGameState() == GameState.LOGGED_IN) {
            this.handleAutoEat();
            if (this.timeout > 0) {
                --this.timeout;
            } else {
                this.handleState();
                switch (SyntheticClass_1.$SwitchMap$net$runelite$client$plugins$AutoCakeThiever$State[this.currentState.ordinal()] ^ (2076241760 >>> ("052s".hashCode() ^ 1490819)) + (-593028634 >>> 11905)) {
                    case 1915851878:
                        this.closeBank();
                        break;
                    case 1915851879:
                        this.Idle();
                        break;
                    case 1915851880:
                        this.openBank();
                        break;
                    case 1915851881:
                        this.depositCakes();
                        break;
                    case 1915851882:
                        this.dropBreadAndSlices();
                        break;
                    case 1915851883:
                        this.walkToBank();
                        break;
                    case 1915851884:
                        this.walkToStall();
                        break;
                    case 1915851885:
                        this.thieveCakes();
                        break;
                    case 1915851887:
                        this.handleState();
                }

            }
        }
    }

    private void handleState() {
        if (this.startup) {
            this.currentState = State.STARTING;
            this.startup = false;
        }

        if (this.isInArdougneMarketArea() && !this.isAtStallTile()) {
            this.currentState = State.WALKING_TO_STALL;
        }

        if (!this.isInventoryFull() && this.isAtStallTile() && this.shouldThieve) {
            this.currentState = State.THIEVING_CAKE;
        }

        if (this.shouldDropItems()) {
            this.currentState = State.DROPPING_REST;
        }

        if (Inventory.full()) {
            this.currentState = State.WALKING_TO_BANK;
        }

        if (this.isAtBank() && this.isInventoryFull()) {
            this.currentState = State.OPENING_BANK;
        }

        if (Bank.isOpen() && this.isInventoryFull()) {
            this.currentState = State.DEPOSIT_CAKES;
        }

        if (this.isAtBank() && Bank.isOpen() && this.inventoryIsEmpty()) {
            this.currentState = State.CLOSE_BANK;
        }

    }

    public void thieveCakes() {
        TileObjects.search().withName("Baker's stall").withId((11541 << 12417) - ((char)1419 << (char)12195)).withinDistance(-67179913 + 402724233 >>> -14425 + 28691).nearestToPlayer().ifPresent((stall) -> {
            TileObjectInteraction.interact(stall, new String[]{"Steal-from"});
            this.timeout += 4194304 >>> 7339 >>> (1685 << 545);
        });
    }

    public void openBank() {
        Optional<TileObject> chest = TileObjects.search().withName("Bank chest").nearestToPlayer();
        Optional<NPC> banker = NPCs.search().withAction("Bank").nearestToPlayer();
        Optional<TileObject> booth = TileObjects.search().withAction("Bank").nearestToPlayer();
        if (chest.isPresent()) {
            TileObjectInteraction.interact((TileObject)chest.get(), new String[]{"Use"});
        } else if (booth.isPresent()) {
            TileObjectInteraction.interact((TileObject)booth.get(), new String[]{"Bank"});
        } else if (banker.isPresent()) {
            NPCInteraction.interact((NPC)banker.get(), new String[]{"Bank"});
        } else {
            if (!chest.isPresent() && !booth.isPresent() && !banker.isPresent()) {
                this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "No bank nearby, please relocate.", (String)null);
                EthanApiPlugin.stopPlugin(this);
            }

        }
    }

    public void depositCakes() {
        if (Bank.isOpen()) {
            this.timeout += 8 << 11876 >>> -7676 - -19618;
            Widget depositInventory = this.client.getWidget(WidgetInfo.BANK_DEPOSIT_INVENTORY);
            if (depositInventory != null) {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetAction(depositInventory, new String[]{"Deposit inventory"});
            }
        }

    }

    public void closeBank() {
        MousePackets.queueClickPacket();
        this.client.runScript(new Object[]{29 << 15648 << 13937 - 4561});
    }

    public void handleAutoEat() {
        int currentHealth = this.client.getBoostedSkillLevel(Skill.HITPOINTS);
        int eatThreshold = this.config.getHealthThreshold();
        String foodName = this.config.getFoodName();
        if (currentHealth < eatThreshold) {
            Optional<Widget> foodItem = Inventory.search().matchesWildCardNoCase(foodName).first();
            if (foodItem.isPresent()) {
                InventoryInteraction.useItem((Widget)foodItem.get(), new String[]{"Eat"});
            }
        }

    }

    public void dropBreadAndSlices() {
        Optional<Widget> bread = Inventory.search().withName("Bread").first();
        Optional<Widget> chocolateSlice = Inventory.search().withName("Chocolate slice").first();
        Optional<Widget> sliceOfCake = Inventory.search().withName("Slice of cake").first();
        Optional<Widget> twoThirdsCake = Inventory.search().withName("2/3 cake").first();
        bread.ifPresent((b) -> {
            InventoryInteraction.useItem(b, new String[]{"Drop"});
        });
        chocolateSlice.ifPresent((c) -> {
            InventoryInteraction.useItem(c, new String[]{"Drop"});
        });
        sliceOfCake.ifPresent((s) -> {
            InventoryInteraction.useItem(s, new String[]{"Drop"});
        });
        twoThirdsCake.ifPresent((t) -> {
            InventoryInteraction.useItem(t, new String[]{"Drop"});
        });
    }

    public void walkToBank() {
        if (!EthanApiPlugin.isMoving()) {
            WorldPoint bankLocation = new WorldPoint(-1832046199 + -1766928777 >>> 19540 - 11266, (24261 ^ 21014) << (-16625 ^ -28913), 0);
            PathingTesting.walkTo(bankLocation);
        }

    }

    public void walkToStall() {
        if (!EthanApiPlugin.isMoving()) {
            WorldPoint stall = new WorldPoint(349831168 >>> 3300 >>> (-8878 ^ -6465), (4134 ^ 2615) - (-14371 - -17734), 0);
            PathingTesting.walkTo(stall);
        }

    }

    public boolean hasBreadAndSlices() {
        boolean hasBread = Inventory.search().withName("Bread").first().isPresent();
        boolean hasChocolateSlice = Inventory.search().withName("Chocolate slice").first().isPresent();
        boolean hasSliceOfCake = Inventory.search().withName("Slice of cake").first().isPresent();
        boolean hasTwoThirdsCake = Inventory.search().withName("2/3 cake").first().isPresent();
        return hasBread || hasChocolateSlice || hasSliceOfCake || hasTwoThirdsCake;
    }

    public void Idle() {
    }

    public boolean isInventoryFull() {
        if (Inventory.full()) {
            this.shouldThieve = false;
            return true;
        } else {
            return false;
        }
    }

    public boolean isAtStallTile() {
        if (this.client.getLocalPlayer().getWorldLocation().equals(this.StallTile)) {
            this.shouldThieve = true;
        }

        return this.client.getLocalPlayer().getWorldLocation().equals(this.StallTile);
    }

    public boolean isAtBank() {
        return this.client.getLocalPlayer().getWorldLocation().distanceTo(this.BankTile) <= 48 >>> 5668 << (26372 >>> 8546);
    }

    public boolean isInArdougneMarketArea() {
        return this.client.getLocalPlayer().getWorldLocation().getRegionID() == '눡' + -26071 + -11765 + 2782;
    }

    public boolean inventoryIsEmpty() {
        return Inventory.getEmptySlots() == 702339460 + -701421956 >>> ((char)3918 ^ 14945);
    }

    public boolean shouldDropItems() {
        return this.config.dropBreadAndSlices() && this.hasBreadAndSlices();
    }

    public boolean shouldBank() {
        return this.isInventoryFull() && (!this.config.dropBreadAndSlices() || this.inventoryHasOnlyCakes());
    }

    public boolean inventoryHasOnlyCakes() {
        return Inventory.search().withId(-9939 + 9631 ^ 14948 + -16565).result().size() == -957176008 + 420305096 >>> 21006 + -12595;
    }
}
