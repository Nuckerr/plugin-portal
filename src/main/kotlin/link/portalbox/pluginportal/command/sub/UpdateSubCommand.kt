package link.portalbox.pluginportal.command.sub

import gg.flyte.pplib.type.plugin.MarketplacePlugin
import gg.flyte.pplib.util.getPluginFromID
import link.portalbox.pluginportal.PluginPortal
import link.portalbox.pluginportal.command.SubCommand
import link.portalbox.pluginportal.type.Config
import link.portalbox.pluginportal.type.Data
import link.portalbox.pluginportal.type.language.Message
import link.portalbox.pluginportal.type.language.Message.fillInVariables
import link.portalbox.pluginportal.util.*
import gg.flyte.pplib.util.getPluginFromName
import net.kyori.adventure.audience.Audience
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

class UpdateSubCommand(private val pluginPortal: PluginPortal) : SubCommand() {
    override fun execute(audience: Audience, commandSender: CommandSender, args: Array<out String>) {
        if (args.size >= 2) {
            val plugin = getPluginFromName(args[1]) ?: run {
                audience.sendMessage(Message.pluginNotFound)
                return
            }

            val localPlugin = Data.installedPlugins.find { it.marketplacePlugin.id == plugin.id }
            if (localPlugin == null) {
                audience.sendMessage(Message.pluginNotFound)
                return
            }

            if (plugin.version == localPlugin.marketplacePlugin.version) {
                audience.sendMessage(Message.pluginIsUpToDate.fillInVariables(arrayOf(plugin.name)))
                return
            }

            if (!plugin.isValidDownload()) {
                audience.sendMessage(Message.downloadNotFound)
                return
            }

            if (!delete(pluginPortal, localPlugin)) {
                audience.sendMessage(Message.pluginNotUpdated.fillInVariables(arrayOf(plugin.name)))
                return
            }

            install(plugin, pluginPortal).run {
                audience.sendMessage(Message.pluginUpdated)
                audience.sendMessage(if (Config.startupOnInstall) Message.pluginAttemptedEnabling else Message.restartServerToEnablePlugin)
            }

        }

        val needUpdating = mutableListOf<MarketplacePlugin>()
        for (plugin in Data.installedPlugins) {
            val spigetPlugin = getPluginFromID(plugin.marketplacePlugin.id) ?: continue
            if (spigetPlugin.version != plugin.marketplacePlugin.version) {
                needUpdating.add(spigetPlugin)
            }
        }

        if (needUpdating.isEmpty()) {
            audience.sendMessage(Message.noPluginRequireAnUpdate)
            return
        }

        audience.sendMessage(Message.listingAllOutdatedPlugins)
        for (spigetPlugin in needUpdating) {
            audience.sendMessage(Message.installedPlugin.fillInVariables(arrayOf(spigetPlugin.name)))
        }
    }

    override fun tabComplete(sender: CommandSender, args: Array<out String>): MutableList<String>? {
        if (args.size != 2) return null
        return StringUtil.copyPartialMatches(
            args[1],
            Data.installedPlugins.map { it.marketplacePlugin.name },
            mutableListOf()
        )
    }
}