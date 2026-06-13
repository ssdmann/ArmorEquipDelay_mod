package com.armordelay.armorequipdelay;

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

        // Визначаємо: це одягання чи знімання?
        // Одягання: новий предмет — броня, старий — пустий або інша броня
        // Знімання: новий предмет — пустий або не броня, старий — броня
        boolean isEquipping = isArmor(newItem);
        boolean isUnequipping = isArmor(oldItem);

        // Якщо ні те, ні інше — ігноруємо (наприклад, зброя чи інструмент)
        if (!isEquipping && !isUnequipping) return;

        // Отримуємо назву слота для повідомлення
        String slotName = getSlotName(slot);
        boolean equipping = isEquipping; // фінальна змінна для лямбди

        // Визначаємо тривалість дебафу для цього слота
        int durationTicks = getDurationTicks(slot);

        // Формуємо текст повідомлення
        String message;
        if (equipping) {
            message = "§e⚔ Одевается " + slotName + "...";
        } else {
            message = "§7⚔ Снимается " + slotName + "...";
        }

        // Показуємо повідомлення в ActionBar (над панеллю хотбара)
        player.displayClientMessage(Component.literal(message), true);

        // Застосовуємо дебафи, якщо вони увімкнені в конфігу
        if (ArmorDelayConfig.DEBUFFS_ENABLED.get()) {
            applyDebuffs(player, durationTicks);
        }
    }

    /**
     * Застосовує Повільність та Слабкість до гравця.
     * Якщо ефект вже активний — оновлює його тривалість (амплітуда не зменшується).
     */
    private void applyDebuffs(Player player, int durationTicks) {
        int slownessLevel = ArmorDelayConfig.SLOWNESS_LEVEL.get();
        int weaknessLevel = ArmorDelayConfig.WEAKNESS_LEVEL.get();

        if (slownessLevel > 0) {
            // Перевіряємо чи вже є активний ефект повільності
            MobEffectInstance existing = player.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
            int newDuration = durationTicks;

            // Якщо є активний ефект — додаємо час (не перезапускаємо, а продовжуємо)
            if (existing != null) {
                newDuration = Math.max(existing.getDuration(), durationTicks);
            }

            player.addEffect(new MobEffectInstance(
                MobEffects.MOVEMENT_SLOWDOWN,
                newDuration,
                slownessLevel - 1, // Рівні ефектів в Minecraft йдуть з 0 (0 = Slowness I)
                false,  // ambient — без частинок від маяка
                true,   // visible — показувати іконку ефекту
                true    // showIcon — показувати в куті екрана
            ));
        }

        if (weaknessLevel > 0) {
            MobEffectInstance existing = player.getEffect(MobEffects.WEAKNESS);
            int newDuration = durationTicks;

            if (existing != null) {
                newDuration = Math.max(existing.getDuration(), durationTicks);
            }

            player.addEffect(new MobEffectInstance(
                MobEffects.WEAKNESS,
                newDuration,
                weaknessLevel - 1,
                false,
                true,
                true
            ));
        }
    }

    /**
     * Повертає тривалість дебафу в тіках для конкретного слота броні.
     * 1 секунда = 20 тіків.
     */
    private int getDurationTicks(EquipmentSlot slot) {
        double seconds = switch (slot) {
            case HEAD    -> ArmorDelayConfig.HELMET_DELAY_SECONDS.get();
            case CHEST   -> ArmorDelayConfig.CHESTPLATE_DELAY_SECONDS.get();
            case LEGS    -> ArmorDelayConfig.LEGGINGS_DELAY_SECONDS.get();
            case FEET    -> ArmorDelayConfig.BOOTS_DELAY_SECONDS.get();
            default      -> 2.0;
        };
        return (int) Math.round(seconds * 20);
    }

    /**
     * Повертає читабельну назву частини броні (російською, як просили).
     */
    private String getSlotName(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD  -> "шлем";
            case CHEST -> "кираса";
            case LEGS  -> "штаны";
            case FEET  -> "сапоги";
            default    -> "броня";
        };
    }

    /**
     * Перевіряє чи є предмет бронею (будь-якою: ванільною або з модів).
     */
    private boolean isArmor(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.getItem() instanceof ArmorItem;
    }

    /**
     * Перевіряє чи є слот броньовим.
     */
    private boolean isArmorSlot(EquipmentSlot slot) {
        return slot == EquipmentSlot.HEAD
            || slot == EquipmentSlot.CHEST
            || slot == EquipmentSlot.LEGS
            || slot == EquipmentSlot.FEET;
    }
}
