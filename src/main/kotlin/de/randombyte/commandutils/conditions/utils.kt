package de.randombyte.commandutils.conditions

import org.spongepowered.api.command.CommandResult

fun Boolean.toCommandResult() = if (this) CommandResult.success() else CommandResult.empty()