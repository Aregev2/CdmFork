package com.mrcrayfish.device;

import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.api.print.PrintingManager;
import com.mrcrayfish.device.api.task.TaskManager;
import com.mrcrayfish.device.core.io.task.*;
import com.mrcrayfish.device.core.network.task.TaskConnect;
import com.mrcrayfish.device.core.network.task.TaskPing;
import com.mrcrayfish.device.core.print.task.TaskPrint;
import com.mrcrayfish.device.event.BankEvents;
import com.mrcrayfish.device.event.EmailEvents;
import com.mrcrayfish.device.gui.GuiHandler;
import com.mrcrayfish.device.init.*;
import com.mrcrayfish.device.network.PacketHandler;
import com.mrcrayfish.device.programs.*;
import com.mrcrayfish.device.programs.debug.ApplicationTextArea;
import com.mrcrayfish.device.programs.email.ApplicationEmail;
import com.mrcrayfish.device.programs.email.task.*;
import com.mrcrayfish.device.programs.system.ApplicationBank;
import com.mrcrayfish.device.programs.system.ApplicationExplorer;
import com.mrcrayfish.device.programs.system.ApplicationSettings;
import com.mrcrayfish.device.programs.system.task.*;
import com.mrcrayfish.device.proxy.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.Logger;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION, acceptedMinecraftVersions = Reference.WORKING_MC_VERSION)
public class MrCrayfishDeviceMod 
{
	@Instance(Reference.MOD_ID)
	public static MrCrayfishDeviceMod instance;
	
	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.COMMON_PROXY_CLASS)
	public static CommonProxy proxy;
	
	public static CreativeTabs tabDevice = new DeviceTab("cdmTabDevice");

	private static Logger logger;

	public static final boolean DEVELOPER_MODE = false;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) throws LaunchException
	{
		if(DEVELOPER_MODE && !(Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment"))
		{
			throw new LaunchException();
		}
		logger = event.getModLog();

		DeviceConfig.load(event.getSuggestedConfigurationFile());
		MinecraftForge.EVENT_BUS.register(new DeviceConfig());

		RegistrationHandler.init();
		
		proxy.preInit();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) 
	{
		/* Tile Entity Registering */
		DeviceTileEntites.register();

		/* Packet Registering */
		PacketHandler.init();

		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

		MinecraftForge.EVENT_BUS.register(new EmailEvents());
		MinecraftForge.EVENT_BUS.register(new BankEvents());

		registerApplications();

		proxy.init();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) 
	{
		proxy.postInit();
	}

	private void registerApplications()
	{
		// Applications (Both)
		ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID,"calculator"), ApplicationCalculator.class);
		ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "explorer"), ApplicationExplorer.class);
		ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "note_stash"), ApplicationNoteStash.class);
		ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "package_manager"), ApplicationPackageManager.class);
		ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "settings"), ApplicationSettings.class);
		ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "ender_mail"), ApplicationEmail.class);
		ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "pixel_painter"), ApplicationPixelPainter.class);

		// Core
		TaskManager.registerTask(TaskUpdateApplicationData.class);
		TaskManager.registerTask(TaskPrint.class);
		TaskManager.registerTask(TaskUpdateSystemData.class);

		//Bank
		TaskManager.registerTask(ApplicationBank.TaskDeposit.class);
		TaskManager.registerTask(ApplicationBank.TaskWithdraw.class);
		TaskManager.registerTask(TaskGetBalance.class);
		TaskManager.registerTask(TaskPay.class);
		TaskManager.registerTask(TaskAdd.class);
		TaskManager.registerTask(TaskRemove.class);

		//File Browser
		TaskManager.registerTask(TaskSendAction.class);
		TaskManager.registerTask(TaskSetupFileBrowser.class);
		TaskManager.registerTask(TaskGetFiles.class);
		TaskManager.registerTask(TaskGetStructure.class);
		TaskManager.registerTask(TaskGetMainDrive.class);

		//Ender Mail
		TaskManager.registerTask(TaskUpdateInbox.class);
		TaskManager.registerTask(TaskSendEmail.class);
		TaskManager.registerTask(TaskCheckEmailAccount.class);
		TaskManager.registerTask(TaskRegisterEmailAccount.class);
		TaskManager.registerTask(TaskDeleteEmail.class);
		TaskManager.registerTask(TaskViewEmail.class);

		//Router
		TaskManager.registerTask(TaskConnect.class);
		TaskManager.registerTask(TaskPing.class);

		if(!DEVELOPER_MODE)
		{
			// Applications (Normal)
			//ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "boat_racers"), ApplicationBoatRacers.class);
			//ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "mine_bay"), ApplicationMineBay.class);

			// Tasks (Normal)
			//TaskManager.registerTask(TaskAddAuction.class);
			//TaskManager.registerTask(TaskGetAuctions.class);
			//TaskManager.registerTask(TaskBuyItem.class);
		}
		else
		{
			// Applications (Developers)
			ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "example"), ApplicationExample.class);
			ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "icons"), ApplicationIcons.class);
			ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "text_area"), ApplicationTextArea.class);
			ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "test"), ApplicationTest.class);
		}

		PrintingManager.registerPrint(new ResourceLocation(Reference.MOD_ID, "picture"), ApplicationPixelPainter.PicturePrint.class);
	}

	public static Logger getLogger()
	{
		return logger;
	}
}
