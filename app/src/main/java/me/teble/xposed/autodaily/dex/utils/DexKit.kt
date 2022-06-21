package me.teble.xposed.autodaily.dex.utils

import com.hankcs.algorithm.AhoCorasickDoubleArrayTrie
import me.teble.xposed.autodaily.BuildConfig
import me.teble.xposed.autodaily.dex.struct.*
import me.teble.xposed.autodaily.dex.utils.ByteUtils.readInt
import me.teble.xposed.autodaily.dex.utils.ByteUtils.readShort
import me.teble.xposed.autodaily.hook.base.hostContext
import me.teble.xposed.autodaily.hook.base.hostPackageName
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.invokeAs
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URL
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

object DexKit {

    private fun getAllDexEntry(zipFile: ZipFile): List<ZipEntry> {
        val zip: Enumeration<out ZipEntry> = zipFile.entries()
        val list = ArrayList<ZipEntry>()
        while (zip.hasMoreElements()) {
            val zipEntry = zip.nextElement()
//            LogUtil.log("file -> ${zipEntry.name}")
            if (zipEntry.name.endsWith(".dex")) {
                list.add(zipEntry)
            }
        }
//        LogUtil.log("dexList -> $list")
        return list.sortedWith(compareBy { it.name })
    }

    private fun getAllDex(classLoader: ClassLoader, packageName: String): List<URL> {
        val dexUrls: MutableList<URL> = ArrayList()
        var index = 0
        while (true) {
            index++
            val name = if (index == 1) "classes.dex" else "classes$index.dex"
//            LogUtil.log("classLoader -> ${classLoader.parent::class.java}")
            val enu: Enumeration<URL> = classLoader.invokeAs("findResources", name) ?: break
            var idx = 0
            while (enu.hasMoreElements()) {
                val url = enu.nextElement()
//                LogUtil.log("dex -> ${url.file}")
                if (url.file.contains(packageName)) {
                    idx++
                    dexUrls.add(url)
                }
            }
            if (idx == 0) {
                break
            }
        }
        return dexUrls
    }

    fun getDexBytes(zipFile: ZipFile, zipEntry: ZipEntry): ByteArray {
        val bis = BufferedInputStream(zipFile.getInputStream(zipEntry))
        val bos = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var len: Int
        while (bis.read(buffer).also { len = it } != -1) {
            bos.write(buffer, 0, len)
        }
        return bos.toByteArray()
    }

    fun getDexBytes(url: URL): ByteArray? {
        try {
            val buf = ByteArray(4096)
            val stream = url.openStream()
            val baos = ByteArrayOutputStream()
            var ii: Int
            while (stream.read(buf).also { ii = it } != -1) {
                baos.write(buf, 0, ii)
            }
            stream.close()
            return baos.toByteArray()
        } catch (e: Exception) {
            LogUtil.e(e)
        }
        return null
    }

