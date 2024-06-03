package com.polyplugins.AutoMiner;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Keybind;
import net.runelite.client.config.Range;

@ConfigGroup("AutoMiner")
public interface AutoMinerConfiguration extends Config {
    String version = "v0.1";
    @ConfigSection(
            name = "Lee esto",
            description = "Importante.",
            position = 2
    )
    String instructionsConfig2 = "instructionsConfig2";
    @ConfigSection(
            name = "Instrucciones",
            description = "Plugin Instrucciones.",
            position = 2
    )
    String instructionsConfig = "instructionsConfig";
    @ConfigSection(
            name = "configuración",
            description = "Plugin configuración.",
            position = 5
    )
    String setupConfig = "setupConfig";
    @ConfigSection(
            name = "Guild Configuracion",
            description = "Mining Guild sección de configuración.",
            position = 8
    )
    String guildConfig = "guildConfig";
    @ConfigSection(
            name = "Game Tick Configuracion",
            description = "Cada tick es 600ms",
            position = 57
    )
    String delayTickConfig = "delayTickConfig";
    @ConfigSection(
            name = "UI configuracion",
            description = "UI configuracion.",
            position = 80
    )
    String UIConfig = "UIConfig";

    @ConfigItem(
            keyName = "instructions5",
            name = "",
            description = "Instructions.",
            position = 20,
            section = "instructionsConfig2"
    )
    default String instructions5() {
        return "Importante";
    }

    @ConfigItem(
            keyName = "instructions44",
            name = "",
            description = "Instructions.",
            position = 1,
            section = "instructionsConfig"
    )
    default String instructions44() {
        return "Un plugin de ayuda en la mining guild.";
    }

    @ConfigItem(
            keyName = "start/stop hotkey",
            name = "Tecla",
            description = "Alternar para activar y desactivar el complemento.",
            position = 6,
            section = "setupConfig"
    )
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "MineMode",
            name = "Mine modO:",
            position = 7,
            section = "setupConfig",
            description = "Seleccione su método de minería."
    )
    default Mode Mode() {
        return Mode.MINING_GUILD;
    }

    @ConfigItem(
            keyName = "guildRock",
            name = "Rock",
            position = 9,
            section = "guildConfig",
            description = "Qué roca quieres minar?."
    )
    default MiningGuildRocks RockToMine() {
        return MiningGuildRocks.IRON_1;
    }

    @Range(
            min = 0,
            max = 10
    )
    @ConfigItem(
            keyName = "tickDelayMin",
            name = "Game Tick Min",
            description = "",
            position = 58,
            section = "delayTickConfig"
    )
    default int tickDelayMin() {
        return 1;
    }

    @Range(
            min = 0,
            max = 10
    )
    @ConfigItem(
            keyName = "tickDelayMax",
            name = "Game Tick Max",
            description = "",
            position = 59,
            section = "delayTickConfig"
    )
    default int tickDelayMax() {
        return 3;
    }

    @Range(
            min = 0,
            max = 10
    )
    @ConfigItem(
            keyName = "tickDelayTarget",
            name = "Game Tick Target",
            description = "",
            position = 60,
            section = "delayTickConfig"
    )
    default int tickDelayTarget() {
        return 2;
    }

    @Range(
            min = 0,
            max = 10
    )
    @ConfigItem(
            keyName = "tickDelayDeviation",
            name = "Game Tick Deviacion",
            description = "",
            position = 61,
            section = "delayTickConfig"
    )
    default int tickDelayDeviation() {
        return 1;
    }

    @ConfigItem(
            keyName = "tickDelayWeightedDistribution",
            name = "Game Tick Weighted Distribucion",
            description = "",
            position = 62,
            section = "delayTickConfig"
    )
    default boolean tickDelayWeightedDistribution() {
        return false;
    }

    @ConfigItem(
            keyName = "UISetting",
            name = "UI Diseño: ",
            description = "",
            position = 81,
            section = "UIConfig",
            hidden = false
    )
    default UISettings UISettings() {
        return UISettings.FULL;
    }

    @ConfigItem(
            keyName = "enableUI",
            name = "Habilitar UI",
            description = "",
            section = "UIConfig",
            position = 140
    )
    default boolean enableUI() {
        return true;
    }
}
