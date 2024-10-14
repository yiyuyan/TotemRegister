package cn.ksmcbrigade.tr.config;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder().comment("TotemRegister Mod Config");
    public static final ForgeConfigSpec.ConfigValue<String> REGISTER = BUILDER.comment("Register the items,use the \",\" to split the items,for example,minecraft:dirt,minecraft:diamond").define("registers","");
    public static final ForgeConfigSpec CONFIG = BUILDER.build();

    public static List<Item> get(){
        if(REGISTER.get().isEmpty()) return List.of();
        String config = REGISTER.get();
        String str = config+(config.contains(",")?"":",");

        ArrayList<Item> items = new ArrayList<>();

        for (String s : str.split(",")) {
            Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(s));
            if(item!=null) items.add(item);
        }

        return items;
    }
}
