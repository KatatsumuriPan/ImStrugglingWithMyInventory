package kpan.kuso.iswmi;

import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public enum EnumState {
    DISABLED,
    HOTBAR,
    ROW1,
    ROW2,
    ROW3,
    ARMOR,
    ;
    public EnumState next() {
        return switch (this) {
            case DISABLED -> DISABLED;
            case ROW1 -> ROW2;
            case ROW2 -> ROW3;
            case ROW3 -> HOTBAR;
            case HOTBAR -> ARMOR;
            case ARMOR -> ROW1;
        };
    }
    public int color() {
        return switch (this) {
            case DISABLED -> 0xFF00_0000;
            case HOTBAR -> 0xFFFF_8888;
            case ROW1 -> 0xFF88_88FF;
            case ROW2 -> 0xFF77_EE77;
            case ROW3 -> 0xFFEE_EE55;
            case ARMOR -> 0xFFFF_55FF;
        };
    }
    public SoundEvent sound() {
        return switch (this) {
            case DISABLED -> SoundEvents.BLOCK_NOTE_BLOCK_PLING.value();
            case HOTBAR -> SoundEvents.BLOCK_NOTE_BLOCK_PLING.value();
            case ROW1 -> SoundEvents.BLOCK_NOTE_BLOCK_HARP.value();
            case ROW2 -> SoundEvents.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE.value();
            case ROW3 -> SoundEvents.BLOCK_NOTE_BLOCK_CHIME.value();
            case ARMOR -> SoundEvents.BLOCK_NOTE_BLOCK_GUITAR.value();
        };

    }
}
