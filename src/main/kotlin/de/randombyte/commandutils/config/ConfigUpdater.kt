package de.randombyte.commandutils.config

import de.randombyte.kosp.extensions.toConfigurationLoader
import org.slf4j.Logger

object ConfigUpdater {

    /**
     * Splits the one config into four parts.
     */
    fun from1_8(configAccessor: ConfigAccessor, logger: Logger) {
        val sharedConfigPath = configAccessor.configPath.parent
        val oldPath = sharedConfigPath.resolve("command-utils.conf")
        if (!oldPath.toFile().exists()) return

        logger.info("Updating old config...")

        val oldNode = oldPath.toConfigurationLoader().load()

        configAccessor.aliases.configManager.configLoader.save(oldNode.getNode("alias"))
        logger.info("Extracted aliases into separate file.")
        configAccessor.executeWhenOnline.configManager.configLoader.save(oldNode.getNode("execute-when-online"))
        logger.info("Extracted execute-when-online into separate file.")
        configAccessor.executeOnServerStartup.configManager.configLoader.save(oldNode.getNode("execute-on-server-startup"))
        logger.info("Extracted execute-on-server-startup into separate file.")
        configAccessor.lastAliasExecutions.configManager.configLoader.save(oldNode.getNode("last-alias-executions-config"))
        logger.info("Extracted last-alias-executions-config into separate file.")

        oldPath.toFile().renameTo(sharedConfigPath.resolve("command-utils-old.conf").toFile())
        logger.info("Renamed old config file (can be deleted when the updating process seemed to be successful).")
    }
}