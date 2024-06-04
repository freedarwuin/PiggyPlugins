package com.polyplugins.crabs;

import net.runelite.api.coords.WorldPoint;

import java.util.List;

public enum TilePelea {
    CRABS_3("3", List.of(new WorldPoint(1776, 3468, 0), new WorldPoint(1773, 3461, 0), new WorldPoint(1749, 3469, 0))),
    CRABS_2("2", List.of(new WorldPoint(1791, 3468, 0))),
    CRABS_4("4", List.of(new WorldPoint(1765, 3468, 0))),
    CRABS_1("1", List.of(new WorldPoint(1768, 3409, 0), new WorldPoint(1780, 3407, 0), new WorldPoint(1749, 3412, 0)));

    private final String nCrabs;
    private final List<WorldPoint> puntos;

    public String toString() {
        return this.nCrabs;
    }

    public String getNCrabs() {
        return this.nCrabs;
    }

    public List<WorldPoint> getPuntos() {
        return this.puntos;
    }

    private TilePelea(String nCrabs, List puntos) {
        this.nCrabs = nCrabs;
        this.puntos = puntos;
    }
}

