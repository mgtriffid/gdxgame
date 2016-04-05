package com.mgtriffid.gdxgame

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class MyGdxGame : ApplicationAdapter() {
    internal lateinit var batch: SpriteBatch

    internal var tick: Byte = 0

    internal var currentTime = System.currentTimeMillis() * 0.001
    internal var accumulator = 0.0
    internal var gameInput = GameInput()
    private lateinit var cam: OrthographicCamera
    internal var cameraPositionXOffset = 0f;
    internal var cameraPositionYOffset = 0f;

    private lateinit var networkingThread: NetworkingThread

    private lateinit var gameLogic: GameLogic

    override fun create() {
        batch = SpriteBatch()
        cam = OrthographicCamera(CAMERA_WIDTH, CAMERA_HEIGHT);
        gameLogic = GameLogic(gameInput)
        gameLogic.init()
        networkingThread = NetworkingThread()
        networkingThread.run()
        gameLogic.networkingThread = networkingThread
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

        while (accumulator >= gameLogic.dt) {
            tick++
            sampleInputsToNetwork()
            gameLogic.grabStateFromNetwork()
            gameLogic.shiftState()

            accumulator -= gameLogic.dt
            resetInputs()
        }

        val alpha = accumulator / gameLogic.dt

        interpolateForRender(alpha)

        Gdx.gl.glClearColor(1f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        cam.update()
        batch.projectionMatrix = cam.combined
        cam.stare(gameLogic.gameState, cameraPositionXOffset, cameraPositionYOffset)
        batch.begin()
        gameLogic.gameState.render(batch)
        batch.end()
    }

    private fun sampleInputsToNetwork() {
        networkingThread.submitInputs(gameInput, tick)
    }

    private fun resetInputs() {
        gameInput.leftPressed = false
        gameInput.rightPressed = false
        gameInput.upPressed = false
    }

    private fun interpolateForRender(alpha: Double) {
        gameLogic.gameState.interpolateForRender(alpha)
    }

    private fun receiveInputs() {
        gameInput.leftPressed = gameInput.leftPressed or Gdx.input.isKeyPressed(Input.Keys.A)
        gameInput.rightPressed = gameInput.rightPressed or Gdx.input.isKeyPressed(Input.Keys.D)
        gameInput.upPressed = gameInput.upPressed or Gdx.input.isKeyPressed(Input.Keys.W)
        gameInput.mouseClicked = Gdx.input.isButtonPressed(Input.Buttons.LEFT)
        val xRaw = Gdx.input.x
        val yRaw = Gdx.input.y
        cameraPositionXOffset = (xRaw - CAMERA_HALF_WIDTH) * 0.7f
        cameraPositionYOffset = (CAMERA_HALF_HEIGHT - yRaw) * 0.7f
        gameInput.mousePositionX = xRaw - CAMERA_HALF_WIDTH + cameraPositionXOffset + gameLogic.gameState.heroState.xRender
        gameInput.mousePositionY = CAMERA_HALF_HEIGHT - yRaw + cameraPositionYOffset + gameLogic.gameState.heroState.yRender + HALF_KNIGHT_HEIGHT
    }
}

fun OrthographicCamera.stare(gameState: GameState, xOffset: Float, yOffset: Float) {
    this.position.x = gameState.heroState.xRender + xOffset
    this.position.y = gameState.heroState.yRender + HALF_KNIGHT_WIDTH + yOffset
}

private val CAMERA_WIDTH = 640f
private val CAMERA_HALF_WIDTH = CAMERA_WIDTH / 2
private val CAMERA_HEIGHT = 480f
private val CAMERA_HALF_HEIGHT = CAMERA_HEIGHT / 2