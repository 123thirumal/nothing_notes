package com.example.mynotes

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.savedstate.savedState
import com.example.mynotes.data.viewmodel.CommonViewModel
import com.example.mynotes.pages.FolderPage
import com.example.mynotes.pages.NotePage
import com.example.mynotes.pages.HomePage
import com.example.mynotes.pages.PrivateLockPage
import com.example.mynotes.pages.PrivateLockSetupPage
import com.example.mynotes.pages.RemovePrivateFilesPage


@Composable
fun Navigation(navController: NavHostController = rememberNavController()) {
    val commonViewModel: CommonViewModel =
        viewModel(viewModelStoreOwner = LocalViewModelStoreOwner.current!!)

    NavHost(
        navController = navController,
        startDestination = "home_page",
        modifier = Modifier.background(Color.Black)
    ){
        composable(route = "home_page",
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) },   // slide out to left when going forward to CardPage
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) }, // slide in from left when returning from other screen){
        ){
            HomePage(navController = navController, isPrivateUnlocked = commonViewModel.isPrivateFilesUnlocked, headSelect = commonViewModel.headSelect,
                lockViewModel = commonViewModel.lockViewModel, noteViewModel = commonViewModel.noteViewModel, folderViewModel = commonViewModel.folderViewModel,
                noteBlockViewModel = commonViewModel.noteBlockViewModel)
        }




        composable(//note page
            route = "card_page/{cardId}/{folderId}/{isPrivate}",
            arguments = listOf(
                navArgument("cardId") {
                    type = NavType.LongType
                },
                navArgument("folderId") {
                    type = NavType.LongType
                },
                navArgument("isPrivate") {
                    type = NavType.BoolType
                }
            ),
            enterTransition = { slideInHorizontally(initialOffsetX = { it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) },  // slide in from right
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) },  // slide out to left when going to another screen
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) }, // slide in from left when coming back
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) },   // slide out to right when going back
        ) { backStackEntry ->
            val cardId = backStackEntry.arguments?.getLong("cardId")?:-1L
            val folderId = backStackEntry.arguments?.getLong("folderId") ?: -1L
            val isPrivate = backStackEntry.arguments?.getBoolean("isPrivate") == true
            if(cardId ==-1L && folderId==-1L&& !isPrivate){ //creating new note in ALL
                NotePage(lockViewModel = commonViewModel.lockViewModel, noteViewModel = commonViewModel.noteViewModel, newNote = true,navController=navController,
                    folderViewModel = commonViewModel.folderViewModel, noteBlockViewModel = commonViewModel.noteBlockViewModel,
                    imageDemoViewModel = commonViewModel.imageDemoViewModel,)
            }
            else if(cardId==-1L&&!isPrivate) { //creating new note in a folder
                NotePage(lockViewModel = commonViewModel.lockViewModel,noteViewModel = commonViewModel.noteViewModel, newNote = true,folderId = folderId,navController=navController,
                    folderViewModel = commonViewModel.folderViewModel,noteBlockViewModel = commonViewModel.noteBlockViewModel,
                    imageDemoViewModel = commonViewModel.imageDemoViewModel,)
            }
            else if(cardId==-1L&&isPrivate) { //creating new in  private files
                NotePage(lockViewModel = commonViewModel.lockViewModel,noteViewModel = commonViewModel.noteViewModel, newNote = true, isPrivate = true,navController=navController,
                    folderViewModel = commonViewModel.folderViewModel,noteBlockViewModel = commonViewModel.noteBlockViewModel,
                    imageDemoViewModel = commonViewModel.imageDemoViewModel, )
            }
            else{ //open existing note
                NotePage(lockViewModel = commonViewModel.lockViewModel,noteViewModel = commonViewModel.noteViewModel, newNote = false,noteId=cardId,navController=navController,
                    folderViewModel = commonViewModel.folderViewModel,noteBlockViewModel = commonViewModel.noteBlockViewModel,
                    imageDemoViewModel = commonViewModel.imageDemoViewModel,)
            }
        }



        composable(//folder page
            route = "folder_page/{folderId}",
            arguments = listOf(
                navArgument("folderId") {
                    type = NavType.LongType
                }
            ),
            enterTransition = { slideInHorizontally(initialOffsetX = { it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) },  // slide in from right
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) },  // slide out to left when going to another screen
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) }, // slide in from left when coming back
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) },   // slide out to right when going back
        ) { backStackEntry ->
            val folderId = backStackEntry.arguments?.getLong("folderId") ?: -1L
            FolderPage(folderId= folderId,navController=navController, noteViewModel = commonViewModel.noteViewModel,
                folderViewModel = commonViewModel.folderViewModel, lockViewModel = commonViewModel.lockViewModel, noteBlockViewModel = commonViewModel.noteBlockViewModel)
        }


        composable(route = "private_lock_setup_page",
            enterTransition = { slideInHorizontally(initialOffsetX = { it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) },   // slide out to left when going forward to CardPage
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) }, // slide in from left when returning from other screen){
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) },   // slide out to right when going back
        ){
            PrivateLockSetupPage(lockViewModel = commonViewModel.lockViewModel, navController = navController, isUnlocked = commonViewModel.isPrivateFilesUnlocked)
        }


        composable(route = "private_lock_page",
            enterTransition = { slideInHorizontally(initialOffsetX = { it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) },   // slide out to left when going forward to CardPage
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) }, // slide in from left when returning from other screen){
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) },   // slide out to right when going back
        ){
            PrivateLockPage(lockViewModel = commonViewModel.lockViewModel, navController = navController, isUnlocked = commonViewModel.isPrivateFilesUnlocked)
        }


        composable(route = "remove_private_files_page",
            enterTransition = { slideInHorizontally(initialOffsetX = { it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) },   // slide out to left when going forward to CardPage
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) }, // slide in from left when returning from other screen){
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) },   // slide out to right when going back
        ){
            RemovePrivateFilesPage(lockViewModel = commonViewModel.lockViewModel, navController = navController, isPrivateUnlocked = commonViewModel.isPrivateFilesUnlocked, noteViewModel = commonViewModel.noteViewModel)
        }
    }
}