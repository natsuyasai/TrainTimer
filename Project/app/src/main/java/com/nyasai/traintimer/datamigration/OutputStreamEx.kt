package com.nyasai.traintimer.datamigration

import java.io.OutputStream

fun OutputStream.writeLine(str: String) {
    this.write("${str}\r\n".toByteArray())
}

fun OutputStream.writeLine() {
    this.write("\r\n".toByteArray())
}