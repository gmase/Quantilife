package com.firstry.gmase.quantilife.activities

//import com.facebook.stetho.Stetho
import android.app.ActivityOptions
import android.app.TaskStackBuilder
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.facebook.FacebookSdk
import com.firstry.gmase.quantilife.R
import com.firstry.gmase.quantilife.controler.MyModeldApter
import com.firstry.gmase.quantilife.model.AllQuestions
import com.github.amlcurran.showcaseview.ShowcaseView
import com.github.amlcurran.showcaseview.targets.ViewTarget

//AppCompatActivity

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, LoginDialog.IdentityDialogListener {
    var modelAdapeter: MyModeldApter? = null
    var toolbar: Toolbar? = null

    override fun onResume() {
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Stetho.initializeWithDefaults(this)
        FacebookSdk.sdkInitialize(this)
        setContentView(R.layout.activity_main)


        toolbar = findViewById(R.id.toolbar) as Toolbar

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.setHomeButtonEnabled(false)
        supportActionBar!!.setDisplayShowHomeEnabled(false)
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)


        val layoutParams = android.support.v7.app.ActionBar.LayoutParams(android.support.v7.app.ActionBar.LayoutParams.WRAP_CONTENT, android.support.v7.app.ActionBar.LayoutParams.MATCH_PARENT)
        layoutParams.gravity = Gravity.LEFT
        supportActionBar!!.setCustomView(layoutInflater.inflate(R.layout.top_bar_main, null), layoutParams)

        val button2 = findViewById(R.id.go_questions) as ImageButton
        button2.setOnClickListener { goToQuestions() }
        val button3 = findViewById(R.id.go_stats) as ImageButton
        button3.setOnClickListener { goToStats() }

        val adviceL = findViewById(R.id.iVRelationshipsProgress) as ImageView
        adviceL.setOnClickListener { showAdvice("love") }
        val adviceW = findViewById(R.id.iVWorkProgress) as ImageView
        adviceW.setOnClickListener { showAdvice("work") }
        val adviceH = findViewById(R.id.iVHealthProgress) as ImageView
        adviceH.setOnClickListener { showAdvice("health") }
        val adviceE = findViewById(R.id.iVEthicsProgress) as ImageView
        adviceE.setOnClickListener { showAdvice("ethics") }


        modelAdapeter = MyModeldApter(this)

        //en preferences tenemos
        //bdVersion
        //firstMain
        //firstStats
        //identity
        val settings = getSharedPreferences(resources.getString(R.string.pref), 0)
        if (settings.getInt("bdVersion", 0) != modelAdapeter!!.dbVersion) {
            modelAdapeter!!.reload(true)
            val editor = settings.edit()
            editor.putInt("bdVersion", modelAdapeter!!.dbVersion)
            editor.apply()
        } else
            modelAdapeter!!.reload()

        updateLines()
        updateCounters()
        AllQuestions.updateFormers()

        //Reviso si es la primera vez en main
        if (settings.getBoolean("firstMain", true))
            firstTime(settings, "firstMain")
        else if (checkLogged() == 0)
            goToLogin()
    }

    fun firstTime(settings: SharedPreferences, key: String) {
        val button = Button(this)
        button.text = ""
        button.isEnabled = false
        button.visibility = View.GONE

        // ShowcaseView.Builder(this).setTarget(ViewTarget(findViewById(R.id.go_questions))).setContentTitle(R.string.showMainTitle).setContentText(R.string.showMainText).hideOnTouchOutside().replaceEndButton(button).setStyle(R.style.CustomShowcaseTheme3).build()
        ShowcaseView.Builder(this).setTarget(ViewTarget(findViewById(R.id.go_questions))).setContentTitle(R.string.showMainTitle).setContentText(R.string.showMainText).replaceEndButton(button).setStyle(R.style.CustomShowcaseTheme3).build()
        //Pongo el valor a false para que no vuelva a ocurrir
        val editor = settings.edit()
        editor.putBoolean(key, false)
        editor.apply()
    }

    fun debugRestartFirstTime() {
        val settings = getSharedPreferences(resources.getString(R.string.pref), 0)
        val editor = settings.edit()
        editor.putBoolean("firstMain", true)
        editor.putBoolean("firstStats", true)
        editor.putString("identity", "")
        editor.apply()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.help) {
            // modelAdapeter!!.recoverResultsToday()
            //throw UnsupportedOperationException("Fallo a proposito")
            //Toast.makeText(applicationContext, R.string.reviewToast, Toast.LENGTH_LONG).show()
            return true
        }
        if (id == R.id.about) {
            // modelAdapeter!!.recoverResults()
            return true
        }
        if (id == R.id.debugFirstTime) {
            debugRestartFirstTime()
            return true
        }
        if (id == R.id.goToIdentify) {
            if (checkLogged() == 1)
                Toast.makeText(applicationContext, R.string.alreadyLoggedIn, Toast.LENGTH_SHORT).show()
            else goToLogin()
            return true
        }
