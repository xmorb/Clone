package com.walkerit.lgirmagicremote;

/**
 * Known LG television NEC infrared command frames.
 *
 * Each value is the complete 32-bit frame commonly represented by universal
 * remote databases, for example 0x20DF10EF for power. The transmitter sends
 * each byte least-significant-bit first as required by NEC encoding.
 */
public enum LgCommand {
    POWER(0x20DF10EF),
    INPUT(0x20DFD02F),
    TV_RADIO(0x20DF0FF0),
    SETTINGS(0x20DFC23D),
    QUICK_SETTINGS(0x20DFA25D),
    HOME(0x20DF3EC1),
    GUIDE(0x20DFD52A),
    INFO(0x20DF55AA),
    BACK(0x20DF14EB),
    EXIT(0x20DFDA25),
    UP(0x20DF02FD),
    DOWN(0x20DF827D),
    LEFT(0x20DFE01F),
    RIGHT(0x20DF609F),
    OK(0x20DF22DD),
    VOLUME_UP(0x20DF40BF),
    VOLUME_DOWN(0x20DFC03F),
    MUTE(0x20DF906F),
    CHANNEL_UP(0x20DF00FF),
    CHANNEL_DOWN(0x20DF807F),
    LIST(0x20DFCA35),
    Q_MENU(0x20DFA25D),
    RED(0x20DF4EB1),
    GREEN(0x20DF8E71),
    YELLOW(0x20DFC639),
    BLUE(0x20DF8679),
    PLAY(0x20DF0DF2),
    PAUSE(0x20DF5DA2),
    STOP(0x20DF8D72),
    REWIND(0x20DFF10E),
    FAST_FORWARD(0x20DF718E),
    RECORD(0x20DFBD42),
    NUM_0(0x20DF08F7),
    NUM_1(0x20DF8877),
    NUM_2(0x20DF48B7),
    NUM_3(0x20DFC837),
    NUM_4(0x20DF28D7),
    NUM_5(0x20DFA857),
    NUM_6(0x20DF6897),
    NUM_7(0x20DFE817),
    NUM_8(0x20DF18E7),
    NUM_9(0x20DF9867);

    private final int frame;

    LgCommand(int frame) {
        this.frame = frame;
    }

    public int getFrame() {
        return frame;
    }
}
