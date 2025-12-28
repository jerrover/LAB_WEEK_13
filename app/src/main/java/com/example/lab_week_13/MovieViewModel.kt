package com.example.lab_week_13

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab_week_13.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Calendar

class MovieViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    // define the StateFlow in replace of the LiveData
    private val _popularMovies = MutableStateFlow(emptyList<Movie>())
    val popularMovies: StateFlow<List<Movie>> = _popularMovies

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    init {
        fetchPopularMovies()
    }

    // fetch movies from the API
    private fun fetchPopularMovies() {
        // Launch a coroutine in viewModelScope
        // Dispatchers.IO means that this coroutine will run on a shared pool of threads
        viewModelScope.launch(Dispatchers.IO) {
            movieRepository.fetchMovies()
                .map { movies ->
                    // Melakukan filter dan sorting data menggunakan operator map sebelum data di-emit ke StateFlow.
                    val currentYear = Calendar.getInstance().get(Calendar.YEAR).toString()
                    movies.filter { movie ->
                        movie.releaseDate?.startsWith(currentYear) == true
                    }.sortedByDescending { it.popularity }
                }
                .catch {
                    // catch is a terminal operator that catches exceptions from the Flow
                    _error.value = "An exception occurred: ${it.message}"
                }
                .collect {
                    // collect is a terminal operator that collects the values from the Flow
                    // the results are emitted to the StateFlow
                    _popularMovies.value = it
                }
        }
    }
}