package de.randombyte.commandutils.alias

import de.randombyte.commandutils.CommandUtils
import de.randombyte.commandutils.executeCommand
import de.randombyte.kosp.config.serializers.duration.SimpleDurationTypeSerializer
import de.randombyte.kosp.extensions.red
import de.randombyte.kosp.extensions.toText
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.command.SendCommandEvent
import org.spongepowered.api.event.filter.cause.First
import java.time.Instant
import java.util.*

class CommandListener {
    @Listener
    fun onCommand(event: SendCommandEvent, @First commandSource: CommandSource) {
        val commandUtils = CommandUtils.INSTANCE
        val configAccessor = commandUtils.configAccessor
        val aliaseConfig = configAccessor.aliases.get()
        val lastExecConfig = configAccessor.lastAliasExecutions.get()

        val wholeCommand = event.wholeCommand
        val matchedAliasedMap = aliaseConfig.aliases.mapNotNull { (alias, aliasConfig) ->
            (alias to aliasConfig) to (AliasParser.parse(alias, wholeCommand) ?: return@mapNotNull null)
        }.toList()

        if (matchedAliasedMap.isEmpty()) return // doesn't match any of our aliases

        event.isCancelled = true

        if (matchedAliasedMap.size > 1) {
            val matchedAliasesString = matchedAliasedMap.joinToString(separator = ", ", prefix = "[", postfix = "]", transform = { "'${it.first.first}'" })
            commandUtils.logger.error("More than one alias matched! command: '$wholeCommand'; matched aliases: $matchedAliasesString")
            throw IllegalArgumentException("More than one alias matched, report to admin!")
        }

        val (aliasEntry, arguments) = matchedAliasedMap.single()
        val (alias, aliasConfig) = aliasEntry
        if (!commandSource.hasPermission(aliasConfig.permission)) {
            commandSource.sendMessage("You don't have the permission to execute this command!".toText())
            return
        }

        if (aliasConfig.cooldown != null && commandSource is Player) {
            val lastExecution = lastExecConfig.get(commandSource.uniqueId, alias)
            if (lastExecution != null) {
                val remainingCooldown = lastExecConfig.remainingCooldown(lastExecution, aliasConfig.cooldown)
                if (!remainingCooldown.isNegative) {
                    val cooldownString = SimpleDurationTypeSerializer.serialize(remainingCooldown, outputMilliseconds = false)
                    commandSource.sendMessage("The cooldown for this command is still up! Wait another $cooldownString.".red())
                    return
                }
            }

            val newLastExecutionsConfig = lastExecConfig.add(commandSource.uniqueId, alias, Date.from(Instant.now()))
            configAccessor.lastAliasExecutions.save(newLastExecutionsConfig)
        }

        aliasConfig.commands.forEachIndexed { index, command ->
            val replacements = if (commandSource is Player) {
                // adding the legacy '$p'
                arguments + Pair("\$p", commandSource.name)
            } else arguments

            executeCommand(
                    command = command,
                    commandSource = commandSource,
                    replacements = replacements,
                    commandIndex = index
            )
        }
    }

    private val SendCommandEvent.wholeCommand: String
        get() = "$command $arguments".trim()
}