package com.polyplugins.AutoMiner;

public enum AutoMinerState {
    TIMEOUT,
    FIND_BANK,
    ANIMATING,
    MINE,
    HANDLE_BANK,
    DROP_INVENTORY,
    UNHANDLED_STATE,
    BANK_PIN,
    MOVING,
    IDLE;

    private AutoMinerState() {
    }
}
