package com.armordelay.armorequipdelay;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ArmorEquipDelay.MOD_ID)
public class ArmorEquipDelay {

    public static final String MOD_ID = "armorequipdelay";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public ArmorEquipDelay() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ArmorDelayConfig.SERVER_SPEC, "armorequipdelay-server.toml");
        MinecraftForge.EVENT_BUS.register(new ArmorEquipEventHandler());

        LOGGER.info("ArmorEquipDelay mod loaded!");
    }
}
