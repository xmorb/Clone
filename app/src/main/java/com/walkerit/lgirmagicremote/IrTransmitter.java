package com.walkerit.lgirmagicremote;

import android.content.Context;
import android.hardware.ConsumerIrManager;

/** Encodes and transmits LG NEC infrared frames through Android ConsumerIrManager. */
public final class IrTransmitter {
    private static final int CARRIER_FREQUENCY_HZ = 38_000;
    private static final int HEADER_MARK_US = 9_000;
    private static final int HEADER_SPACE_US = 4_500;
    private static final int BIT_MARK_US = 560;
    private static final int ONE_SPACE_US = 1_690;
    private static final int ZERO_SPACE_US = 560;

    private final ConsumerIrManager consumerIrManager;

    public IrTransmitter(Context context) {
        consumerIrManager = (ConsumerIrManager) context.getSystemService(Context.CONSUMER_IR_SERVICE);
    }

    public boolean isAvailable() {
        return consumerIrManager != null && consumerIrManager.hasIrEmitter();
    }

    public void transmit(LgCommand command) {
        if (!isAvailable()) {
            throw new IllegalStateException("This device does not expose a consumer IR emitter.");
        }
        consumerIrManager.transmit(CARRIER_FREQUENCY_HZ, buildNecPattern(command.getFrame()));
    }

    private int[] buildNecPattern(int frame) {
        int[] pattern = new int[67];
        int position = 0;
        pattern[position++] = HEADER_MARK_US;
        pattern[position++] = HEADER_SPACE_US;

        for (int byteIndex = 3; byteIndex >= 0; byteIndex--) {
            int currentByte = (frame >>> (byteIndex * 8)) & 0xFF;
            for (int bitIndex = 0; bitIndex < 8; bitIndex++) {
                boolean one = ((currentByte >>> bitIndex) & 1) == 1;
                pattern[position++] = BIT_MARK_US;
                pattern[position++] = one ? ONE_SPACE_US : ZERO_SPACE_US;
            }
        }

        pattern[position] = BIT_MARK_US;
        return pattern;
    }
}
