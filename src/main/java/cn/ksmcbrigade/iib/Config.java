package cn.ksmcbrigade.iib;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec.BooleanValue SAVE_CURRENT_INV_INDEX = BUILDER.define("save_current_inventory_index",false);
    public static final ForgeConfigSpec SPEC = BUILDER.build();
}
