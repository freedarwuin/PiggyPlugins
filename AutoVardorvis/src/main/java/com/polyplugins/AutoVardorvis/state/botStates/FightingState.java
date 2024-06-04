package com.polyplugins.AutoVardorvis.state.botStates;

import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.TileItems;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.Collections.Widgets;
import com.example.InteractionApi.InventoryInteraction;
import com.example.InteractionApi.NPCInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.WidgetPackets;
import com.piggyplugins.PiggyUtils.API.PrayerUtil;
import com.piggyplugins.PiggyUtils.API.SpellUtil;
import com.polyplugins.AutoVardorvis.AutoVardorvisConfig;
import com.polyplugins.AutoVardorvis.AutoVardorvisPlugin;
import com.polyplugins.AutoVardorvis.state.StateHandler.State;
import java.util.List;
import java.util.Optional;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Prayer;
import net.runelite.api.Skill;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;

public class FightingState {
    private final String VARDORVIS = "Vardorvis";
    private Client client;
    private static WorldPoint safeTile = null;
    private static WorldPoint axeMoveTile = null;
    private boolean drankSuperCombat;
    private static boolean hasCastDeathCharge = false;
    private static int axeTicks = 0;
    private static int specTicks = 0;
    private static int thrallTicks = 0;
    private static int vardorvisHpPercent = 6400 >>> (char)4680 << 5480 - -1242;
    private AutoVardorvisPlugin.MainClassContext context;

    public FightingState() {
    }

    public void execute(AutoVardorvisPlugin.MainClassContext context) {
        this.client = context.getClient();
        this.context = context;
        AutoVardorvisConfig config = context.getConfig();
        this.drankSuperCombat = context.isDrankSuperCombat();
        List<NPC> newAxes = NPCs.search().withId(12225 << 16160 << -14428 - -19676).result();
        List<NPC> activeAxes = NPCs.search().withId(-4962 - -14021 ^ -13821 ^ -14685).result();
        Optional<NPC> vardorvis = NPCs.search().nameContains("Vardorvis").first();
        WorldPoint playerTile = this.client.getLocalPlayer().getWorldLocation();
        Optional<TileObject> safeRock = TileObjects.search().withAction("Leave").first();
        vardorvis.ifPresent(FightingState::updateNpcHp);
        if (!TileItems.search().empty()) {
            TileItems.search().first().ifPresent((item) -> {
                if (Inventory.full() && !Inventory.search().withAction("Eat").empty()) {
                    Inventory.search().withAction("Eat").result().stream().findFirst().ifPresent((food) -> {
                        InventoryInteraction.useItem(food, new String[]{"Eat"});
                    });
                }

                item.interact(false);
            });
        } else {
            if (TileObjects.search().nameContains("Portal Nexus").first().isPresent()) {
                System.out.println("inside house in fighting state");
                context.setContextBotState(State.GO_TO_BANK);
            }

            if (!PrayerUtil.isPrayerActive(Prayer.PIETY)) {
                PrayerUtil.togglePrayer(Prayer.PIETY);
            }

            if (safeTile != null) {
                if (!newAxes.isEmpty()) {
                    newAxes.forEach((axe) -> {
                        if (axe.getWorldLocation().getX() == safeTile.getX() - 1 && axe.getWorldLocation().getY() == safeTile.getY() - 1) {
                            this.handleAxeMove();
                        }

                    });
                } else if (!activeAxes.isEmpty()) {
                    activeAxes.forEach((axe) -> {
                        if (axe.getWorldLocation().getX() == safeTile.getX() + 1 && axe.getWorldLocation().getY() == safeTile.getY() - 1) {
                            axeTicks = 1;
                            this.handleAxeMove();
                        }

                    });
                }
            }

            this.doBloodCaptcha();
            this.drinkPrayer(config.DRINKPRAYERAT());
            this.eat(config.EATAT());
            this.useSpecialAttack();
            if (!this.isInFight(this.client)) {
                this.turnOffPrayers();
                if (TileItems.search().first().isEmpty() && !this.enoughFood()) {
                    this.teleToHouse();
                    return;
                }

                if (safeTile != null && (playerTile.getX() != safeTile.getX() || playerTile.getY() != safeTile.getY())) {
                    this.movePlayerToTile(safeTile);
                }
            }

            if (vardorvis.isPresent() && safeTile != null) {
                if (((NPC)vardorvis.get()).getWorldLocation().getX() == safeTile.getX() + (16 >>> 12899 << 117 - -13356) && ((NPC)vardorvis.get()).getWorldLocation().getY() == safeTile.getY() - 1 && ((NPC)vardorvis.get()).getAnimation() == -1 << (char)2272 << (-19754 ^ -23850)) {
                    if (this.enoughFood()) {
                        vardorvis.ifPresent((npc) -> {
                            NPCInteraction.interact(npc, new String[]{"Attack"});
                            if (!this.drankSuperCombat) {
                                Inventory.search().nameContains("Divine super combat").first().ifPresent((potion) -> {
                                    InventoryInteraction.useItem(potion, new String[]{"Drink"});
                                    this.drankSuperCombat = true;
                                    context.setDrankSuperCombat(true);
                                });
                            }

                            this.summonThrall();
                        });
                    } else {
                        this.teleToHouse();
                        context.setContextTickDelay(3 << 8096 << (-14812 ^ -13820));
                    }

                    return;
                }

                if (((NPC)vardorvis.get()).getWorldLocation().getX() == safeTile.getX()) {
                    EthanApiPlugin.sendClientMessage("Vardorvis stuck");
                    this.movePlayerToTile(safeTile);
                    this.eat(config.EATAT());
                    return;
                }
            }

            if (safeTile == null || playerTile.getX() == safeTile.getX() && playerTile.getY() == safeTile.getY()) {
                if (safeRock.isPresent() && safeTile == null) {
                    WorldPoint safeRockLocation = ((TileObject)safeRock.get()).getWorldLocation();
                    safeTile = new WorldPoint(safeRockLocation.getX() + ((-810114125 ^ -810638413) >>> (-7101 ^ -8015)), safeRockLocation.getY() - (5120 << (char)997 >>> (30121 ^ 28263)), 0);
                    axeMoveTile = new WorldPoint(safeTile.getX() + (-1510671687 - -1510704455 >>> (-25599 ^ -20209)), safeTile.getY() - (1 << 4160 << (559168 >>> 6790)), 0);
                }

                context.setDrankSuperCombat(this.drankSuperCombat);
                if (!this.client.getLocalPlayer().isInteracting()) {
                    NPCs.search().nameContains("Vardorvis").first().ifPresent((npc) -> {
                        NPCInteraction.interact(npc, new String[]{"Attack"});
                    });
                }

                if (vardorvisHpPercent <= 7680 >>> 8041 << (13633 << (char)14880) && !hasCastDeathCharge) {
                    this.castDeathCharge();
                    hasCastDeathCharge = true;
                }

            } else {
                this.movePlayerToTile(safeTile);
            }
        }
    }

