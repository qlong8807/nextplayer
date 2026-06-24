package dev.anilbeesetti.nextplayer.core.model

import java.io.Serializable

data class Folder(
    val name: String,
    val path: String,
    val dateModified: Long,
    val parentPath: String? = null,
    val totalSize: Long = 0,
    val totalDuration: Long = 0,
    val videosCount: Int = 0,
    val foldersCount: Int = 0,
) : Serializable {
    companion object {
        val sample = Folder(
            name = "Folder 1",
            path = "/storage/emulated/0/DCIM/Camera/Live Photos",
            dateModified = 2000,
        )
    }
}

/**
 * 将已置顶的文件夹排在前面，置顶文件夹之间按置顶时间降序排列（最近置顶的在最前）。
 * 非置顶文件夹保持原有顺序不变。
 */
fun List<Folder>.sortedWithPinned(pinnedFolders: Map<String, Long>): List<Folder> {
    if (pinnedFolders.isEmpty()) return this
    val pinned = filter { it.path in pinnedFolders }
        .sortedByDescending { pinnedFolders[it.path] ?: 0L }
    val unpinned = filter { it.path !in pinnedFolders }
    return pinned + unpinned
}

fun List<Folder>.findClosestFolder(videoPath: String): Folder? {
    val videoDirectory = videoPath.substringBeforeLast("/")

    return filter { folder ->
        // Match the folder itself or an ancestor, respecting path-segment boundaries so
        // that e.g. "/storage/Movies" is not treated as an ancestor of "/storage/Movies2".
        videoDirectory == folder.path || videoDirectory.startsWith(folder.path + "/")
    }.maxByOrNull { folder ->
        folder.path.length
    }
}
