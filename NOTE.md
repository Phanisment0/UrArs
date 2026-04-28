Read this if you want continue the development

## ERROR on launch
Something is wrong but i dont know whay but the cause is when i add TickLogger `init()` in main class.

### Log
```
---- Minecraft Crash Report ----
// Hi. I'm Minecraft, and I'm a crashaholic.

Time: 2026-04-28 19:00:11
Description: Initializing game

java.lang.RuntimeException: Could not execute entrypoint stage 'main' due to errors, provided by 'urars' at 'io.phanisment.urars.UrArs'!
	at net.fabricmc.loader.impl.FabricLoaderImpl.lambda$invokeEntrypoints$0(FabricLoaderImpl.java:409)
	at net.fabricmc.loader.impl.util.ExceptionUtil.gatherExceptions(ExceptionUtil.java:33)
	at net.fabricmc.loader.impl.FabricLoaderImpl.invokeEntrypoints(FabricLoaderImpl.java:407)
	at net.fabricmc.loader.impl.game.minecraft.Hooks.startClient(Hooks.java:52)
	at knot//net.minecraft.client.Minecraft.<init>(Minecraft.java:477)
	at knot//net.minecraft.client.main.Main.main(Main.java:232)
	at net.fabricmc.loader.impl.game.minecraft.MinecraftGameProvider.launch(MinecraftGameProvider.java:514)
	at net.fabricmc.loader.impl.launch.knot.Knot.launch(Knot.java:72)
	at net.fabricmc.loader.impl.launch.knot.KnotClient.main(KnotClient.java:23)
	at org.prismlauncher.launcher.impl.StandardLauncher.launch(StandardLauncher.java:102)
	at org.prismlauncher.EntryPoint.listen(EntryPoint.java:129)
	at org.prismlauncher.EntryPoint.main(EntryPoint.java:70)
Caused by: java.lang.NoSuchMethodError: 'void io.phanisment.urars.UrArs.serverStart(net.minecraft.server.MinecraftServer)'
	at knot//io.phanisment.urars.UrArs.onInitialize(UrArs.java:26)
	at net.fabricmc.loader.impl.FabricLoaderImpl.invokeEntrypoints(FabricLoaderImpl.java:405)
	... 9 more
```