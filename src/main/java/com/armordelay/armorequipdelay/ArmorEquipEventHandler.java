package com.armordelay.armorequipdelay;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ArmorEquipEventHandler {

    /**
     * Викликається щоразу, коли у будь-якого LivingEntity змінюється спорядження.
     * Це покриває ВСІ способи зміни броні:
     *  - Ручне перетягування в інвентарі
     *  - Shift+ПКМ (швидке одягання)
     *  - Команди, інші моди тощо
     */
    @SubscribeEvent
    public void onEquipmentChange(LivingEquipmentChangeEvent event) {
        // Нас цікавлять лише гравці
        if (!(event.getEntity() instanceof Player player)) return;

        // Нас цікавлять лише слоти броні
        EquipmentSlot slot = event.getSlot();
        if (!isArmorSlot(slot)) return;

        ItemStack oldItem = event.getFrom();
        ItemStack newItem = event.getTo();

        boolean isEquipping = isArmor(newItem);
        boolean isUnequipping = isArmor(oldItem);

        if (!isEquipping && !isUnequipping) return;

        // Визначаємо ключ дії та слота для локалізації
        String actionKey = isEquipping ? "equipping" : "unequipping";
        String slotKey = getSlotKey(slot);

        // Показуємо локалізоване повідомлення в ActionBar
        // Гра сама підбере потрібну мову з lang файлів
        player.displayClientMessage(
            Component.translatable("armorequipdelay." + actionKey + "." + slotKey)
                     .withStyle(isEquipping ? ChatFormatting.YELLOW : ChatFormatting.GRAY),
            true
        );

        // Застосовуємо дебафи, якщо вони увімкнені в конфігу
        int durationTicks = getDurationTicks(slot);
        if (ArmorDelayConfig.DEBUFFS_ENABLED.get()) {
            applyDebuffs(player, durationTicks);
        }
    }

    /**
     * Застосовує Повільність та Слабкість до гравця.
     * Якщо ефект вже активний — продовжує його тривалість.
     */
    private void applyDebuffs(Player player, int durationTicks) {
        int slownessLevel = ArmorDelayConfig.SLOWNESS_LEVEL.get();
        int weaknessLevel = ArmorDelayConfig.WEAKNESS_LEVEL.get();

        if (slownessLevel > 0) {
            MobEffectInstance existing = player.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
            int newDuration = (existing != null)
                ? Math.max(existing.getDuration(), durationTicks)
                : durationTicks;

            player.addEffect(new MobEffectInstance(
                MobEffects.MOVEMENT_SLOWDOWN,
                newDuration,
                slownessLevel - 1,
                false, true, true
            ));
        }

        if (weaknessLevel > 0) {
            MobEffectInstance existing = player.getEffect(MobEffects.WEAKNESS);
            int newDuration = (existing != null)
                ? Math.max(existing.getDuration(), durationTicks)
                : durationTicks;

            player.addEffect(new MobEffectInstance(
                MobEffects.WEAKNESS,
                newDuration,
                weaknessLevel - 1,
                false, true, true
            ));
        }
    }

    /**
     * Повертає ключ слота для локалізації.
     * Використовується як частина ключа у lang файлах.
     */
    private String getSlotKey(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD  -> "helmet";
            case CHEST -> "chestplate";
            case LEGS  -> "leggings";
            case FEET  -> "boots";
            default    -> "armor";
        };
    }

    /**
     * Повертає тривалість дебафу в тіках для конкретного слота броні.
     * 1 секунда = 20 тіків.
     */
    private int getDurationTicks(EquipmentSlot slot) {
        double seconds = switch (slot) {
            case HEAD  -> ArmorDelayConfig.HELMET_DELAY_SECONDS.get();
            case CHEST -> ArmorDelayConfig.CHESTPLATE_DELAY_SECONDS.get();
            case LEGS  -> ArmorDelayConfig.LEGGINGS_DELAY_SECONDS.get();
            case FEET  -> ArmorDelayConfig.BOOTS_DELAY_SECONDS.get();
            default    -> 2.0;
        };
        return (int) Math.round(seconds * 20);
    }

    private boolean isArmor(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.getItem() instanceof ArmorItem;
    }

    private boolean isArmorSlot(EquipmentSlot slot) {
        return slot == EquipmentSlot.HEAD
            || slot == EquipmentSlot.CHEST
            || slot == EquipmentSlot.LEGS
            || slot == EquipmentSlot.FEET;
    }
}
