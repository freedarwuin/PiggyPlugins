package com.example.AutoThiever;

//import com.example.EthanApiPlugin.Collections.NPCs;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Keybind;
import net.runelite.client.config.Range;

@ConfigGroup("AutoThiever")
public interface AutoThieverConfiguration extends Config {
    String version = "v1";
/*    @ConfigSection(
            name = "Lee esto",
            description = "Importante.",
            position = 2
    )*/
    String instructionsConfig2 = "instructionsConfig2";
    @ConfigSection(
            name = "Instrucciones",
            description = "Plugin Instrucciones.",
            position = 2
    )
    String instructionsConfig = "instructionsConfig";
    @ConfigSection(
            name = "Configuracion",
            description = "Plugin Configuracion.",
            position = 5
    )
    String setupConfig = "setupConfig";
    @ConfigSection(
            name = "Otro NPC Config",
            description = "Sección de configuración personalizada de NPC.",
            position = 13
    )
    String customNPCConfig = "customNPCConfig";
    @ConfigSection(
            name = "Game Tick Configuracion",
            description = "Configure cómo el bot maneja los retrasos en los ticks del juego; 1 tick del juego equivale aproximadamente a 600 ms",
            position = 57
    )
    String delayTickConfig = "delayTickConfig";
    @ConfigSection(
            name = "UI Ajustes",
            description = "UI Ajustes.",
            position = 80
    )
    String UIConfig = "UIConfig";

    //@ConfigItem(
    //        keyName = "instructions5",
    //        name = "",
    //        description = "Instructions.",
    //        position = 20,
    //        section = "instructionsConfig2"
    //)
    //default String instructions5() {
    //    return "";
        //return "Lee esto. \n\nLlamame Ing Darwuin Pedroza. \n\nEsto esta editado por El Guason user de discord factord.crypto\n\nCanal de Youtube   \n\nCanal de Discord ";
    //}

    @ConfigItem(
            keyName = "instructions7",
            name = "",
            description = "Instructions.",
            position = 1,
            section = "instructionsConfig"
    )
    default String instructions7() {
        return "Configure la tecla de acceso rápido y active el complemento con la tecla de acceso rápido.\n\nAbre automáticamente pouches al azar entre su rango de entrada.\n\nDeves escribir bien la comida que usaras.\n\nPara usar Shadow Veil, tienes que tener las runas en el inventorio. SI usas Rune Pouch, Asegúrate de tener suficientes runas ya que el complemento no buscará runas dentro de la bolsa..\n\nSi estas usando Redemption/Ancient brews No usarás comida a menos que te quedes sin brews..\n\n W322";
    }

    @ConfigItem(
            keyName = "start/stop hotkey",
            name = "Tecla",
            description = "Toca para activar y desactivar el plugin.",
            position = 6,
            section = "setupConfig"
    )
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "ThieveNPC",
            name = "NPC que vas a robar:",
            position = 7,
            section = "setupConfig",
            description = "Ingresa el tipo de NPC del que deseas robar."
    )
    default NPCs NPCToThieve() {
        return com.example.AutoThiever.NPCs.ARDOUGNE_KNIGHT;
    }

    @ConfigItem(
            keyName = "useShadowVeil",
            name = "Usar Shadow Veil?",
            description = "Habilite esto para usar el hechizo Velo de las Sombras.",
            section = "setupConfig",
            position = 8
    )
    default boolean shadowVeil() {
        return false;
    }

    @ConfigItem(
            keyName = "useRedemption",
            name = "Usar Redemption/Ancient Brews?",
            description = "Habilite esto para usar redemption prayer y ancient brews",
            section = "setupConfig",
            position = 9
    )
    default boolean useRedemption() {
        return false;
    }

    @ConfigItem(
            keyName = "dodgyNecklace",
            name = "Usar Dodgy?",
            description = "Habilite esto para usar dodgy necklaces",
            section = "setupConfig",
            position = 10
    )
    default boolean dodgyNecklace() {
        return false;
    }

    @ConfigItem(
            keyName = "dodgyAmount",
            name = "Cantidad de necklaces",
            position = 11,
            section = "setupConfig",
            description = "Cantidad de collares dudosos a retirar."
    )
    default int dodgyAmount() {
        return 0;
    }

    @ConfigItem(
            keyName = "AncientBrewAmount",
            name = "Ancient Brew:",
            position = 12,
            section = "setupConfig",
            description = "Cuantas quieres sacar"
    )
    default int AncientBrewAmount() {
        return 0;
    }

    @ConfigItem(
            keyName = "HealthLowAmount",
            name = "Comer si mi vida es menos:",
            position = 14,
            section = "setupConfig",
            description = "Comerá si la vida establecida + 0-5 para aleatorizar."
    )
    default int HealthLowAmount() {
        return 10;
    }

    @ConfigItem(
            keyName = "FoodItemName",
            name = "Food que quieras comer",
            description = "El nombre de la comida.",
            position = 15,
            section = "setupConfig"
    )
    default String FoodName() {
        return "";
    }

    @ConfigItem(
            keyName = "FoodAmount",
            name = "# cuantas",
            position = 16,
            section = "setupConfig",
            description = "Como cuantas quieres sacar."
    )
    default int FoodAmount() {
        return 5;
    }

    @Range(
            min = 1,
            max = 139
    )
    @ConfigItem(
            keyName = "minPouch",
            name = "Min Pouches",
            position = 17,
            section = "setupConfig",
            description = "Cantidad mínima de bolsas a tener en inventario antes de abrir."
    )
    default int MinPouches() {
        return 15;
    }

    @Range(
            min = 2,
            max = 140
    )
    @ConfigItem(
            keyName = "maxPouch",
            name = "Max Pouches",
            position = 18,
            section = "setupConfig",
            description = "Cantidad maxima de bolsas a tener en inventario antes de abrir."
    )
    default int MaxPouches() {
        return 28;
    }

    @ConfigItem(
            keyName = "customNPC",
            name = "Nombre del npc:",
            description = "Introduce el nombre del npc que quieres robar.",
            position = 15,
            section = "customNPCConfig"
    )
    default String CustomNPCName() {
        return "";
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
            name = "Game Tick Deviation",
            description = "",
            position = 61,
            section = "delayTickConfig"
    )
    default int tickDelayDeviation() {
        return 1;
    }

    @ConfigItem(
            keyName = "tickDelayWeightedDistribution",
            name = "Game Tick Weighted Distribution",
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
            description = "Elija el diseño de interfaz de usuario que desee.",
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
            description = "Habilitar para activar en el juego UI",
            section = "UIConfig",
            position = 140
    )
    default boolean enableUI() {
        return true;
    }
}
