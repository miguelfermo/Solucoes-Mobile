package com.example.calculadora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculadoraApp()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalculadoraApp() {
    var display by remember { mutableStateOf("0") }
    var operador by remember { mutableStateOf<String?>(null) }
    var num1 by remember { mutableStateOf("") }
    var num2 by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf("") }

    val buttons = listOf(
        listOf("C", "+/-", "%", "÷"),
        listOf("7", "8", "9", "×"),
        listOf("4", "5", "6", "-"),
        listOf("1", "2", "3", "+"),
        listOf("0", ".", "=")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = resultado.ifEmpty { display },
            fontSize = 64.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.End
        )

        for (row in buttons) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (button in row) {
                    CalculadoraButton(
                        text = button,
                        modifier = Modifier
                            .weight(if (button == "0") 2f else 1f),
                        onClick = {
                            val newState = onButtonClick(
                                button, display, operador, num1, num2,
                                { operador = it }, { num1 = it }, { num2 = it }
                            )
                            display = newState.display
                            resultado = newState.resultado
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun CalculadoraButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val backgroundColor = when (text) {
        "C", "+/-", "%" -> Color.Gray
        "÷", "×", "-", "+", "=" -> Color(255, 149, 0)
        else -> Color.DarkGray
    }
    val textColor = if (text in listOf("÷", "×", "-", "+", "=")) Color.White else Color.Black

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .aspectRatio(1f)
            .background(backgroundColor, shape = CircleShape)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Text(text = text, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = textColor)
    }
}

data class CalculatorState(val display: String, val resultado: String)

fun onButtonClick(
    button: String,
    currentDisplay: String,
    operador: String?,
    num1: String,
    num2: String,
    setOperador: (String?) -> Unit,
    setNum1: (String) -> Unit,
    setNum2: (String) -> Unit
): CalculatorState {
    var display = currentDisplay
    var resultado = ""

    when (button) {
        "C" -> {
            setOperador(null)
            setNum1("")
            setNum2("")
            return CalculatorState("0", "")
        }
        "+", "-", "×", "÷" -> {
            setOperador(button)
            setNum1(display)
            return CalculatorState("0", "")
        }
        "=" -> {
            val n1 = num1.takeIf { it.isNotEmpty() }?.toDoubleOrNull() ?: 0.0
            val n2 = display.takeIf { it.isNotEmpty() }?.toDoubleOrNull() ?: 0.0

            resultado = when (operador) {
                "+" -> (n1 + n2).toString()
                "-" -> (n1 - n2).toString()
                "×" -> (n1 * n2).toString()
                "÷" -> if (n2 != 0.0) (n1 / n2).toString() else "Erro"
                else -> "Erro"
            }

            setOperador(null)
            setNum1("")
            setNum2("")
            return CalculatorState("0", resultado)
        }
        else -> {
            display = if (currentDisplay == "0") button else currentDisplay + button
        }
    }

    return CalculatorState(display, "")
}
