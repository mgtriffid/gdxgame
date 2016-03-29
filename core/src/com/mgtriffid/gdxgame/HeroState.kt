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
    val x : Float
        get() = current.xPos

    val y : Float
        get() = current.yPos

    val xRender : Float
        get() = forRender.xPos

    val yRender : Float
        get() = forRender.yPos




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
                val line = ((yPos - HALF_KNIGHT_HEIGHT )/ TILE_SIZE).toInt();
                val xBottomCenter = xPos
                val xBottomRight = xBottomCenter + HALF_KNIGHT_WIDTH
                val xBottomLeft = xBottomCenter - HALF_KNIGHT_WIDTH
                val lineToleft = lineLess(xBottomLeft)
                val lineToRight = lineGreater(xBottomRight)
                for (j in lineToleft..lineToRight - 1) {
                    if (gameMap.mapTiles[line-1][j] == 0) {
                        isStanding = false;
                    }
                }
            }

            if (right) {
                val lineBehindCurrent = lineLess(xPos + HALF_KNIGHT_WIDTH)
                val lineInFrontOfPrevious = lineGreater(previous.xPos + HALF_KNIGHT_WIDTH)
                if (lineBehindCurrent <= lineInFrontOfPrevious) {
                    outer@ for (j in lineBehindCurrent downTo lineInFrontOfPrevious) {
                        val yBottom = yPos - HALF_KNIGHT_HEIGHT
                        val yTop = yPos + HALF_KNIGHT_HEIGHT
                        val lineBelow = lineLess(yBottom)
                        val lineAbove = lineGreater(yTop)
                        for (i in lineBelow + 1 .. lineAbove) {
                            if (gameMap.isSolid(i - 1, j)) {
                                xPos = (j * TILE_SIZE).toFloat() - HALF_KNIGHT_WIDTH
                                break@outer
                            }
                        }
                    }
                }
            } else {
                val lineBehindCurrent = lineGreater(xPos - HALF_KNIGHT_WIDTH)
                val lineInFrontOfPrevious = lineLess(previous.xPos - HALF_KNIGHT_WIDTH)
                if (lineBehindCurrent >= lineInFrontOfPrevious) {
                    outer@ for (j in lineBehindCurrent..lineInFrontOfPrevious) {
                        val yBottom = yPos - HALF_KNIGHT_HEIGHT
                        val yTop = yPos + HALF_KNIGHT_HEIGHT
                        val lineBelow = lineLess(yBottom)
                        val lineAbove = lineGreater(yTop)
                        for (i in lineBelow + 1 .. lineAbove) {
                            if (gameMap.isSolid(i - 1, j - 1)) {
                                xPos = (j * TILE_SIZE).toFloat() + HALF_KNIGHT_WIDTH
                                break@outer
                            }
                        }
                    }
                }
            }
            if (yPos < previous.yPos) {
                val lineBelowPrev = lineLess(previous.yPos - HALF_KNIGHT_HEIGHT)
                val lineAboveCurr = lineGreater(yPos - HALF_KNIGHT_HEIGHT)
                if (lineAboveCurr <= lineBelowPrev) {
                    outer@ for (i in lineAboveCurr downTo lineBelowPrev) {
                        val xBottomCenter = xPos
                        val xBottomRight = xBottomCenter + HALF_KNIGHT_WIDTH
                        val xBottomLeft = xBottomCenter - HALF_KNIGHT_WIDTH
                        val lineToleft = lineLess(xBottomLeft)
                        val lineToRight = lineGreater(xBottomRight)
                        for (j in lineToleft..lineToRight - 1) {
                            if (gameMap.isFooting(i - 1, j)) {
                                yPos = (i * TILE_SIZE).toFloat() + HALF_KNIGHT_HEIGHT
                                isStanding = true
                                ySpeed = 0f
                                break@outer
                            }
                        }
                    }
                }
            } else {
                val lineAbovePrev = lineGreater(previous.yPos + HALF_KNIGHT_HEIGHT)
                val lineBelowCurr = lineLess(yPos + HALF_KNIGHT_HEIGHT)
                if (lineBelowCurr >= lineAbovePrev) {
                    outer@ for (i in lineAbovePrev..lineBelowCurr) {
                        val lineToleft = lineLess(xPos - HALF_KNIGHT_WIDTH)
                        val lineToRight = lineGreater(xPos + HALF_KNIGHT_WIDTH)
                        for (j in lineToleft..lineToRight - 1) {
                            if (gameMap.isSolid(i, j)) {
                                yPos = (i * TILE_SIZE).toFloat() - HALF_KNIGHT_HEIGHT
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

    private @Strictfp fun lineGreater(xBottomRight: Float) = StrictMath.ceil((xBottomRight / TILE_SIZE).toDouble()).toInt()

    private @Strictfp fun lineLess(xBottomLeft: Float) = StrictMath.floor((xBottomLeft / TILE_SIZE).toDouble()).toInt()

    fun GameMap.isFooting(i: Int, j: Int) : Boolean = mapTiles[i][j] > mapTiles[i + 1][j]

    fun GameMap.isSolid(i: Int, j: Int) : Boolean = mapTiles[i][j] == 3

    fun interpolateForRender(alpha: Double) {
        forRender.xPos = (previous.xPos + alpha * (current.xPos - previous.xPos).toDouble() * 1.0).toFloat()
        forRender.yPos = (previous.yPos + alpha * (current.yPos - previous.yPos).toDouble() * 1.0).toFloat()
        forRender.right = current.right
    }

    fun render(batch: SpriteBatch) {
        batch.draw(if (forRender.right) knight else knightLeft, forRender.xPos - HALF_KNIGHT_WIDTH, forRender.yPos - HALF_KNIGHT_HEIGHT)
    }

    private inner class HeroStateSnapshot {
        internal var xPos = 450f
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
            knight = TextureRegion(img, 0, 27, KNIGHT_WIDTH, KNIGHT_HEIGHT)
            knightLeft = TextureRegion(img, KNIGHT_WIDTH, 27, -KNIGHT_WIDTH, KNIGHT_HEIGHT)
        }
    }
}

private var knight: TextureRegion? = null
private var knightLeft: TextureRegion? = null
private val HERO_VELOCITY = 300f
private val JUMP_INITIAL_SPEED = 800f
private val KNIGHT_HEIGHT = 65
private val KNIGHT_WIDTH = 52
val HALF_KNIGHT_HEIGHT = KNIGHT_HEIGHT / 2
val HALF_KNIGHT_WIDTH = KNIGHT_WIDTH / 2
