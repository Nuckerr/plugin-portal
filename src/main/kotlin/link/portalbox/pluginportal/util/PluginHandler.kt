package link.portalbox.pluginportal.util

import link.portalbox.pluginportal.PluginPortal
import link.portalbox.pluginportal.file.Data
import link.portalbox.pluginportal.file.LocalPlugin
import link.portalbox.pplib.type.MarketplacePlugin
import link.portalbox.pplib.util.download
import java.io.File
import java.io.FileNotFoundException
import java.net.URL

fun delete(pluginPortal: PluginPortal, localPlugin: LocalPlugin): Boolean {
    for (loadedPlugin in pluginPortal.server.pluginManager.plugins) {
        val pluginClass = loadedPlugin.javaClass
        val codeSource = pluginClass.protectionDomain.codeSource
        if (codeSource != null) {
            try {
                val file = File(codeSource.location.toURI().path)
                if (localPlugin.fileSha == getSHA(file)) {
                    pluginPortal.server.pluginManager.disablePlugin(loadedPlugin)
                    file.delete()
                    Data.delete(localPlugin.id)
                    return true
                }
            } catch (x: FileNotFoundException) {
                continue
            }
        }
    }
    return false
}

fun install(plugin: MarketplacePlugin, downloadURL: URL) {
    val outputFile = File("plugins", "${plugin.name}-${plugin.version} (PP).jar")
    download(downloadURL, outputFile)
    Data.update(plugin.id.toInt(), plugin.version, getSHA(outputFile))
    addValueToPieChart(Chart.MOST_DOWNLOADED, plugin.id)
}