package cn.ksmcbrigade.iib.network;

import cn.ksmcbrigade.iib.InfinityInventoryBackpack;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public record ClientBoundCallbackNetworkMessage(int index, Collection<ItemStack> stacks) {
    public static void encode(ClientBoundCallbackNetworkMessage message, FriendlyByteBuf buf){
        try {
            buf.writeInt(message.index);
            JsonArray array = new JsonArray();
            for (ItemStack stack : message.stacks) {
                array.add(NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE,stack.save(new CompoundTag())));
            }
            buf.writeByteArray(array.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ClientBoundCallbackNetworkMessage decode(FriendlyByteBuf friendlyByteBuf){
        List<ItemStack> stacks1 = new ArrayList<>();
        int index = friendlyByteBuf.readInt();
        try {
            String s = new String(friendlyByteBuf.readByteArray());
            for (JsonElement jsonElement : JsonParser.parseString(s).getAsJsonArray()) {
                stacks1.add(ItemStack.of((CompoundTag) JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE,jsonElement)));
            }
        } catch (Exception e) {
            InfinityInventoryBackpack.LOGGER.info("Can't parse the string.",e);
        }
        return new ClientBoundCallbackNetworkMessage(index,stacks1);
    }
}
