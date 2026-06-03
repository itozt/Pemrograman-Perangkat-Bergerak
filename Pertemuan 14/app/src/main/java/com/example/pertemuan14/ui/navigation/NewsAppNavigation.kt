package com.example.pertemuan14.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.pertemuan14.data.Article
import com.example.pertemuan14.ui.screen.DetailNewsScreen
import com.example.pertemuan14.ui.screen.HomeNewsScreen
import com.example.pertemuan14.ui.screen.SearchNewsScreen
import com.example.pertemuan14.viewmodel.NewsViewModel
import com.google.gson.Gson
import java.net.URLDecoder
import java.net.URLEncoder

sealed class NavigationScreen(val route: String) {
    object Home : NavigationScreen("home")
    object Search : NavigationScreen("search")
    object Detail : NavigationScreen("detail/{articleJson}") {
        fun createRoute(article: Article): String {
            val json = Gson().toJson(article)
            val encoded = URLEncoder.encode(json, "UTF-8")
            return "detail/$encoded"
        }
    }
}

@Composable
fun NewsAppNavigation(navController: NavHostController) {
    val viewModel: NewsViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = NavigationScreen.Home.route
    ) {
        composable(NavigationScreen.Home.route) {
            HomeNewsScreen(
                viewModel = viewModel,
                onSearchClick = { navController.navigate(NavigationScreen.Search.route) },
                onArticleClick = { article ->
                    navController.navigate(NavigationScreen.Detail.createRoute(article))
                }
            )
        }

        composable(NavigationScreen.Search.route) {
            SearchNewsScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onArticleClick = { article ->
                    navController.navigate(NavigationScreen.Detail.createRoute(article))
                }
            )
        }

        composable(NavigationScreen.Detail.route) { backStackEntry ->
            val articleJson = backStackEntry.arguments?.getString("articleJson")
            if (articleJson != null) {
                val decoded = URLDecoder.decode(articleJson, "UTF-8")
                val article = Gson().fromJson(decoded, Article::class.java)
                DetailNewsScreen(
                    article = article,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
