package de.randombyte.commandutils.alias

import de.randombyte.commandutils.ConfigAccessor
import de.randombyte.commandutils.execute
import de.randombyte.commandutils.executeForPlayer
import org.slf4j.Logger
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.command.SendCommandEvent
import org.spongepowered.api.event.filter.cause.First

class CommandListener(
        val logger: Logger,
        val configAccessor: ConfigAccessor
) {
    @Listener
    fun onCommand(event: SendCommandEvent, @First commandSource: CommandSource) {
        val wholeCommand = event.wholeCommand
        val matchedAliasedMap = configAccessor.get().alias.aliases.mapNotNull { (alias, aliasConfig) ->
            aliasConfig to (AliasParser.parse(alias, wholeCommand) ?: return@mapNotNull null)
        }.toList()

        if (matchedAliasedMap.isEmpty()) return // doesn't match any of our aliases

        if (matchedAliasedMap.size > 1) {
            val matchedAliasesString = matchedAliasedMap.joinToString(separator = ", ", prefix = "[", postfix = "]", transform = { "'$it'" })
            logger.error("More than alias matched! command: '$wholeCommand'; matched aliases: $matchedAliasesString")
            throw IllegalArgumentException("More than one alias matched, report to admin!")
        }

        val (aliasConfig, arguments) = matchedAliasedMap.single()
        aliasConfig.commands.forEach {
            var modifiedWholeCommand = it
            arguments.forEach { (parameter, argument) -> modifiedWholeCommand = modifiedWholeCommand.replace(parameter, argument) }
            if (commandSource is Player) executeForPlayer(modifiedWholeCommand, commandSource)
            else execute(modifiedWholeCommand, commandSource)
        }

        event.isCancelled = true
    }

    private val SendCommandEvent.wholeCommand: String
        get() = "$command $arguments"
}