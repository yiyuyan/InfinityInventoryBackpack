package cn.ksmcbrigade.iib.util;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface PlayerExInventoryDataAccess {
    void setStacks(int index, List<ItemStack> stacks);
    List<ItemStack> getStacks(int index);
    void setStop(boolean value);
    boolean tickStop();
}
