package com.example.AutoThiever;

import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.*;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.InteractionApi.*;
import com.example.PacketUtils.PacketUtilsPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.WidgetPackets;
import com.google.inject.Inject;
import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
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
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@PluginDependencies({@PluginDependency(EthanApiPlugin.class), @PluginDependency(PacketUtilsPlugin.class)})
@PluginDescriptor(
        name = "<html><font color=\"#ff4d00\">[GS]</font> Auto Thiever</html>",
        enabledByDefault = false,
        description = "Auto Thiev por ti: Elf, Master farmer, Ardy Knights y otros.",
        tags = {"EL Guason"}
)
public class AutoThieverPlugin extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(AutoThieverPlugin.class);
    protected static final Random random = new Random();
    Instant botTimer;
    boolean enablePlugin;
    @Inject
    Client client;
    @Inject
    PluginManager pluginManager;
    @Inject
    AutoThieverConfiguration config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ConfigManager configManager;
    @Inject
    private AutoThieverOverlay overlay;
    @Inject
    private ClientThread clientThread;
    @Inject
    private KeyManager keyManager;
    @Inject
    private ChatMessageManager chatMessageManager;
    AutoThieverState state;
    int timeout = 0;
    int pouches = 0;
    int hitpoints = 0;
    int prayer = 0;
    double successRate = 0.0;
    double successfulThieves = 0.0;
    double failedThieves = 0.0;
    UISettings uiSetting;
    private int nextRunEnergy;
    public int nextPouchOpen;
    boolean isOutOfDodgy = false;
    boolean isOutOfAncientBrews = false;
    public int shadowVeilCooldown = 0;
    boolean isShadowVeilActive = false;
    String npcToThieve;
    WorldArea LINDIR_HOUSE = new WorldArea(new WorldPoint(3242, 6069, 0), 4, 3);
    List<Integer> ITEMS_TO_DEPOSIT = List.of(560, 1993, 561, 569, 1601, 23959, 444, 1935);
    private final HotkeyListener pluginToggle = new HotkeyListener(() -> {
        return this.config.toggle();
    }) {
        public void hotkeyPressed() {
            AutoThieverPlugin.this.togglePlugin();
        }
    };

    public AutoThieverPlugin() {
    }

    @Provides
    AutoThieverConfiguration provideConfig(ConfigManager configManager) {
        return (AutoThieverConfiguration)configManager.getConfig(AutoThieverConfiguration.class);
    }

    protected void startUp() {
        this.timeout = 0;
        this.nextRunEnergy = 0;
        this.nextPouchOpen = 0;
        this.shadowVeilCooldown = 0;
        this.successRate = 0.0;
        this.successfulThieves = 0.0;
        this.failedThieves = 0.0;
        this.isOutOfDodgy = false;
        this.isOutOfAncientBrews = false;
        this.isShadowVeilActive = false;
        this.npcToThieve = null;
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
        this.nextRunEnergy = 0;
        this.nextPouchOpen = 0;
        this.shadowVeilCooldown = 0;
        this.successRate = 0.0;
        this.successfulThieves = 0.0;
        this.failedThieves = 0.0;
        this.npcToThieve = null;
        this.isShadowVeilActive = false;
        this.isOutOfDodgy = false;
        this.isOutOfAncientBrews = false;
        this.enablePlugin = false;
        this.keyManager.unregisterKeyListener(this.pluginToggle);
        this.uiSetting = null;
        this.botTimer = null;
    }

    public AutoThieverState getState() {
        Player player = this.client.getLocalPlayer();
        if (player == null) {
            return AutoThieverState.UNHANDLED_STATE;
        } else {
            if (this.shadowVeilCooldown > 0) {
                --this.shadowVeilCooldown;
            } else {
                this.isShadowVeilActive = false;
            }

            if (this.timeout > 0) {
                return AutoThieverState.TIMEOUT;
            } else if (this.isBankPinOpen()) {
                this.overlay.infoStatus = "Bank Pin";
                return AutoThieverState.IDLE;
            } else if (this.client.getLocalPlayer().getGraphic() == 245) {
                return AutoThieverState.STUNNED;
            } else {
                this.hitpoints = this.client.getBoostedSkillLevel(Skill.HITPOINTS);
                this.prayer = this.client.getBoostedSkillLevel(Skill.PRAYER);
                if ("Otro" == this.config.NPCToThieve().getNpcName()) {
                    this.npcToThieve = this.config.CustomNPCName();
                } else {
                    this.npcToThieve = this.config.NPCToThieve().getNpcName();
                }

                if (this.successfulThieves + this.failedThieves != 0.0) {
                    this.successRate = this.successfulThieves / (this.successfulThieves + this.failedThieves) * 100.0;
                }

                this.handlePouch(this.config.MinPouches(), this.config.MaxPouches());
                if (this.isBankOpen() && this.hasItemsToDeposit()) {
                    return AutoThieverState.DEPOSIT;
                } else if (this.shouldWithdrawFood()) {
                    return AutoThieverState.WITHDRAW_FOOD;
                } else if (this.dodgyCheck()) {
                    return AutoThieverState.WITHDRAW_DODGY;
                } else if (this.useAncientBrewCheck()) {
                    return AutoThieverState.WITHDRAW_ANCIENT_BREW;
                } else if (this.shouldEatFood()) {
                    return AutoThieverState.HANDLE_FOOD;
                } else if (this.shouldFindBank()) {
                    return AutoThieverState.FIND_BANK;
                } else if (this.config.dodgyNecklace() && !this.isWearingDodgyNecklace() && this.hasDodgy()) {
                    return AutoThieverState.HANDLE_DODGY_NECKLACE;
                } else if (this.shadowVeilCheck()) {
                    return AutoThieverState.HANDLE_SHADOW_VEIL;
                } else if (this.handleAncientBrewCheck()) {
                    return AutoThieverState.HANDLE_ANCIENT_BREW;
                } else {
                    return this.shouldThieve() ? AutoThieverState.THIEVE : AutoThieverState.UNHANDLED_STATE;
                }
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
                    case HANDLE_FOOD:
                        this.handleFood();
                        break;
                    case FIND_BANK:
                        this.openNearestBank();
                        break;
                    case WITHDRAW_FOOD:
                        this.handleWithdrawFood();
                        break;
                    case WITHDRAW_DODGY:
                        this.handleWithdrawDodgy();
                        break;
                    case WITHDRAW_ANCIENT_BREW:
                        this.handleWithdrawAncientBrews();
                        break;
                    case HANDLE_DODGY_NECKLACE:
                        this.handleDodgyNecklace();
                        break;
                    case HANDLE_SHADOW_VEIL:
                        this.handleShadowVeil();
                        break;
                    case DEPOSIT:
                        this.handleDeposit();
                        break;
                    case THIEVE:
                        this.handleThieve();
                        break;
                    case HANDLE_ANCIENT_BREW:
                        this.handleAncientBrew();
                        break;
                    case UNHANDLED_STATE:
                        this.overlay.infoStatus = "Dead";
                        break;
                    case STUNNED:
                        this.overlay.infoStatus = "Stunned";
                        this.timeout = 7;
                        break;
                    case MOVING:
                    case BANK_PIN:
                    case IDLE:
                        this.handleRun(30, 20);
                        this.timeout = this.tickDelay();
                }

            }
        }
    }

    private void handleShadowVeil() {
        this.overlay.infoStatus = "Casting Shadow veil";
        Widget shadow_veil = this.client.getWidget(14287025);
        int magicLevel = this.client.getRealSkillLevel(Skill.MAGIC);
        int animationID = this.client.getLocalPlayer().getAnimation();
        if (animationID == -1) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(shadow_veil, new String[]{"Cast"});
        }

        if (animationID == 8979) {
            this.shadowVeilCooldown = (int)Math.ceil((double)magicLevel * 0.6 * 1.7) + this.tickDelay();
            this.isShadowVeilActive = true;
        }

    }

    public boolean hasShadowVeilRequirements() {
        int magicLevel = this.client.getBoostedSkillLevel(Skill.MAGIC);
        Optional<Widget> earth_rune = Inventory.search().matchesWildCardNoCase("*Earth rune*").first();
        Optional<Widget> cosmic_rune = Inventory.search().matchesWildCardNoCase("*Cosmic rune*").first();
        Optional<Widget> fire_rune = Inventory.search().matchesWildCardNoCase("*Fire rune*").first();
        Optional<EquipmentItemWidget> lava_staff = Equipment.search().matchesWildCardNoCase("*lava*").first();
        Optional<EquipmentItemWidget> fire_staff = Equipment.search().matchesWildCardNoCase("*fire*").first();
        Optional<EquipmentItemWidget> earth_staff = Equipment.search().matchesWildCardNoCase("*earth*").first();
        boolean hasRequiredRunes = (lava_staff.isPresent() || (earth_rune.isPresent() && ((Widget)earth_rune.get()).getItemQuantity() >= 5 || earth_staff.isPresent()) && fire_rune.isPresent() && ((Widget)fire_rune.get()).getItemQuantity() >= 5 || fire_staff.isPresent()) && cosmic_rune.isPresent() && ((Widget)cosmic_rune.get()).getItemQuantity() >= 5 || Inventory.search().matchesWildCardNoCase("Rune pouch").first().isPresent();
        return magicLevel >= 47 && hasRequiredRunes;
    }

    private boolean hasItemsToDeposit() {
        return BankInventory.search().idInList(this.ITEMS_TO_DEPOSIT).first().isPresent() || BankInventory.search().nameContains("seed").first().isPresent();
    }

    private void handleWithdrawDodgy() {
        this.overlay.infoStatus = "Withdrawing dodgy necklace";
        Optional<Widget> dodgy = Bank.search().withName("Dodgy necklace").first();
        if (dodgy.isPresent()) {
            BankInteraction.withdrawX((Widget)dodgy.get(), this.config.dodgyAmount() - BankInventory.search().matchesWildCardNoCase("*Dodgy necklace*").result().size());
            this.timeout = 1;
        } else {
            if (!this.hasDodgy()) {
                this.sendGameMessage("Out of dodgy necklaces.");
                this.isOutOfDodgy = true;
                this.timeout = this.tickDelay();
            }

        }
    }

    private void handleWithdrawAncientBrews() {
        this.overlay.infoStatus = "Withdrawing Ancient brews";
        Optional<Widget> brew = Bank.search().matchesWildCardNoCase("*Ancient brew*").first();
        if (brew.isPresent()) {
            BankInteraction.withdrawX((Widget)brew.get(), this.config.AncientBrewAmount() - BankInventory.search().matchesWildCardNoCase("*Ancient brew*").result().size());
            this.timeout = 1;
        } else {
            if (!this.hasAncientBrew()) {
                this.sendGameMessage("Out of Ancient brews");
                this.isOutOfAncientBrews = true;
                this.timeout = this.tickDelay();
            }

        }
    }

    private void handleDeposit() {
        if (this.npcToThieve == "Master Farmer") {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetActionPacket(1, 786474, -1, -1);
        } else {
            BankInventory.search().idInList(this.ITEMS_TO_DEPOSIT).first().ifPresent((x) -> {
                BankInventoryInteraction.useItem(x, new String[]{"Deposit-All"});
            });
        }
    }

    private void handleThieve() {
        this.overlay.infoStatus = "Thieving";
        if (this.npcToThieve == "Lindir") {
            this.handleLindir();
        } else {
            NPCs.search().withName(this.npcToThieve).nearestToPlayer().ifPresent((x) -> {
                NPCInteraction.interact(x, new String[]{"Pickpocket"});
            });
        }
    }

    private void handleLindir() {
        Optional closed_door;
        if (this.isStandingInLindirHouse()) {
            closed_door = TileObjects.search().withAction("Close").atLocation(3243, 6071, 0).first();
            if (closed_door.isPresent() && this.client.getPlayers().stream().noneMatch((x) -> {
                return x.getWorldLocation().isInArea(new WorldArea[]{new WorldArea(new WorldPoint(3242, 6069, 0), 4, 4)});
            })) {
                TileObjectInteraction.interact((TileObject)closed_door.get(), new String[]{"Close"});
            } else {
                NPCs.search().withName(this.npcToThieve).nearestToPlayer().ifPresent((x) -> {
                    NPCInteraction.interact(x, new String[]{"Pickpocket"});
                });
            }
        } else {
            closed_door = TileObjects.search().withAction("Open").atLocation(3243, 6072, 0).first();
            if (closed_door.isPresent()) {
                this.overlay.infoStatus = "Opening door";
                TileObjectInteraction.interact((TileObject)closed_door.get(), new String[]{"Open"});
            } else {
                WorldPoint lindirHouse = new WorldPoint(3243, 6071, 0);
                MovementPackets.queueMovement(lindirHouse);
            }
        }
    }

    private void handleWithdrawFood() {
        this.overlay.infoStatus = "Withdrawing food";
        Optional<Widget> food = Bank.search().matchesWildCardNoCase("*" + this.config.FoodName() + "*").first();
        if (food.isPresent()) {
            BankInteraction.withdrawX((Widget)food.get(), this.config.FoodAmount() - BankInventory.search().matchesWildCardNoCase("*" + this.config.FoodName() + "*").result().size());
        } else if (!this.hasFood()) {
            this.sendGameMessage("Out of food, disabling plugin.");
            this.enablePlugin = false;
            return;
        }

        this.timeout = this.tickDelay();
    }

    private void handleDodgyNecklace() {
        Inventory.search().withName("Dodgy necklace").first().ifPresent((x) -> {
            this.overlay.infoStatus = "Equipping dodgy necklace";
            InventoryInteraction.useItem(x, new String[]{"Wear"});
        });
    }

    public boolean dodgyCheck() {
        return this.config.dodgyNecklace() && !this.isOutOfDodgy && this.isBankOpen() && BankInventory.search().matchesWildCardNoCase("*Dodgy necklace*").result().size() < this.config.dodgyAmount();
    }

    private boolean redemptionCheck() {
        return this.config.useRedemption() && !this.hasAncientBrew() && !this.isOutOfAncientBrews && this.prayer == 0;
    }

    private boolean foodCheck() {
        return !this.hasFood() && this.hitpoints <= this.config.HealthLowAmount() && !this.config.useRedemption();
    }

    private boolean handleAncientBrewCheck() {
        return this.config.useRedemption() && this.isStandingInLindirHouse() && (this.prayer == 0 && this.hasAncientBrew() || !this.isRedemptionActive() && this.prayer > 0);
    }

    private boolean shouldFindBank() {
        return !this.hasFood() && this.hitpoints <= this.config.HealthLowAmount() && this.isOutOfAncientBrews || this.dodgyCheck() || this.foodCheck() || Inventory.full() || this.redemptionCheck();
    }

    private boolean shouldEatFood() {
        return (!this.config.useRedemption() || this.isOutOfAncientBrews && !this.hasAncientBrew()) && this.hasFood() && this.hitpoints <= this.config.HealthLowAmount();
    }

    private boolean shouldThieve() {
        return this.hasFood() || this.hitpoints >= this.config.HealthLowAmount() || this.config.useRedemption() && !this.isOutOfAncientBrews && (this.hasAncientBrew() || this.prayer > 0);
    }

    private boolean shouldWithdrawFood() {
        return this.isBankOpen() && (this.isOutOfAncientBrews || !this.config.useRedemption()) && BankInventory.search().matchesWildCardNoCase("*" + this.config.FoodName() + "*").result().size() < this.config.FoodAmount();
    }

    public boolean shadowVeilCheck() {
        return this.config.shadowVeil() && this.shadowVeilCooldown == 0 && this.hasShadowVeilRequirements() && !this.isShadowVeilActive;
    }

    public boolean useAncientBrewCheck() {
        return this.config.useRedemption() && !this.isOutOfAncientBrews && this.isBankOpen() && BankInventory.search().matchesWildCardNoCase("*Ancient brew*").result().size() < this.config.AncientBrewAmount();
    }

    private boolean isRedemptionActive() {
        return EthanApiPlugin.getClient().isPrayerActive(Prayer.REDEMPTION);
    }

    private void handleFood() {
        this.overlay.infoStatus = "Eating";
        Inventory.search().matchesWildCardNoCase("*" + this.config.FoodName() + "*").first().ifPresent((x) -> {
            InventoryInteraction.useItem(x, new String[]{"Eat", "Drink"});
            this.timeout = this.tickDelay();
        });
    }

    private void handleAncientBrew() {
        if (this.prayer == 0) {
            Inventory.search().nameContains("Ancient brew").first().ifPresent((x) -> {
                InventoryInteraction.useItem(x, new String[]{"Drink"});
            });
        }

        if (!this.isRedemptionActive() && this.prayer > 0) {
            this.overlay.infoStatus = "Activating Redemption";
            PrayerInteraction.togglePrayer(Prayer.REDEMPTION);
        }

    }

    public void togglePlugin() {
        this.enablePlugin = !this.enablePlugin;
        if (this.client.getGameState() == GameState.LOGGED_IN) {
            if (!this.enablePlugin) {
                this.sendGameMessage("Auto Thiever El Guason apagado.");
            } else {
                this.sendGameMessage("Auto Thiever El Guason encendido.");
            }

        }
    }

    private boolean isWearingDodgyNecklace() {
        return Equipment.search().nameContains("Dodgy necklace").first().isPresent();
    }

    private void handlePouch(int minPouch, int randMax) {
        if (this.nextPouchOpen < minPouch || this.nextPouchOpen > randMax) {
            this.nextPouchOpen = this.getRandomIntBetweenRange(minPouch, randMax);
        }

        Inventory.search().matchesWildCardNoCase("*Coin pouch*").first().ifPresent((x) -> {
            this.pouches = x.getItemQuantity();
        });
        if (this.pouches >= this.nextPouchOpen) {
            this.overlay.infoStatus = "Open pouch";
            Inventory.search().withName("Coin pouch").first().ifPresent((x) -> {
                InventoryInteraction.useItem(x, new String[]{"Open-all"});
            });
            this.nextPouchOpen = 0;
            this.pouches = 0;
        }

    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (this.enablePlugin) {
            ChatMessageType chatMessageType = event.getType();
            if (chatMessageType == ChatMessageType.GAMEMESSAGE || chatMessageType == ChatMessageType.SPAM) {
                if (event.getMessage().startsWith("You pick")) {
                    ++this.successfulThieves;
                }

                if (event.getMessage().startsWith("You fail") || event.getMessage().startsWith("Your dodgy necklace protects")) {
                    ++this.failedThieves;
                }

                if (event.getMessage().startsWith("You need to empty your")) {
                    Inventory.search().withName("Coin pouch").first().ifPresent((x) -> {
                        InventoryInteraction.useItem(x, new String[]{"Open-all"});
                    });
                    this.nextPouchOpen = 0;
                }

            }
        }
    }

    public void openNearestBank() {
        Optional<TileObject> bank = TileObjects.search().withName("Bank booth").withAction("Bank").nearestToPlayer();
        if (!this.isBankOpen() && !EthanApiPlugin.isMoving()) {
            Optional<TileObject> door = TileObjects.search().withAction("Open").atLocation(3243, 6072, 0).first();
            if (this.isStandingInLindirHouse() && door.isPresent()) {
                this.overlay.infoStatus = "Opening door";
                TileObjectInteraction.interact((TileObject)door.get(), new String[]{"Open"});
                return;
            }

            if (bank.isPresent()) {
                this.overlay.infoStatus = "Banking";
                TileObjectInteraction.interact((TileObject)bank.get(), new String[]{"Bank"});
            } else {
                this.sendGameMessage("No bank found, disabling plugin.");
                this.enablePlugin = false;
            }
        }

        if (this.isRedemptionActive()) {
            PrayerInteraction.togglePrayer(Prayer.REDEMPTION);
        }

        this.timeout = this.tickDelay();
    }

    private boolean isStandingInLindirHouse() {
        return this.LINDIR_HOUSE.contains(this.client.getLocalPlayer().getWorldLocation());
    }

    public boolean isBankOpen() {
        return this.client.getWidget(WidgetInfo.BANK_CONTAINER) != null;
    }

    public boolean hasFood() {
        return this.isBankOpen() ? BankInventory.search().matchesWildCardNoCase("*" + this.config.FoodName() + "*").first().isPresent() : Inventory.search().matchesWildCardNoCase("*" + this.config.FoodName() + "*").first().isPresent();
    }

    public boolean hasAncientBrew() {
        return this.isBankOpen() ? BankInventory.search().matchesWildCardNoCase("*Ancient brew*").first().isPresent() : Inventory.search().matchesWildCardNoCase("*Ancient brew*").first().isPresent();
    }

    public boolean hasDodgy() {
        if (this.isBankOpen()) {
            return BankInventory.search().withName("Dodgy necklace").first().isPresent() || this.isWearingDodgyNecklace();
        } else {
            return Inventory.search().withName("Dodgy necklace").first().isPresent() || this.isWearingDodgyNecklace();
        }
    }

    public boolean isRunEnabled() {
        return this.client.getVarpValue(173) == 1;
    }

    public boolean isBankPinOpen() {
        return this.client.getWidget(WidgetInfo.BANK_PIN_CONTAINER) != null;
    }

    public void handleRun(int minEnergy, int randMax) {
        if (this.nextRunEnergy < minEnergy || this.nextRunEnergy > minEnergy + randMax) {
            this.nextRunEnergy = this.getRandomIntBetweenRange(minEnergy, minEnergy + this.getRandomIntBetweenRange(0, randMax));
        }

        if ((this.client.getEnergy() / 100 > this.nextRunEnergy || this.client.getVarbitValue(25) != 0) && !this.isRunEnabled()) {
            this.nextRunEnergy = 0;
            Widget runOrb = this.client.getWidget(WidgetInfo.MINIMAP_RUN_ORB);
            if (runOrb != null) {
                this.enableRun();
            }
        }

    }

    public void enableRun() {
        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetActionPacket(1, 10485787, -1, -1);
    }

    public int getRandomIntBetweenRange(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public void sendGameMessage(String message) {
        String chatMessage = (new ChatMessageBuilder()).append(ChatColorType.HIGHLIGHT).append(message).build();
        this.chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.CONSOLE).runeLiteFormattedMessage(chatMessage).build());
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
}
