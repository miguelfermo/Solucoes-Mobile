package com.example.cacaaotesouro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cacaaotesouro.ui.theme.CacaAoTesouroTheme
import java.time.LocalTime
import kotlin.time.Duration.Companion.seconds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CacaAoTesouroTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TreasureHuntApp()
                }
            }
        }
    }
}

@Composable
fun TreasureHuntApp() {
    val navController = rememberNavController()
    var startTime by remember { mutableStateOf(LocalTime.now()) }

    NavHost(
        navController = navController,
        startDestination = "/home",
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                animationSpec = tween(300)
            )
        }
    ) {
        composable("/home") {
            HomeScreen(
                onStartClick = {
                    startTime = LocalTime.now()
                    navController.navigate("/tela1")
                }
            )
        }

        composable("/tela1") {
            ClueScreen(
                clue = "Qual é o animal que late?",
                correctAnswer = "cachorro",
                onNextClick = { navController.navigate("/tela2") },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("/tela2") {
            ClueScreen(
                clue = "Qual fruta é amarela e curva?",
                correctAnswer = "banana",
                onNextClick = { navController.navigate("/tela3") },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("/tela3") {
            ClueScreen(
                clue = "Qual é a capital do Brasil?",
                correctAnswer = "brasilia",
                onNextClick = {
                    val endTime = LocalTime.now()
                    val duration = java.time.Duration.between(startTime, endTime)
                    navController.currentBackStackEntry?.savedStateHandle?.set("totalTime", duration)
                    navController.navigate("/treasure")
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("/treasure") {
            val totalTime = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<java.time.Duration>("totalTime")

            TreasureScreen(
                totalTime = totalTime,
                onResetClick = {
                    navController.popBackStack("/home", false)
                }
            )
        }
    }
}

@Composable
fun HomeScreen(onStartClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Caça ao Tesouro",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onStartClick) {
            Text("Iniciar Caça ao Tesouro")
        }
    }
}

@Composable
fun ClueScreen(
    clue: String,
    correctAnswer: String,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var answer by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Pista:",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = clue,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = answer,
            onValueChange = { answer = it },
            label = { Text("Sua resposta") }
        )
        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (answer.lowercase().trim() == correctAnswer) {
                    errorMessage = ""
                    onNextClick()
                } else {
                    errorMessage = "Resposta incorreta. Tente novamente!"
                }
            }
        ) {
            Text("Próxima Pista")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onBackClick) {
            Text("Voltar")
        }
    }
}

@Composable
fun TreasureScreen(
    totalTime: java.time.Duration?,
    onResetClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Parabéns! Você encontrou o tesouro!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (totalTime != null) {
            Text(
                text = "Tempo total: ${totalTime.seconds} segundos",
                fontSize = 18.sp
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onResetClick) {
            Text("Iniciar Novamente")
        }
    }
}