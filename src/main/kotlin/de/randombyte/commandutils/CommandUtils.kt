package de.randombyte.commandutils

import com.google.inject.Inject
import de.randombyte.commandutils.CommandUtils.Companion.AUTHOR
import de.randombyte.commandutils.CommandUtils.Companion.ID
import de.randombyte.commandutils.CommandUtils.Companion.NAME
import de.randombyte.commandutils.CommandUtils.Companion.VERSION
import de.randombyte.commandutils.alias.CommandListener
import de.randombyte.commandutils.delay.DelayCommand
import de.randombyte.commandutils.executewhenonline.ExecuteWhenOnlineCommand
import de.randombyte.commandutils.executewhenonline.PlayerJoinListener
import de.randombyte.commandutils.service.CommandUtilsService
import de.randombyte.commandutils.service.CommandUtilsServiceImpl
import de.randombyte.kosp.bstats.BStats
import de.randombyte.kosp.config.ConfigManager
import de.randombyte.kosp.extensions.toText
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.loader.ConfigurationLoader
import org.slf4j.Logger
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.args.GenericArguments.remainingRawJoinedStrings
import org.spongepowered.api.command.args.GenericArguments.string
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.config.DefaultConfig
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.GameReloadEvent
import org.spongepowered.api.event.game.state.GameInitializationEvent
import org.spongepowered.api.event.game.state.GamePostInitializationEvent
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.plugin.PluginContainer

@Plugin(id = ID,
        name = NAME,
        version = VERSION,
        authors = arrayOf(AUTHOR))
class CommandUtils @Inject constructor(
        val logger: Logger,
        @DefaultConfig(sharedRoot = true) configurationLoader: ConfigurationLoader<CommentedConfigurationNode>,
        val bStats: BStats,
        val pluginContainer: PluginContainer
) {
    companion object {
        const val ID = "command-utils"
        const val NAME = "CommandUtils"
        const val VERSION = "1.6"
        const val AUTHOR = "RandomByte"

        const val ROOT_PERMISSION = ID

        const val PLAYER_OR_UUID_ARG = "playerOrUuid"
        const val COMMAND_ARG = "command"

        const val DELAY_ARGUMENT = "delay"
    }

    private val configManager = ConfigManager(
            configLoader = configurationLoader,
            clazz = Config::class.java,
            hyphenSeparatedKeys = true
    )

    private lateinit var config: Config

    private val configAccessor = object : ConfigAccessor {
        override fun get() = this@CommandUtils.config
        override fun save(newConfig: Config) {
            this@CommandUtils.config = newConfig
            saveConfig()
        }
    }

    @Listener
    fun onInit(event: GameInitializationEvent) {
        reloadConfig()
        registerCommands()
    }

    @Listener
    fun onPostInit(event: GamePostInitializationEvent) {
        Sponge.getEventManager().registerListeners(this, PlayerJoinListener(this, configAccessor))
        Sponge.getEventManager().registerListeners(this, CommandListener(logger, configAccessor))

        Sponge.getServiceManager().setProvider(this, CommandUtilsService::class.java,
                CommandUtilsServiceImpl(configAccessor))

        logger.info("Loaded $NAME: $VERSION")
    }

    @Listener
    fun onReload(event: GameReloadEvent) {
        reloadConfig()

        logger.info("Reloaded!")
    }

    private fun reloadConfig() {
        config = configManager.get()
        saveConfig() // generate config
    }

    private fun saveConfig() {
        configManager.save(config)
    }

    private fun registerCommands() {
        Sponge.getCommandManager().register(this, CommandSpec.builder()
                .child(CommandSpec.builder()
                        .permission("$ROOT_PERMISSION.execute-when-online")
                        .arguments(string(PLAYER_OR_UUID_ARG.toText()), remainingRawJoinedStrings(COMMAND_ARG.toText()))
                        .executor(ExecuteWhenOnlineCommand())
                        .build(), "executeWhenOnline")
                .child(CommandSpec.builder()
                        .permission("$ROOT_PERMISSION.delay")
                        .arguments(
                                string(DELAY_ARGUMENT.toText()),
                                GenericArguments.remainingJoinedStrings(COMMAND_ARG.toText()))
                        .executor(DelayCommand(plugin = this))
                        .build(), "delay")
                .build(), "commandUtils", "cmdUtils", "cu")
    }
}