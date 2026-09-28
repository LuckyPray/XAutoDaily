@file:OptIn(SealedSerializationApi::class)

package me.teble.xposed.autodaily.task.model

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SealedSerializationApi
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = PersistentList::class)
class UpdateInfoPersistentListSerializer(
    private val serializer: KSerializer<UpdateInfo>,
) : KSerializer<PersistentList<UpdateInfo>> {

    private class PersistentListDescriptor :
        SerialDescriptor by serialDescriptor<List<UpdateInfo>>() {
        @ExperimentalSerializationApi
        override val serialName: String = "kotlinx.serialization.immutable.persistentList"
    }

    override val descriptor: SerialDescriptor = PersistentListDescriptor()

    override fun serialize(encoder: Encoder, value: PersistentList<UpdateInfo>) {
        return ListSerializer(serializer).serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): PersistentList<UpdateInfo> {
        return ListSerializer(serializer).deserialize(decoder).toPersistentList()
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = PersistentList::class)
class TaskGroupPersistentListSerializer(
    private val serializer: KSerializer<TaskGroup>,
) : KSerializer<PersistentList<TaskGroup>> {

    private class PersistentListDescriptor :
        SerialDescriptor by serialDescriptor<List<TaskGroup>>() {
        @ExperimentalSerializationApi
        override val serialName: String = "kotlinx.serialization.immutable.persistentList"
    }

    override val descriptor: SerialDescriptor = PersistentListDescriptor()

    override fun serialize(encoder: Encoder, value: PersistentList<TaskGroup>) {
        return ListSerializer(serializer).serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): PersistentList<TaskGroup> {
        return ListSerializer(serializer).deserialize(decoder).toPersistentList()
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = PersistentList::class)
class TaskPersistentListSerializer(
    private val serializer: KSerializer<Task>,
) : KSerializer<PersistentList<Task>> {

    private class PersistentListDescriptor :
        SerialDescriptor by serialDescriptor<List<Task>>() {
        @ExperimentalSerializationApi
        override val serialName: String = "kotlinx.serialization.immutable.persistentList"
    }

    override val descriptor: SerialDescriptor = PersistentListDescriptor()

    override fun serialize(encoder: Encoder, value: PersistentList<Task>) {
        return ListSerializer(serializer).serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): PersistentList<Task> {
        return ListSerializer(serializer).deserialize(decoder).toPersistentList()
    }
}


@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = PersistentList::class)
class TaskEnvPersistentListSerializer(
    private val serializer: KSerializer<TaskEnv>,
) : KSerializer<PersistentList<TaskEnv>> {

    private class PersistentListDescriptor :
        SerialDescriptor by serialDescriptor<List<TaskEnv>>() {
        @ExperimentalSerializationApi
        override val serialName: String = "kotlinx.serialization.immutable.persistentList"
    }

    override val descriptor: SerialDescriptor = PersistentListDescriptor()

    override fun serialize(encoder: Encoder, value: PersistentList<TaskEnv>) {
        return ListSerializer(serializer).serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): PersistentList<TaskEnv> {
        return ListSerializer(serializer).deserialize(decoder).toPersistentList()
    }
}


@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = PersistentList::class)
class TaskConditionPersistentListSerializer(
    private val serializer: KSerializer<TaskCondition>,
) : KSerializer<PersistentList<TaskCondition>> {

    private class PersistentListDescriptor :
        SerialDescriptor by serialDescriptor<List<TaskCondition>>() {
        @ExperimentalSerializationApi
        override val serialName: String = "kotlinx.serialization.immutable.persistentList"
    }

    override val descriptor: SerialDescriptor = PersistentListDescriptor()

    override fun serialize(encoder: Encoder, value: PersistentList<TaskCondition>) {
        return ListSerializer(serializer).serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): PersistentList<TaskCondition> {
        return ListSerializer(serializer).deserialize(decoder).toPersistentList()
    }
}


@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = PersistentMap::class)
class ReqHeadersPersistentMapSerializer(
    private val keySerializer: KSerializer<String>,
    private val valueSerializer: KSerializer<String>
) : KSerializer<PersistentMap<String, String>> {

    private class PersistentMapDescriptor :
        SerialDescriptor by serialDescriptor<Map<String, String>>() {
        @ExperimentalSerializationApi
        override val serialName: String = "kotlinx.serialization.immutable.persistentMap"
    }

    override val descriptor: SerialDescriptor = PersistentMapDescriptor()

    override fun serialize(encoder: Encoder, value: PersistentMap<String, String>) {
        return MapSerializer(keySerializer, valueSerializer).serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): PersistentMap<String, String> {
        return MapSerializer(keySerializer, valueSerializer).deserialize(decoder).toPersistentMap()
    }
}


@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = PersistentList::class)
class MsgExtractPersistentMapSerializer(
    private val serializer: KSerializer<MsgExtract>,
) : KSerializer<PersistentList<MsgExtract>> {

    private class PersistentListDescriptor :
        SerialDescriptor by serialDescriptor<List<MsgExtract>>() {
        @ExperimentalSerializationApi
        override val serialName: String = "kotlinx.serialization.immutable.persistentList"
    }

    override val descriptor: SerialDescriptor = PersistentListDescriptor()

    override fun serialize(encoder: Encoder, value: PersistentList<MsgExtract>) {
        return ListSerializer(serializer).serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): PersistentList<MsgExtract> {
        return ListSerializer(serializer).deserialize(decoder).toPersistentList()
    }
}


@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = PersistentList::class)
class MsgReplacePersistentMapSerializer(
    private val serializer: KSerializer<MsgReplace>,
) : KSerializer<PersistentList<MsgReplace>> {

    private class PersistentListDescriptor :
        SerialDescriptor by serialDescriptor<List<MsgReplace>>() {
        @ExperimentalSerializationApi
        override val serialName: String = "kotlinx.serialization.immutable.persistentList"
    }

    override val descriptor: SerialDescriptor = PersistentListDescriptor()

    override fun serialize(encoder: Encoder, value: PersistentList<MsgReplace>) {
        return ListSerializer(serializer).serialize(encoder, value)
    }

    override fun deserialize(decoder: Decoder): PersistentList<MsgReplace> {
        return ListSerializer(serializer).deserialize(decoder).toPersistentList()
    }
}