    fun locateClasses(resourceMap: Map<String, Set<String>?>): Map<String, List<String>> {
        val startTime = System.currentTimeMillis()
        val acdat = AhoCorasickDoubleArrayTrie<String>().apply {
            this.build(TreeMap<String, String>().apply {
                resourceMap.entries.forEach { it.value?.forEach { this[it] = it } }
            })
        }
        val resultMap = mutableMapOf<String, LinkedList<String>>().apply {
            resourceMap.keys.forEach { this[it] = LinkedList() }
        }
        val applicationInfo = hostContext.packageManager.getApplicationInfo(
            hostPackageName,
            0
        )
        LogUtil.log("path -> ${applicationInfo.sourceDir}")
        val apkFile = File(applicationInfo.sourceDir)
        val zipFile = ZipFile(apkFile)
        val zipEntryList = getAllDexEntry(zipFile)
        for (zipEntry in zipEntryList) {
            val src = getDexBytes(zipFile, zipEntry)
            // index -> string
            val strIndexMap = HashMap<Int, String>()
            val stringIdsItems = StringIdsItem.parserStringIdsItems(src).apply {
                for (i in this.indices) {
                    val idsItem = this[i]
                    val dataItem = StringDataItem.parser(src, idsItem.stringDataOff)
                    acdat.parseText(
                        dataItem.data,
                        AhoCorasickDoubleArrayTrie.IHit { _, _, value -> strIndexMap[i] = value }
                    )
                }
            }
            // 该dex不存在特征串
            if (strIndexMap.isEmpty()) {
                LogUtil.log("该dex不存在特征串")
                continue
            }
            var confuseFlag = false
            resourceMap.forEach { (className, features) ->
                val featureStrSet = strIndexMap.values.toSet()
                if (featureStrSet.containsAll(features!!)) {
                    confuseFlag = true
                }
            }
            if (!confuseFlag) continue

            // start
            val classDefItems = ClassDefItem.parserClassDefItems(src)
            for (i in classDefItems.indices) {
                val classDefItem = classDefItems[i]
                val idsSet = mutableSetOf<Int>()
                val klass = StringDataItem.parser(
                    src,
                    stringIdsItems[TypeIdsItem.parserAndGet(
                        src,
                        classDefItem.classIdx
                    ).descriptorIdx].stringDataOff
                ).data
                if (classDefItem.staticValueOff != 0) {
                    val encodeArray =
                        EncodeArray.parser(src, IntArray(1) { classDefItem.staticValueOff })
                    idsSet.addAll(encodeArray.strIdSet)
                }
                if (classDefItem.classDataOff != 0) {
                    val dataItem = ClassDataItem.parser(src, classDefItem.classDataOff)
                    for (j in dataItem.directMethods.indices) {
                        val encodedMethod = dataItem.directMethods[j]
                        val codeItem = CodeItem.parser(src, encodedMethod!!.codeOff)
                        var index = 0
                        while (index < codeItem.insns.size) {
                            val op: Int = codeItem.insns[index].toInt() and 0xff
                            if (op == 0x00 && index + 1 < codeItem.insns.size && (codeItem.insns[index + 1] in 1..3)) {
                                val short = readShort(codeItem.insns, index + 2)
                                when (codeItem.insns[index + 1].toInt()) {
                                    1 -> {
                                        index += (short * 2 + 4) * 2
                                    }
                                    2 -> {
                                        index += (short * 4 + 2) * 2
                                    }
                                    3 -> {
                                        index += ((short * readInt(
                                            codeItem.insns,
                                            index + 4
                                        ) + 1) / 2 + 4) * 2
                                    }
                                }
                                continue
                            }
                            if (op == 0x1a) {
                                idsSet.add(readShort(codeItem.insns, index + 2))
                            } else if (op == 0x1b) {
                                idsSet.add(readInt(codeItem.insns, index + 2))
                            }
                            index += OpCodeFormatUtils.getOpSize(op)
                        }
                    }
                    for (encodedMethod in dataItem.virtualMethods) {
                        val codeItem = CodeItem.parser(src, encodedMethod!!.codeOff)
                        var index = 0
                        while (index < codeItem.insns.size) {
                            val op: Int = codeItem.insns[index].toInt() and 0xff
                            if (op == 0x00 && index + 1 < codeItem.insns.size && (codeItem.insns[index + 1] in 1..3)) {
                                val short = readShort(codeItem.insns, index + 2)
                                when (codeItem.insns[index + 1].toInt()) {
                                    1 -> {
                                        index += (short * 2 + 4) * 2
                                    }
                                    2 -> {
                                        index += (short * 4 + 2) * 2
                                    }
                                    3 -> {
                                        index += ((short * readInt(
                                            codeItem.insns,
                                            index + 4
                                        ) + 1) / 2 + 4) * 2
                                    }
                                }
                                continue
                            }
                            if (op == 0x1a) {
                                idsSet.add(readShort(codeItem.insns, index + 2))
                            } else if (op == 0x1b) {
                                idsSet.add(readInt(codeItem.insns, index + 2))
                            }
                            index += OpCodeFormatUtils.getOpSize(op)
                        }
                    }
                    val strSet = idsSet.mapNotNull { strIndexMap[it] }
                    resourceMap.entries.forEach { entry ->
                        val set = entry.value ?: emptySet()
                        if (set.size == (set intersect strSet).size) {
                            resultMap[entry.key]?.push(klass)
                        }
                    }
                }
            }
        }
        LogUtil.log(
            "search dex: ${zipEntryList.size}, search class: ${resultMap.size}, " +
                "search time:${System.currentTimeMillis() - startTime} ms"
        )
        return resultMap
    }

    private fun getSignName(cls: Class<*>): String {
        return "L" + cls.canonicalName?.replace('.', '/') + ';'
    }

    fun <T> findSubClasses(classLoader: ClassLoader, cls: Class<T>): MutableList<String> {
        val start = System.currentTimeMillis()
        val classList: MutableList<String> = ArrayList()
        val urls = getAllDex(classLoader, BuildConfig.APPLICATION_ID)
        val superClassName = getSignName(cls)
        for (url in urls) {
            val src = getDexBytes(url)
//            LogUtil.log(url.file)
            val stringIdsItems = StringIdsItem.parserStringIdsItems(src)
            val classDefItems = ClassDefItem.parserClassDefItems(src)
            for (classDefItem in classDefItems) {
                val currSuperClassName = StringDataItem.parser(
                    src,
                    stringIdsItems[TypeIdsItem.parserAndGet(
                        src,
                        classDefItem.superclassIdx
                    ).descriptorIdx].stringDataOff
                ).data
                if (currSuperClassName == superClassName) {
                    val currClassName = StringDataItem.parser(
                        src,
                        stringIdsItems[TypeIdsItem.parserAndGet(
                            src,
                            classDefItem.classIdx
                        ).descriptorIdx].stringDataOff
                    ).data
                    classList.add(currClassName)
                }
            }
        }
        LogUtil.log("search time -> ${(System.currentTimeMillis() - start)}, \n$superClassName's sub classes ->" + classList)
        return classList
    }
}