package com.polyplugins.AutoCakeThiever;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("autoCakeThieverConfig")
public interface AutoCakeThieverConfig extends Config {
    @ConfigSection(
            name = "<html><font color=\"#86C43FFF\">Lee esto</font>",
            description = "Importante.",
            position = -56
    )
    String instructionsConfig2 = "instructionsConfig2";
    @ConfigSection(
            name = "<html><font color=\"#86C43FFF\">Instrucciones</font>",
            description = "Instructions on how to use the plugin",
            position = 0
    )
    String instructionsSection = "instructionsSection";
    @ConfigSection(
            name = "<html><font color=\"#86C43FFF\">UI Settings</font>",
            description = "Settings related to the user interface",
            position = 4
    )
    String uiSettings = "uiSettings";
    @ConfigSection(
            name = "<html><font color=\"#86C43FFF\">Configuracion Comida</font>",
            description = "Food eating",
            position = 6
    )
    String foodEatingSection = "foodEatingSection";
    @ConfigSection(
            name = "<html><font color=\"#86C43FFF\">Drop Configuracion</font>",
            description = "Food eating",
            position = 7
    )
    String dropsettings = "dropBreadAndSlices";

    @ConfigItem(
            keyName = "instructions5",
            name = "",
            description = "Instructions.",
            position = -56,
            section = "instructionsConfig2"
    )
    default String instructions5() {
        return "Lee esto. \n\nEres un idiota si pagaste esto por algun grupo de whatsapp o facebook por este plugin. \n\nEsto esta editado por ElKondo user de discord factord.crypto\n\nCanal de Youtube https://www.youtube.com/@kondo239  \n\nCanal de Discord https://discord.com/invite/URXjtjambp";
    }

    @ConfigItem(
            position = 1,
            keyName = "instructionsText3",
            name = "Plugin Instrucciones",
            description = "Detailed instructions on how to use the plugin",
            section = "instructionsSection"
    )
    default String instructionsText3() {
        return "Empienza este plugin desde el banco de ardy, el hara el resto.";
    }

    @ConfigItem(
            keyName = "uiLayout",
            name = "UI Layout",
            description = "Select the UI layout for the overlay",
            section = "uiSettings",
            position = 5
    )
    default UiLayoutOption uiLayout() {
        return UiLayoutOption.FULL;
    }

    @ConfigItem(
            position = 1,
            keyName = "autoEatEnabled",
            name = "Habilitar Auto-Comer",
            description = "Comer automaticamente cuando la vida este baja",
            section = "foodEatingSection"
    )
    default boolean autoEatEnabled() {
        return false;
    }

    @ConfigItem(
            position = 2,
            keyName = "foodName",
            name = "Nombre de la comida",
            description = "Nombre or id",
            section = "foodEatingSection"
    )
    default String getFoodName() {
        return "Cake";
    }

    @ConfigItem(
            position = 3,
            keyName = "healthThreshold",
            name = "Comer entre",
            description = "Comer apartir que la vida este en",
            section = "foodEatingSection"
    )
    default int getHealthThreshold() {
        return 819200 >>> 10223 << (-26164 ^ -19411);
    }

    @ConfigItem(
            position = 2,
            keyName = "dropBreadAndSlices",
            name = "Drop Bread y Chocolate Slices",
            description = "Si lo activa botara los panes y los pedazos de chocolate.",
            section = "dropBreadAndSlices"
    )
    default boolean dropBreadAndSlices() {
        return false;
    }
}
