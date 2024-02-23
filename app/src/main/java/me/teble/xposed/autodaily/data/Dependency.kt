package me.teble.xposed.autodaily.data

import kotlinx.serialization.Serializable


@Serializable
data class Dependency(
    val mavenCoordinates: MavenCoordinates,
    val name: String,
    val description: String,
    val licenses: List<License>
)

@Serializable
data class MavenCoordinates(
    val groupId: String,
    val artifactId: String,
    val version: String
)

@Serializable
data class License(
    val spdxLicenseIdentifier: String?,
    val name: String,
    val url: String
)