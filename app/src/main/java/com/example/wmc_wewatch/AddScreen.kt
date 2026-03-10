package com.example.wmc_wewatch

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    onNavigateBack: () -> Unit,      // для кнопки "назад"
    onSearchClick: (String, String) -> Unit,       // когда нажимают на поиск
    onAddMovieClick: () -> Unit      // когда добавляют фильм
) {
    // Состояние для полей ввода
    var searchQuery by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Добавить фильм") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Поле для поиска (обязательное)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Название фильма") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if(searchQuery.isNotBlank()){
                                onSearchClick(searchQuery,year)
                            }
                        }) {
                        Icon(Icons.Default.Search, contentDescription = "Поиск")
                    }
                }
            )

            // Поле для года (необязательное)
            OutlinedTextField(
                value = year,
                onValueChange = { year = it },
                label = { Text("Год (необязательно)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Кнопка добавления
            Button(
                onClick = onAddMovieClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = searchQuery.isNotBlank() // активна только если есть название
            ) {
                Text("Добавить фильм")
            }
        }
    }
}