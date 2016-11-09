package com.firstry.gmase.quantilife.activities

import android.app.ActivityOptions
import android.app.TaskStackBuilder
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Toast
import com.firstry.gmase.quantilife.Http.HttpRequestTask
import com.firstry.gmase.quantilife.R
import com.firstry.gmase.quantilife.controler.MyModeldApter
import com.firstry.gmase.quantilife.model.AllQuestions
import com.firstry.gmase.quantilife.model.Tag
import com.firstry.gmase.quantilife.model.TagDictionary
import com.firstry.gmase.quantilife.view.CardsAdapter
import com.firstry.gmase.quantilife.view.ObjLayoutManager
import jp.wasabeef.recyclerview.animators.FadeInRightAnimator

class QuestionsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, QuestionDialog.OnTagSelectedListener, MyModeldApter.OnQuestionAnswered {
    var dirty = false
    var mRecyclerView: RecyclerView? = null
    var modelAdapeter: MyModeldApter? = null
    var toolbar: Toolbar? = null
    var firstTimeHere = false

    override fun onStop() {
        if (dirty) {
            val settings = getSharedPreferences(resources.getString(R.string.pref), 0)
            val identity = settings.getString("identity", "")
            if (identity != "" && identity != "cancelled") {
                Toast.makeText(this, "Results uploaded", Toast.LENGTH_SHORT).show()
                for (i in AllQuestions.dirties())
                    HttpRequestTask(context = this, q = i, userId = identity).execute()
            }
        }
        dirty = false
        super.onStop()
    }

    override fun OnQuestionAnswered() {
        dirty = true
    }
    override fun OnTagSelectedListener(position: Int, inputTag: String) {
        val mRV = findViewById(R.id.my_recycler_view) as RecyclerView
        if (inputTag == resources.getString(R.string.other)) {
            (mRV.adapter as CardsAdapter).deleteByTag(position, Tag(tagId = "zzz"))
        } else (mRV.adapter as CardsAdapter).deleteByTag(position, TagDictionary.getByPhrase(inputTag)!!)
    }

    override fun onResume() {
        super.onResume()
        val mAdapter = CardsAdapter(modelAdapeter, supportFragmentManager)
        mRecyclerView?.adapter = mAdapter
        initRecycler()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Reviso si es la primera vez en main
        val settings = getSharedPreferences(resources.getString(R.string.pref), 0)
        firstTimeHere = settings.getBoolean("firstQuestions", true)
        //TODO hacer algo especial si firstTimeHere


        setContentView(R.layout.activity_question)
        toolbar = findViewById(R.id.toolbarQ) as Toolbar

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.setHomeButtonEnabled(false)
        supportActionBar!!.setDisplayShowHomeEnabled(false)
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)


        val layoutParams = android.support.v7.app.ActionBar.LayoutParams(android.support.v7.app.ActionBar.LayoutParams.WRAP_CONTENT, android.support.v7.app.ActionBar.LayoutParams.MATCH_PARENT)
        layoutParams.gravity = Gravity.LEFT
        supportActionBar!!.setCustomView(layoutInflater.inflate(R.layout.top_bar_questions, null), layoutParams)


        val button1 = findViewById(R.id.go_main) as ImageButton
        button1.setOnClickListener { goToMain() }
        val button3 = findViewById(R.id.go_stats) as ImageButton
        button3.setOnClickListener { goToStats() }


        modelAdapeter = MyModeldApter(this)
        modelAdapeter!!.reload()

        initRecycler()
    }

    fun initRecycler() {
        val mRecyclerView = findViewById(R.id.my_recycler_view) as RecyclerView
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(false)
        // use a linear layout manager
        val mLayoutManager = ObjLayoutManager(this)
        mRecyclerView.layoutManager = mLayoutManager

        val mAdapter = CardsAdapter(modelAdapeter, supportFragmentManager)
        mRecyclerView.adapter = mAdapter

        mRecyclerView.itemAnimator = FadeInRightAnimator()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_questions, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.makeVisible) {
            modelAdapeter!!.recoverResultsToday()
            Toast.makeText(applicationContext, R.string.reviewToast, Toast.LENGTH_LONG).show()
            initRecycler()
            return true
        }
        if (id == R.id.hideAnswered) {
            modelAdapeter!!.recoverResults()
            //Toast.makeText(applicationContext, R.string.reviewToast, Toast.LENGTH_LONG).show()
            initRecycler()
            return true
        }
        if (id == R.id.reloadDB) {
            modelAdapeter!!.reload(true)
            initRecycler()
            return true
        }
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
//            val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
//            drawer.closeDrawer(GravityCompat.START)
            //           goToStats()
        }

        return true
    }

    private fun goToStats() {
        val intent = Intent(this, StatsActivity::class.java)
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())

    }

    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())

    }
}
