package com.mgtriffid.gdxgame

import com.badlogic.gdx.graphics.g2d.SpriteBatch

/**
 * Created by mgtriffid on 10.03.16.
 */
class GameState internal constructor() {
    private val heroState: HeroState
    private val gameMap: GameMap

    fun currentToPrevious() {
        heroState.currentToPrevious()
    }

    fun integrateCurrent(t: Float, dt: Float, gameInput: GameInput) {
        heroState.integrateCurrent(t, dt, gameInput, gameMap)
    }

    fun interpolateForRender(alpha: Double) {
        heroState.interpolateForRender(alpha)
    }

    fun render(batch: SpriteBatch) {
        gameMap.render(batch)
        heroState.render(batch)
    }

    init {
        heroState = HeroState()
        HeroState.init()
        gameMap = GameMap()
        GameMap.init()
    }
}
