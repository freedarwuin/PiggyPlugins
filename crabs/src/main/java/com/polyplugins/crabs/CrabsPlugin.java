
package com.polyplugins.crabs;

import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.InteractionApi.NPCInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.google.inject.Provides;
import java.awt.Color;
import java.awt.geom.GeneralPath;
import java.time.Instant;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.CollisionData;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.Keybind;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.WorldService;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.npcunaggroarea.NpcAggroAreaPlugin;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.HotkeyListener;
import net.runelite.client.util.WorldUtil;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldResult;
import net.runelite.http.api.worlds.WorldType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(
        name = "<html><font color=\"#00D8FF\">[Crabs]</font></font></html>",
        tags = {"El Guason"},
        enabledByDefault = false
)
@PluginDependency(NpcAggroAreaPlugin.class)
public class CrabsPlugin extends Plugin {
    private static final Logger log = LoggerFactory.getLogger(CrabsPlugin.class);
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private KeyManager keyManager;
    @Inject
    private NpcAggroAreaPlugin npcAggroAreaPlugin;
    @Inject
    private CrabsConfig config;
    @Inject
    private WorldService worldService;
    @Inject
    private CrabsOverlay crabsOverlay;
    @Inject
    private OverlayManager overlayManager;
    private GeneralPath AreaSafe;
    private boolean reseteando = false;
    private int timeout = -1;
    private int contador = 0;
    private int llave;
    private final Color pint;
    private Estados estado;
    protected final int MAX_DISTANT;
    private static WorldPoint choosen = null;
    public static WorldPoint tilePelea = null;
    private boolean enAccion;
    private final KeyListener caminador;
    private int mundoActual;
    private int hopFailSafe;
    private int hopTimeout;
    private final int TICKS_FOR_RETRY_HOP;
    private final int RETRIES_FOR_HOP;
    private int timeNoFighting;
    private final int MAX_TIME_NO_FIGHTING;
    private int resetTileTries;
    private final int MAX_RESETTILE_TRIES;
    private final int[] SAND_CRABS_REGION_IDS;

    public CrabsPlugin() {
        this.pint = Color.magenta;
        this.estado = CrabsPlugin.Estados.STARTING;
        this.MAX_DISTANT = 14;
        this.enAccion = false;
        this.caminador = new HotkeyListener(() -> {
            return new Keybind(117, 0);
        }) {
            public void hotkeyPressed() {
                CrabsPlugin.this.clientThread.invoke(() -> {
                    CrabsPlugin.this.enAccion = !CrabsPlugin.this.enAccion;
                    if (!CrabsPlugin.this.enAccion) {
                        CrabsPlugin.tilePelea = null;
                        CrabsPlugin.this.estado = CrabsPlugin.Estados.APAGADO;
                        CrabsPlugin.log.info("Se apago la wea");
                        CrabsPlugin.this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", ColorUtil.wrapWithColorTag("Apagado", Color.red), "");
                    } else {
                        CrabsPlugin.tilePelea = CrabsPlugin.this.client.getLocalPlayer().getWorldLocation();
                        CrabsPlugin.this.estado = CrabsPlugin.Estados.STARTING;
                        CrabsPlugin.log.info("Se prendio la wea");
                        CrabsPlugin.this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", ColorUtil.wrapWithColorTag("Prendido", Color.GREEN), "");
                    }

                });
            }
        };
        this.mundoActual = 0;
        this.hopFailSafe = 0;
        this.hopTimeout = 0;
        this.TICKS_FOR_RETRY_HOP = 12;
        this.RETRIES_FOR_HOP = 3;
        this.timeNoFighting = 0;
        this.MAX_TIME_NO_FIGHTING = 100;
        this.resetTileTries = 0;
        this.MAX_RESETTILE_TRIES = 3;
        this.SAND_CRABS_REGION_IDS = new int[]{6710, 6966, 7222, 7478, 7479, 6965, 7221};
    }

    @Provides
    CrabsConfig getConfig(ConfigManager configManager) {
        return (CrabsConfig)configManager.getConfig(CrabsConfig.class);
    }

    protected void startUp() throws Exception {
        this.overlayManager.add(this.crabsOverlay);
        this.keyManager.registerKeyListener(this.caminador);
        this.reseteando = false;
        this.enAccion = false;
        tilePelea = null;
        choosen = null;
    }