    private void useSpecialAttack() {
        if (specTicks > 0) {
            --specTicks;
        }

        if (this.client.getVarpValue(-22236 ^ 6908 ^ -43207 - -23483) >= (char)125 << 14336 << (553648128 >>> 11319) && vardorvisHpPercent != (char)25 << 14848 << (817 << (char)2785) && vardorvisHpPercent >= 818791143 - -20069657 >>> (12415 ^ 15079)) {
            if (Inventory.search().nameContains("Voidwaker").first().isPresent()) {
                InventoryInteraction.useItem("Voidwaker", new String[]{"Wield"});
            } else if (specTicks == 0) {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetActionPacket(1, Integer.parseInt("-2oufo9", 32) + 866384534 ^ -1160581448 >>> 7362, -1 << 1312 << -8611 - -17571, -1 << 544 >>> (25812 ^ 29492));
                specTicks = 48 << (char)2633 >>> (6262 << 4833);
            }
        } else if (Inventory.search().nameContains("Abyssal tentacle").first().isPresent()) {
            InventoryInteraction.useItem("Abyssal tentacle", new String[]{"Wield"});
        }

    }

    private void castDeathCharge() {
        Widget deathChargeWidget = SpellUtil.getSpellWidget(this.client, "Death Charge");
        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetAction(deathChargeWidget, new String[]{"Cast"});
    }

    private void summonThrall() {
        if (thrallTicks > 0) {
            --thrallTicks;
        } else if (thrallTicks == 0) {
            Widget thrallSpellWidget = SpellUtil.getSpellWidget(this.client, "Resurrect Greater Ghost");
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(thrallSpellWidget, new String[]{"Cast"});
            thrallTicks = -1367640322 - -1367722242 >>> ('\uaad0' >>> 15396);
        }

    }

