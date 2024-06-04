package com.polyplugins.crabs;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("crabs")
public interface CrabsConfig extends Config {
    @ConfigSection(
            name = "Lee esto",
            description = "Importante.",
            position = -56
    )
    String instructionsConfig2 = "instructionsConfig2";

    @ConfigItem(
            name = "On/Off",
            keyName = "onOff",
            description = "Start/stop button"
    )
    default boolean onOff() {
        return false;
    }

    @ConfigItem(
            name = "Spot",
            keyName = "spot",
            description = "Choose spot"
    )
    default TilePelea spot() {
        return TilePelea.CRABS_3;
    }

    @ConfigItem(
            name = "Evade crashers",
            keyName = "evadeCrashers",
            description = "It will hop or change spot if crasher"
    )
    default boolean evadeCrashers() {
        return false;
    }

    @ConfigItem(
            keyName = "instructions5",
            name = "",
            description = "Instructions.",
            position = -56,
            section = "instructionsConfig2"
    )
    default String instructions5() {
        return "Lee esto. \n\nEres un idiota si pagaste esto por algun grupo de whatsapp o facebook por este plugin. \n\nEsto esta editado por El Guason user de discord factord.crypto\n\nCanal de Youtube https://www.youtube.com/@El Guason  \n\nCanal de Discord https://discord.com/invite/URXjtjambp";
    }

    public static enum nCrabs {
        CRAB_1("1 Crab"),
        CRAB_2("2 Crabs"),
        CRAB_3("3 Crabs"),
        CRAB_4("4 Crabs");

        private final String texto;

        private nCrabs(String texto) {
            this.texto = texto;
        }

        public String toString() {
            return this.texto;
        }
    }
}

