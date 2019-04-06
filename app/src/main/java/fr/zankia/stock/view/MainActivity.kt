package fr.zankia.stock.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import fr.zankia.stock.R
import fr.zankia.stock.dao.StockJSON
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Locale

class MainActivity : Activity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            signInUser()
        } else {
            StockJSON.selectNode(currentUser)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mailSend -> {
                sendMail()
                true
            }
            R.id.disconnect -> {
                signOutUser()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun signInUser() {
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(
                    arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
                )
                .build(),
            RC_SIGN_IN
        )
    }

    private fun signOutUser() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                recreate()
            }
    }

    fun setListActivity(view: View) {
        startActivity(Intent(this, ManageActivity::class.java))
    }

    fun setDisplayActivity(view: View) {
        startActivity(Intent(this, GridActivity::class.java))
    }

    private fun sendMail() {
        val csvString = StringBuilder(getString(R.string.csvHeader))
        var i = 2
        for (categoryName in StockJSON.categoryNames) {
            for ((name, quantity, price) in StockJSON.getCategory(categoryName).products) {
                csvString.append("\n\"")
                    .append(name)
                    .append("\";\"")
                    .append(quantity)
                    .append("\";\"")
                    .append(String.format(Locale.getDefault(), "%.2f", price))
                    .append("\";\"=B").append(i).append("*C").append(i).append("\"")
                ++i
            }
        }
        csvString.append("\n\"\";\"\";\"Total\";\"=")
            .append(getString(R.string.sumFunction))
            .append("(D2:D").append(i - 1).append(")\"")

        val file = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), getString(R.string.csvFile))
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        try {
            val fs = FileOutputStream(file)
            fs.write(csvString.toString().toByteArray())
            fs.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.sendSubject))
        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
        sendIntent.type = "plain/text"
        startActivity(sendIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != RC_SIGN_IN || resultCode != Activity.RESULT_OK || auth.currentUser == null) {
            return
        }

        StockJSON.selectNode(auth.currentUser!!)
    }

    companion object {
        private const val RC_SIGN_IN = 11
    }
}
