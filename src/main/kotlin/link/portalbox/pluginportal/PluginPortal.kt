package link.portalbox.pluginportal

import gg.flyte.pplib.manager.TabManager
import link.portalbox.pluginportal.command.PPCommand
import link.portalbox.pluginportal.type.Config
import link.portalbox.pluginportal.type.Data
import link.portalbox.pluginportal.listener.PluginValidator
import link.portalbox.pluginportal.listener.UpdateListener
import link.portalbox.pluginportal.type.language.Message
import link.portalbox.pluginportal.util.*
import gg.flyte.pplib.type.version.VersionType
import io.papermc.lib.PaperLib
import okhttp3.OkHttpClient
import org.bstats.bukkit.Metrics
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

class PluginPortal : JavaPlugin() {
    var versionType: VersionType = VersionType.UNRELEASED
    var tabManager: TabManager = TabManager()

    override fun onEnable() {
        Config.init(this)
        Message.init(this)
        Data.init(this)

        val command = PPCommand(this)
        getCommand("pluginportal")!!.setExecutor(command)
        getCommand("pluginportal")!!.tabCompleter = command

        server.pluginManager.registerEvents(PluginValidator(), this)
        server.pluginManager.registerEvents(UpdateListener(this), this)

        setupMetrics(Metrics(this, 18005))
        PaperLib.suggestPaper(this)
    }

    override fun onDisable() {
        Message.audiences.close();
    }
}