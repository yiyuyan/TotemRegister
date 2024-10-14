package cn.ksmcbrigade.tr;

import cn.ksmcbrigade.tr.config.Config;
import cn.ksmcbrigade.tr.config.TotemInfo;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiConsumer;

@Mod(TotemRegister.MODID)
public class TotemRegister {

    public static final String MODID = "tr";

    private static final Map<Item,Map<BiConsumer<ServerPlayer, ItemStack>,Boolean>> registered = new HashMap<>();

    public TotemRegister() {
        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.CONFIG);

        new Thread(()->{
            while(!Config.CONFIG.isLoaded()){
                Thread.yield();
            }
            for (Item item : Config.get()) {
                register(item);
                System.out.println("[TR] Registered a totem: "+item);
            }
        }).start();
    }

    public static void register(Item totem){
        registered.put(totem,null);
    }

    public static void register(Item totem,BiConsumer<ServerPlayer, ItemStack> runnable){
        register(totem,runnable,false);
    }

    public static void register(Item totem,BiConsumer<ServerPlayer, ItemStack> runnable,boolean overwrite){
        Map<BiConsumer<ServerPlayer, ItemStack>,Boolean> tmp = new HashMap<>();
        tmp.put(runnable,overwrite);
        registered.put(totem,tmp);
    }

    public static void register(Class<?> clazz) throws InstantiationException, IllegalAccessException {
        for (Field declaredField : clazz.getDeclaredFields()) {
            if(declaredField.getType().equals(Item.class)){
                declaredField.setAccessible(true);
                register((Item) declaredField.get(clazz.newInstance()));
            }
        }
    }



    public static void register(Class<?> clazz,BiConsumer<ServerPlayer, ItemStack> runnable) throws InstantiationException, IllegalAccessException {
        for (Field declaredField : clazz.getDeclaredFields()) {
            if(declaredField.getType().equals(Item.class)){
                declaredField.setAccessible(true);
                register((Item) declaredField.get(clazz.newInstance()),runnable);
            }
        }
    }



    public static void register(Class<?> clazz, List<BiConsumer<ServerPlayer, ItemStack>> runnable) throws InstantiationException, IllegalAccessException {
        Field[] fields = clazz.getDeclaredFields();
        for (int i=0;i<fields.length;i++) {
            Field declaredField = fields[i];
            if(declaredField.getType().equals(Item.class)){
                declaredField.setAccessible(true);
                register((Item) declaredField.get(clazz.newInstance()),runnable.get(i));
            }
        }
    }

    public static void register(Class<?> clazz, List<BiConsumer<ServerPlayer, ItemStack>> runnable,boolean overwrite) throws InstantiationException, IllegalAccessException {
        Field[] fields = clazz.getDeclaredFields();
        for (int i=0;i<fields.length;i++) {
            Field declaredField = fields[i];
            if(declaredField.getType().equals(Item.class)){
                declaredField.setAccessible(true);
                register((Item) declaredField.get(clazz.newInstance()),runnable.get(i),overwrite);
            }
        }
    }

    public static void register(Class<?> clazz, BiConsumer<ServerPlayer, ItemStack> runnable,List<Boolean> overwrite) throws InstantiationException, IllegalAccessException {
        Field[] fields = clazz.getDeclaredFields();
        for (int i=0;i<fields.length;i++) {
            Field declaredField = fields[i];
            if(declaredField.getType().equals(Item.class)){
                declaredField.setAccessible(true);
                register((Item) declaredField.get(clazz.newInstance()),runnable,overwrite.get(i));
            }
        }
    }

    public static void register(Class<?> clazz, List<BiConsumer<ServerPlayer, ItemStack>> runnable,List<Boolean> overwrite) throws InstantiationException, IllegalAccessException {
        Field[] fields = clazz.getDeclaredFields();
        for (int i=0;i<fields.length;i++) {
            Field declaredField = fields[i];
            if(declaredField.getType().equals(Item.class)){
                declaredField.setAccessible(true);
                register((Item) declaredField.get(clazz.newInstance()),runnable.get(i),overwrite.get(i));
            }
        }
    }

    public static void register(Class<?> clazz,BiConsumer<ServerPlayer, ItemStack> runnable,boolean overwrite) throws InstantiationException, IllegalAccessException {
        for (Field declaredField : clazz.getDeclaredFields()) {
            if(declaredField.getType().equals(Item.class)){
                declaredField.setAccessible(true);
                register((Item) declaredField.get(clazz.newInstance()),runnable,overwrite);
            }
        }
    }

    public static Set<Item> getAllRegisteredTotems(){
        return registered.keySet();
    }

    public static List<TotemInfo> getAllRegisteredTotemInfo(){
        ArrayList<TotemInfo> totemInfos = new ArrayList<>();
        registered.forEach((i,h)->{
            BiConsumer<ServerPlayer, ItemStack> runnable = h.keySet().toArray(new BiConsumer[0])[0];
            totemInfos.add(new TotemInfo(i,runnable,h.get(runnable)));
        });
        return totemInfos;
    }

    public static TotemInfo getInfo(Item item){
        try {
            Map<BiConsumer<ServerPlayer, ItemStack>,Boolean> get = registered.get(item);
            BiConsumer<ServerPlayer, ItemStack> runnable = get.keySet().toArray(new BiConsumer[0])[0];
            return new TotemInfo(item,runnable,get.get(runnable));
        } catch (Exception e) {
            return null;
        }
    }
}
