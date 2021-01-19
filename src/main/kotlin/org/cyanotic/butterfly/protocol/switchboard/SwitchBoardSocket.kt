package org.cyanotic.butterfly.protocol.switchboard

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class SwitchBoardSocket {

    private lateinit var socket: Socket
    private lateinit var writer: PrintWriter
    private lateinit var reader: BufferedReader

    fun connect(endpoint: String, port: Int) {
        print("SB Connecting to $endpoint:$port...")
        socket = Socket(endpoint, port)
        writer = PrintWriter(socket.outputStream)
        reader = BufferedReader(InputStreamReader(socket.inputStream))
        println("Done!")
    }

    fun sendMessage(message: String, sendNewLine: Boolean = true) {
        val open = if(!socket.isClosed) "O" else "X"
        val connected = if(socket.isConnected) "C" else "D"
        print("SB ($open$connected) >> ")
        val messageToSend = if(sendNewLine) "$message\r\n" else message
        writer.write(messageToSend)
        writer.flush()
        println(messageToSend)
    }

    fun readMessage(): String {
        val response = reader.readLine()
        println("SB << $response")
        return response
    }

    fun readRaw(length: Int): String {
        print("SB << ")
        var output = ""
        var remaining = length
        while (remaining > 0) {
            val buffer = CharArray(remaining)
            remaining -= reader.read(buffer)
            output += String(buffer)
        }
        println(output)
        return output
    }

    fun close() {
        print("SB Closing connection...")
        writer.close()
        reader.close()
        socket.close()
        println("Done!")
    }
}