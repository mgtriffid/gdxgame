package com.mgtriffid.gdxgame

/**
 * Created by mgtriffid on 10.03.16.
 */
class GameInput {
    @Volatile var leftPressed = false
    @Volatile var rightPressed = false
    @Volatile var upPressed = false
    @Volatile var mouseClicked = false
    @Volatile var mousePositionX = 0f
    @Volatile var mousePositionY = 0f

    fun cloneInto(gameInput: GameInput) {
        gameInput.leftPressed = leftPressed
        gameInput.rightPressed = rightPressed
        gameInput.upPressed = upPressed
        gameInput.mouseClicked = mouseClicked
        gameInput.mousePositionX = mousePositionX
        gameInput.mousePositionY = mousePositionY
    }
}
