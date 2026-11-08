package com.bca.mobile

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.bca.mobile.data.model.NetworkSignalState
import com.bca.mobile.data.repository.BcaRepository
import com.bca.mobile.data.updater.GitHubUpdateManager
import com.bca.mobile.data.updater.UpdateCheckResult
import com.bca.mobile.data.updater.UpdateInfo
import com.bca.mobile.ui.components.BcaAlertResultDialog
import com.bca.mobile.ui.components.BcaNavTab
import com.bca.mobile.ui.components.BcaUpdateDialog
import com.bca.mobile.ui.screens.*
import com.bca.mobile.ui.theme.BcaMobileTheme
import kotlinx.coroutines.launch

sealed interface Screen {
    data object Hub : Screen
    data object Dashboard : Screen
    data object MInfoMenu : Screen
    data object MutasiRekening : Screen
    data object InfoKurs : Screen
    data object MTransferMenu : Screen
    data object TransferAntarRekening : Screen
    data object TransferAntarBank : Screen
    data object DaftarTransferAntarRekening : Screen
    data object DaftarTransferAntarBank : Screen
    data object FlazzScreen : Screen
    data object CardlessScreen : Screen
    data object Notifikasi : Screen
    data object AkunSaya : Screen
    data object TransferInbox : Screen
}

