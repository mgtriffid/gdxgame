package com.mgtriffid.gdxgame

/**
 * Created by mgtriffid on 04.04.16.
 */
class GameLogic(val gameInput: GameInput) {

    lateinit var gameState: GameState
    internal var t = 0.0f
    internal var dt = 0.04f
    lateinit var networkingThread: NetworkingThread

    fun init() {
        gameState = GameState()
    }

    fun grabStateFromNetwork() {

    }

    fun shiftState() {
        gameState.currentToPrevious()
        gameState.integrateCurrent(t, dt, gameInput)
        t += dt
    }
}