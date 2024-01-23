package net.nymtech.nymvpn.ui.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.window.core.layout.WindowHeightSizeClass
import net.nymtech.nymvpn.R
import net.nymtech.nymvpn.model.Country
import net.nymtech.nymvpn.model.NetworkMode
import net.nymtech.nymvpn.ui.MainActivity
import net.nymtech.nymvpn.ui.NavItem
import net.nymtech.nymvpn.ui.common.animations.SpinningIcon
import net.nymtech.nymvpn.ui.common.buttons.ListOptionSelectionButton
import net.nymtech.nymvpn.ui.common.buttons.MainStyledButton
import net.nymtech.nymvpn.ui.common.buttons.RadioSurfaceButton
import net.nymtech.nymvpn.ui.common.labels.GroupLabel
import net.nymtech.nymvpn.ui.common.labels.StatusInfoLabel
import net.nymtech.nymvpn.ui.model.ConnectionState
import net.nymtech.nymvpn.ui.model.StateMessage
import net.nymtech.nymvpn.ui.theme.CustomColors
import net.nymtech.nymvpn.util.StringUtils

@Composable
fun MainScreen(navController: NavController, viewModel: MainViewModel = hiltViewModel()) {

  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val context = LocalContext.current
  val padding = when(MainActivity.windowHeightSizeClass) {
      WindowHeightSizeClass.MEDIUM, WindowHeightSizeClass.COMPACT -> 16.dp
      else -> { 24.dp }}

  @Composable
  fun determineCountryIcon(country: Country): @Composable () -> Unit {
    val image =
        if (country.isFastest) ImageVector.vectorResource(R.drawable.bolt)
        else ImageVector.vectorResource(StringUtils.getImageVectorByName(context, country.isoCode.lowercase()))
    return {
      Image(
          image,
          image.name,
          modifier = Modifier.padding(16.dp),
          colorFilter =
              if (country.isFastest) ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
              else null)
    }
  }

  Column(
      verticalArrangement = Arrangement.spacedBy(padding, Alignment.Top),
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxSize()) {
      val snackAreaPadding = when(MainActivity.windowHeightSizeClass) {
      WindowHeightSizeClass.MEDIUM, WindowHeightSizeClass.COMPACT -> 24.dp
      else -> { 96.dp } }
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = snackAreaPadding)) {
              ConnectionStateDisplay(connectionState = uiState.connectionState)
              uiState.stateMessage.let {
                when (it) {
                  is StateMessage.Info ->
                      StatusInfoLabel(
                          message = it.message.asString(context),
                          textColor = MaterialTheme.colorScheme.onSurfaceVariant)
                  is StateMessage.Error ->
                      StatusInfoLabel(
                          message = it.message.asString(context), textColor = CustomColors.error)
                }
              }
              StatusInfoLabel(
                  message = uiState.connectionTime, textColor = MaterialTheme.colorScheme.onSurface)
            }

        val firstHopName = StringUtils.buildCountryNameString(uiState.firstHopCounty, context)
        val lastHopName = StringUtils.buildCountryNameString(uiState.lastHopCountry, context)
        val firstHopIcon = determineCountryIcon(uiState.firstHopCounty)
        val lastHopIcon = determineCountryIcon(uiState.lastHopCountry)
        val spacePaddingMain = when(MainActivity.windowHeightSizeClass) {
          WindowHeightSizeClass.MEDIUM, WindowHeightSizeClass.COMPACT -> 16.dp
          else -> { 36.dp }}
        Column(
            verticalArrangement = Arrangement.spacedBy(spacePaddingMain, Alignment.Bottom),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(bottom = padding)) {
            val spacePaddingNetwork = when(MainActivity.windowHeightSizeClass) {
                WindowHeightSizeClass.MEDIUM, WindowHeightSizeClass.COMPACT -> 12.dp
                else -> { 24.dp }}
                Column(
                  verticalArrangement = Arrangement.spacedBy(spacePaddingNetwork, Alignment.Bottom),
                  modifier = Modifier.padding(horizontal = padding)) {
                    GroupLabel(title = stringResource(R.string.select_network))
                    RadioSurfaceButton(
                        leadingIcon = ImageVector.vectorResource(R.drawable.mixnet),
                        title = stringResource(R.string.five_hop),
                        description = stringResource(R.string.five_hop_description),
                        onClick = {
                          if (uiState.connectionState == ConnectionState.Disconnected)
                              viewModel.onFiveHopSelected()
                        },
                        selected = uiState.networkMode == NetworkMode.FIVE_HOP_MIXNET)
                    RadioSurfaceButton(
                        leadingIcon = ImageVector.vectorResource(R.drawable.shield),
                        title = stringResource(R.string.two_hop),
                        description = stringResource(R.string.two_hop_description),
                        onClick = {
                          if (uiState.connectionState == ConnectionState.Disconnected)
                              viewModel.onTwoHopSelected()
                        },
                        selected = uiState.networkMode == NetworkMode.TWO_HOP_WIREGUARD)
                  }
              val countrySelectionSpacing = when(MainActivity.windowHeightSizeClass) {
                  WindowHeightSizeClass.MEDIUM, WindowHeightSizeClass.COMPACT -> 12.dp
                  else -> { 24.dp } }
              Column(
                  verticalArrangement = Arrangement.spacedBy(countrySelectionSpacing, Alignment.Bottom),
                  modifier = Modifier.padding(horizontal = padding)) {
                    GroupLabel(title = stringResource(R.string.connect_to))
                    if (uiState.firstHopEnabled) {
                      ListOptionSelectionButton(
                          label = stringResource(R.string.first_hop),
                          value = firstHopName,
                          onClick = { navController.navigate(NavItem.Hop.Entry.route) },
                          leadingIcon = firstHopIcon)
                    }
                    ListOptionSelectionButton(
                        label = stringResource(R.string.last_hop),
                        value = lastHopName,
                        onClick = { navController.navigate(NavItem.Hop.Exit.route) },
                        leadingIcon = lastHopIcon)
                  }
              Box(modifier = Modifier.padding(horizontal = padding)) {
                when (uiState.connectionState) {
                  is ConnectionState.Disconnected ->
                      MainStyledButton(
                          onClick = { viewModel.onConnect() },
                          content = {
                            Text(
                                stringResource(id = R.string.connect),
                                style = MaterialTheme.typography.labelLarge)
                          })
                  is ConnectionState.Disconnecting,
                  ConnectionState.Connecting -> {
                    val loading = ImageVector.vectorResource(R.drawable.loading)
                    MainStyledButton(onClick = {}, content = { SpinningIcon(icon = loading) })
                  }
                  is ConnectionState.Connected ->
                      MainStyledButton(
                          onClick = { viewModel.onDisconnect() },
                          content = {
                            Text(
                                stringResource(id = R.string.disconnect),
                                style = MaterialTheme.typography.labelLarge)
                          },
                          color = MaterialTheme.colorScheme.secondary)
                }
              }
            }
      }
}
