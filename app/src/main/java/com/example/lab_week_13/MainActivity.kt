package com.example.lab_week_13

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.lab_week_13.databinding.ActivityMainBinding
import com.example.lab_week_13.model.Movie

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This will generate a Binding Object for your
        // activity_main.xml to use.
        val binding: ActivityMainBinding = DataBindingUtil
            .setContentView(this, R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.movie_list)

        val movieAdapter = MovieAdapter(object : MovieAdapter.MovieClickListener {
            override fun onMovieClick(movie: Movie) {
                val intent = Intent(this@MainActivity, DetailsActivity::class.java)
                intent.putExtra(DetailsActivity.EXTRA_TITLE, movie.title)
                intent.putExtra(DetailsActivity.EXTRA_RELEASE, movie.releaseDate)
                intent.putExtra(DetailsActivity.EXTRA_OVERVIEW, movie.overview)
                intent.putExtra(DetailsActivity.EXTRA_POSTER, movie.posterPath)
                startActivity(intent)
            }
        })

        recyclerView.adapter = movieAdapter

        val movieRepository = (application as MovieApplication).movieRepository
        val movieViewModel = ViewModelProvider(
            this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MovieViewModel(movieRepository) as T
                }
            })[MovieViewModel::class.java]

        binding.viewModel = movieViewModel
        binding.lifecycleOwner = this
    }
}