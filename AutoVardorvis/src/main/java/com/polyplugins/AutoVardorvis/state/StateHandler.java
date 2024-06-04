package com.polyplugins.AutoVardorvis.state;

import com.polyplugins.AutoVardorvis.AutoVardorvisPlugin;
import com.polyplugins.AutoVardorvis.state.botStates.BankingState;
import com.polyplugins.AutoVardorvis.state.botStates.FightingState;
import com.polyplugins.AutoVardorvis.state.botStates.GoToBankState;
import com.polyplugins.AutoVardorvis.state.botStates.GoToVardorvisState;
import com.polyplugins.AutoVardorvis.state.botStates.TestingState;

public class StateHandler {
    public StateHandler() {
    }

    public void handleState(State state, AutoVardorvisPlugin.MainClassContext context) {
        switch (SyntheticClass_1.$SwitchMap$com$wigglydonplugins$AutoVardorvis$state$StateHandler$State[state.ordinal()] ^ -1250670369 >>> ("028e".hashCode() ^ 1483695) >>> -11250 + 19666) {
            case -1250670374:
                (new GoToBankState()).execute(context);
                break;
            case -1250670373:
                (new GoToVardorvisState()).execute(context);
                break;
            case -1250670372:
                (new FightingState()).execute(context);
                break;
            case -1250670371:
                (new BankingState()).execute(context);
                break;
            case -1250670370:
                (new TestingState()).execute(context);
                break;
            default:
                System.out.println("Unknown state!");
        }

    }

    public static enum State {
        TESTING,
        BANKING,
        GO_TO_VARDORVIS,
        FIGHTING,
        GO_TO_BANK;

        private State() {
        }

        static {
            State[] var10000 = new State[163840 >>> 13551 << -8441 - -13017];
            var10000[0] = TESTING;
            var10000[1] = BANKING;
            var10000[2048 >>> 10699 << 17205 + -2484] = GO_TO_VARDORVIS;
            var10000[3072 >>> 13770 << (40763392 >>> 13068)] = FIGHTING;
            var10000[(-8185 ^ -4089) >>> (2245 << 15457)] = GO_TO_BANK;
        }
    }
}
