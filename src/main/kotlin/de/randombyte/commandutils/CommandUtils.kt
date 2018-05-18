package de.randombyte.commandutils

import com.google.inject.Inject
import de.randombyte.commandutils.CommandUtils.Companion.AUTHOR
import de.randombyte.commandutils.CommandUtils.Companion.ID
import de.randombyte.commandutils.CommandUtils.Companion.NAME
import de.randombyte.commandutils.CommandUtils.Companion.PLACEHOLDER_API_ID
import de.randombyte.commandutils.CommandUtils.Companion.VERSION
import de.randombyte.commandutils.alias.CommandListener
import de.randombyte.commandutils.config.ConfigAccessor
import de.randombyte.commandutils.config.ConfigUpdater
import de.randombyte.commandutils.execute.after.AfterCommand
import de.randombyte.commandutils.execute.before.BeforeCommand
import de.randombyte.commandutils.execute.delay.DelayCommand
import de.randombyte.commandutils.execute.whenonline.ExecuteWhenOnlineCommand
import de.randombyte.commandutils.execute.whenonline.PlayerJoinListener
import de.randombyte.commandutils.executeonserverstartup.ServerStartupListener
import de.randombyte.commandutils.service.CommandUtilsService
import de.randombyte.commandutils.service.CommandUtilsServiceImpl
import de.randombyte.kosp.extensions.toText
import de.randombyte.kosp.getServiceOrFail
import me.rojo8399.placeholderapi.PlaceholderService
import org.slf4j.Logger
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.args.GenericArguments.*
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.config.ConfigDir
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.GameReloadEvent
import org.spongepowered.api.event.game.state.GameInitializationEvent
import org.spongepowered.api.event.game.state.GamePostInitializationEvent
import org.spongepowered.api.plugin.Dependency
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.plugin.PluginContainer
import java.nio.file.Path

@Plugin(id = ID,
        name = NAME,
        version = VERSION,
        authors = [AUTHOR],
        dependencies = [(Dependency(id = PLACEHOLDER_API_ID, optional = true))])
class CommandUtils @Inject constructor(
        val logger: Logger,
        @ConfigDir(sharedRoot = false) configPath: Path,
        //val bStats: Metrics,
        val pluginContainer: PluginContainer
) {
    companion object {
        const val ID = "command-utils"
        const val NAME = "CommandUtils"
        const val VERSION = "1.8"
        const val AUTHOR = "RandomByte"

        const val PLACEHOLDER_API_ID = "placeholderapi"

        const val ROOT_PERMISSION = ID

        const val PLAYER_NAME_ARG = "player_name"
        const val PLAYER_UUID_ARG = "player_uuid"
        const val COMMAND_ARG = "command"
        const val DELAY_ARG = "delay"
        const val TIMESTAMP_ARG = "timestamp"

        private val LAZY_INSTANCE = lazy { Sponge.getPluginManager().getPlugin(ID).get().instance.get() as CommandUtils }
        val INSTANCE: CommandUtils
            get() = LAZY_INSTANCE.value
    }

    var placeholderApi: PlaceholderService? = null

    val configAccessor = ConfigAccessor(configPath)

    @Listener
    fun onInit(event: GameInitializationEvent) {
        loadPlaceholderApi()
        ConfigUpdater.from1_8(configAccessor, logger)
        configAccessor.reloadAll()
        registerCommands()
    }

    @Listener
    fun onPostInit(event: GamePostInitializationEvent) {
        Sponge.getEventManager().registerListeners(this, PlayerJoinListener())
        Sponge.getEventManager().registerListeners(this, ServerStartupListener())
        Sponge.getEventManager().registerListeners(this, CommandListener())

        Sponge.getServiceManager().setProvider(this, CommandUtilsService::class.java, CommandUtilsServiceImpl(configAccessor))

        logger.info("Loaded $NAME: $VERSION")
    }

    @Listener
    fun onReload(event: GameReloadEvent) {
        configAccessor.reloadAll()

        logger.info("Reloaded!")
    }

    private fun registerCommands() {
        val executeWhenOnlineCommandSpec = CommandSpec.builder()
                .permission("$ROOT_PERMISSION.execute-when-online")
                .arguments(
                        firstParsing(
                                uuid(PLAYER_UUID_ARG.toText()),
                                user(PLAYER_NAME_ARG.toText())),
                        remainingRawJoinedStrings(COMMAND_ARG.toText()))
                .executor(ExecuteWhenOnlineCommand())
                .build()

        val executeDelayCommandSpec = CommandSpec.builder()
                .permission("$ROOT_PERMISSION.delay")
                .arguments(
                        string(DELAY_ARG.toText()),
                        remainingRawJoinedStrings(COMMAND_ARG.toText()))
                .executor(DelayCommand())
                .build()

        val executeBeforeCommandSpec = CommandSpec.builder()
                .permission("$ROOT_PERMISSION.before")
                .arguments(
                        string(TIMESTAMP_ARG.toText()),
                        remainingRawJoinedStrings(COMMAND_ARG.toText()))
                .executor(BeforeCommand())
                .build()

        val executeAfterCommandSpec = CommandSpec.builder()
                .permission("$ROOT_PERMISSION.after")
                .arguments(
                        string(TIMESTAMP_ARG.toText()),
                        remainingRawJoinedStrings(COMMAND_ARG.toText()))
                .executor(AfterCommand())
                .build()

        Sponge.getCommandManager().register(this, CommandSpec.builder()
                .child(CommandSpec.builder()
                        .child(executeWhenOnlineCommandSpec, "whenOnline")
                        .child(executeDelayCommandSpec, "delay")
                        .child(executeBeforeCommandSpec, "before")
                        .child(executeAfterCommandSpec, "after")
                        .build(), "execute")

                .child(executeDelayCommandSpec, "delay") // legacy
                .child(executeWhenOnlineCommandSpec, "executeWhenOnline") // legacy

                .build(), "commandutils", "cmdutils", "cu")
    }

    private fun loadPlaceholderApi() {
        if (Sponge.getPluginManager().getPlugin(PLACEHOLDER_API_ID).isPresent) {
            placeholderApi = getServiceOrFail(PlaceholderService::class,
                    failMessage = "Failed getting the placeholder API despite the plugin itself being loaded!")
        }
    }
}