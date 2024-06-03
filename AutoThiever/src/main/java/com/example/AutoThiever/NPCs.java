package com.example.AutoThiever;

public enum NPCs {
    MASTER_FARMER("Master Farmer"),
    ARDOUGNE_KNIGHT("Knight of Ardougne"),
    PRIF_LINDIR("Lindir"),
    Otro("Otro");

    private final String npcName;

    private NPCs(String npcName) {
        this.npcName = npcName;
    }

    public String getNpcName() {
        return this.npcName;
    }
}
