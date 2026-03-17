package com.ud.mygolfclub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.room.Room
import com.ud.mygolfclub.ui.data.db.AppDB
import com.ud.mygolfclub.ui.screen.LoginScreen
import com.ud.mygolfclub.ui.screen.VM.LoginVM
import com.ud.mygolfclub.ui.theme.MyGolfClubTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDB::class.java,
            "golf_db"
        ).build()

        enableEdgeToEdge()
        setContent {
            val viewM = LoginVM(db.ClientDAO())
            LoginScreen(viewM)
        }
    }
}
