package com.mgtriffid.gdxgame

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion

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
                    xSpeed = -HERO_VELOCITY
                    right = false
                }
            } else if (gameInput.rightPressed) {
                xSpeed = HERO_VELOCITY
                right = true
            } else {
                xSpeed = 0f
            }
            xPos += (xSpeed * dt).toFloat()
            if (isStanding && gameInput.upPressed) {
                ySpeed = JUMP_INITIAL_SPEED
                isStanding = false
            }
            if (!isStanding) {
                yPos += (ySpeed * dt + gAccel.toDouble() * dt * dt / 2).toFloat()

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

            if (right) {
                val lineBehindCurrent = Math.floor(((xPos + 26) / 70).toDouble()).toInt()
                val lineInFrontOfPrevious = Math.ceil(((previous.xPos + 26) / 70).toDouble()).toInt()
                if (lineBehindCurrent <= lineInFrontOfPrevious) {
                    outer@ for (j in lineBehindCurrent downTo lineInFrontOfPrevious) {
                        val yBottom = yPos
                        val yTop = yPos + 92
                        val lineBelow = Math.floor((yBottom / 70).toDouble()).toInt()
                        val lineAbove = Math.ceil((yTop / 70).toDouble()).toInt()
                        for (i in lineBelow + 1 .. lineAbove) {
                            if (gameMap.isSolid(i - 1, j)) {
                                xPos = (j * 70).toFloat() - 26
                                break@outer
                            }
                        }
                    }
                }
            } else {
                val lineBehindCurrent = Math.ceil(((xPos - 26) / 70).toDouble()).toInt()
                val lineInFrontOfPrevious = Math.floor(((previous.xPos - 26)/ 70).toDouble()).toInt()
                if (lineBehindCurrent >= lineInFrontOfPrevious) {
                    outer@ for (j in lineBehindCurrent..lineInFrontOfPrevious) {
                        val yBottom = yPos
                        val yTop = yPos + 92
                        val lineBelow = Math.floor((yBottom / 70).toDouble()).toInt()
                        val lineAbove = Math.ceil((yTop / 70).toDouble()).toInt()
                        for (i in lineBelow + 1 .. lineAbove) {
                            if (gameMap.isSolid(i - 1, j - 1)) {
                                xPos = (j * 70).toFloat() + 26
                                break@outer
                            }
                        }
                    }
                }
            }

            val lineBelowPrev = Math.floor((previous.yPos / 70).toDouble()).toInt()
            val lineAboveCurr = Math.ceil((yPos / 70).toDouble()).toInt()
            if (lineAboveCurr <= lineBelowPrev) {
                outer@ for (i in lineAboveCurr downTo lineBelowPrev) {
                    val xBottomCenter = xPos
                    val xBottomRight = xBottomCenter + 26
                    val xBottomLeft = xBottomCenter - 26
                    val lineToleft = Math.floor((xBottomLeft / 70).toDouble()).toInt()
                    val lineToRight = Math.ceil((xBottomRight / 70).toDouble()).toInt()
                    for (j in lineToleft..lineToRight - 1) {
                        if (gameMap.isFooting(i - 1, j)) {
                            yPos = (i * 70).toFloat()
                            isStanding = true
                            ySpeed = 0f
                            break@outer
                        }
                    }
                }
            }
            ySpeed = if (isStanding) 0f else ySpeed + (dt * gAccel).toFloat()

        }
    }

    fun GameMap.isFooting(i: Int, j: Int) : Boolean = mapTiles[i][j] > mapTiles[i + 1][j]

    fun GameMap.isSolid(i: Int, j: Int) : Boolean = mapTiles[i][j] == 3

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
        internal var right = true
        internal var isStanding = false
    }

    companion object {
        private var knight: TextureRegion? = null
        private var knightLeft: TextureRegion? = null
        private val HERO_VELOCITY = 300f
        private val JUMP_INITIAL_SPEED = 800f
        fun init() {
            val img = Texture("CadashSheet.gif")
            knight = TextureRegion(img, 0, 0, 52, 92)
            knightLeft = TextureRegion(img, 52, 0, -52, 92)
        }
    }
}