class MainActivity : ComponentActivity() {
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val initialCapabilities = cm?.getNetworkCapabilities(cm.activeNetwork)
        val hasInternet = initialCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        BcaRepository.getInstance().setNetworkSignal(
            if (hasInternet) NetworkSignalState.READY else NetworkSignalState.OFFLINE
        )

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                runOnUiThread {
                    BcaRepository.getInstance().setNetworkSignal(NetworkSignalState.READY)
                }
            }
            override fun onLost(network: Network) {
                runOnUiThread {
                    BcaRepository.getInstance().setNetworkSignal(NetworkSignalState.OFFLINE)
                }
            }
            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                val isOnline = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                runOnUiThread {
                    if (isOnline) {
                        if (BcaRepository.getInstance().networkSignal.value == NetworkSignalState.OFFLINE) {
                            BcaRepository.getInstance().setNetworkSignal(NetworkSignalState.READY)
                        }
                    } else {
                        BcaRepository.getInstance().setNetworkSignal(NetworkSignalState.OFFLINE)
                    }
                }
            }
        }
        try {
            networkCallback?.let { cm?.registerDefaultNetworkCallback(it) }
        } catch (_: Exception) {}

        setContent {
            BcaMobileTheme {
                val repository = remember { BcaRepository.getInstance() }
                val account by repository.account.collectAsState()
                val mutations by repository.mutations.collectAsState()
                val recipients by repository.recipients.collectAsState()
                val flazzCard by repository.flazzCard.collectAsState()
                val exchangeRates by repository.exchangeRates.collectAsState()

                val backStack = remember { mutableStateListOf<Screen>(Screen.Hub) }
                val currentScreen = backStack.lastOrNull() ?: Screen.Hub

                var bottomNavAlertMessage by remember { mutableStateOf<String?>(null) }

                val context = LocalContext.current
                val coroutineScope = rememberCoroutineScope()

                var availableUpdate by remember { mutableStateOf<UpdateInfo?>(null) }
                var isDownloadingUpdate by remember { mutableStateOf(false) }
                var updateDownloadProgress by remember { mutableFloatStateOf(0f) }

                // Check GitHub Releases for updates in background
                LaunchedEffect(Unit) {
                    try {
                        val currentVersion = context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "1.0.0"
                        when (val res = GitHubUpdateManager.checkForUpdate(currentVersion)) {
                            is UpdateCheckResult.UpdateAvailable -> {
                                availableUpdate = res.info
                            }
                            else -> {}
                        }
                    } catch (_: Exception) {}
                }

                fun navigateTo(screen: Screen) {
                    if (currentScreen != screen) {
                        if (screen != Screen.Hub) {
                            repository.triggerProcessingPulse(400)
                        }
                        backStack.add(screen)
                    }
                }

                fun popBack() {
                    if (backStack.size > 1) {
                        backStack.removeAt(backStack.lastIndex)
                    }
                }

                fun handleBottomNavTab(tab: BcaNavTab) {
                    repository.triggerProcessingPulse(350)
                    when (tab) {
                        BcaNavTab.HOME -> {
                            if (currentScreen != Screen.Dashboard) {
                                // Navigate back to Dashboard root
                                while (backStack.size > 1 && backStack.last() != Screen.Dashboard) {
                                    backStack.removeAt(backStack.lastIndex)
                                }
                                if (!backStack.contains(Screen.Dashboard)) {
                                    backStack.add(Screen.Dashboard)
                                }
                            }
                        }
                        BcaNavTab.TRANSAKSI -> {
                            navigateTo(Screen.MTransferMenu)
                        }
                        BcaNavTab.NOTIFIKASI -> {
                            navigateTo(Screen.Notifikasi)
                        }
                        BcaNavTab.QRIS -> {
                            bottomNavAlertMessage = "Layanan QRIS Scanner aktif. Arahkan kamera ke QRIS BCA / Merchant."
                        }
                        BcaNavTab.AKUN_SAYA -> {
                            navigateTo(Screen.AkunSaya)
                        }
                        BcaNavTab.NONE -> {}
                    }
                }

                BackHandler(enabled = backStack.size > 1) {
                    popBack()
                }

                Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
                    when (screen) {
                        Screen.Hub -> BcaHubScreen(
                            onLoginSuccess = {
                                navigateTo(Screen.Dashboard)
                            },
                            onOpenFlazz = {
                                navigateTo(Screen.FlazzScreen)
                            }
                        )

                        Screen.Dashboard -> MbcaDashboardScreen(
                            userName = account.accountHolder,
                            onLogout = {
                                backStack.clear()
                                backStack.add(Screen.Hub)
                            },
                            onNavigateToMInfo = {
                                navigateTo(Screen.MInfoMenu)
                            },
                            onNavigateToMTransfer = {
                                navigateTo(Screen.MTransferMenu)
                            },
                            onNavigateToFlazz = {
                                navigateTo(Screen.FlazzScreen)
                            },
                            onNavigateToCardless = {
                                navigateTo(Screen.CardlessScreen)
                            },
                            onTabSelected = ::handleBottomNavTab
                        )

                        Screen.MInfoMenu -> MInfoMenuScreen(
                            account = account,
                            onBack = { popBack() },
                            onNavigateToMutasi = {
                                navigateTo(Screen.MutasiRekening)
                            },
                            onNavigateToKurs = {
                                navigateTo(Screen.InfoKurs)
                            },
                            onTabSelected = ::handleBottomNavTab
                        )

                        Screen.MutasiRekening -> MutasiRekeningScreen(
                            account = account,
                            mutations = mutations,
                            onBack = { popBack() }
                        )

                        Screen.InfoKurs -> InfoKursScreen(
                            rates = exchangeRates,
                            onBack = { popBack() }
                        )

                        Screen.MTransferMenu -> MTransferMenuScreen(
                            onBack = { popBack() },
                            onNavigateTransferBca = {
                                navigateTo(Screen.TransferAntarRekening)
                            },
                            onNavigateTransferBank = {
                                navigateTo(Screen.TransferAntarBank)
                            },
                            onNavigateDaftarTransferBca = {
                                navigateTo(Screen.DaftarTransferAntarRekening)
                            },
                            onNavigateDaftarTransferBank = {
                                navigateTo(Screen.DaftarTransferAntarBank)
                            },
                            onNavigateToInbox = {
                                navigateTo(Screen.TransferInbox)
                            },
                            onTabSelected = ::handleBottomNavTab
                        )

                        Screen.DaftarTransferAntarRekening -> DaftarTransferAntarRekeningScreen(
                            onBack = { popBack() }
                        )

                        Screen.DaftarTransferAntarBank -> DaftarTransferAntarBankScreen(
                            onBack = { popBack() }
                        )

                        Screen.TransferAntarRekening -> TransferAntarRekeningScreen(
                            account = account,
                            recipients = recipients,
                            onBack = { popBack() }
                        )

                        Screen.TransferAntarBank -> TransferAntarBankScreen(
                            account = account,
                            recipients = recipients,
                            onBack = { popBack() }
                        )

                        Screen.TransferInbox -> TransferInboxScreen(
                            onBack = { popBack() }
                        )

                        Screen.FlazzScreen -> FlazzScreen(
                            flazzCard = flazzCard,
                            onBack = { popBack() }
                        )

                        Screen.CardlessScreen -> CardlessScreen(
                            account = account,
                            onBack = { popBack() }
                        )

                        Screen.Notifikasi -> NotifikasiScreen(
                            onBack = { popBack() },
                            onTabSelected = ::handleBottomNavTab
                        )

                        Screen.AkunSaya -> AkunSayaScreen(
                            account = account,
                            onBack = { popBack() },
                            onTabSelected = ::handleBottomNavTab
                        )
                    }
                }

                if (bottomNavAlertMessage != null) {
                    BcaAlertResultDialog(
                        title = "m-BCA",
                        message = bottomNavAlertMessage ?: "",
                        onDismiss = { bottomNavAlertMessage = null },
                        confirmText = "OK"
                    )
                }

                if (availableUpdate != null) {
                    BcaUpdateDialog(
                        updateInfo = availableUpdate!!,
                        isDownloading = isDownloadingUpdate,
                        downloadProgress = updateDownloadProgress,
                        onDismiss = { availableUpdate = null },
                        onConfirmUpdate = {
                            isDownloadingUpdate = true
                            coroutineScope.launch {
                                GitHubUpdateManager.downloadAndInstallApk(
                                    context = context,
                                    downloadUrl = availableUpdate!!.apkDownloadUrl,
                                    onProgress = { updateDownloadProgress = it },
                                    onError = {
                                        isDownloadingUpdate = false
                                        availableUpdate = null
                                    }
                                )
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        networkCallback?.let {
            try {
                cm?.unregisterNetworkCallback(it)
            } catch (_: Exception) {}
        }
    }
}
