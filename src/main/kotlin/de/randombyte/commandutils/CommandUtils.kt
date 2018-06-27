package de.randombyte.commandutils

import com.google.inject.Inject
import de.randombyte.commandutils.CommandUtils.Companion.AUTHOR
import de.randombyte.commandutils.CommandUtils.Companion.ID
import de.randombyte.commandutils.CommandUtils.Companion.NAME
import de.randombyte.commandutils.CommandUtils.Companion.PLACEHOLDER_API_ID
import de.randombyte.commandutils.CommandUtils.Companion.VERSION
import de.randombyte.commandutils.alias.CommandListener
import de.randombyte.commandutils.conditions.*
import de.randombyte.commandutils.config.ConfigAccessor
import de.randombyte.commandutils.config.ConfigUpdater
import de.randombyte.commandutils.execute.delay.DelayCommand
import de.randombyte.commandutils.execute.ifcondition.IfCommand
import de.randombyte.commandutils.execute.parse.ParseCommand
import de.randombyte.commandutils.execute.userUuidFromNameOrUuid
import de.randombyte.commandutils.execute.whenonline.ExecuteWhenOnlineCommand
import de.randombyte.commandutils.execute.whenonline.PlayerJoinListener
import de.randombyte.commandutils.executeonserverstartup.ServerStartupListener
import de.randombyte.commandutils.service.CommandUtilsService
import de.randombyte.commandutils.service.CommandUtilsServiceImpl
import de.randombyte.kosp.extensions.toText
import org.bstats.sponge.Metrics
import org.slf4j.Logger
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.args.GenericArguments.*
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.config.ConfigDir
import org.spongepowered.api.data.type.HandTypes
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.GameReloadEvent
import org.spongepowered.api.event.game.state.GameInitializationEvent
import org.spongepowered.api.event.game.state.GamePostInitializationEvent
import org.spongepowered.api.plugin.Dependency
import org.spongepowered.api.plugin.Plugin
import java.nio.file.Path

@Plugin(id = ID,
        name = NAME,
        version = VERSION,
        authors = [AUTHOR],
        dependencies = [(Dependency(id = PLACEHOLDER_API_ID, optional = true))])
