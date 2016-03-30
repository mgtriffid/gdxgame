package com.mgtriffid.gdxgame

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Affine2

/**
 * Created by mgtriffid on 26.03.16.
 */

class Bullet {
    private val current = BulletSnapshot()
    private val previous = BulletSnapshot()
    private val forRender = BulletSnapshot()
    private var renderingTick = 0
    private var rotation = 0f
    var isDead = true

    fun start(x: Float, y: Float, toX: Float, toY: Float) {
        current.xPos = x;
        current.yPos = y;
        val hypotenuse = StrictMath.sqrt(((toX - x) * (toX - x) + (toY - y) * (toY - y)).toDouble()).toFloat()
        val cos = (toX - x) / hypotenuse
        current.xSpeed = cos * BULLET_SPEED
        val sin = (toY - y) / hypotenuse
        current.ySpeed = sin * BULLET_SPEED
        currentToPrevious()
        rotation = Math.toDegrees(Math.acos(cos.toDouble()) * (if (sin > 0) 1 else -1)).toFloat()
        isDead = false
    }

    private class BulletSnapshot {
        internal var xPos = 0f
        internal var yPos = 0f
        internal var xSpeed = 0f
        internal var ySpeed = 0f
    }

    fun currentToPrevious() {
        previous.xPos = current.xPos
        previous.yPos = current.yPos
        previous.xSpeed = current.xSpeed
        previous.ySpeed = current.ySpeed
    }

    @Strictfp fun integrateCurrent(t: Float, dt: Float) {
        current.xPos += dt * current.xSpeed
        current.yPos += dt * current.ySpeed

        if ((current.xPos < 20) or (current.xPos > 1300) or (current.yPos < -50) or (current.yPos > 800)) {
            isDead = true
        }
    }

    fun interpolateForRender(alpha: Double) {
        forRender.xPos = (previous.xPos + alpha * (current.xPos - previous.xPos).toDouble() * 1.0).toFloat()
        forRender.yPos = (previous.yPos + alpha * (current.yPos - previous.yPos).toDouble() * 1.0).toFloat()
    }

    private var textureToRender = 0

    fun render(batch: SpriteBatch) {
        if (renderingTick == 3) {
            renderingTick = 0
            textureToRender++
            textureToRender %= 8
        } else {
            renderingTick++
        }
        batch.draw(FIREBALL_TEXTURES.get(textureToRender), forRender.xPos - 32, forRender.yPos - 32, 32f, 32f, 64f, 64f, 1f , 1f ,rotation)
//        batch.draw(FIREBALL_TEXTURE, forRender.xPos - FIREBALL_HALF_WIDTH, forRender.yPos - FIREBALL_HALF_WIDTH)
    }

}

private val BULLET_SPEED = 500f
val FIREBALL_WIDTH = 20
val FIREBALL_HALF_WIDTH = FIREBALL_WIDTH / 2
val FIREBALL_HEIGHT = 42
val FIREBALL_HALF_HEIGHT = FIREBALL_HEIGHT / 2
private val FIREBALL_SOURCE_TEXTURE = Texture("fireball_0.png")
private val FIREBALL_TEXTURE = TextureRegion(Texture("rpg_magic_zps7b6c1492.PNG"), 168, 102, FIREBALL_WIDTH, FIREBALL_HEIGHT);
private val FIREBALL_TEXTURES = (0..7).map { TextureRegion(FIREBALL_SOURCE_TEXTURE, it * 64, 256, 64, 64) }
