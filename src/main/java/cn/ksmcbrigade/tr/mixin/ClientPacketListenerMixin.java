package cn.ksmcbrigade.tr.mixin;

import cn.ksmcbrigade.tr.TotemRegister;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;

@OnlyIn(Dist.CLIENT)
@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Redirect(method = "findTotem",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
    private static boolean check(ItemStack instance, Item p_150931_){
        ArrayList<Item> stacks = new ArrayList<>();
        stacks.add(p_150931_);
        stacks.addAll(TotemRegister.getAllRegisteredTotems());

        for (Item stack : stacks) {
            if(instance.is(stack)){
                return true;
            }
        }

        return false;
    }
}
