package net.bagatelle.afkpeace;

import net.bagatelle.afkpeace.util.ActiveStates;
import net.bagatelle.afkpeace.util.ConnectUtil;
import net.bagatelle.afkpeace.util.SetupUtil;
import net.fabricmc.api.ClientModInitializer;

public class AFKPeace implements ClientModInitializer {
	public static SetupUtil setupUtil = new SetupUtil();
	public static ConnectUtil connectUtil = new ConnectUtil();
	public static ActiveStates activeStates = new ActiveStates();

	@Override
	public void onInitializeClient() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		setupUtil.configureKeybinds();
		setupUtil.clientTickCallbackActivation();
	}
}
