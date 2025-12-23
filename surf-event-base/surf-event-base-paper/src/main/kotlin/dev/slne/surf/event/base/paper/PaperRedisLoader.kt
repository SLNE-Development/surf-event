package dev.slne.surf.event.base.paper

import dev.slne.surf.event.base.paper.redis.listener.EventServerStateRequestListener
import dev.slne.surf.redis.RedisApi

val redisLoader = PaperRedisLoader()
val redisApi get() = redisLoader.redisApi

class PaperRedisLoader {
    lateinit var redisApi: RedisApi

    fun connect() {
        redisApi = RedisApi.create(plugin.dataPath)
        registerListeners()

        redisApi.freezeAndConnect()
    }

    private fun registerListeners() {
        redisApi.registerRequestHandler(EventServerStateRequestListener)
    }

    fun disconnect() {
        redisApi.disconnect()
    }
}