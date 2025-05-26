package com.github.catomon.moewpaper.utils

import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.DesktopWindow
import com.sun.jna.platform.WindowUtils
import com.sun.jna.platform.win32.User32Util
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinDef.HWND
import com.sun.jna.platform.win32.WinUser.SW_RESTORE
import com.sun.jna.win32.StdCallLibrary
import com.sun.jna.win32.StdCallLibrary.StdCallCallback
import java.awt.Desktop
import java.io.File

object DesktopUtils {
    fun getWindows(): List<DesktopWindow> {
        return WindowUtils.getAllWindows(true).filter { it.title.isNotBlank() }
    }

    fun closeWindow(hwnd: HWND) {
        User32Util.destroyWindow(hwnd)
//        User32.INSTANCE.PostMessage(hwnd, com.sun.jna.platform.win32.WinUser.WM_CLOSE, null, null)
    }

    fun getWindowProcessId(hwnd: HWND) : Int {
        val processIdPtr = Pointer(Native.malloc(4))
        User32.INSTANCE.GetWindowThreadProcessId(hwnd, processIdPtr)

        val pid = processIdPtr.getInt(0)
        Native.free(Pointer.nativeValue(processIdPtr))

        return pid
    }

    fun closeWindowsOfProcess(processId: Int) {
        val windows = mutableListOf<HWND>()

        WindowUtils.getAllWindows(false).forEach { dWindow ->
            val hwnd = dWindow?.hwnd

            if (hwnd != null) {
                if (getWindowProcessId(hwnd) == processId) {
                    windows += hwnd
                }
            }
        }

        windows.forEach {
            closeWindow(it)
        }
    }

    fun openFile(file: File): Boolean {
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

    interface User32 : StdCallLibrary {
        fun EnumWindows(lpEnumFunc: WinUser.WNDENUMPROC?, arg: Pointer?): Boolean
        fun GetWindowThreadProcessId(hWnd: HWND?, processId: Pointer?): Int
        fun SetForegroundWindow(hWnd: HWND): Boolean
        fun ShowWindow(hWnd: HWND, nCmdShow: Int): Boolean
        fun GetWindowTextLength(hWnd: HWND): Int
        fun PostMessage(hWnd: WinDef.HWND, msg: Int, wParam: WinDef.WPARAM?, lParam: WinDef.LPARAM?)

        companion object {
            val INSTANCE: User32 = Native.loadLibrary(
                "user32",
                User32::class.java
            )
        }
    }

    interface WinUser : StdCallLibrary {
        interface WNDENUMPROC : StdCallCallback {
            fun callback(hwnd: HWND?, arg: Pointer?): Boolean
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
                    val titleLength =
                        com.sun.jna.platform.win32.User32.INSTANCE.GetWindowTextLength(hwnd)

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