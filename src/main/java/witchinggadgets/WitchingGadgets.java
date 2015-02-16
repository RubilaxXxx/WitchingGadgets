package witchinggadgets;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import witchinggadgets.common.CommonProxy;
import witchinggadgets.common.WGConfig;
import witchinggadgets.common.WGContent;
import witchinggadgets.common.WGModCompat;
import witchinggadgets.common.util.WGCreativeTab;
import witchinggadgets.common.util.handler.EventHandler;
import witchinggadgets.common.util.handler.PlayerTickHandler;
import witchinggadgets.common.util.handler.WGWandManager;
import witchinggadgets.common.util.network.WGPacketPipeline;
import witchinggadgets.common.world.VillageComponentPhotoshop;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;

@Mod(modid = WitchingGadgets.MODID, name = WitchingGadgets.MODNAME, version = WitchingGadgets.VERSION, dependencies="required-after:Thaumcraft;required-after:TravellersGear@[1.15,);after:TwilightForest;after:Mystcraft;after:TConstruct;after:MagicBees;after:ForgeMultipart")
public class WitchingGadgets
{
	public static final String MODID = "WitchingGadgets";
	public static final String MODNAME = "Witching Gadgets";
	public static final String VERSION = "${version}";

	public PlayerTickHandler playerTickHandler;

	public WGWandManager wgWandManager = new WGWandManager();

	public static CreativeTabs tabWG = new WGCreativeTab(CreativeTabs.getNextID(), "witchinggadgets");
	public static final Logger logger = LogManager.getLogger("WitchingGadgets");
	public EventHandler eventHandler;

	@Instance("WitchingGadgets")
	public static WitchingGadgets instance = new WitchingGadgets();	

	@SidedProxy(clientSide="witchinggadgets.client.ClientProxy", serverSide="witchinggadgets.common.CommonProxy")
	public static CommonProxy proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger.log(Level.INFO, "Setting up 'WitchingGadgets'");

		WGConfig.loadConfig(event);
		WGContent.preInit();

		eventHandler = new EventHandler();
		MinecraftForge.EVENT_BUS.register(eventHandler);
		playerTickHandler = new PlayerTickHandler();
		FMLCommonHandler.instance().bus().register(eventHandler);
		FMLCommonHandler.instance().bus().register(playerTickHandler);


		VillagerRegistry.instance().registerVillageCreationHandler(new VillageComponentPhotoshop.VillageManager());
		try
		{
			MapGenStructureIO.func_143031_a(VillageComponentPhotoshop.class, "WGVillagePhotoWorkshop");
		}
		catch (Exception e)
		{
			logger.log(Level.ERROR, "Photographer's Workshop not added to Villages");
		}
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.registerRenders();
		WGPacketPipeline.INSTANCE.initialise();

		WGContent.init();
		
		proxy.registerHandlers();
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);

	}

	@Mod.EventHandler
	public static void postInit(FMLPostInitializationEvent event)
	{
		WGModCompat.init();
		WGContent.postInit();
		WGPacketPipeline.INSTANCE.postInitialise();
	}
}