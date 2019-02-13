package de.randombyte.commandutils.config

import de.randombyte.kosp.config.ConfigAccessor
import java.nio.file.Path

class ConfigAccessor(configPath: Path) : ConfigAccessor(configPath) {

    val general = getConfigHolder<GeneralConfig>("general.conf")
    val aliases = getConfigHolder<AliasConfig>("aliases.conf")
    val executeWhenOnline = getConfigHolder<ExecuteWhenOnlineConfig>("execute-when-online.conf")
    val executeOnServerStartup = getConfigHolder<ExecuteOnServerStartupConfig>("execute-on-server-startup.conf")
    val lastAliasExecutions = getConfigHolder<LastAliasExecutionsConfig>("last-alias-executions.conf")

    override val holders = listOf(general, aliases, executeWhenOnline, executeOnServerStartup, lastAliasExecutions)
}