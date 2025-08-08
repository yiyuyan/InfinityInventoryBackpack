package cn.ksmcbrigade.iib.mixin;

import cn.ksmcbrigade.iib.InfinityInventoryBackpack;
import cn.ksmcbrigade.iib.util.PlayerExInventoryDataAccess;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.extensions.IForgeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements IForgeEntity, PlayerExInventoryDataAccess {

    @Unique
    private ArrayList<List<ItemStack>> infinityinventorybackpack$arrayLists = new ArrayList<>();
    @Unique
    private boolean infinityinventorybackpack$stopTick = false;
    
    protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Inject(method = "addAdditionalSaveData",at = @At("TAIL"))
    public void addData(CompoundTag p_36265_, CallbackInfo ci){
        InfinityInventoryBackpack.LOGGER.info("Saving the ex inventory...");
        JsonObject o = new JsonObject();
        JsonArray array = new JsonArray();
        for (List<ItemStack> arrayList : infinityinventorybackpack$arrayLists) {
            JsonArray array1 = new JsonArray();
            for (ItemStack stack : arrayList) {
                array1.add(NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, stack.save(new CompoundTag())));
            }
            array.add(array1);
        }
        o.add("data",array);
        p_36265_.put("exInventory",JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE,o));

        InfinityInventoryBackpack.LOGGER.debug("Saved {}", o);
    }

    @Inject(method = "readAdditionalSaveData",at = @At("TAIL"))
    public void readData(CompoundTag p_36265_, CallbackInfo ci){
        InfinityInventoryBackpack.LOGGER.info("Reading the ex inventory...");
        if(p_36265_.contains("exInventory")){
            JsonArray array = NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE,p_36265_.getCompound("exInventory")).getAsJsonObject().getAsJsonArray("data");
            for (JsonElement jsonElement : array) {
                if(jsonElement instanceof JsonArray object){
                    ArrayList<ItemStack> stacks = new ArrayList<>();
                    for (JsonElement items : object) {
                        stacks.add(ItemStack.of((CompoundTag) JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE,items)));
                    }
                    infinityinventorybackpack$arrayLists.add(stacks);
                }
            }
            InfinityInventoryBackpack.LOGGER.debug("Read: {}", array);
        }
        infinityinventorybackpack$arrayLists.trimToSize();
    }
    
    @Unique
    @Override
    public void setStacks(int index, List<ItemStack> stacks){
        infinityinventorybackpack$arrayLists.set(index,stacks);
    }
    
    @Unique
    @Override
    public List<ItemStack> getStacks(int index){
        if(infinityinventorybackpack$arrayLists.size()<=index){
            ArrayList<ItemStack> stacks = new ArrayList<>();
            for (int i = 0; i < 54; i++) {
                stacks.add(ItemStack.EMPTY);
            }
            infinityinventorybackpack$arrayLists.add(stacks);
        }
        return infinityinventorybackpack$arrayLists.get(index);
    }

    public void setStop(boolean value){
        this.infinityinventorybackpack$stopTick = value;
    }
    public boolean tickStop(){
        return this.infinityinventorybackpack$stopTick;
    }
}
