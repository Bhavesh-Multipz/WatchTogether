package com.instaconnect.android.fileHelper

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.DatabaseUtils
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.instaconnect.android.utils.LocalStorageProvider
import gun0912.tedimagepicker.util.ToastUtil.context
import java.io.File
import java.io.FileFilter
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel
import java.text.DecimalFormat
import java.util.*

class FileUtils     //private constructor to enforce Singleton pattern
    (private val context: Context) {
    /**
     * Convert File into Uri.
     *
     * @param file
     * @return uri
     */
    fun getUri(file: File?): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                val packageId = context.packageName
                FileProvider.getUriForFile(context, packageId, file!!)
            } catch (e: IllegalArgumentException) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    throw SecurityException()
                } else {
                    Uri.fromFile(file)
                }
            }
        } else {
            Uri.fromFile(file)
        }
    }

    /**
     * @return The MIME type for the give Uri.
     */
    fun getMimeType(uri: Uri): String? {
        val file = File(getPath(uri))
        return getMimeType(file)
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.<br></br>
     * <br></br>
     * Callers should check whether the path is local before assuming it
     * represents a local file.
     *
     * @param uri The Uri to query.
     * @see .isLocal
     * @see .getFile
     * @author paulburke
     */
    fun getPath(uri: Uri): String? {
        if (DEBUG) Log.d(
            TAG + " File -",
            "Authority: " + uri.authority +
                    ", Fragment: " + uri.fragment +
                    ", Port: " + uri.port +
                    ", Query: " + uri.query +
                    ", Scheme: " + uri.scheme +
                    ", Host: " + uri.host +
                    ", Segments: " + uri.pathSegments.toString()
        )
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // LocalStorageProvider
            if (isLocalStorageDocument(uri)) {
                // The path is the id
                return DocumentsContract.getDocumentId(uri)
            } else if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                    ).toString() + "/" + split[1]
                }

                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {

            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
                context,
                uri,
                null,
                null
            )
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    /**
     * Convert Uri into File, if possible.
     *
     * @return file A local file that the Uri was pointing to, or null if the
     * Uri is unsupported or pointed to a remote resource.
     * @see .getPath
     * @author paulburke
     */
    fun getFile(uri: Uri?): File? {
        if (uri != null) {
            val path = getPath(uri)
            if (path != null && isLocal(path)) {
                return File(path)
            }
        }
        return null
    }

    /**
     * Attempt to retrieve the thumbnail of given File from the MediaStore. This
     * should not be called on the UI thread.
     *
     * @param file
     * @return
     * @author paulburke
     */
    fun getThumbnail(file: File): Bitmap? {
        return getThumbnail(getUri(file), getMimeType(file))
    }

    /**
     * Attempt to retrieve the thumbnail of given Uri from the MediaStore. This
     * should not be called on the UI thread.
     *
     * @param uri
     * @return
     * @author paulburke
     */
    fun getThumbnail(uri: Uri): Bitmap? {
        return getThumbnail(uri, getMimeType(uri))
    }

    /**
     * Attempt to retrieve the thumbnail of given Uri from the MediaStore. This
     * should not be called on the UI thread.
     *
     * @param uri
     * @param mimeType
     * @return
     * @author paulburke
     */
    fun getThumbnail(uri: Uri?, mimeType: String?): Bitmap? {
        if (DEBUG) Log.d(TAG, "Attempting to get thumbnail")
        if (!isMediaUri(uri)) {
            Log.e(TAG, "You can only retrieve thumbnails for images and videos.")
            return null
        }
        var bm: Bitmap? = null
        if (uri != null) {
            val resolver = context.contentResolver
            var cursor: Cursor? = null
            try {
                cursor = resolver.query(uri, null, null, null, null)
                if (cursor!!.moveToFirst()) {
                    val id = cursor.getInt(0)
                    if (DEBUG) Log.d(TAG, "Got thumb ID: $id")
                    if (mimeType!!.contains("video")) {
                        bm = MediaStore.Video.Thumbnails.getThumbnail(
                            resolver,
                            id.toLong(),
                            MediaStore.Video.Thumbnails.MINI_KIND,
                            null
                        )
                    } else if (mimeType.contains(MIME_TYPE_IMAGE)) {
                        bm = MediaStore.Images.Thumbnails.getThumbnail(
                            resolver,
                            id.toLong(),
                            MediaStore.Images.Thumbnails.MINI_KIND,
                            null
                        )
                    }
                }
            } catch (e: Exception) {
                if (DEBUG) Log.e(TAG, "getThumbnail", e)
            } finally {
                cursor?.close()
            }
        }
        return bm
    }

    companion object {
        /** TAG for log messages.  */
        const val TAG = "FileUtils"
        private const val DEBUG = false // Set to true to enable logging
        const val MIME_TYPE_AUDIO = "audio/*"
        const val MIME_TYPE_TEXT = "text/*"
        const val MIME_TYPE_IMAGE = "image/*"
        const val MIME_TYPE_VIDEO = "video/*"
        const val MIME_TYPE_APP = "application/*"
        const val HIDDEN_PREFIX = "."

        /**
         * Gets the extension of a file name, like ".png" or ".jpg".
         *
         * @param uri
         * @return Extension including the dot("."); "" if there is no extension;
         * null if uri was null.
         */
        fun getExtension(uri: String?): String? {
            if (uri == null) {
                return null
            }
            val dot = uri.lastIndexOf(".")
            return if (dot >= 0) {
                uri.substring(dot)
            } else {
                // No extension.
                ""
            }
        }

        /**
         * @return Whether the URI is a local one.
         */
        fun isLocal(url: String?): Boolean {
            return if (url != null && !url.startsWith("http://") && !url.startsWith("https://")) {
                true
            } else false
        }

        /**
         * @return True if Uri is a MediaStore Uri.
         * @author paulburke
         */
        fun isMediaUri(uri: Uri?): Boolean {
            return "media".equals(uri!!.authority, ignoreCase = true)
        }

        /**
         * Returns the path only (without file name).
         *
         * @param file
         * @return
         */
        fun getPathWithoutFilename(file: File?): File? {
            return if (file != null) {
                if (file.isDirectory) {
                    // no file to be split off. Return everything
                    file
                } else {
                    val filename = file.name
                    val filepath = file.absolutePath

                    // Construct path without file name.
                    var pathwithoutname = filepath.substring(
                        0,
                        filepath.length - filename.length
                    )
                    if (pathwithoutname.endsWith("/")) {
                        pathwithoutname = pathwithoutname.substring(0, pathwithoutname.length - 1)
                    }
                    File(pathwithoutname)
                }
            } else null
        }

        /**
         * @return The MIME type for the given file.
         */
        fun getMimeType(file: File): String? {
            val extension = getExtension(file.name)
            return if (extension!!.length > 0) MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                extension.substring(1)
            ) else "application/octet-stream"
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is [LocalStorageProvider].
         * @author paulburke
         */
        fun isLocalStorageDocument(uri: Uri): Boolean {
            return LocalStorageProvider.AUTHORITY == uri.authority
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is ExternalStorageProvider.
         * @author paulburke
         */
        fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents" == uri.authority
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is DownloadsProvider.
         * @author paulburke
         */
        fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri.authority
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is MediaProvider.
         * @author paulburke
         */
        fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents" == uri.authority
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is Google Photos.
         */
        fun isGooglePhotosUri(uri: Uri): Boolean {
            return "com.google.android.apps.photos.content" == uri.authority
        }

        /**
         * Get the value of the data column for this Uri. This is useful for
         * MediaStore Uris, and other file-based ContentProviders.
         *
         * @param context The context.
         * @param uri The Uri to query.
         * @param selection (Optional) Filter used in the query.
         * @param selectionArgs (Optional) Selection arguments used in the query.
         * @return The value of the _data column, which is typically a file path.
         * @author paulburke
         */
        fun getDataColumn(
            context: Context, uri: Uri?, selection: String?,
            selectionArgs: Array<String>?
        ): String? {
            var cursor: Cursor? = null
            val column = "_data"
            val projection = arrayOf(
                column
            )
            try {
                cursor = context.contentResolver.query(
                    uri!!, projection, selection, selectionArgs,
                    null
                )
                if (cursor != null && cursor.moveToFirst()) {
                    if (DEBUG) DatabaseUtils.dumpCursor(cursor)
                    val column_index = cursor.getColumnIndexOrThrow(column)
                    return cursor.getString(column_index)
                }
            } finally {
                cursor?.close()
            }
            return null
        }

        /**
         * Get the file size in a human-readable string.
         *
         * @param size
         * @return
         * @author paulburke
         */
        fun getReadableFileSize(size: Int): String {
            val BYTES_IN_KILOBYTES = 1024
            val dec = DecimalFormat("###.#")
            val KILOBYTES = " KB"
            val MEGABYTES = " MB"
            val GIGABYTES = " GB"
            var fileSize = 0f
            var suffix = KILOBYTES
            if (size > BYTES_IN_KILOBYTES) {
                fileSize = (size / BYTES_IN_KILOBYTES).toFloat()
                if (fileSize > BYTES_IN_KILOBYTES) {
                    fileSize = fileSize / BYTES_IN_KILOBYTES
                    if (fileSize > BYTES_IN_KILOBYTES) {
                        fileSize = fileSize / BYTES_IN_KILOBYTES
                        suffix = GIGABYTES
                    } else {
                        suffix = MEGABYTES
                    }
                }
            }
            return dec.format(fileSize.toDouble()) + suffix
        }

        /**
         * File and folder comparator. TODO Expose sorting option method
         *
         * @author paulburke
         */
        var sComparator =
            Comparator<File> { f1, f2 -> // Sort alphabetically by lower case, which is much cleaner
                f1.name.toLowerCase().compareTo(
                    f2.name.toLowerCase()
                )
            }

        /**
         * File (not directories) filter.
         *
         * @author paulburke
         */
        var sFileFilter = FileFilter { file ->
            val fileName = file.name
            // Return files only (not directories) and skip hidden files
            file.isFile && !fileName.startsWith(HIDDEN_PREFIX)
        }

        /**
         * Folder (directories) filter.
         *
         * @author paulburke
         */
        var sDirFilter = FileFilter { file ->
            val fileName = file.name
            // Return directories only and skip hidden directories
            file.isDirectory && !fileName.startsWith(HIDDEN_PREFIX)
        }

        /**
         * Get the Intent for selecting content to be used in an Intent Chooser.
         *
         * @return The intent for opening a file with Intent.createChooser()
         * @author paulburke
         */
        fun createGetContentIntent(): Intent {
            // Implicitly allow the user to select a particular kind of data
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            // The MIME data type filter
            intent.type = "*/*"
            // Only return URIs that can be opened with ContentResolver
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            return intent
        }

        fun copyFile(sourceFile: File, destFile: File?) {
            try {
                if (!sourceFile.exists()) {
                    return
                }
                var source: FileChannel? = null
                var destination: FileChannel? = null
                source = FileInputStream(sourceFile).channel
                destination = FileOutputStream(destFile).channel
                if (destination != null && source != null) {
                    destination.transferFrom(source, 0, source.size())
                }
                source?.close()
                destination?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun deleteFile(path: String?): Boolean {
            return try {
                val file = File(path)
                if (file.exists()) file.delete()
                true
            } catch (e: Exception) {
                false
            }
        }

        fun log2(n: Long): Double {
            // Implement this but without inaccuracies due to FP math.
            // Just count the number of leading zeros and do the math.
            return Math.log(n.toDouble()) / Math.log(2.0)
        }

        fun getFileSize(fileSize: Long): String {
            return try {
                val logSize = log2(fileSize)
                    .toLong()
                val suffixes =
                    arrayOf(" B", " Kb", " Mb", " Gb", " Tb", " Pb", " EiB", " ZiB", " YiB")
                val suffixIndex = (logSize / 10).toInt() // 2^10 = 1024
                val displaySize = fileSize / Math.pow(2.0, (suffixIndex * 10).toDouble())
                val df = DecimalFormat("#.##")
                df.format(displaySize) + suffixes[suffixIndex]
            } catch (e: Exception) {
                e.printStackTrace()
                "0 Kb"
            }
        }

        fun sortByNumber(files: Array<String>) {
            Arrays.sort(files, object : Comparator<String> {
                override fun compare(o1: String, o2: String): Int {
                    val n1 = extractNumber(o1)
                    val n2 = extractNumber(o2)
                    return n1 - n2
                }

                private fun extractNumber(name: String): Int {
                    var i = 0
                    i = try {
                        val s = name.indexOf('_') + 1
                        val e = name.lastIndexOf('.')
                        val number = name.substring(s, e)
                        number.toInt()
                    } catch (e: Exception) {
                        0 // if filename does not match the format
                        // then default to 0
                    }
                    return i
                }
            })
            for (f in files) {
                println(f)
            }
        }
    }


}