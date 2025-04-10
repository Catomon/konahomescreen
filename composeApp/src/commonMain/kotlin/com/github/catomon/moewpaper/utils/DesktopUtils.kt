package com.github.catomon.moewpaper.utils

import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinUser.SW_RESTORE
import com.sun.jna.win32.StdCallLibrary
import com.sun.jna.win32.StdCallLibrary.StdCallCallback
import java.awt.Desktop
import java.io.File


object DesktopUtils {
    fun openFile(file: File) : Boolean {
        val desktop = Desktop.getDesktop()
        try {
            desktop.open(file)
            return true
        } catch (e: Exception) {
            println("error opening a file: ${file.path}")
            return false
        }
    }

    fun killProcess(pid: Long) {
        val process = ProcessHandle.of(pid).orElse(null)

        if (process != null) {
            process.destroy()
            println("Process terminated gracefully.")

            if (process.isAlive) {
                process.destroyForcibly()
                println("Process terminated forcibly.")
            }
        } else {
            println("Process not found.")
        }
    }
}

object BringWindowToForeground {
    private interface User32 : StdCallLibrary {
        fun EnumWindows(lpEnumFunc: WinUser.WNDENUMPROC?, arg: Pointer?): Boolean
        fun GetWindowThreadProcessId(hWnd: HWND?, processId: Pointer?): Int
        fun SetForegroundWindow(hWnd: HWND): Boolean
        fun ShowWindow(hWnd: HWND, nCmdShow: Int): Boolean
        fun GetWindowTextLength(hWnd: HWND): Int

        companion object {
            val INSTANCE: User32 = Native.loadLibrary(
                "user32",
                User32::class.java
            )
        }
    }

    private interface WinUser : StdCallLibrary {
        interface WNDENUMPROC : StdCallCallback {
            fun callback(hwnd: HWND?, arg: Pointer?): Boolean
        }

        companion object {
            val INSTANCE: WinUser = Native.loadLibrary(
                "user32",
                WinUser::class.java
            )
        }
    }

    @Throws(Exception::class)
    @JvmStatic
    fun bringMainWindowToForeground(processId: Long) {
        var mainHwnd: HWND? = null

        User32.INSTANCE.EnumWindows(object : WinUser.WNDENUMPROC {
            override fun callback(hwnd: HWND?, arg: Pointer?): Boolean {
                if (hwnd == null) return true

                val processIdPtr = Pointer(Native.malloc(4))
                User32.INSTANCE.GetWindowThreadProcessId(hwnd, processIdPtr)

                val pid = processIdPtr.getInt(0)
                Native.free(Pointer.nativeValue(processIdPtr))

                if (pid.toLong() == processId) {
                    val titleLength = com.sun.jna.platform.win32.User32.INSTANCE.GetWindowTextLength(hwnd)

                    if (titleLength > 0) {
                        mainHwnd = hwnd
                        return false
                    }
                }
                return true
            }
        }, null)

        val hwnd = mainHwnd
        if (hwnd != null) {
            User32.INSTANCE.ShowWindow(hwnd, SW_RESTORE)
            val result = User32.INSTANCE.SetForegroundWindow(hwnd)
            if (result) {
                println("Main window brought to foreground successfully.")
            } else {
                println("Failed to bring main window to foreground.")
            }
        } else {
            println("Main window not found for process ID $processId.")
        }
    }
}