//        if (id == R.id.id1) {
//            openEvolActivity()
//            return true
//        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateNavigateUpTaskStack(builder: TaskStackBuilder?) {
        Toast.makeText(applicationContext, R.string.reviewToast, Toast.LENGTH_LONG).show()
    }

    @SuppressWarnings("StatementWithEmptyBody")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.nav_relationships) {

        } else if (id == R.id.nav_health) {

        } else if (id == R.id.nav_work) {

        } else if (id == R.id.progress) {
        }

        return true
    }


    fun updateCounters() {
        val total = findViewById(R.id.mainTotal) as TextView
        val diferenciaValue = (AllQuestions.total - AllQuestions.formerTotal).toInt()
        var diferencia = ""
        if (diferenciaValue >= 0) {
            diferencia = "+"
        }
        diferencia += diferenciaValue.toString()
        if (AllQuestions.formerTotal.toInt() != 0 && diferenciaValue != 0) {
            total.text = AllQuestions.total.toInt().toString() + "(" + diferencia + ")" + " " + getString(R.string.total)
        } else total.text = AllQuestions.total.toInt().toString() + " " + getString(R.string.total)


        val relationships = findViewById(R.id.scoreRelationships) as TextView
        relationships.text = (AllQuestions.totalRelationships / 10f).toString() + "% "


        val health = findViewById(R.id.scoreHealth) as TextView
        health.text = (AllQuestions.totalHealth / 10f).toString() + "% "

        val work = findViewById(R.id.scoreWork) as TextView
        work.text = (AllQuestions.totalWork / 10f).toString() + "% "

        val ethics = findViewById(R.id.scoreEthics) as TextView
        ethics.text = (AllQuestions.totalEthics / 10f).toString() + "% "
    }

    fun updateLines() {

        var line: ImageView


        //Red
        var iter = (AllQuestions.totalRelationships / 100.0)
        var former = (AllQuestions.formerRelationships / 100.0)
        //Para que las primeras lineas aparezcan antes
        if (AllQuestions.totalRelationships >= 1) {
            line = findViewById(R.id.line_red_1) as ImageView
            line.visibility = View.VISIBLE
        }
        if (iter >= 2) {
            line = findViewById(R.id.line_red_2) as ImageView
            line.visibility = View.VISIBLE
        }
        if (iter >= 3) {
            line = findViewById(R.id.line_red_3) as ImageView
            line.visibility = View.VISIBLE
        }
        if (iter >= 4) {
            line = findViewById(R.id.line_red_4) as ImageView
            line.visibility = View.VISIBLE
        }
        if (iter >= 5) {
            line = findViewById(R.id.line_red_5) as ImageView
            line.visibility = View.VISIBLE
        }
        if (iter >= 6) {
            line = findViewById(R.id.line_red_6) as ImageView
            line.visibility = View.VISIBLE
        }
        if (iter >= 7) {
            line = findViewById(R.id.line_red_7) as ImageView
            line.visibility = View.VISIBLE
        }
        if (iter >= 8) {
            line = findViewById(R.id.line_red_8) as ImageView
            line.visibility = View.VISIBLE
        }
        if (iter >= 9) {
            line = findViewById(R.id.line_red_9) as ImageView
            line.visibility = View.VISIBLE
        }
        if (iter >= 9.5) {
            line = findViewById(R.id.line_red_10) as ImageView
            line.visibility = View.VISIBLE
        }
        if (iter < former) {
            if (AllQuestions.totalRelationships < 1) {
                line = findViewById(R.id.line_red_1) as ImageView
                line.visibility = View.INVISIBLE
            }
            if (iter < 2) {
                line = findViewById(R.id.line_red_2) as ImageView
                line.visibility = View.INVISIBLE
            }
            if (iter < 3) {
                line = findViewById(R.id.line_red_3) as ImageView
                line.visibility = View.INVISIBLE
            }
            if (iter < 4) {
                line = findViewById(R.id.line_red_4) as ImageView
                line.visibility = View.INVISIBLE
            }
            if (iter < 5) {
                line = findViewById(R.id.line_red_5) as ImageView
                line.visibility = View.INVISIBLE
            }
            if (iter < 6) {
                line = findViewById(R.id.line_red_6) as ImageView
                line.visibility = View.INVISIBLE
            }
            if (iter < 7) {
                line = findViewById(R.id.line_red_7) as ImageView
                line.visibility = View.INVISIBLE
            }
            if (iter < 8) {
                line = findViewById(R.id.line_red_8) as ImageView
                line.visibility = View.INVISIBLE
            }
            if (iter < 9) {
                line = findViewById(R.id.line_red_9) as ImageView
                line.visibility = View.INVISIBLE
            }
            if (iter < 9.5) {
                line = findViewById(R.id.line_red_10) as ImageView
                line.visibility = View.INVISIBLE
            }
        }
        //Yellow
        iter = (AllQuestions.totalWork / 100.0)
        former = (AllQuestions.formerWork / 100.0)
        if (AllQuestions.totalWork >= 1) {
            line = findViewById(R.id.line_yellow_1) as ImageView
            line.visibility = View.VISIBLE
        }
        if (iter >= 2) {
            line = findViewById(R.id.line_yellow_2) as ImageView
            line.visibility = View.VISIBLE
        }
        if (iter >= 3) {
            line = findViewById(R.id.line_yellow_3) as ImageView
            line.visibility = View.VISIBLE
        }
        if (iter >= 4) {
            line = findViewById(R.id.line_yellow_4) as ImageView
            line.visibility = View.VISIBLE
        }
        if (iter >= 5) {
            line = findViewById(R.id.line_yellow_5) as ImageView
            line.visibility = View.VISIBLE
        }
        if (iter >= 6) {
            line = findViewById(R.id.line_yellow_6) as ImageView
            line.visibility = View.VISIBLE
        }
        if (iter >= 7) {
            line = findViewById(R.id.line_yellow_7) as ImageView
            line.visibility = View.VISIBLE
        }
        if (iter >= 8) {
            line = findViewById(R.id.line_yellow_8) as ImageView
            line.visibility = View.VISIBLE
        }
        if (iter >= 9) {
            line = findViewById(R.id.line_yellow_9) as ImageView
            line.visibility = View.VISIBLE
        }
        if (iter >= 9.5) {
            line = findViewById(R.id.line_yellow_10) as ImageView
            line.visibility = View.VISIBLE
        }
        if (iter < former) {
            if (AllQuestions.totalWork < 1) {
                line = findViewById(R.id.line_yellow_1) as ImageView
                line.visibility = View.INVISIBLE
            }
            if (iter < 2) {
                line = findViewById(R.id.line_yellow_2) as ImageView
                line.visibility = View.INVISIBLE
            }
            if (iter < 3) {
                line = findViewById(R.id.line_yellow_3) as ImageView
                line.visibility = View.INVISIBLE
            }
            if (iter < 4) {
                line = findViewById(R.id.line_yellow_4) as ImageView
                line.visibility = View.INVISIBLE
            }
            if (iter < 5) {
                line = findViewById(R.id.line_yellow_5) as ImageView
                line.visibility = View.INVISIBLE
            }
            if (iter < 6) {
                line = findViewById(R.id.line_yellow_6) as ImageView
                line.visibility = View.INVISIBLE
            }
            if (iter < 7) {
                line = findViewById(R.id.line_yellow_7) as ImageView
                line.visibility = View.INVISIBLE
            }
            if (iter < 8) {
                line = findViewById(R.id.line_yellow_8) as ImageView
                line.visibility = View.INVISIBLE
            }
            if (iter < 9) {
                line = findViewById(R.id.line_yellow_9) as ImageView
                line.visibility = View.INVISIBLE
            }
            if (iter < 9.5) {
                line = findViewById(R.id.line_yellow_10) as ImageView
                line.visibility = View.INVISIBLE
            }
        }
        //Blue
        iter = (AllQuestions.totalHealth / 100.0)
        former = (AllQuestions.formerHealth / 100.0)
        if (AllQuestions.totalHealth >= 1) {
            line = findViewById(R.id.line_blue_1) as ImageView
            line.visibility = View.VISIBLE
        }
        if (iter >= 2) {
            line = findViewById(R.id.line_blue_2) as ImageView
            line.visibility = View.VISIBLE
        }
        if (iter >= 3) {
            line = findViewById(R.id.line_blue_3) as ImageView
            line.visibility = View.VISIBLE
        }
        if (iter >= 4) {
            line = findViewById(R.id.line_blue_4) as ImageView
            line.visibility = View.VISIBLE
        }
        if (iter >= 5) {
            line = findViewById(R.id.line_blue_5) as ImageView
            line.visibility = View.VISIBLE
        }
        if (iter >= 6) {
            line = findViewById(R.id.line_blue_6) as ImageView
            line.visibility = View.VISIBLE
        }
        if (iter >= 7) {
            line = findViewById(R.id.line_blue_7) as ImageView
            line.visibility = View.VISIBLE
        }
        if (iter >= 8) {
            line = findViewById(R.id.line_blue_8) as ImageView
            line.visibility = View.VISIBLE
        }
        if (iter >= 9) {
            line = findViewById(R.id.line_blue_9) as ImageView
            line.visibility = View.VISIBLE
        }
        if (iter >= 9.5) {
            line = findViewById(R.id.line_blue_10) as ImageView
            line.visibility = View.VISIBLE
        }
        if (iter < former) {
            if (AllQuestions.totalHealth < 1) {
                line = findViewById(R.id.line_blue_1) as ImageView
                line.visibility = View.INVISIBLE
            }
            if (iter < 2) {
                line = findViewById(R.id.line_blue_2) as ImageView
                line.visibility = View.INVISIBLE
            }
            if (iter < 3) {
                line = findViewById(R.id.line_blue_3) as ImageView
                line.visibility = View.INVISIBLE
            }
            if (iter < 4) {
                line = findViewById(R.id.line_blue_4) as ImageView
                line.visibility = View.INVISIBLE
            }
            if (iter < 5) {
                line = findViewById(R.id.line_blue_5) as ImageView
                line.visibility = View.INVISIBLE
            }
            if (iter < 6) {
                line = findViewById(R.id.line_blue_6) as ImageView
                line.visibility = View.INVISIBLE
            }
            if (iter < 7) {
                line = findViewById(R.id.line_blue_7) as ImageView
                line.visibility = View.INVISIBLE
            }
            if (iter < 8) {
                line = findViewById(R.id.line_blue_8) as ImageView
                line.visibility = View.INVISIBLE
            }
            if (iter < 9) {
                line = findViewById(R.id.line_blue_9) as ImageView
                line.visibility = View.INVISIBLE
            }
            if (iter < 9.5) {
                line = findViewById(R.id.line_blue_10) as ImageView
                line.visibility = View.INVISIBLE
            }
        }
        //Ethics
        iter = (AllQuestions.totalEthics / 100.0)
        former = (AllQuestions.formerEthics / 100.0)
        if (AllQuestions.totalEthics >= 1) {
            findViewById(R.id.yin_1).visibility = View.VISIBLE
            findViewById(R.id.yin_grey1).visibility = View.INVISIBLE
        }
        if (iter >= 3) {
            findViewById(R.id.yin_2).visibility = View.VISIBLE
            findViewById(R.id.yin_grey2).visibility = View.INVISIBLE
        }
        if (iter >= 5) {
            findViewById(R.id.yin_3).visibility = View.VISIBLE
            findViewById(R.id.yin_grey3).visibility = View.INVISIBLE
        }
        if (iter >= 7) {
            findViewById(R.id.yin_4).visibility = View.VISIBLE
            findViewById(R.id.yin_grey4).visibility = View.INVISIBLE
        }
        if (iter >= 9) {
            findViewById(R.id.yin_5).visibility = View.VISIBLE
            findViewById(R.id.yin_grey5).visibility = View.INVISIBLE
        }
        if (iter < former) {
            if (AllQuestions.totalEthics < 1) {
                findViewById(R.id.yin_1).visibility = View.INVISIBLE
                findViewById(R.id.yin_grey1).visibility = View.VISIBLE
            }
            if (iter >= 3) {
                findViewById(R.id.yin_2).visibility = View.INVISIBLE
                findViewById(R.id.yin_grey2).visibility = View.VISIBLE
            }
            if (iter >= 5) {
                findViewById(R.id.yin_3).visibility = View.INVISIBLE
                findViewById(R.id.yin_grey3).visibility = View.VISIBLE
            }
            if (iter >= 7) {
                findViewById(R.id.yin_4).visibility = View.INVISIBLE
                findViewById(R.id.yin_grey4).visibility = View.VISIBLE
            }
            if (iter >= 9) {
                findViewById(R.id.yin_5).visibility = View.INVISIBLE
                findViewById(R.id.yin_grey5).visibility = View.VISIBLE
            }
        }
    }

    //1 Logged in
    //-1 Cancelled
    //0 Not logged in
    private fun checkLogged(): Int {
        val settings = getSharedPreferences(resources.getString(R.string.pref), 0)
        val output: Int
        when (settings.getString("identity", "")) {
            "" -> output = 0
            "cancelled" -> output = -1
            else -> output = 1
        }
        return output
    }

    private fun goToLogin() {
        val yourDialog = LoginDialog()
        yourDialog.show(supportFragmentManager, "some_optional_tag")
    }

    override fun onFinishEditDialog(inputText: String) {
        val settings = getSharedPreferences(resources.getString(R.string.pref), 0)
        val editor = settings.edit()
        editor.putString("identity", inputText)
        editor.apply()
    }


    private fun goToStats() {
        val intent = Intent(this, StatsActivity::class.java)
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }

    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }

    private fun goToQuestions() {
        val intent = Intent(this, QuestionsActivity::class.java)
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }
    
    //TODO
    private fun showAdvice(tipo:String) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }
}
