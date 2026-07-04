package com.meekdev.frrktagger;

//? if fabric {
import net.fabricmc.api.ClientModInitializer;
//?}
//? if neoforge {
/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
*///?}

//? if neoforge {
/*@Mod(value = FrrkTagger.MOD_ID, dist = Dist.CLIENT)
*///?}
public class FrrkTaggerClient /*? if fabric {*/ implements ClientModInitializer /*?}*/ {

    //? if fabric {
    @Override
    public void onInitializeClient() {
        FrrkTagger.init();
        FrrkClientSetup.setupFabric();
    }
    //?}

    //? if neoforge {
    /*public FrrkTaggerClient(IEventBus modBus) {
        FrrkTagger.init();
        //? if <26 {
        FrrkClientSetup.setupNeoforge(modBus);
        //?}
    }
    *///?}
}
