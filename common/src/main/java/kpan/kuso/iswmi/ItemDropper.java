package kpan.kuso.iswmi;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

public class ItemDropper {

    public static void drop(PlayerEntity player, EnumState enumState) {
        PlayerInventory inventory = player.getInventory();
        List<Integer> slots = getSlots(enumState);
        if (slots.isEmpty())
            return;
        boolean duplicate = player.getRandom().nextFloat() < 0.05F;
        boolean boostSpeed = player.getRandom().nextFloat() < 0.05F;
        for (Integer index : slots) {
            ItemStack itemStack = inventory.removeStack(index);
            if (itemStack.isEmpty())
                continue;
            ItemEntity itemEntity = createDropItem(player, itemStack, index != inventory.selectedSlot, true);
            if (boostSpeed)
                itemEntity.setVelocity(itemEntity.getVelocity().multiply(10, 1, 10).add(0, 0.2, 0));
            spawn(itemEntity, player);
            if (duplicate) {
                ItemEntity itemEntity2 = player.dropItem(itemStack, index != inventory.selectedSlot, true);
                if (boostSpeed)
                    itemEntity2.setVelocity(itemEntity2.getVelocity().multiply(10, 1, 10).add(0, 0.2, 0));
                spawn(itemEntity, player);
            }
        }
    }

    public static List<Integer> getSlots(EnumState enumState) {
        List<Integer> slots = new ArrayList<>();
        switch (enumState) {
            case DISABLED -> {
                return slots;
            }
            case HOTBAR -> {
                slots.addAll(IntStream.range(0, 9).boxed().toList());
            }
            case ROW1 -> {
                slots.addAll(IntStream.range(9, 18).boxed().toList());
            }
            case ROW2 -> {
                slots.addAll(IntStream.range(18, 27).boxed().toList());
            }
            case ROW3 -> {
                slots.addAll(IntStream.range(27, 36).boxed().toList());
            }
            case ARMOR -> {
                slots.addAll(IntStream.range(36, 36 + 4 + 1).boxed().toList());
            }
        }
        return slots;
    }

    public static ItemEntity createDropItem(PlayerEntity player, ItemStack stack, boolean throwRandomly, boolean retainOwnership) {
        ItemEntity itemEntity = new ItemEntity(player.getWorld(), player.getX(), player.getEyeY() - 0.3, player.getZ(), stack);
        itemEntity.setPickupDelay(40);
        if (retainOwnership)
            itemEntity.setThrower(player.getUuid());

        Random random = player.getRandom();
        if (throwRandomly) {
            float x = random.nextFloat() * 0.5F;
            float z = random.nextFloat() * (MathHelper.PI * 2);
            itemEntity.setVelocity(-MathHelper.sin(z) * x, 0.2, MathHelper.cos(z) * x);
        } else {
            float velocity = 0.3F;
            float yawSin = MathHelper.sin(player.getYaw() * (MathHelper.PI / 180));
            float yawCos = MathHelper.cos(player.getYaw() * (MathHelper.PI / 180));
            float randomTheta = random.nextFloat() * (MathHelper.PI * 2);
            float randomness = 0.02F * random.nextFloat();
            itemEntity.setVelocity(-yawSin * velocity + Math.cos(randomTheta) * randomness, 0.1F + (random.nextFloat() - random.nextFloat()) * 0.1F, (double) (yawCos * velocity) + Math.sin(randomTheta) * (double) randomness);
        }

        return itemEntity;
    }

    private static void spawn(ItemEntity itemEntity, PlayerEntity player) {
        player.getEntityWorld().spawnEntity(itemEntity);
        ItemStack itemStack = itemEntity.getStack();
        player.increaseStat(Stats.DROPPED.getOrCreateStat(itemStack.getItem()), itemStack.getCount());
        player.incrementStat(Stats.DROP);
    }
}
