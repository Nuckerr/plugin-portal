package link.portalbox.pluginportal.command.sub

import gg.flyte.pplib.util.getPluginFromName
import link.portalbox.pluginportal.PluginPortal
import link.portalbox.pluginportal.command.SubCommand
import link.portalbox.pluginportal.type.Data
import link.portalbox.pluginportal.type.language.Message
import link.portalbox.pluginportal.type.language.Message.fillInVariables
import link.portalbox.pluginportal.util.delete

import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil

class DeleteSubCommand(private val pluginPortal: PluginPortal) : SubCommand() {
    override fun execute(sender: CommandSender, args: Array<out String>) {
        if (args.size <= 1) {
            sender.sendMessage(Message.noPluginSpecified)
            return
        }

        val plugin = getPluginFromName(args[1]) ?: run {
            sender.sendMessage(Message.pluginNotFound)
            return
        }

        val localPlugin = Data.installedPlugins.find { it.marketplacePlugin.id == plugin.id }
        if (localPlugin == null) {
            sender.sendMessage(Message.pluginNotInstalled.fillInVariables(arrayOf(args[1])))
            return
        }

        if (!delete(pluginPortal, localPlugin)) {
            sender.sendMessage(Message.pluginNotDeleted.fillInVariables(arrayOf(args[1])))
            return
        }

        sender.sendMessage(Message.pluginDeleted.fillInVariables(arrayOf(args[1])))
        return
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
