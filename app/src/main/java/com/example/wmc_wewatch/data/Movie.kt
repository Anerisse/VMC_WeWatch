package com.example.wmc_wewatch.data
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Уникальный идентификатор
    val title: String,       // Название фильма
    val year: String,        // Год выпуска
    val posterUrl: String?,  // Ссылка на постер (может быть null)
    val genre: String? = null,
    val isSelected: Boolean  // Флаг для удаления
)