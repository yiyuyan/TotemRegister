package cn.ksmcbrigade.tr.mixin;

import cn.ksmcbrigade.tr.TotemRegister;
import cn.ksmcbrigade.tr.config.TotemInfo;
import net.minecraft.advancements.critereon.UsedTotemTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Unique
    private Item tr$item;

    @Unique
    private TotemInfo tr$info;

    @Unique
    private ServerPlayer tr$player;

    @Unique
    private ItemStack tr$stack;

    public LivingEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Redirect(method = "checkTotemDeathProtection",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
    public boolean check(ItemStack instance, Item p_150931_){
        ArrayList<Item> stacks = new ArrayList<>();
        stacks.add(p_150931_);
        stacks.addAll(TotemRegister.getAllRegisteredTotems());

        for (Item stack : stacks) {
            if(instance.is(stack)){
                tr$item = stack;
                return true;
            }
        }

        return false;
    }

    @Redirect(method = "checkTotemDeathProtection",at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/critereon/UsedTotemTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/item/ItemStack;)V"))
    public void record(UsedTotemTrigger instance, ServerPlayer p_74432_, ItemStack p_74433_){
        this.tr$player = p_74432_;
        this.tr$stack = p_74433_;
        instance.trigger(p_74432_,p_74433_);
    }

    @Inject(method = "checkTotemDeathProtection",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setHealth(F)V",shift = At.Shift.BEFORE), cancellable = true)
    public void trigger(DamageSource p_21263_, CallbackInfoReturnable<Boolean> cir){
        tr$info = TotemRegister.getInfo(tr$item);

        if(tr$info!=null && tr$info.overwrite() && tr$info.runnable()!=null){
            tr$info.runnable().accept(this.tr$player,this.tr$stack);
            this.level().broadcastEntityEvent(this, (byte)35);
            cir.setReturnValue(tr$item!=null);
        }
    }

    @Inject(method = "checkTotemDeathProtection",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z",shift = At.Shift.AFTER,ordinal = 2))
    public void trigger2(DamageSource p_21263_, CallbackInfoReturnable<Boolean> cir){
        tr$info = TotemRegister.getInfo(tr$item);

        if(tr$info!=null && tr$info.runnable()!=null){
            tr$info.runnable().accept(this.tr$player,this.tr$stack);
        }
    }
}
