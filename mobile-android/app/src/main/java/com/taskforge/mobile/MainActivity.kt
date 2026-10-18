package com.taskforge.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.taskforge.mobile.ui.AuthScreen
import com.taskforge.mobile.ui.TaskForgeViewModel
import com.taskforge.mobile.ui.TaskListScreen

class MainActivity : ComponentActivity() {

    private val viewModel: TaskForgeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    if (viewModel.loggedIn) {
                        TaskListScreen(viewModel)
                    } else {
                        AuthScreen(viewModel)
                    }
                }
            }
        }
    }
}
