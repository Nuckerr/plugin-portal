package link.portalbox.pluginportal.command.sub

import link.portalbox.pluginportal.command.SubCommand
import link.portalbox.pluginportal.type.language.Message
import gg.flyte.pplib.util.*
import org.bukkit.command.CommandSender

class RequestSubCommand : SubCommand() {
    override fun execute(sender: CommandSender, args: Array<out String>) {
        if (args.size <= 1) {
            sender.sendMessage(Message.noPluginSpecified)
            return
        }

        val plugin = getPluginFromName(args[1]) ?: run {
            sender.sendMessage(Message.pluginNotFound)
            return
        }

        if (plugin.isValidDownload()) {
            sender.sendMessage(Message.pluginIsSupported)
        } else {
            sender.sendMessage(Message.pluginRequested)
        }
    }

    override fun tabComplete(sender: CommandSender, args: Array<out String>): MutableList<String>? {
        if (args.size != 2) return null

        return if (args[1].length <= 2) {
            mutableListOf(Message.keepTyping)
        } else {
            val completion = searchPlugins(args[1])
            if (completion.isEmpty()) {
                mutableListOf(Message.noPluginsFound)
            } else {
                completion.toMutableList()
            }
        }
    }
}