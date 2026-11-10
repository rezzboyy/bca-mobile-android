package com.bca.mobile

import android.app.Application
import com.bca.mobile.data.repository.BcaRepository

/**
 * Custom Application class for BCA Mobile.
 * Initializes persistent local repository immediately at process start,
 * ensuring all custom values (Nama, Saldo, Jenis Rekening, No Rekening, dsb.)
 * are permanently preserved across app exit, device reboot, and app updates.
 */
class BcaApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        BcaRepository.initialize(this)
    }
}
