package com.mgtriffid.gdxgame

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.Array as GdxArray

/**
 * Created by mgtriffid on 10.03.16.
 */
class GameState internal constructor() {
    val heroState: HeroState
    private val gameMap: GameMap
    private val bulletPool: Pool<Bullet>
    private val activeBullets: GdxArray<Bullet>

    fun currentToPrevious() {
        heroState.currentToPrevious()
        activeBullets.forEach { it.currentToPrevious() }
    }

    private var recharge = 0f

    fun integrateCurrent(t: Float, dt: Float, gameInput: GameInput) {
        heroState.integrateCurrent(t, dt, gameInput, gameMap)
        if (gameInput.mouseClicked && recharge < t) {
            val newBullet = bulletPool.obtain()
            activeBullets.add(newBullet)
            newBullet.start(heroState.x, heroState.y, gameInput.mousePositionX, gameInput.mousePositionY);
            recharge = t + 0.2f
        }
        var index = 0;
        while (index < activeBullets.size) {
            activeBullets[index].integrateCurrent(t, dt);
            if (activeBullets[index].isDead) {
                bulletPool.free(activeBullets.removeIndex(index))

            } else {
                index++
            }
        }
    }

    fun interpolateForRender(alpha: Double) {
        heroState.interpolateForRender(alpha)
        activeBullets.forEach { it.interpolateForRender(alpha) }
    }

    fun render(batch: SpriteBatch) {
        gameMap.render(batch)
        heroState.render(batch)
        activeBullets.forEach { it.render(batch) }
    }

    init {
        heroState = HeroState()
        HeroState.init()
        gameMap = GameMap()
        GameMap.init()
        bulletPool = object: Pool<Bullet>() {
            override fun newObject() : Bullet {
                return Bullet();
            }
        }
        activeBullets = GdxArray<Bullet>(false, 16)


    }
}
