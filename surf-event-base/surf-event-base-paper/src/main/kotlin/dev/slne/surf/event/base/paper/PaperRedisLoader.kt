package dev.slne.surf.event.base.paper

import dev.slne.surf.event.base.paper.manager.eventServerManager
import dev.slne.surf.redis.RedisApi

val redisLoader = PaperRedisLoader()
val redisApi get() = redisLoader.redisApi

class PaperRedisLoader {
    lateinit var redisApi: RedisApi

    fun connect() {
        redisApi = RedisApi.create(plugin.dataPath)
        eventServerManager.init()
        redisApi.freezeAndConnect()
    }

    fun disconnect() {
        redisApi.disconnect()
    }
}