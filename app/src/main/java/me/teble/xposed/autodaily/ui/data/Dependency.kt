@file:SuppressLint("UnsafeOptInUsageError")
package me.teble.xposed.autodaily.ui.data

import android.annotation.SuppressLint
import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SealedSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Stable
@Serializable
data class Dependency(
    val mavenCoordinates: MavenCoordinates,
    val name: String,
    val description: String,
    @Serializable(with = DependencyPersistentListSerializer::class)
    val licenses: PersistentList<License>
)

@Stable
@Serializable
data class MavenCoordinates(
    val groupId: String,
    val artifactId: String,
    val version: String
)

@Stable
@Serializable
data class License(
    val spdxLicenseIdentifier: String?,
    val name: String,
    val url: String
)

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = PersistentList::class)
class DependencyPersistentListSerializer(
    private val serializer: KSerializer<License>,
) : KSerializer<PersistentList<License>> {

    @OptIn(SealedSerializationApi::class)
    private class PersistentListDescriptor :
        SerialDescriptor by serialDescriptor<List<License>>() {
        @ExperimentalSerializationApi
        override val serialName: String = "kotlinx.serialization.immutable.persistentList"
    }

    override val descriptor: SerialDescriptor = PersistentListDescriptor()

    override fun serialize(encoder: Encoder, value: PersistentList<License>) {
        return ListSerializer(serializer).serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): PersistentList<License> {
        return ListSerializer(serializer).deserialize(decoder).toPersistentList()
    }

}