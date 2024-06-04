package com.polyplugins.AutoVardorvis;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("AutoVardorvis")
public interface AutoVardorvisConfig extends Config {
    @ConfigSection(
            name = "Lee esto",
            description = "Importante.",
            position = -56
    )
    String instructionsConfig2 = "instructionsConfig2";

    @ConfigItem(
            keyName = "instructions5",
            name = "",
            description = "Instructions.",
            position = -56,
            section = "instructionsConfig2"
    )
    default String instructions5() {
        return "Lee esto. \n\nEres un idiota si pagaste esto por algun grupo de whatsapp o facebook por este plugin. \n\nEsto esta editado por ElKondo user de discord factord.crypto\n\nCanal de Youtube https://www.youtube.com/@ElKondo  \n\nCanal de Discord https://discord.com/invite/URXjtjambp";
    }

    @ConfigItem(
            keyName = "eatat",
            name = "Comer cuando mi vida este en",
            description = "Eat at what health?",
            position = 0
    )
    default int EATAT() {
        return (char)9600 >>> 12039 << (164 << 8741);
    }

    @ConfigItem(
            keyName = "drinkprayerat",
            name = "Tomar Prayer potion en*",
            description = "",
            position = 1
    )
    default int DRINKPRAYERAT() {
        return 1186809889 + -1060980769 >>> (74168 >>> 2243);
    }

    @ConfigItem(
            keyName = "minFood",
            name = "Comida minima*",
            description = "",
            position = 2
    )
    default int MIN_FOOD() {
        return (char)224 >>> 2885 << (316669952 >>> (char)5359);
    }

    @ConfigItem(
            keyName = "minPrayer",
            name = "Minimo Prayer",
            description = "",
            position = 2
    )
    default int MIN_PRAYER_POTIONS() {
        return 1;
    }

    @ConfigItem(
            keyName = "ppotsToBring",
            name = "Prayer Pots traer*",
            description = "",
            position = 3
    )
    default int PPOTS_TO_BRING() {
        return 234347103 + -209181279 >>> -23965 + 25716;
    }
}
