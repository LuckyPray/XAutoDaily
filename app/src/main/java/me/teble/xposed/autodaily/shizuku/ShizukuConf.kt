@file:SuppressLint("UnsafeOptInUsageError")
package me.teble.xposed.autodaily.shizuku

import android.annotation.SuppressLint
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SealedSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class ShizukuConf(
    val enableKeepAlive: Boolean,
    @Serializable(with = ShizukuPersistentMapSerializer::class)
    val alivePackages: PersistentMap<String, Boolean>
)


@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = PersistentMap::class)
class ShizukuPersistentMapSerializer(
    private val keySerializer: KSerializer<String>,
    private val valueSerializer: KSerializer<Boolean>
) : KSerializer<PersistentMap<String, Boolean>> {

    @OptIn(SealedSerializationApi::class)
    private class PersistentMapDescriptor :
        SerialDescriptor by serialDescriptor<Map<String, Boolean>>() {
        @ExperimentalSerializationApi
        override val serialName: String = "kotlinx.serialization.immutable.persistentMap"
    }

    override val descriptor: SerialDescriptor = PersistentMapDescriptor()

    override fun serialize(encoder: Encoder, value: PersistentMap<String, Boolean>) {
        return MapSerializer(keySerializer, valueSerializer).serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): PersistentMap<String, Boolean> {
        return MapSerializer(keySerializer, valueSerializer).deserialize(decoder).toPersistentMap()
    }
}