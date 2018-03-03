package de.randombyte.commandutils.alias

import de.randombyte.commandutils.ConfigAccessor
import de.randombyte.commandutils.execute
import de.randombyte.commandutils.executeForPlayer
import de.randombyte.kosp.config.serializers.duration.SimpleDurationTypeSerializer
import de.randombyte.kosp.extensions.red
import de.randombyte.kosp.extensions.toText
import org.slf4j.Logger
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.command.SendCommandEvent
import org.spongepowered.api.event.filter.cause.First
import java.time.Instant
import java.util.*

class CommandListener(
        val logger: Logger,
        val configAccessor: ConfigAccessor
) {
    @Listener
    fun onCommand(event: SendCommandEvent, @First commandSource: CommandSource) {
        val config = configAccessor.get()

        val wholeCommand = event.wholeCommand
        val matchedAliasedMap = config.alias.aliases.mapNotNull { (alias, aliasConfig) ->
            (alias to aliasConfig) to (AliasParser.parse(alias, wholeCommand) ?: return@mapNotNull null)
        }.toList()

        if (matchedAliasedMap.isEmpty()) return // doesn't match any of our aliases

        event.isCancelled = true

        if (matchedAliasedMap.size > 1) {
            val matchedAliasesString = matchedAliasedMap.joinToString(separator = ", ", prefix = "[", postfix = "]", transform = { "'${it.first.first}'" })
            logger.error("More than one alias matched! command: '$wholeCommand'; matched aliases: $matchedAliasesString")
            throw IllegalArgumentException("More than one alias matched, report to admin!")
        }

        val (aliasEntry, arguments) = matchedAliasedMap.single()
        val (alias, aliasConfig) = aliasEntry
        if (!commandSource.hasPermission(aliasConfig.permission)) {
            commandSource.sendMessage("You don't have the permission to execute this command!".toText())
            return
        }

        if (aliasConfig.cooldown != null && commandSource is Player) {
            val lastExecution = config.lastAliasExecutionsConfig.get(commandSource.uniqueId, alias)
            if (lastExecution != null) {
                val remainingCooldown = config.lastAliasExecutionsConfig.remainingCooldown(lastExecution, aliasConfig.cooldown)
                if (!remainingCooldown.isNegative) {
                    val cooldownString = SimpleDurationTypeSerializer.serialize(remainingCooldown, outputMilliseconds = false)
                    commandSource.sendMessage("The cooldown for this command is still up! Wait another $cooldownString.".red())
                    return
                }
            }

            val newLastExecutionsConfig = config.lastAliasExecutionsConfig.add(commandSource.uniqueId, alias, Date.from(Instant.now()))
            configAccessor.save(config.copy(lastAliasExecutionsConfig = newLastExecutionsConfig))
        }

        aliasConfig.commands.forEach {
            var modifiedWholeCommand = it
            arguments.forEach { (parameter, argument) -> modifiedWholeCommand = modifiedWholeCommand.replace(parameter, argument) }
            if (commandSource is Player) executeForPlayer(modifiedWholeCommand, commandSource)
            else execute(modifiedWholeCommand, commandSource)
        }
    }

    private val SendCommandEvent.wholeCommand: String
        get() = "$command $arguments".trim()
}