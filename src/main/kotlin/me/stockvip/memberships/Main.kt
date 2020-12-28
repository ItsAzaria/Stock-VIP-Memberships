package me.stockvip.memberships

import com.gitlab.kordlib.gateway.Intents
import com.gitlab.kordlib.gateway.PrivilegedIntent
import me.jakejmattson.discordkt.api.dsl.bot
import java.awt.Color

@PrivilegedIntent
suspend fun main(args: Array<String>) {
    val token = System.getenv("BOT_TOKEN") ?: null
    val prefix = System.getenv("DEFAULT_PREFIX") ?: "memberships!"
    val upgradeChannel = System.getenv("UPGRADE_CHAN_ID") ?: null

    require(token != null) { "Expected the bot token as an environment variable" }
    require (upgradeChannel != null) { "Expected the upgrade channel as an environment variable" }

    bot(token) {
        prefix {
            prefix
        }

        configure {
            allowMentionPrefix = false
            commandReaction = null
            theme = Color.MAGENTA
        }

        permissions {
            true
        }

        intents {
            Intents.all.intents.forEach {
                +it
            }
        }
    }
}