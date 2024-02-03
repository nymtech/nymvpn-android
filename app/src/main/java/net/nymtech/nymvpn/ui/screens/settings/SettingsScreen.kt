package net.nymtech.nymvpn.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import net.nymtech.nymvpn.BuildConfig
import net.nymtech.nymvpn.R
import net.nymtech.nymvpn.ui.NavItem
import net.nymtech.nymvpn.ui.common.buttons.SelectionItem
import net.nymtech.nymvpn.ui.common.buttons.SurfaceSelectionGroupButton
import net.nymtech.nymvpn.ui.theme.screenPadding
import timber.log.Timber

@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel = hiltViewModel()) {

  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val context = LocalContext.current

  fun openWebPage(url: String) {
    try {
      val webpage: Uri = Uri.parse(url)
      val intent = Intent(Intent.ACTION_VIEW, webpage)
      context.startActivity(intent)
    } catch (e: Exception) {
      Timber.e("Failed to launch webpage")
    }
  }

  Column(
      horizontalAlignment = Alignment.Start,
      verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.Top),
      modifier =
          Modifier.verticalScroll(rememberScrollState())
              .fillMaxSize()
              .padding(top = screenPadding)
              .padding(horizontal = screenPadding)) {
        SurfaceSelectionGroupButton(
            listOf(
                SelectionItem(
                    ImageVector.vectorResource(R.drawable.auto),
                    {
                      Switch(uiState.isAutoConnectEnabled, { viewModel.onAutoConnectSelected(it) })
                    },
                    stringResource(R.string.auto_connect),
                    stringResource(R.string.auto_connect_description),
                    {}),
                SelectionItem(
                    ImageVector.vectorResource(R.drawable.two),
                    {
                      Switch(
                          uiState.isFirstHopSelectionEnabled,
                          { viewModel.onEntryLocationSelected(it) })
                    },
                    stringResource(R.string.entry_location),
                    stringResource(R.string.entry_location_description),
                    {})))
        SurfaceSelectionGroupButton(
            listOf(
                SelectionItem(
                    ImageVector.vectorResource(R.drawable.contrast),
                    title = stringResource(R.string.display_theme),
                    onClick = { navController.navigate(NavItem.Settings.Display.route) })))
        SurfaceSelectionGroupButton(
            listOf(
                SelectionItem(
                    ImageVector.vectorResource(R.drawable.logs),
                    title = stringResource(R.string.logs),
                    onClick = { navController.navigate(NavItem.Settings.Logs.route) })))
        SurfaceSelectionGroupButton(
            listOf(
                SelectionItem(
                    ImageVector.vectorResource(R.drawable.feedback),
                    title = stringResource(R.string.feedback),
                    onClick = { navController.navigate(NavItem.Settings.Feedback.route) }),
                SelectionItem(
                    ImageVector.vectorResource(R.drawable.support),
                    title = stringResource(R.string.support),
                    //TODO fix, direct to support
                    onClick = { navController.navigate(NavItem.Settings.Support.route) })
                    )
        )
        SurfaceSelectionGroupButton(
            listOf(
                SelectionItem(
                    title = stringResource(R.string.legal),
                    onClick = { navController.navigate(NavItem.Settings.Legal.route) })))
      Box(contentAlignment =  Alignment.BottomStart, modifier = Modifier.fillMaxSize().padding(
          screenPadding)) {
          Text("Version: ${BuildConfig.VERSION_NAME}", style = MaterialTheme.typography.bodyMedium)
      }
  }
}
