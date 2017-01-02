package com.firstry.gmase.quantilife.controler

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.firstry.gmase.quantilife.R
import com.firstry.gmase.quantilife.model.AllQuestions
import com.firstry.gmase.quantilife.model.AppDay
import com.firstry.gmase.quantilife.model.Tag
import com.firstry.gmase.quantilife.model.TagDictionary
import org.json.JSONObject
import java.io.InputStream
import java.util.*


class MyModeldApter(val context: Context) {
    var db: SQLiteDatabase? = null
    val dbVersion = 6
    var mListener: OnQuestionAnswered? = null

    interface OnQuestionAnswered {
        fun OnQuestionAnswered()
    }

    init {
    }

    fun reload(re: Boolean = false) {
        db = context.openOrCreateDatabase("myLifeDB", Context.MODE_PRIVATE, null)
        var c = db!!.rawQuery("SELECT name FROM sqlite_master WHERE type='table' and name in ('results','dates')", null)
        if (c.count != 2 || re) {
            restartDB()
        }
        if (db!!.version != dbVersion) {
            //llamar a actualizador DB. db.version=dbVersion
        }

        c.close()

        val lang = Locale.getDefault().isO3Language


        val obj = JSONObject(loadJSONFromAsset(context.resources.openRawResource(R.raw.tags)))
        val obj2 = JSONObject(loadJSONFromAsset(context.resources.openRawResource(R.raw.questions)))
        if (re || AllQuestions.questions.isEmpty()) {
            if (lang == "spa")
                TagDictionary.loadFromJSON(obj.getJSONArray("tags_es"))
            else
                TagDictionary.loadFromJSON(obj.getJSONArray("tags_en"))
            if (lang == "spa")
                AllQuestions.loadFromJSON(obj2.getJSONArray("questions_es"))
            else
                AllQuestions.loadFromJSON(obj2.getJSONArray("questions_en"))
        }


        c = db!!.rawQuery("SELECT timestamp FROM dates WHERE key='installation';", null)
        c.moveToFirst()
        AppDay.installation = c.getLong(0)
        c.close()
        recorverUserTags()
        recoverResults()
        db!!.close()
    }

    private fun restartDB() {
        db!!.execSQL("DROP TABLE IF EXISTS results;")
        db!!.execSQL("DROP TABLE IF EXISTS myData;")
        db!!.execSQL("DROP TABLE IF EXISTS dates;")
        db!!.execSQL("DROP TABLE IF EXISTS tags;")

        db!!.execSQL("CREATE TABLE results(" +
                "id INTEGER,internalResult INTEGER,textResult TEXT,day INTEGER, PRIMARY KEY(id,day)) WITHOUT ROWID;")
        db!!.execSQL("CREATE TABLE tags(" +
                "id TEXT,day INTEGER,status INTEGER, PRIMARY KEY(id,day)) WITHOUT ROWID;")
        db!!.execSQL("CREATE TABLE dates(" +
                "key TEXT,timestamp LONG);")

        val calendar = Calendar.getInstance()
        calendar.time = Date(System.currentTimeMillis())
        calendar.set(Calendar.HOUR_OF_DAY, 4)
        calendar.set(Calendar.MINUTE, 1)
        calendar.set(Calendar.MILLISECOND, 0)

        db!!.execSQL("INSERT INTO dates VALUES" +
                "('installation'," + calendar.timeInMillis / 1000 + ");")
    }

    fun saveResult(id: Int, result: Int, text: String) {
        val values = ContentValues()
        values.put("id", id)
        values.put("internalResult", result)
        values.put("textResult", text)
        values.put("day", AppDay.today())
        db = context.openOrCreateDatabase("myLifeDB", Context.MODE_PRIVATE, null)
        db!!.insertWithOnConflict("results", null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db!!.close()

        AllQuestions.getId(id).dirty = true
        mListener = context as OnQuestionAnswered
        mListener!!.OnQuestionAnswered()

    }

    fun saveTag(tag: Tag) {
        //Special case tag "other"
        if (tag.tagId != "zzz") {
            val values = ContentValues()
            values.put("id", tag.tagId)
            values.put("day", AppDay.today())
            values.put("status", tag.state)
            db = context.openOrCreateDatabase("myLifeDB", Context.MODE_PRIVATE, null)
            db!!.insertWithOnConflict("tags", null, values, SQLiteDatabase.CONFLICT_REPLACE)
            db!!.close()
            TagDictionary.changeStatus(tag.tagId, AppDay.today(), tag.state)
        }

        // Log.d("Guardado Tag", ""+ tag.tagId+ 1)
    }

    fun loadJSONFromAsset(input: InputStream): String? {
        val buffer = ByteArray(input.available())
        input.read(buffer)
        input.close()
        return String(buffer)
    }

    fun recorverUserTags() {
        val caducidadTag = 60
        db = context.openOrCreateDatabase("myLifeDB", Context.MODE_PRIVATE, null)
        val c = db!!.rawQuery("SELECT V.id,V.day,V.status FROM tags V INNER JOIN (SELECT id,MAX(day) As most_recent FROM tags GROUP BY id) R ON V.id = R.id AND V.day = R.most_recent;", null)

        if (c.count > 0) {
            c.moveToFirst()
            while (!c.isLast) {
                //Solo recupero los tags de los ultimos 60 dias
                if (AppDay.today() - c.getInt(1) < caducidadTag) {
                    TagDictionary.changeStatus(c.getString(0), c.getInt(1), c.getInt(2))
                }
                c.moveToNext()
            }
            if (AppDay.today() - c.getInt(1) < caducidadTag) {
                TagDictionary.changeStatus(c.getString(0), c.getInt(1), c.getInt(2))
            }
        }
        c.close()
        db!!.close()
    }

    fun recoverResults() {
        val query = "SELECT V.id,V.internalResult,V.day,V.textResult FROM results V INNER JOIN (SELECT id,MAX(day) As most_recent FROM results GROUP BY id) R ON V.id = R.id AND V.day = R.most_recent;"
        updateResults(query)
        AllQuestions.reviewVisibility()
        AllQuestions.computeTotals()
    }


    fun recoverResultsToday() {
        val query = "SELECT V.id,V.internalResult,V.day,V.textResult FROM results V INNER JOIN (SELECT id,MAX(day) As most_recent FROM results GROUP BY id) R ON V.id = R.id AND V.day = R.most_recent;"
        updateResults(query)
        AllQuestions.reviewVisibilityToday()
        AllQuestions.computeTotals()
    }


    fun historicResult(day: Int) {
        val max = day
        AllQuestions.resetAll()
        val query = "SELECT V.id,V.internalResult,V.day,V.textResult FROM results V INNER JOIN (SELECT id,MAX(day) As most_recent FROM results WHERE  day<=$max GROUP BY id) R ON V.id = R.id AND V.day = R.most_recent;"
        updateResults(query)
        AllQuestions.computeTotals()
    }

    fun updateResults(query: String) {
        db = context.openOrCreateDatabase("myLifeDB", Context.MODE_PRIVATE, null)
        val c = db!!.rawQuery(query, null)
        if (c.count > 0) {
            c.moveToFirst()
            while (!c.isLast) {
                AllQuestions.setResult(c.getInt(0), c.getInt(1), c.getString(3), c.getInt(2))
                c.moveToNext()
            }
            AllQuestions.setResult(c.getInt(0), c.getInt(1), c.getString(3), c.getInt(2))
        }
        c.close()
        db!!.close()
    }
}