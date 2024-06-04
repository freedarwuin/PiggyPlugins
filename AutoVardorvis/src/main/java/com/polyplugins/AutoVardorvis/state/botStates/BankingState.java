package com.polyplugins.AutoVardorvis.state.botStates;

import com.example.EthanApiPlugin.Collections.Bank;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.Collections.Widgets;
import com.example.InteractionApi.BankInteraction;
import com.example.InteractionApi.InventoryInteraction;
import com.example.InteractionApi.NPCInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.WidgetPackets;
import com.polyplugins.AutoVardorvis.AutoVardorvisConfig;
import com.polyplugins.AutoVardorvis.AutoVardorvisPlugin;
import com.polyplugins.AutoVardorvis.state.StateHandler.State;
import java.awt.event.KeyEvent;
import java.util.Optional;
import net.runelite.api.Client;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

public class BankingState {
    private AutoVardorvisConfig config;
    private Client client;
    private final WorldArea bankArea = new WorldArea(10378 - 9330 << (-4657 ^ -12882), 8009728 >>> 133 >>> (3699 << 10721), (17616 ^ (char)17808) >>> 2476 + -200, 22528 >>> 5835 << (201326592 >>> 4815), 0);
    private final WorldPoint bankLocation = new WorldPoint((char)12579 - 348 - (18685 + -8553), 2006528 >>> 2340 >>> (3900 ^ 7065), 0);

    public BankingState() {
    }

    public void execute(AutoVardorvisPlugin.MainClassContext context) {
        int tickDelay = context.getContextTickDelay();
        this.config = context.getConfig();
        this.client = context.getClient();
        Optional<TileObject> strangleWoodPyramid = TileObjects.search().withId(-1069123203 - -1168907907 >>> ("087 ".hashCode() ^ -1489780) + (char)18350).first();
        strangleWoodPyramid.ifPresent((e) -> {
            context.setContextBotState(State.GO_TO_VARDORVIS);
        });
        if (this.preparedForTrip() && !Bank.isOpen() && NPCs.search().nameContains("Jack").nearestToPlayer().isPresent()) {
            Widgets.search().withTextContains("Enter amount:").first().ifPresent((w) -> {
                Client var10000 = this.client;
                Object[] var10001 = new Object[1048576 << 9218 >>> '陫' + -23671];
                var10001[0] = (-22998 ^ 538) + ('첛' - 28576);
                var10001[1] = 1;
                var10001[-27568 - -27824 >>> (32109 ^ (char)24586)] = 0;
                var10001[3 << 2496 << (-10591 ^ -11551)] = 0;
                var10000.runScript(var10001);
            });
            Widgets.search().withTextContains("Where would you like to teleport to?").first().ifPresentOrElse((e) -> {
                WidgetPackets.queueResumePause(e.getId(), 327680 >>> 13616 << 5093 + 6363);
                context.setContextTickDelay(-1970652349 + 1970717885 >>> ((char)5511 << 5857));
            }, () -> {
                Inventory.search().nameContains("Ring of shadows").result().stream().findFirst().ifPresent((ring) -> {
                    InventoryInteraction.useItem(ring, new String[]{"Teleport"});
                });
            });
        } else if (!Bank.isOpen() && !this.preparedForTrip()) {
            if (this.client.getLocalPlayer().getWorldLocation().getX() != this.bankLocation.getX() && this.client.getLocalPlayer().getWorldLocation().getY() != this.bankLocation.getY()) {
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(this.bankLocation);
            } else {
                NPCs.search().nameContains("Jack").nearestToPlayer().ifPresent((bank) -> {
                    NPCInteraction.interact(bank, new String[]{"Bank"});
                    context.setContextTickDelay(3 << 3296 << (-9170 ^ -9330));
                });
            }
        } else if (Bank.isOpen() && !this.preparedForTrip()) {
            this.bank(context);
        } else if (Bank.isOpen() && this.preparedForTrip()) {
            this.sendKey(884736 >>> 5135 << -13590 + (char)28598);
            Widgets.search().withTextContains("Enter amount:").first().ifPresent((w) -> {
                Client var10000 = this.client;
                Object[] var10001 = new Object[32 << 8963 >>> (10956800 >>> 4139)];
                var10001[0] = -17661 ^ -26269 ^ 10364 ^ 2871;
                var10001[1] = 1;
                var10001['訡' - 31265 >>> (5611 << 5920)] = 0;
                var10001[786432 >>> 2130 << 9229 - 4077] = 0;
                var10000.runScript(var10001);
            });
        }

    }

    private void bank(AutoVardorvisPlugin.MainClassContext context) {
        if (Bank.isOpen() && !this.preparedForTrip() && Inventory.getEmptySlots() != 7 << 9857 << (1539 ^ 4610)) {
            Widgets.search().filter((widget) -> {
                return widget.getParentId() != (-1217689752 ^ "058 ".hashCode() ^ -1216669488) << (15371 ^ 14506);
            }).withAction("Deposit inventory").first().ifPresent((button) -> {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetAction(button, new String[]{"Deposit inventory"});
            });
        } else if (!this.preparedForTrip()) {
            this.withdraw("Ring of shadows", 1);
            this.withdraw("Teleport to house", 1638400 >>> 9488 << 9311 - -3010);
            this.withdraw("Divine super combat", 1);
            this.withdraw("Rune pouch", 1);
            this.withdraw("Soul rune", 1582662772 + -1517126772 >>> (-9191 ^ -7703));
            this.withdraw("Death rune", (31903 << 14528) + (-30903 >>> 9408));
            this.withdraw("Book of the dead", 1);
            this.withdraw("Voidwaker", 1);
            this.withdraw("Prayer potion", this.config.PPOTS_TO_BRING());
            this.withdraw("Manta ray", 25 << 7808 << (6609 << 15329));
            this.sendKey(56623104 >>> 10485 << -1752 + 4184);
            context.setContextTickDelay(128 >>> 10753 >>> (476381184 >>> 15888));
        } else {
            this.sendKey(27 << 8032 << (11730 ^ 15666));
        }

    }

    private void withdraw(String name, int amount) {
        Bank.search().nameContains(name).first().ifPresent((item) -> {
            BankInteraction.withdrawX(item, amount);
        });
    }

    private boolean hasItemQuantity(String name, int quantity) {
        return Inventory.search().nameContains(name).result().size() == quantity;
    }

    private boolean preparedForTrip() {
        return this.hasItemQuantity("Ring of shadows", 1) && !Inventory.search().nameContains("Teleport to house").result().isEmpty() && this.hasItemQuantity("Divine super combat", 1) && this.hasItemQuantity("Prayer potion", this.config.PPOTS_TO_BRING()) && Inventory.search().nameContains("Manta ray").result().size() >= this.config.MIN_FOOD() && Inventory.full();
    }

    private int getEmptySlots() {
        return (917504 >>> 7143 >>> (-13792 ^ -9464)) - Inventory.search().result().size();
    }

    private void sendKey(int key) {
        this.keyEvent((-13026 >>> 9120) - (-13427 << 11168), key);
        this.keyEvent(3293184 >>> 1993 >>> (99872 >>> 11939), key);
    }

    private void keyEvent(int id, int key) {
        KeyEvent e = new KeyEvent(this.client.getCanvas(), id, System.currentTimeMillis(), 0, key, (char)(Integer.parseInt("929i33b", 22) - 1032962362 << -9705 - -11145));
        this.client.getCanvas().dispatchEvent(e);
    }
}
