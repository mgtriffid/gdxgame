package com.mgtriffid.gdxgame

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion

import com.badlogic.gdx.math.MathUtils.floor
import com.badlogic.gdx.math.MathUtils.isZero

/**
 * Created by mgtriffid on 10.03.16.
 */
class HeroState {
    private val current = HeroStateSnapshot()
    private val previous = HeroStateSnapshot()
    private val forRender = HeroStateSnapshot()

    fun currentToPrevious() {
        previous.xPos = current.xPos
        previous.yPos = current.yPos
        previous.xSpeed = current.xSpeed
        previous.isStanding = current.isStanding
        previous.right = current.right
    }

    @Strictfp fun integrateCurrent(t: Float, dt: Float, gameInput: GameInput, gameMap: GameMap) {
        with(current) {
            if (gameInput.leftPressed) {
                if (!gameInput.rightPressed) {
                    xSpeed = -300f
                    right = false
                }
            } else if (gameInput.rightPressed) {
                xSpeed = 300f
                right = true
            } else {
                xSpeed = 0f
            }
            xPos += (xSpeed * dt).toFloat()
            if (isStanding && gameInput.upPressed) {
                ySpeed = 800f
                isStanding = false
            }
            if (!isStanding) {
                yPos += (ySpeed * dt + gAccel.toDouble() * dt * dt / 2).toFloat()


                val lineBelowPrev = Math.floor((previous.yPos / 70).toDouble()).toInt()
                val lineAboveCurr = Math.ceil((yPos / 70).toDouble()).toInt()
                if (lineAboveCurr <= lineBelowPrev) {
                    outer@ for (i in lineAboveCurr downTo lineBelowPrev) {
                        val xBottomCenter = xPos + (xPos - previous.xPos) *
                                (i * 70 - previous.yPos) / (yPos - previous.yPos)
                        val xBottomRight = xBottomCenter + 26
                        val xBottomLeft = xBottomCenter - 26
                        val lineToleft = Math.floor((xBottomLeft / 70).toDouble()).toInt()
                        val lineToRight = Math.ceil((xBottomRight / 70).toDouble()).toInt()
                        for (j in lineToleft..lineToRight - 1) {
                            if (gameMap.isFooting(i - 1, j)) {
                                yPos = (i * 70).toFloat()
                                xPos = xBottomCenter
                                isStanding = true
                                ySpeed = 0f
                                break@outer
                            }
                        }
                    }
                }
            } else {
                val line = (yPos / 70).toInt();
                val xBottomCenter = xPos
                val xBottomRight = xBottomCenter + 26
                val xBottomLeft = xBottomCenter - 26
                val lineToleft = Math.floor((xBottomLeft / 70).toDouble()).toInt()
                val lineToRight = Math.ceil((xBottomRight / 70).toDouble()).toInt()
                for (j in lineToleft..lineToRight - 1) {
                    if (gameMap.mapTiles[line-1][j] == 0) {
                        isStanding = false;
                    }
                }
            }
            ySpeed = if (isStanding) 0f else ySpeed + (dt * gAccel).toFloat()

        }
    }

    fun GameMap.isFooting(i: Int, j: Int) : Boolean = mapTiles[i][j] > mapTiles[i + 1][j]

    fun interpolateForRender(alpha: Double) {
        forRender.xPos = (previous.xPos + alpha * (current.xPos - previous.xPos).toDouble() * 1.0).toFloat()
        forRender.yPos = (previous.yPos + alpha * (current.yPos - previous.yPos).toDouble() * 1.0).toFloat()
        forRender.right = current.right
    }

    fun render(batch: SpriteBatch) {
        batch.draw(if (forRender.right) knight else knightLeft, forRender.xPos - 26, forRender.yPos)
    }

    private inner class HeroStateSnapshot {
        internal var xPos = 400f
        internal var yPos = 400f
        internal var xSpeed: Float = 0.toFloat()
        internal var ySpeed: Float = 0.toFloat()
        internal var gAccel = -2000f
        internal var previousStepTick: Int = 0
        internal var previousStep: Int = 0
        internal var right = true
        internal var isStanding = false
    }

    companion object {
        private var knight: TextureRegion? = null
        private var knightLeft: TextureRegion? = null

        fun init() {
            val img = Texture("CadashSheet.gif")
            knight = TextureRegion(img, 0, 0, 52, 92)
            knightLeft = TextureRegion(img, 52, 0, -52, 92)
        }
    }
}
