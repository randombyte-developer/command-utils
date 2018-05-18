package de.randombyte.commandutils.config

import de.randombyte.kosp.config.ConfigAccessor
import de.randombyte.kosp.config.ConfigHolder
import de.randombyte.kosp.config.ConfigManager
import de.randombyte.kosp.config.toConfigHolder
import de.randombyte.kosp.extensions.toConfigurationLoader
import java.nio.file.Path

class ConfigAccessor(val configPath: Path) : ConfigAccessor(configPath) {

    val aliases: ConfigHolder<AliasConfig> = getConfigHolder("aliases.conf")
    val executeWhenOnline: ConfigHolder<ExecuteWhenOnlineConfig> = getConfigHolder("execute-when-online.conf")
    val executeOnServerStartup: ConfigHolder<ExecuteOnServerStartupConfig> = getConfigHolder("execute-on-server-startup.conf")
    val lastAliasExecutions: ConfigHolder<LastAliasExecutionsConfig> = getConfigHolder("last-alias-executions.conf")

    override val holders = listOf(aliases, executeWhenOnline, executeOnServerStartup, lastAliasExecutions)

    private inline fun <reified T : Any> getConfigHolder(configName: String): ConfigHolder<T> {
        return ConfigManager(configPath.resolve(configName).toConfigurationLoader(), T::class.java).toConfigHolder()
    }
}