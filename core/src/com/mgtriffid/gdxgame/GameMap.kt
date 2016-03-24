package com.mgtriffid.gdxgame

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion

/**
 * Created by mgtriffid on 10.03.16.
 */
class GameMap {
    internal var mapTiles: Array<IntArray>

    fun render(batch: SpriteBatch) {
        var tile: Int
        for (i in 0..10) {
            for (j in 0..18) {
                tile = mapTiles[i][j]
                if (tile > 0) {
                    batch.draw(textures!![tile], (j * 70).toFloat(), (i * 70).toFloat())
                }
            }
        }
    }

    init {
        mapTiles = arrayOf(
                intArrayOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3),
                intArrayOf(3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3),
                intArrayOf(3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3),
                intArrayOf(3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3),
                intArrayOf(3, 0, 0, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 3),
                intArrayOf(3, 0, 0, 0, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 3),
                intArrayOf(3, 0, 0, 0, 0, 3, 3, 0, 0, 0, 0, 0, 2, 2, 2, 2, 0, 0, 3),
                intArrayOf(3, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 3),
                intArrayOf(3, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 3),
                intArrayOf(3, 0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3),
                intArrayOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3)
        )
        mapTiles.reverse()
    }

    companion object {

        private var textures: Array<TextureRegion?>? = null

        fun init() {
            val grassMid = TextureRegion(Texture("grassMid.png"), 0, 0, 70, 70)
            val grassCenter = TextureRegion(Texture("grassCenter.png"), 0, 0, 70, 70)
            val bricks = TextureRegion(Texture("brickWall.png"), 0, 0, 70, 70)
            textures = arrayOf(null, grassCenter, grassMid, bricks)

        }
    }
}
