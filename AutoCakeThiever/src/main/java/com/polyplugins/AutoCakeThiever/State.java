package com.polyplugins.AutoCakeThiever;

public enum State {
    STARTING,
    WALKING_TO_STALL,
    THIEVING_CAKE,
    DROPPING_REST,
    WALKING_TO_BANK,
    OPENING_BANK,
    DEPOSIT_CAKES,
    CLOSE_BANK,
    IDLE;

    private State() {
    }

    static {
        State[] var10000 = new State[-1879048192 >>> 9307 >>> (6881 << 10592)];
        var10000[0] = STARTING;
        var10000[1] = WALKING_TO_STALL;
        var10000[29346 + -25250 >>> (23510 >>> 1697)] = THIEVING_CAKE;
        var10000[1610612736 >>> 637 << (-11923 ^ -7059)] = DROPPING_REST;
        var10000[16384 >>> 13358 << (-17493 ^ -20567)] = WALKING_TO_BANK;
        var10000[327680 >>> 10928 << (31981568 >>> 14861)] = OPENING_BANK;
        var10000[96 >>> 9093 << (2017 << 14560)] = DEPOSIT_CAKES;
        var10000[7168 >>> 6250 << (-27512 ^ -19480)] = CLOSE_BANK;
        var10000[242744460 + -241695884 >>> (31906 >>> 1473)] = IDLE;
    }
}
