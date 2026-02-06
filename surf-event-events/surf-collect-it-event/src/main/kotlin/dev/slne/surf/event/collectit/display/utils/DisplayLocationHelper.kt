package dev.slne.surf.event.collectit.display.utils

import org.bukkit.Location
import org.bukkit.block.BlockFace
import org.bukkit.util.Vector
import org.joml.Quaternionf
import kotlin.math.asin
import kotlin.math.atan2
import org.spongepowered.math.imaginary.Quaternionf as SpongeQuaternionf

object DisplayLocationHelper {
    fun calculateDisplayRotation(facing: BlockFace): SpongeQuaternionf {
        val quaternion = Quaternionf()

        when (facing) {
            BlockFace.NORTH -> quaternion.rotationY(0.0f)
            BlockFace.EAST -> quaternion.rotationY(Math.toRadians(90.0).toFloat())
            BlockFace.SOUTH -> quaternion.rotationY(Math.toRadians(180.0).toFloat())
            BlockFace.WEST -> quaternion.rotationY(Math.toRadians(270.0).toFloat())
            else -> throw IllegalArgumentException("Unsupported facing direction: $facing")
        }

        return SpongeQuaternionf(quaternion.x, quaternion.y, quaternion.z, quaternion.w)
    }

    private fun SpongeQuaternionf.toEulerAngles(): Pair<Double, Double> {
        val ysqr = y() * y()

        // yaw x-asis rotation
        val t0 = +2.0 * (w() * x() + y() * z())
        val t1 = +1.0 - 2.0 * (x() * x() + ysqr)
        val yaw = atan2(t0, t1)

        // pitch y-axis rotation
        val t2 = +2.0 * (w() * y() - z() * x())
        val t3 = if (t2 > 1.0) 1.0 else if (t2 < -1.0) -1.0 else t2
        val pitch = asin(t3)

        return Pair(yaw, pitch)
    }

    fun calculateDisplayContentLocation(
        baseLocation: Location,
        facing: BlockFace,
        centerOffset: Vector
    ): Location {
        return baseLocation.clone().add(centerOffset).apply {
            val euler = calculateDisplayRotation(facing).toEulerAngles()

            yaw = Math.toDegrees(euler.first).toFloat()
            pitch = Math.toDegrees(euler.second).toFloat()
        }
    }

    fun calculateDisplayTextLocation(
        baseLocation: Location,
        facing: BlockFace,
        offset: Float
    ): Location {
        val location = baseLocation.clone()
        val offset = offset.toDouble()

        when (facing) {
            BlockFace.NORTH -> location.add(0.0, 0.0, offset)
            BlockFace.SOUTH -> location.add(0.0, 0.0, -offset)
            BlockFace.EAST -> location.add(-offset, 0.0, 0.0)
            BlockFace.WEST -> location.add(offset, 0.0, 0.0)
            else -> throw IllegalArgumentException("Unsupported facing direction: $facing")
        }

        val euler = calculateDisplayRotation(facing).toEulerAngles()
        location.yaw = Math.toDegrees(euler.first).toFloat()
        location.pitch = Math.toDegrees(euler.second).toFloat()

        return location
    }
}