    protected void shutDown() throws Exception {
        this.keyManager.unregisterKeyListener(this.caminador);
        this.overlayManager.remove(this.crabsOverlay);
        this.reseteando = false;
        this.enAccion = false;
        tilePelea = null;
        choosen = null;
    }

    @Subscribe
    void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("crabs") && event.getKey().equals("onOff")) {
            this.enAccion = !this.enAccion;
            this.clientThread.invoke(() -> {
                if (!this.enAccion) {
                    tilePelea = null;
                    this.estado = CrabsPlugin.Estados.APAGADO;
                    this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", ColorUtil.wrapWithColorTag("Apagado", Color.red), "");
                } else {
                    tilePelea = this.client.getLocalPlayer().getWorldLocation();
                    this.estado = CrabsPlugin.Estados.STARTING;
                    this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", ColorUtil.wrapWithColorTag("Prendido", Color.green), "");
                }

            });
        }

    }

    public boolean InsideSafe() {
        return this.AreaSafe.contains((double)this.client.getLocalPlayer().getLocalLocation().getX(), (double)this.client.getLocalPlayer().getLocalLocation().getY());
    }

    public boolean InsideSafe(WorldPoint pt) {
        return this.AreaSafe.contains((double)((LocalPoint)Objects.requireNonNull(LocalPoint.fromWorld(this.client, pt))).getX(), (double)((LocalPoint)Objects.requireNonNull(LocalPoint.fromWorld(this.client, pt))).getY());
    }

    @Subscribe
    void onGameTick(GameTick event) {
        if (this.enAccion) {
            if (this.timeout > 0) {
                --this.timeout;
            } else if (this.npcAggroAreaPlugin.getEndTime() == null) {
                log.info("npc agresion timer no activado");
                this.timeout = 10;
                this.client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Npc agresion timer no activado", "");
            } else {
                this.AreaSafe = this.npcAggroAreaPlugin.getLinesToDisplay()[this.client.getPlane()];
                if (this.AreaSafe != null) {
                    CollisionData[] collisionData = this.client.getCollisionMaps();
                    if (collisionData != null) {
                        CollisionData collActual = collisionData[this.client.getPlane()];
                        Player jugador = this.client.getLocalPlayer();
                        WorldArea playerArea = this.client.getLocalPlayer().getWorldArea();
                        WorldPoint playerPoint = this.client.getLocalPlayer().getWorldLocation();
                        int plano = this.client.getPlane();
                        int ScenePlayerX = this.client.getLocalPlayer().getLocalLocation().getSceneX();
                        int ScenePlayerY = this.client.getLocalPlayer().getLocalLocation().getSceneY();
                        int baseX = this.client.getBaseX();
                        int baseY = this.client.getBaseY();
                        log.info("estado: {}", this.estado);
                        if (this.estado == CrabsPlugin.Estados.STARTING) {
                            if (Arrays.stream(this.SAND_CRABS_REGION_IDS).anyMatch((x) -> {
                                return x == playerPoint.getRegionID();
                            })) {
                                this.estado = CrabsPlugin.Estados.SEARCHING_WORLD;
                            }
                        } else if (this.estado == CrabsPlugin.Estados.SEARCHING_WORLD) {
                            if (this.mundoActual == 0) {
                                tilePelea = null;
                                Iterator var12 = this.config.spot().getPuntos().iterator();

                                while(var12.hasNext()) {
                                    WorldPoint wp = (WorldPoint)var12.next();
                                    if (wp.distanceTo(playerPoint) <= 14 && this.client.getPlayers().stream().noneMatch((x) -> {
                                        return x.getWorldLocation().isInArea(new WorldArea[]{this.toWorldArea(wp, 2)});
                                    })) {
                                        tilePelea = wp;
                                        break;
                                    }
                                }

                                if (tilePelea == null) {
                                    this.mundoActual = this.client.getWorld();
                                    log.info("hopeando");
                                    this.client.openWorldHopper();
                                    this.hop(false);
                                } else {
                                    this.estado = CrabsPlugin.Estados.EN_COMBATE;
                                }
                            } else if (this.mundoActual != this.client.getWorld()) {
                                this.mundoActual = 0;
                            } else {
                                ++this.hopTimeout;
                                if (this.hopTimeout > 12) {
                                    ++this.hopFailSafe;
                                    if (this.hopFailSafe > 3) {
                                        this.estado = CrabsPlugin.Estados.APAGADO;
                                        log.info("No se pudo hopear");
                                    } else {
                                        log.info("hopeando denuevo");
                                        this.hop(false);
                                    }
                                }
                            }
                        } else {
                            List NPCsCercanos;
                            if (this.estado == CrabsPlugin.Estados.EN_COMBATE) {
                                if (this.npcAggroAreaPlugin.getEndTime().isBefore(Instant.now())) {
                                    this.estado = CrabsPlugin.Estados.RESETTING;
                                } else if (!playerPoint.equals(tilePelea)) {
                                    if (jugador.getPoseAnimation() == jugador.getIdlePoseAnimation()) {
                                        MousePackets.queueClickPacket();
                                        MovementPackets.queueMovement(tilePelea);
                                    }
                                } else if (jugador.isInteracting()) {
                                    this.timeNoFighting = 0;
                                } else {
                                    ++this.timeNoFighting;
                                    if (this.timeNoFighting > 100) {
                                        NPCsCercanos = NPCs.search().withinWorldArea(this.toWorldArea(playerPoint, 3)).result();
                                        if (!NPCsCercanos.isEmpty()) {
                                            NPCsCercanos = (List)NPCsCercanos.stream().filter((x) -> {
                                                return !x.isInteracting();
                                            }).collect(Collectors.toList());
                                            NPCInteraction.interact((NPC)NPCsCercanos.get(0), new String[0]);
                                            this.timeout = 5;
                                            this.timeNoFighting = 50;
                                        } else {
                                            log.info("No hay NPCs cerca atakables");
                                        }
                                    }
                                }
                            } else if (this.estado == CrabsPlugin.Estados.RESETTING) {
                                if (this.npcAggroAreaPlugin.getEndTime().isBefore(Instant.now())) {
                                    if (this.client.getFollower() != null) {
                                        NPCsCercanos = (List)this.client.getNpcs().stream().filter((x) -> {
                                            return x.getId() != this.client.getFollower().getId();
                                        }).collect(Collectors.toList());
                                    } else {
                                        NPCsCercanos = this.client.getNpcs();
                                    }

                                    if (NPCsCercanos.stream().noneMatch((x) -> {
                                        return x.getInteracting() != null && x.getInteracting().getName() != null && x.getInteracting().getName().equalsIgnoreCase(jugador.getName());
                                    })) {
                                        log.info("no hay targeteandome");
                                        if (jugador.getPoseAnimation() == jugador.getIdlePoseAnimation()) {
                                            log.info("meow");
                                            choosen = null;

                                            for(int i = 1; i < 22; ++i) {
                                                if (this.isWalkable(collActual, ScenePlayerX + i, ScenePlayerY + i) && !this.InsideSafe(playerPoint.dx(i).dy(i))) {
                                                    choosen = new WorldPoint(baseX + ScenePlayerX + i, baseY + ScenePlayerY + i, this.client.getPlane());
                                                    break;
                                                }

                                                if (this.isWalkable(collActual, ScenePlayerX - i, ScenePlayerY + i) && !this.InsideSafe(playerPoint.dx(-i).dy(i))) {
                                                    choosen = new WorldPoint(baseX + ScenePlayerX - i, baseY + ScenePlayerY + i, this.client.getPlane());
                                                    break;
                                                }

                                                if (this.isWalkable(collActual, ScenePlayerX - i, ScenePlayerY - i) && !this.InsideSafe(playerPoint.dx(-i).dy(-i))) {
                                                    choosen = new WorldPoint(baseX + ScenePlayerX - i, baseY + ScenePlayerY - i, this.client.getPlane());
                                                    break;
                                                }

                                                if (this.isWalkable(collActual, ScenePlayerX + i, ScenePlayerY - i) && !this.InsideSafe(playerPoint.dx(i).dy(-i))) {
                                                    choosen = new WorldPoint(baseX + ScenePlayerX + i, baseY + ScenePlayerY - i, this.client.getPlane());
                                                    break;
                                                }
                                            }

                                            if (choosen == null) {
                                                ++this.resetTileTries;
                                                if (this.resetTileTries > 3) {
                                                    log.info("No se encontro un resetTile");
                                                    this.estado = CrabsPlugin.Estados.APAGADO;
                                                    this.enAccion = false;
                                                }
                                            } else {
                                                log.info("Moviendo hacia un Tile reseteador");
                                                MousePackets.queueClickPacket();
                                                MovementPackets.queueMovement(choosen);
                                                log.info("Tile escogido: {}", choosen);
                                            }

                                            this.timeout = 1;
                                        }
                                    }
                                } else {
                                    log.info("Woof");
                                    if (!playerPoint.equals(tilePelea)) {
                                        if (jugador.getPoseAnimation() == jugador.getIdlePoseAnimation()) {
                                            MousePackets.queueClickPacket();
                                            MovementPackets.queueMovement(tilePelea);
                                        }
                                    } else {
                                        this.estado = CrabsPlugin.Estados.EN_COMBATE;
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    private void revisarTiles() {
    }

    public boolean InCombat(Player yo) {
        return yo.isInteracting() || yo.getAnimation() != -1 || this.client.getNpcs().stream().anyMatch((mono) -> {
            return mono.getInteracting() != null ? mono.getInteracting().equals(yo) : false;
        });
    }

    public boolean isWalkable(CollisionData colData, int x, int y) {
        return (colData.getFlags()[x][y] & 2359552) == 0;
    }

    @Subscribe
    void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOGGED_IN) {
            this.AreaSafe = this.npcAggroAreaPlugin.getLinesToDisplay()[this.client.getPlane()];
        }

    }

    private WorldArea toWorldArea(WorldPoint worldPoint, int radio) {
        return new WorldArea(worldPoint.dx(-radio).dy(-radio), 2 * radio + 1, 2 * radio + 1);
    }

    public void hop(boolean previous) {
        WorldResult worldResult = this.worldService.getWorlds();
        if (worldResult != null && this.client.getGameState() == GameState.LOGGED_IN) {
            World w = worldResult.findWorld(this.client.getWorld());
            EnumSet<WorldType> tipos = w.getTypes().clone();
            tipos.remove(WorldType.BOUNTY);
            tipos.remove(WorldType.LAST_MAN_STANDING);
            tipos.remove(WorldType.SKILL_TOTAL);
            List<World> munditos = worldResult.getWorlds();
            int worldIndex = munditos.indexOf(w);

            World wTest;
            EnumSet types;
            do {
                if (previous) {
                    --worldIndex;
                    if (worldIndex < 0) {
                        worldIndex = munditos.size() - 1;
                    }
                } else {
                    ++worldIndex;
                    if (worldIndex >= munditos.size()) {
                        worldIndex = 0;
                    }
                }

                wTest = (World)munditos.get(worldIndex);
                types = wTest.getTypes().clone();
                types.remove(WorldType.BOUNTY);
                types.remove(WorldType.LAST_MAN_STANDING);
                if (types.contains(WorldType.SKILL_TOTAL)) {
                    try {
                        int totalReq = Integer.parseInt(wTest.getActivity().substring(0, wTest.getActivity().indexOf(" ")));
                        if (this.client.getTotalLevel() > totalReq) {
                            types.remove(WorldType.SKILL_TOTAL);
                        }
                    } catch (NumberFormatException var10) {
                        NumberFormatException ex = var10;
                        log.warn("Failed to parse total level requirement for target world", ex);
                    }
                }
            } while((wTest.getPlayers() >= 1800 || wTest.getPlayers() < 0 || !types.equals(tipos)) && w != wTest);

            if (w == wTest) {
                log.info("No se encontro mundo");
            } else {
                this.hop(wTest.getId());
                log.info("hopeando a {}", wTest.getId());
            }

        }
    }

    private void hop(int w) {
        assert this.client.isClientThread();

        World world = ((WorldResult)Objects.requireNonNull(this.worldService.getWorlds())).findWorld(w);
        if (world == null) {
            log.info("no se encontro el mundo");
        } else {
            log.info("katarina");
            net.runelite.api.World rsWorld = this.client.createWorld();
            rsWorld.setActivity(world.getActivity());
            rsWorld.setAddress(world.getAddress());
            rsWorld.setId(world.getId());
            rsWorld.setPlayerCount(world.getPlayers());
            rsWorld.setLocation(world.getLocation());
            rsWorld.setTypes(WorldUtil.toWorldTypes(world.getTypes()));
            this.client.hopToWorld(rsWorld);
        }
    }

    static enum Estados {
        STARTING,
        EN_COMBATE,
        RESETTING,
        ACTIVAR_AGGRO_PLUG,
        SEARCHING_WORLD,
        APAGADO;

        private Estados() {
        }
    }
}
