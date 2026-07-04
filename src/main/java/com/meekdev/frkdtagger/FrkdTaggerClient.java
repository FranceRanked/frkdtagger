package com.meekdev.frkdtagger;

//? if fabric {
import net.fabricmc.api.ClientModInitializer;
//?}
//? if neoforge {
/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
*///?}

//? if neoforge {
/*@Mod(value = FrkdTagger.MOD_ID, dist = Dist.CLIENT)
*///?}
public class FrkdTaggerClient /*? if fabric {*/ implements ClientModInitializer /*?}*/ {

    //? if fabric {
    @Override
    public void onInitializeClient() {
        FrkdTagger.init();
        FrkdClientSetup.setupFabric();
    }
    //?}

    //? if neoforge {
    /*public FrkdTaggerClient(IEventBus modBus) {
        FrkdTagger.init();
        //? if <26 {
        FrkdClientSetup.setupNeoforge(modBus);
        //?}
    }
    *///?}
}
