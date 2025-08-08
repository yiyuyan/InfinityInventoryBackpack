package cn.ksmcbrigade.iib;

import cn.ksmcbrigade.iib.client.ClientTemps;
import cn.ksmcbrigade.iib.network.ClientBoundCallbackNetworkMessage;
import cn.ksmcbrigade.iib.network.ClientBoundNetworkMessage;
import cn.ksmcbrigade.iib.network.NetworkMessage;
import cn.ksmcbrigade.iib.network.SaveMessage;
import cn.ksmcbrigade.iib.util.PlayerExInventoryDataAccess;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Objects;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(InfinityInventoryBackpack.MOD_ID)
public class InfinityInventoryBackpack {

    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "iib";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static int serverIndex = 0;
    public static boolean check = true;

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(ResourceLocation.tryBuild(MOD_ID,"open"),()->"340","340"::equals,"340"::equals);

    public InfinityInventoryBackpack() {
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON,Config.SPEC);
        CHANNEL.registerMessage(0, NetworkMessage.class,NetworkMessage::encode,NetworkMessage::decode,(msg,context)->{
            try {
                if(context.get().getSender()==null){
                    LOGGER.warn("The sender is null.");
                }
                ((PlayerExInventoryDataAccess) Objects.requireNonNull(context.get().getSender())).setStop(true);
                serverIndex = msg.index();
                CHANNEL.sendTo(new ClientBoundNetworkMessage(serverIndex+1),context.get().getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
                Objects.requireNonNull(context.get().getSender()).openMenu(new SimpleMenuProvider(new MenuConstructor() {
                    @Override
                    public @NotNull AbstractContainerMenu createMenu(int p_39954_, @NotNull Inventory p_39955_, @NotNull Player p_39956_) {
                        AbstractContainerMenu menu = ChestMenu.sixRows(p_39954_,p_39955_);
                        ItemStack NEXT = Items.ARROW.getDefaultInstance();
                        ItemStack LAST = Items.ARROW.getDefaultInstance();
                        NEXT.setHoverName(Component.literal("[IB]Next"));
                        LAST.setHoverName(Component.literal("[IB]Last"));

                        for (int i = 0; i < ((PlayerExInventoryDataAccess) p_39956_).getStacks(msg.index()).size(); i++) {
                            menu.setItem(i,menu.getStateId(),((PlayerExInventoryDataAccess) p_39956_).getStacks(msg.index()).get(i));
                        }

                        menu.setItem(53,menu.getStateId(),NEXT);
                        menu.setItem(45,menu.getStateId(),LAST);
                        return menu;
                    }
                }, Component.literal("InfinityBackpack#"+msg.index())));
                ((PlayerExInventoryDataAccess) Objects.requireNonNull(context.get().getSender())).setStop(false);
                LOGGER.debug("Opened a ex inventory menu {}",msg.index());
            } catch (Exception e) {
                LOGGER.error("Can't open the infinity backpack for player.Index: {}",msg.index(),e);
            }
            context.get().setPacketHandled(true);
        });

        CHANNEL.registerMessage(1, ClientBoundNetworkMessage.class,ClientBoundNetworkMessage::encode,ClientBoundNetworkMessage::decode,(msg,context)->{
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,()->()-> ClientTemps.nextIndex = msg.index());
            serverIndex = msg.index();
            context.get().setPacketHandled(true);
        });

        CHANNEL.registerMessage(2, ClientBoundCallbackNetworkMessage.class,ClientBoundCallbackNetworkMessage::encode,ClientBoundCallbackNetworkMessage::decode,(msg,context)-> {
            check = false;
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,()->()-> ClientTemps.handleCallback(msg));
            context.get().setPacketHandled(true);
        });

        CHANNEL.registerMessage(3, SaveMessage.class,SaveMessage::encode,SaveMessage::decode,(msg,context)->{
            try {

                if(context.get().getSender()!=null){
                    ((PlayerExInventoryDataAccess) Objects.requireNonNull(context.get().getSender())).setStacks(msg.index(),new ArrayList<>(msg.stacks()));
                }

                //LOGGER.info("Saved the ex inventory.");
            } catch (Exception e) {
                LOGGER.error("Failed to save the inventory data.",e);
            }
            context.get().setPacketHandled(true);
        });
    }

}