    private void handleAxeMove() {
        switch (axeTicks ^ (-1182894761 ^ "102s".hashCode() ^ -295660983) - (-1388281524 ^ 496287797)) {
            case -1506433404:
                this.movePlayerToTile(axeMoveTile);
            case -1506433403:
            default:
                if (axeTicks == 1) {
                    axeTicks = 0;
                } else {
                    ++axeTicks;
                }

        }
    }

    private void movePlayerToTile(WorldPoint tile) {
        MousePackets.queueClickPacket();
        MovementPackets.queueMovement(tile);
    }

    private void doBloodCaptcha() {
        List<Widget> captchaBlood = Widgets.search().filter((widget) -> {
            return widget.getParentId() != ("044o".hashCode() ^ -1285399122 ^ -101399023) >>> (231872 >>> (char)2598);
        }).hiddenState(false).withAction("Destroy").result();
        if (!captchaBlood.isEmpty()) {
            captchaBlood.forEach((x) -> {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetAction(x, new String[]{"Destroy"});
            });
        }

    }

    private boolean isInFight(Client client) {
        return client.isInInstancedRegion() && NPCs.search().nameContains("Vardorvis").nearestToPlayer().isPresent();
    }

    private boolean enoughFood() {
        return this.context.getConfig().MIN_FOOD() <= Inventory.search().withAction("Eat").result().size() && this.context.getConfig().MIN_PRAYER_POTIONS() <= Inventory.search().nameContains("Prayer potion").result().size();
    }

    private void turnOffPrayers() {
        if (PrayerUtil.isPrayerActive(Prayer.PIETY)) {
            PrayerUtil.togglePrayer(Prayer.PIETY);
        }

        if (PrayerUtil.isPrayerActive(Prayer.PROTECT_FROM_MELEE)) {
            PrayerUtil.togglePrayer(Prayer.PROTECT_FROM_MELEE);
        }

    }

    private void eat(int at) {
        if (this.needsToEat(at)) {
            Inventory.search().withAction("Eat").result().stream().findFirst().ifPresentOrElse((food) -> {
                InventoryInteraction.useItem(food, new String[]{"Eat"});
            }, this::teleToHouse);
        }

    }

    private void drinkPrayer(int at) {
        if (this.needsToDrinkPrayer(at)) {
            Inventory.search().nameContains("Prayer potion").result().stream().findFirst().ifPresentOrElse((prayerPotion) -> {
                InventoryInteraction.useItem(prayerPotion, new String[]{"Drink"});
            }, this::teleToHouse);
        }

    }

    private boolean needsToEat(int at) {
        return this.client.getBoostedSkillLevel(Skill.HITPOINTS) <= at;
    }

    private void teleToHouse() {
        EthanApiPlugin.sendClientMessage("teleporting to house");
        InventoryInteraction.useItem("Teleport to house", new String[]{"Break"});
        this.drankSuperCombat = false;
        safeTile = null;
        axeMoveTile = null;
        vardorvisHpPercent = 748581790 + -696152990 >>> -3715 + 14102;
        this.context.setContextBotState(State.GO_TO_BANK);
    }

    private boolean needsToDrinkPrayer(int at) {
        return this.client.getBoostedSkillLevel(Skill.PRAYER) <= at;
    }

    public static int getHpPercentValue(float ratio, float scale) {
        return Math.round(ratio / scale * Float.intBitsToFloat(1327731536 - -913075376 >>> 29727 - Integer.parseInt("12p4", 26)));
    }

    public static void updateNpcHp(NPC npc) {
        float healthRatio = (float)npc.getHealthRatio();
        float healthScale = (float)npc.getHealthScale();
        int currentHp = getHpPercentValue(healthRatio, healthScale);
        if (currentHp < vardorvisHpPercent && currentHp > -1 << 4256 >>> 1667 + 2557) {
            vardorvisHpPercent = currentHp;
        }

        if (currentHp == 0 && vardorvisHpPercent == 0) {
            vardorvisHpPercent = 12800 >>> 13320 << -2666 - -11531;
            hasCastDeathCharge = false;
            axeTicks = 0;
        }

    }
}
