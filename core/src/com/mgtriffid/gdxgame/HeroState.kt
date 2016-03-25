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
                val line = (yPos / TILE_SIZE).toInt();
                val xBottomCenter = xPos
                val xBottomRight = xBottomCenter + 26
                val xBottomLeft = xBottomCenter - 26
                val lineToleft = Math.floor((xBottomLeft / TILE_SIZE).toDouble()).toInt()
                val lineToRight = Math.ceil((xBottomRight / TILE_SIZE).toDouble()).toInt()
                for (j in lineToleft..lineToRight - 1) {
                    if (gameMap.mapTiles[line-1][j] == 0) {
                        isStanding = false;
                    }
                }
            }

            if (right) {
                val lineBehindCurrent = Math.floor(((xPos + 26) / TILE_SIZE).toDouble()).toInt()
                val lineInFrontOfPrevious = Math.ceil(((previous.xPos + 26) / TILE_SIZE).toDouble()).toInt()
                if (lineBehindCurrent <= lineInFrontOfPrevious) {
                    outer@ for (j in lineBehindCurrent downTo lineInFrontOfPrevious) {
                        val yBottom = yPos
                        val yTop = yPos + KNIGHT_HEIGHT
                        val lineBelow = Math.floor((yBottom / TILE_SIZE).toDouble()).toInt()
                        val lineAbove = Math.ceil((yTop / TILE_SIZE).toDouble()).toInt()
                        for (i in lineBelow + 1 .. lineAbove) {
                            if (gameMap.isSolid(i - 1, j)) {
                                xPos = (j * TILE_SIZE).toFloat() - 26
                                break@outer
                            }
                        }
                    }
                }
            } else {
                val lineBehindCurrent = Math.ceil(((xPos - 26) / TILE_SIZE).toDouble()).toInt()
                val lineInFrontOfPrevious = Math.floor(((previous.xPos - 26)/ TILE_SIZE).toDouble()).toInt()
                if (lineBehindCurrent >= lineInFrontOfPrevious) {
                    outer@ for (j in lineBehindCurrent..lineInFrontOfPrevious) {
                        val yBottom = yPos
                        val yTop = yPos + KNIGHT_HEIGHT
                        val lineBelow = Math.floor((yBottom / TILE_SIZE).toDouble()).toInt()
                        val lineAbove = Math.ceil((yTop / TILE_SIZE).toDouble()).toInt()
                        for (i in lineBelow + 1 .. lineAbove) {
                            if (gameMap.isSolid(i - 1, j - 1)) {
                                xPos = (j * TILE_SIZE).toFloat() + 26
                                break@outer
                            }
                        }
                    }
                }
            }
            if (yPos < previous.yPos) {
                val lineBelowPrev = Math.floor((previous.yPos / TILE_SIZE).toDouble()).toInt()
                val lineAboveCurr = Math.ceil((yPos / TILE_SIZE).toDouble()).toInt()
                if (lineAboveCurr <= lineBelowPrev) {
                    outer@ for (i in lineAboveCurr downTo lineBelowPrev) {
                        val xBottomCenter = xPos
                        val xBottomRight = xBottomCenter + 26
                        val xBottomLeft = xBottomCenter - 26
                        val lineToleft = Math.floor((xBottomLeft / TILE_SIZE).toDouble()).toInt()
                        val lineToRight = Math.ceil((xBottomRight / TILE_SIZE).toDouble()).toInt()
                        for (j in lineToleft..lineToRight - 1) {
                            if (gameMap.isFooting(i - 1, j)) {
                                yPos = (i * TILE_SIZE).toFloat()
                                isStanding = true
                                ySpeed = 0f
                                break@outer
                            }
                        }
                    }
                }
            } else {
                val lineAbovePrev = Math.ceil(((previous.yPos + KNIGHT_HEIGHT) / TILE_SIZE).toDouble()).toInt()
                val lineBelowCurr = Math.floor(((yPos + KNIGHT_HEIGHT) / TILE_SIZE).toDouble()).toInt()
                if (lineBelowCurr >= lineAbovePrev) {
                    outer@ for (i in lineAbovePrev..lineBelowCurr) {
                        val xBottomCenter = xPos
                        val xBottomRight = xBottomCenter + 26
                        val xBottomLeft = xBottomCenter - 26
                        val lineToleft = Math.floor((xBottomLeft / TILE_SIZE).toDouble()).toInt()
                        val lineToRight = Math.ceil((xBottomRight / TILE_SIZE).toDouble()).toInt()
                        for (j in lineToleft..lineToRight - 1) {
                            if (gameMap.isSolid(i, j)) {
                                yPos = (i * TILE_SIZE).toFloat() -KNIGHT_HEIGHT
                                ySpeed = 0f
                                break@outer
                            }
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

    companion object Constants {
        fun init() {
            val img = Texture("CadashSheet.gif")
            knight = TextureRegion(img, 0, 27, 52, KNIGHT_HEIGHT)
            knightLeft = TextureRegion(img, 52, 27, -52, KNIGHT_HEIGHT)
        }
    }
}

private var knight: TextureRegion? = null
private var knightLeft: TextureRegion? = null
private val HERO_VELOCITY = 300f
private val JUMP_INITIAL_SPEED = 800f
private val KNIGHT_HEIGHT = 65