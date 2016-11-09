package com.firstry.gmase.quantilife.model

import org.json.JSONArray
import org.json.JSONObject
import java.util.*

/**
 * Created by Guille2 on 02/10/2016
 * Have fun
 */
object TagDictionary {
    val tags: ArrayList<Tag>

    init {
        tags = ArrayList<Tag>()
    }

    fun loadFromJSON(items: JSONArray) {
        tags.removeAll(tags)
        var i = 0
        var obj: JSONObject
        while (i < items.length()) {
            obj = items[i] as JSONObject
            tags.add(Tag(tagId = obj.getString("id"), state = 0, YesPhrase = obj.getString("YesPhrase"), NoPhrase = obj.getString("NoPhrase")))
            i++
        }
    }

    fun changeStatus(id: String, day: Int, status: Int) {
        for (i in tags) {
            if (i.tagId == id) {
                i.day = day
                i.state = status
                break
            }
        }
    }

    fun get(id: String): Tag? {
        for (i in tags) {
            if (id == i.tagId)
                return i
        }
        return null
    }

    fun getByPhrase(input: String): Tag? {
        for (i in tags) {
            if (input == i.YesPhrase || input == i.NoPhrase) {
                var value = 1
                if (input == i.NoPhrase)
                    value = -1
                return Tag(tagId = i.tagId, YesPhrase = i.YesPhrase, NoPhrase = i.NoPhrase, day = i.day, state = value)
            }
        }
        return null
    }
}