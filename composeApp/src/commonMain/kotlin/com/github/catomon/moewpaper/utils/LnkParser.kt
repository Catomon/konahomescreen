package com.github.catomon.moewpaper.utils

import mslinks.ShellLink
import rogga.logWarn
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class LnkParser(f: File) {
    var isDirectory: Boolean = false
        private set

    var isLocal: Boolean = false
        private set

    var target: String? = null
        private set

    init {
        try {
            val link = ShellLink(f)
            target = link.resolveTarget()
            isDirectory = f.isDirectory
        } catch (e: Exception) {
            logWarn { e.localizedMessage }
            try {
                parse(f)
            } catch (e: Exception) {
                logWarn { e.localizedMessage }
            }
        }
    }

    @Throws(IOException::class)
    private fun parse(f: File) {
        // read the entire file into a byte buffer
        val fin = FileInputStream(f)
        val bout = ByteArrayOutputStream()
        val buff = ByteArray(256)
        while (true) {
            val n = fin.read(buff)
            if (n == -1) {
                break
            }
            bout.write(buff, 0, n)
        }
        fin.close()
        val link = bout.toByteArray()

        parseLink(link)
    }

    private fun parseLink(link: ByteArray) {
        // get the flags byte
        val flags = link[0x14]

        // get the file attributes byte
        val file_atts_offset = 0x18
        val file_atts = link[file_atts_offset]
        val is_dir_mask = 0x10.toByte()
        isDirectory = if ((file_atts.toInt() and is_dir_mask.toInt()) > 0) {
            true
        } else {
            false
        }

        // if the shell settings are present, skip them
        val shell_offset = 0x4c
        val has_shell_mask = 0x01.toByte()
        var shell_len = 0
        if ((flags.toInt() and has_shell_mask.toInt()) > 0) {
            // the plus 2 accounts for the length marker itself
            shell_len = bytes2short(link, shell_offset) + 2
        }

        // get to the file settings
        val file_start = 0x4c + shell_len

        val file_location_info_flag_offset_offset = 0x08
        val file_location_info_flag =
            link[file_start + file_location_info_flag_offset_offset].toInt()
        isLocal = (file_location_info_flag and 2) == 0
        // get the local volume and local system values
        //final int localVolumeTable_offset_offset = 0x0C;
        val basename_offset_offset = 0x10
        val networkVolumeTable_offset_offset = 0x14
        val finalname_offset_offset = 0x18
        val finalname_offset = link[file_start + finalname_offset_offset] + file_start
        val finalname = getNullDelimitedString(link, finalname_offset)
        if (isLocal) {
            val basename_offset = link[file_start + basename_offset_offset] + file_start
            val basename = getNullDelimitedString(link, basename_offset)
            target = basename + finalname
        } else {
            val networkVolumeTable_offset =
                link[file_start + networkVolumeTable_offset_offset] + file_start
            val shareName_offset_offset = 0x08
            val shareName_offset =
                (link[networkVolumeTable_offset + shareName_offset_offset] + networkVolumeTable_offset)
            val shareName = getNullDelimitedString(link, shareName_offset)
            target = shareName + "\\" + finalname
        }
    }

    companion object {
        private fun getNullDelimitedString(bytes: ByteArray, off: Int): String {
            var len = 0
            // count bytes until the null character (0)
            while (true) {
                if (bytes.size <= off + len || bytes[off + len].toInt() == 0) {
                    break
                }
                len++
            }
            return String(bytes, off, len)
        }

        /*
 * convert two bytes into a short note, this is little endian because it's
 * for an Intel only OS.
 */
        private fun bytes2short(bytes: ByteArray, off: Int): Int {
            return ((bytes[off + 1].toInt() and 0xff) shl 8) or (bytes[off].toInt() and 0xff)
        }
    }
}