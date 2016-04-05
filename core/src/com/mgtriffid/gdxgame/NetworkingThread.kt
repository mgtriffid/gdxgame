package com.mgtriffid.gdxgame

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.SynchronousQueue

/**
 * Created by mgtriffid on 04.04.16.
 */
class NetworkingThread {

    var gameInputs = Array(256, { GameInput() })
    val queue = SynchronousQueue<GameInput>();

    fun getServerActions() : GameServer {
        return GameServer()
    }

    fun submitInputs(gameInput: GameInput, tick: Byte) {
        queue.put(gameInput)
    }

    fun run() {
        Thread(object: Runnable {
            private var tick : Byte = 0
            override fun run() {
                while (true) {
                    queue.take().cloneInto(gameInputs[tick.toInt() + 128])
                    tick = (tick + 1).toByte()
                    receiveStates()
                }
            }

            private fun receiveStates() {

                //
            }
        }).start()
    }
}