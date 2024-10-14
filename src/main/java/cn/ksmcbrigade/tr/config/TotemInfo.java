package cn.ksmcbrigade.tr.config;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiConsumer;

public record TotemInfo(Item totem, BiConsumer<ServerPlayer, ItemStack> runnable, boolean overwrite) { }
