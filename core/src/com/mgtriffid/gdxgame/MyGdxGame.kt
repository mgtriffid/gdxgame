package com.mgtriffid.gdxgame

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion

class MyGdxGame : ApplicationAdapter() {
    internal lateinit var batch: SpriteBatch

    internal var tick: Long = 0
    internal var t = 0.0f
    internal var dt = 0.04f
    internal var currentTime = System.currentTimeMillis() * 0.001
    internal var accumulator = 0.0
    internal var gameInput = GameInput()
    private lateinit var gameState: GameState

    override fun create() {
        batch = SpriteBatch()
        gameState = GameState()
    }

    @Strictfp override fun render() {
        receiveInputs()
        val newTime = System.currentTimeMillis() * 0.001
        var frameTime = newTime - currentTime
        if (frameTime > 0.25) {
            frameTime = 0.25
        }
        currentTime = newTime
        accumulator += frameTime

        while (accumulator >= dt) {
            tick++
            shiftState()
            accumulator -= dt
            resetInputs()
        }

        val alpha = accumulator / dt

        interpolateForRender(alpha)

        Gdx.gl.glClearColor(1f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch.begin()
        gameState.render(batch)
        batch.end()
    }

    private fun shiftState() {
        gameState.currentToPrevious()
        gameState.integrateCurrent(t, dt, gameInput)
        t += dt
    }

    private fun resetInputs() {
        gameInput.leftPressed = false
        gameInput.rightPressed = false
        gameInput.upPressed = false
    }

    private fun interpolateForRender(alpha: Double) {
        gameState.interpolateForRender(alpha)
    }

    private fun receiveInputs() {
        gameInput.leftPressed = gameInput.leftPressed or Gdx.input.isKeyPressed(Input.Keys.A)
        gameInput.rightPressed = gameInput.rightPressed or Gdx.input.isKeyPressed(Input.Keys.D)
        gameInput.upPressed = gameInput.upPressed or Gdx.input.isKeyPressed(Input.Keys.W)
    }

}
