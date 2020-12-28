package me.stockvip.memberships.commands

import me.jakejmattson.discordkt.api.dsl.commands

fun utilCommands() = commands("Util") {
    guildCommand("ping") {
        description = "Respond with pong."
        execute {
            respond("pong")
        }
    }
}