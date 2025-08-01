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
import com.example.mynotes.data.viewmodel.CommonViewModel
import com.example.mynotes.pages.FolderPage
import com.example.mynotes.pages.GoogleSignInPage
import com.example.mynotes.pages.NotePage
import com.example.mynotes.pages.HomePage
import com.example.mynotes.pages.PrivateLockPage
import com.example.mynotes.pages.PrivateLockSetupPage
import com.example.mynotes.pages.RemovePrivateFilesPage
import com.example.mynotes.pages.AddNoteToCloudPage
import com.example.mynotes.pages.ProfilePage
import com.example.mynotes.pages.RemoveNotesFromCloudPage
import com.example.mynotes.pages.ViewCloudNotesPage
import com.example.mynotes.utils.RequestPermissionsOnStart


@Composable
fun Navigation(navController: NavHostController = rememberNavController()) {
    RequestPermissionsOnStart()


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
                noteBlockViewModel = commonViewModel.noteBlockViewModel, currentUser = commonViewModel.currentUser)
        }




        composable(//note page
            route = "card_page/{cardId}/{folderId}/{isPrivate}",
            arguments = listOf(
                navArgument("cardId") {
                    type = NavType.StringType
                },
                navArgument("folderId") {
                    type = NavType.StringType
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
            val cardId = backStackEntry.arguments?.getString("cardId")?:"-1"
            val folderId = backStackEntry.arguments?.getString("folderId") ?: "-1"
            val isPrivate = backStackEntry.arguments?.getBoolean("isPrivate") == true
            if(cardId =="-1" && folderId=="-1"&& !isPrivate){ //creating new note in ALL
                NotePage(lockViewModel = commonViewModel.lockViewModel, noteViewModel = commonViewModel.noteViewModel, newNote = true,navController=navController,
                    folderViewModel = commonViewModel.folderViewModel, noteBlockViewModel = commonViewModel.noteBlockViewModel,
                    imageDemoViewModel = commonViewModel.imageDemoViewModel,)
            }
            else if(cardId=="-1"&&!isPrivate) { //creating new note in a folder
                NotePage(lockViewModel = commonViewModel.lockViewModel,noteViewModel = commonViewModel.noteViewModel, newNote = true,folderId = folderId,navController=navController,
                    folderViewModel = commonViewModel.folderViewModel,noteBlockViewModel = commonViewModel.noteBlockViewModel,
                    imageDemoViewModel = commonViewModel.imageDemoViewModel,)
            }
            else if(cardId=="-1"&&isPrivate) { //creating new in  private files
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
                    type = NavType.StringType
                }
            ),
            enterTransition = { slideInHorizontally(initialOffsetX = { it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) },  // slide in from right
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) },  // slide out to left when going to another screen
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) }, // slide in from left when coming back
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) },   // slide out to right when going back
        ) { backStackEntry ->
            val folderId = backStackEntry.arguments?.getString("folderId") ?: "-1"
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


        composable(route = "google_sign_in_page",
            enterTransition = { slideInHorizontally(initialOffsetX = { it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) },   // slide out to left when going forward to CardPage
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) }, // slide in from left when returning from other screen){
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) },   // slide out to right when going back
        ){
            GoogleSignInPage(navController = navController, setUser = {commonViewModel.setUser()}, noteViewModel = commonViewModel.noteViewModel, noteBlockViewModel = commonViewModel.noteBlockViewModel)
        }


        composable(route = "add_note_to_cloud_page",
            enterTransition = { slideInHorizontally(initialOffsetX = { it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) },   // slide out to left when going forward to CardPage
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) }, // slide in from left when returning from other screen){
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) },   // slide out to right when going back
        ){
            AddNoteToCloudPage(navController = navController, noteViewModel = commonViewModel.noteViewModel,noteBlockViewModel = commonViewModel.noteBlockViewModel)
        }


        composable(route = "profile_page",
            enterTransition = { slideInHorizontally(initialOffsetX = { it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) },   // slide out to left when going forward to CardPage
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) }, // slide in from left when returning from other screen){
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) },   // slide out to right when going back
        ){
            ProfilePage(navController = navController, currentUser = commonViewModel.currentUser, removeUser = {commonViewModel.removeUser()}, noteViewModel = commonViewModel.noteViewModel)
        }


        composable(route = "view_cloud_notes_page",
            enterTransition = { slideInHorizontally(initialOffsetX = { it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) },   // slide out to left when going forward to CardPage
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) }, // slide in from left when returning from other screen){
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) },   // slide out to right when going back
        ){
            ViewCloudNotesPage(navController = navController, noteViewModel = commonViewModel.noteViewModel)
        }


        composable(route = "remove_notes_from_cloud_page",
            enterTransition = { slideInHorizontally(initialOffsetX = { it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) },   // slide out to left when going forward to CardPage
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) }, // slide in from left when returning from other screen){
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it },animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)) },   // slide out to right when going back
        ){
            RemoveNotesFromCloudPage(navController = navController, noteViewModel = commonViewModel.noteViewModel)
        }
    }
}