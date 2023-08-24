package link.portalbox.pluginportal.command.sub

import link.portalbox.pluginportal.command.SubCommand
import link.portalbox.pluginportal.type.Data
import link.portalbox.pluginportal.type.language.Message
import link.portalbox.pluginportal.type.language.Message.fillInVariables
import net.kyori.adventure.audience.Audience
import org.bukkit.command.CommandSender

class ListSubCommand : SubCommand() {
    override fun execute(audience: Audience, commandSender: CommandSender, args: Array<out String>) {
        val installedPlugins = Data.installedPlugins
        if (installedPlugins.isEmpty()) {
            audience.sendMessage(Message.noPluginsInstalled)
            return
        }

        audience.sendMessage(Message.listingAllPlugins)
        for (plugin in installedPlugins) {
            audience.sendMessage(Message.installedPlugin.fillInVariables(arrayOf(plugin.marketplacePlugin.name)))
        }
    }

    override fun tabComplete(sender: CommandSender, args: Array<out String>): MutableList<String>? {
        return null
    }
}