class CommandUtils @Inject constructor(
        val logger: Logger,
        @ConfigDir(sharedRoot = false) configPath: Path,
        val bStats: Metrics
) {
    companion object {
        const val ID = "command-utils"
        const val NAME = "CommandUtils"
        const val VERSION = "2.2.3"
        const val AUTHOR = "RandomByte"

        const val PLACEHOLDER_API_ID = "placeholderapi"

        const val ROOT_PERMISSION = ID

        const val PLAYER_NAME_ARG = "player_name"
        const val PLAYER_UUID_ARG = "player_uuid"

        const val COMMAND_ARG = "command"

        const val DELAY_ARG = "delay"
        const val TIMESTAMP_ARG = "timestamp"
        const val PRICE_ARG = "price"
        const val MONEY_ARG = "money"
        const val QUANTITY_ARG = "quantity"
        const val PERMISSION_ARG = "permission"
        const val ITEM_ARG = "item"
        const val CONDITION_COMMAND_ARG = "condition_command"

        private val LAZY_INSTANCE = lazy { Sponge.getPluginManager().getPlugin(ID).get().instance.get() as CommandUtils }
        val INSTANCE: CommandUtils
            get() = LAZY_INSTANCE.value
    }

    val configAccessor = ConfigAccessor(configPath)

    @Listener
    fun onInit(event: GameInitializationEvent) {
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
                        userUuidFromNameOrUuid,
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

        val isBeforeCommandSpec = CommandSpec.builder()
                .permission("$ROOT_PERMISSION.before")
                .arguments(string(TIMESTAMP_ARG.toText()))
                .executor(IsBeforeCommand())
                .build()

        val isAfterCommandSpec = CommandSpec.builder()
                .permission("$ROOT_PERMISSION.after")
                .arguments(string(TIMESTAMP_ARG.toText()))
                .executor(IsAfterCommand())
                .build()

        val hasMoneyCommandSpec = CommandSpec.builder()
                .permission("$ROOT_PERMISSION.money")
                .arguments(
                        userUuidFromNameOrUuid,
                        doubleNum(MONEY_ARG.toText()))
                .executor(HasMoneyCommand())
                .build()

        val hasPayedCommandSpec = CommandSpec.builder()
                .permission("$ROOT_PERMISSION.payed")
                .arguments(
                        userUuidFromNameOrUuid,
                        doubleNum(PRICE_ARG.toText()))
                .executor(HasPayedCommand())
                .build()

        val hasPermissionCommandSpec = CommandSpec.builder()
                .permission("$ROOT_PERMISSION.permission")
                .arguments(
                        userUuidFromNameOrUuid,
                        string(PERMISSION_ARG.toText()))
                .executor(HasPermissionCommand())
                .build()

        val hasInHandCommandSpec = CommandSpec.builder()
                .permission("$ROOT_PERMISSION.in-hand")
                .arguments(
                        userUuidFromNameOrUuid,
                        string(ITEM_ARG.toText()),
                        optional(integer(QUANTITY_ARG.toText())))
                .executor(HasInHandCommand(HandTypes.MAIN_HAND))
                .build()

        val hasGivenFromHandCommandSpec = CommandSpec.builder()
                .permission("$ROOT_PERMISSION.given-from-hand")
                .arguments(
                        userUuidFromNameOrUuid,
                        string(ITEM_ARG.toText()),
                        optional(integer(QUANTITY_ARG.toText())))
                .executor(HasGivenFromHandCommand(HandTypes.MAIN_HAND))
                .build()

        val executeParsedCommandSpec = CommandSpec.builder()
                .permission("$ROOT_PERMISSION.parsed")
                .arguments(
                        userUuidFromNameOrUuid,
                        remainingRawJoinedStrings(COMMAND_ARG.toText()))
                .executor(ParseCommand())
                .build()

        val executeIfCommandSpec = CommandSpec.builder()
                .permission("$ROOT_PERMISSION.if")
                .arguments(
                        string(CONDITION_COMMAND_ARG.toText()),
                        allOf(string(COMMAND_ARG.toText())))
                .executor(IfCommand(inverted = false))
                .child(CommandSpec.builder()
                        .arguments(
                                string(CONDITION_COMMAND_ARG.toText()),
                                allOf(string(COMMAND_ARG.toText())))
                        .executor(IfCommand(inverted = true))
                        .build(), "not")
                .build()

        Sponge.getCommandManager().register(this, CommandSpec.builder()
                .child(CommandSpec.builder()
                        .child(isBeforeCommandSpec, "before")
                        .child(isAfterCommandSpec, "after")
                        .build(), "is")
                .child(CommandSpec.builder()
                        .child(hasMoneyCommandSpec, "money")
                        .child(hasPayedCommandSpec, "payed")
                        .child(hasPermissionCommandSpec, "permission")
                        .child(CommandSpec.builder()
                                .child(hasInHandCommandSpec, "hand")
                                .build(), "in")
                        .child(CommandSpec.builder()
                                .child(CommandSpec.builder()
                                        .child(hasGivenFromHandCommandSpec, "hand")
                                        .build(), "from")
                                .build(), "given")
                .build(), "has")
                .child(CommandSpec.builder()
                        .child(executeWhenOnlineCommandSpec, "whenOnline")
                        .child(executeDelayCommandSpec, "delayed")
                        .child(executeIfCommandSpec, "if")
                        .child(executeParsedCommandSpec, "parsed")
                        .build(), "execute")

                .child(executeDelayCommandSpec, "delay") // legacy
                .child(executeWhenOnlineCommandSpec, "executeWhenOnline") // legacy

                .build(), "commandutils", "cmdutils", "cu")
    }
}