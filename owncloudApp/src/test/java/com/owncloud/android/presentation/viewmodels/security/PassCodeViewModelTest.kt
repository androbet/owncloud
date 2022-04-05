/**
 * ownCloud Android client application
 *
 * @author Juan Carlos Garrote Gascón
 *
 * Copyright (C) 2021 ownCloud GmbH.
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2,
 * as published by the Free Software Foundation.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.owncloud.android.presentation.viewmodels.security

import android.os.SystemClock
import com.owncloud.android.R
import com.owncloud.android.data.preferences.datasources.SharedPreferencesProvider
import com.owncloud.android.presentation.ui.security.PREFERENCE_LAST_UNLOCK_ATTEMPT_TIMESTAMP
import com.owncloud.android.presentation.ui.security.PREFERENCE_LAST_UNLOCK_TIMESTAMP
import com.owncloud.android.presentation.viewmodels.ViewModelTest
import com.owncloud.android.presentation.ui.security.passcode.PassCodeActivity
import com.owncloud.android.presentation.ui.security.passcode.PassCodeActivity.Companion.ACTION_CHECK
import com.owncloud.android.presentation.ui.security.passcode.PassCodeActivity.Companion.ACTION_REQUEST_WITH_RESULT
import com.owncloud.android.presentation.ui.security.passcode.PassCodeActivity.Companion.PREFERENCE_PASSCODE
import com.owncloud.android.presentation.ui.security.passcode.PassCodeActivity.Companion.PREFERENCE_PASSCODE_D
import com.owncloud.android.presentation.ui.security.passcode.PassCodeActivity.Companion.PREFERENCE_SET_PASSCODE
import com.owncloud.android.presentation.ui.security.passcode.PasscodeAction
import com.owncloud.android.presentation.ui.security.passcode.PasscodeType
import com.owncloud.android.presentation.ui.security.passcode.Status
import com.owncloud.android.presentation.ui.settings.fragments.SettingsSecurityFragment.Companion.PREFERENCE_LOCK_ATTEMPTS
import com.owncloud.android.providers.ContextProvider
import com.owncloud.android.testutil.security.OC_PASSCODE_4_DIGITS
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class PassCodeViewModelTest : ViewModelTest() {
    private lateinit var passCodeViewModel: PassCodeViewModel
    private lateinit var preferencesProvider: SharedPreferencesProvider
    private lateinit var contextProvider: ContextProvider

    @Before
    fun setUp() {
        preferencesProvider = mockk(relaxUnitFun = true)
        contextProvider = mockk(relaxUnitFun = true)
    }

    @Test
    fun `on number clicked - ok`() {
        every { contextProvider.getInt(R.integer.passcode_digits) } returns OC_PASSCODE_4_DIGITS.length   //getNumberOfPassCodeDigits()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE, any()) } returns OC_PASSCODE_4_DIGITS  //getPassCode()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 1, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 2, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 3, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 4, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getInt(PREFERENCE_LOCK_ATTEMPTS, any()) } returns 0    //getNumberOfAttempts()
        every { preferencesProvider.getLong(PREFERENCE_LAST_UNLOCK_ATTEMPT_TIMESTAMP, any()) } returns 0   //getTimeToUnlockLeft()

        passCodeViewModel = PassCodeViewModel(preferencesProvider, contextProvider, PasscodeAction.CHECK)

        passCodeViewModel.onNumberClicked(1)

        assertEquals("1", passCodeViewModel.passcode.value)
    }

    @Test
    fun `on number clicked - 4 numbers`() {
        every { contextProvider.getInt(R.integer.passcode_digits) } returns 0   //getNumberOfPassCodeDigits()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE, any()) } returns null  //getPassCode()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 1, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 2, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 3, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 4, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getInt(PREFERENCE_LOCK_ATTEMPTS, any()) } returns 0    //getNumberOfAttempts()
        every { preferencesProvider.getLong(PREFERENCE_LAST_UNLOCK_ATTEMPT_TIMESTAMP, any()) } returns 0   //getTimeToUnlockLeft()

        passCodeViewModel = PassCodeViewModel(preferencesProvider, contextProvider, PasscodeAction.CHECK)

        passCodeViewModel.onNumberClicked(1)
        passCodeViewModel.onNumberClicked(1)
        passCodeViewModel.onNumberClicked(1)
        passCodeViewModel.onNumberClicked(1)

        assertEquals("1111", passCodeViewModel.passcode.value)
    }

    @Test
    fun `on number clicked - 3 or more attemps`() {
        every { contextProvider.getInt(R.integer.passcode_digits) } returns 0   //getNumberOfPassCodeDigits()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE, any()) } returns null  //getPassCode()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 1, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 2, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 3, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 4, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getInt(PREFERENCE_LOCK_ATTEMPTS, any()) } returns 3    //getNumberOfAttempts()
        every { preferencesProvider.getLong(PREFERENCE_LAST_UNLOCK_ATTEMPT_TIMESTAMP, any()) } returns 0   //getTimeToUnlockLeft()

        passCodeViewModel = PassCodeViewModel(preferencesProvider, contextProvider, PasscodeAction.CHECK)

        passCodeViewModel.onNumberClicked(1)

        assertEquals(null, passCodeViewModel.passcode.value)
    }

    @Test
    fun `on number clicked - lock time`() {
        every { contextProvider.getInt(R.integer.passcode_digits) } returns 0   //getNumberOfPassCodeDigits()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE, any()) } returns null  //getPassCode()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 1, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 2, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 3, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 4, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getInt(PREFERENCE_LOCK_ATTEMPTS, any()) } returns 3    //getNumberOfAttempts()
        every {
            preferencesProvider.getLong(
                PREFERENCE_LAST_UNLOCK_ATTEMPT_TIMESTAMP,
                any()
            )
        } returns SystemClock.elapsedRealtime()   //getTimeToUnlockLeft()

        passCodeViewModel = PassCodeViewModel(preferencesProvider, contextProvider, PasscodeAction.CHECK)

        passCodeViewModel.onNumberClicked(1)

        assertEquals(null, passCodeViewModel.passcode.value)
    }

    @Test
    fun `process full passcode - check - ok`() {
        every { contextProvider.getInt(R.integer.passcode_digits) } returns OC_PASSCODE_4_DIGITS.length   //getNumberOfPassCodeDigits()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE, any()) } returns OC_PASSCODE_4_DIGITS  //getPassCode()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 1, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 2, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 3, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 4, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getInt(PREFERENCE_LOCK_ATTEMPTS, any()) } returns 0    //getNumberOfAttempts()
        every { preferencesProvider.getLong(PREFERENCE_LAST_UNLOCK_ATTEMPT_TIMESTAMP, any()) } returns 0   //getTimeToUnlockLeft()

        passCodeViewModel = PassCodeViewModel(preferencesProvider, contextProvider, PasscodeAction.CHECK)

        passCodeViewModel.onNumberClicked(1)
        passCodeViewModel.onNumberClicked(1)
        passCodeViewModel.onNumberClicked(1)
        passCodeViewModel.onNumberClicked(1)

        assertEquals(Status(PasscodeAction.CHECK, PasscodeType.OK), passCodeViewModel.status.value)

        verify(exactly = 1) {
            preferencesProvider.putInt(PREFERENCE_LOCK_ATTEMPTS, 0)
        }
    }

    @Test
    fun `process full passcode - check - passcode not valid`() {
        every { contextProvider.getInt(R.integer.passcode_digits) } returns OC_PASSCODE_4_DIGITS.length   //getNumberOfPassCodeDigits()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE, any()) } returns OC_PASSCODE_4_DIGITS  //getPassCode()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 1, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 2, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 3, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 4, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getInt(PREFERENCE_LOCK_ATTEMPTS, any()) } returns 0    //getNumberOfAttempts()
        every { preferencesProvider.getLong(PREFERENCE_LAST_UNLOCK_ATTEMPT_TIMESTAMP, any()) } returns 0   //getTimeToUnlockLeft()

        passCodeViewModel = PassCodeViewModel(preferencesProvider, contextProvider, PasscodeAction.CHECK)

        passCodeViewModel.onNumberClicked(2)
        passCodeViewModel.onNumberClicked(1)
        passCodeViewModel.onNumberClicked(1)
        passCodeViewModel.onNumberClicked(1)

        assertEquals(Status(PasscodeAction.CHECK, PasscodeType.ERROR), passCodeViewModel.status.value)

        verify(exactly = 1) {
            preferencesProvider.putInt(PREFERENCE_LOCK_ATTEMPTS, any())
            preferencesProvider.putLong(PREFERENCE_LAST_UNLOCK_ATTEMPT_TIMESTAMP, SystemClock.elapsedRealtime())
        }
    }

    @Test
    fun `process full passcode - remove - ok`() {
        every { contextProvider.getInt(R.integer.passcode_digits) } returns OC_PASSCODE_4_DIGITS.length   //getNumberOfPassCodeDigits()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE, any()) } returns OC_PASSCODE_4_DIGITS  //getPassCode()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 1, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 2, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 3, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 4, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getInt(PREFERENCE_LOCK_ATTEMPTS, any()) } returns 0    //getNumberOfAttempts()
        every { preferencesProvider.getLong(PREFERENCE_LAST_UNLOCK_ATTEMPT_TIMESTAMP, any()) } returns 0   //getTimeToUnlockLeft()

        passCodeViewModel = PassCodeViewModel(preferencesProvider, contextProvider, PasscodeAction.REMOVE)

        passCodeViewModel.onNumberClicked(1)
        passCodeViewModel.onNumberClicked(1)
        passCodeViewModel.onNumberClicked(1)
        passCodeViewModel.onNumberClicked(1)

        assertEquals(Status(PasscodeAction.REMOVE, PasscodeType.OK), passCodeViewModel.status.value)

        verify(exactly = 1) {
            preferencesProvider.removePreference(PREFERENCE_PASSCODE)
            preferencesProvider.putBoolean(PREFERENCE_SET_PASSCODE, false)
        }
    }

    @Test
    fun `process full passcode - remove - passcode not valid`() {
        every { contextProvider.getInt(R.integer.passcode_digits) } returns OC_PASSCODE_4_DIGITS.length   //getNumberOfPassCodeDigits()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE, any()) } returns OC_PASSCODE_4_DIGITS  //getPassCode()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 1, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 2, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 3, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 4, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getInt(PREFERENCE_LOCK_ATTEMPTS, any()) } returns 0    //getNumberOfAttempts()
        every { preferencesProvider.getLong(PREFERENCE_LAST_UNLOCK_ATTEMPT_TIMESTAMP, any()) } returns 0   //getTimeToUnlockLeft()

        passCodeViewModel = PassCodeViewModel(preferencesProvider, contextProvider, PasscodeAction.REMOVE)

        passCodeViewModel.onNumberClicked(2)
        passCodeViewModel.onNumberClicked(1)
        passCodeViewModel.onNumberClicked(1)
        passCodeViewModel.onNumberClicked(1)

        assertEquals(Status(PasscodeAction.REMOVE, PasscodeType.ERROR), passCodeViewModel.status.value)
    }

    @Test
    fun `process full passcode - create - no confirm`() {
        every { contextProvider.getInt(R.integer.passcode_digits) } returns 0   //getNumberOfPassCodeDigits()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE, any()) } returns null  //getPassCode()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 1, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 2, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 3, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 4, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getInt(PREFERENCE_LOCK_ATTEMPTS, any()) } returns 0    //getNumberOfAttempts()
        every { preferencesProvider.getLong(PREFERENCE_LAST_UNLOCK_ATTEMPT_TIMESTAMP, any()) } returns 0   //getTimeToUnlockLeft()

        passCodeViewModel = PassCodeViewModel(preferencesProvider, contextProvider, PasscodeAction.CREATE)

        passCodeViewModel.onNumberClicked(1)
        passCodeViewModel.onNumberClicked(1)
        passCodeViewModel.onNumberClicked(1)
        passCodeViewModel.onNumberClicked(1)

        assertEquals(Status(PasscodeAction.CREATE, PasscodeType.NO_CONFIRM), passCodeViewModel.status.value)
    }

    @Test
    fun `process full passcode - create - confirm`() {
        every { contextProvider.getInt(R.integer.passcode_digits) } returns 0   //getNumberOfPassCodeDigits()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE, any()) } returns null  //getPassCode()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 1, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 2, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 3, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 4, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getInt(PREFERENCE_LOCK_ATTEMPTS, any()) } returns 0    //getNumberOfAttempts()
        every { preferencesProvider.getLong(PREFERENCE_LAST_UNLOCK_ATTEMPT_TIMESTAMP, any()) } returns 0   //getTimeToUnlockLeft()

        passCodeViewModel = PassCodeViewModel(preferencesProvider, contextProvider, PasscodeAction.CREATE)

        passCodeViewModel.onNumberClicked(1)
        passCodeViewModel.onNumberClicked(1)
        passCodeViewModel.onNumberClicked(1)
        passCodeViewModel.onNumberClicked(1)

        passCodeViewModel.onNumberClicked(1)
        passCodeViewModel.onNumberClicked(1)
        passCodeViewModel.onNumberClicked(1)
        passCodeViewModel.onNumberClicked(1)

        assertEquals(Status(PasscodeAction.CREATE, PasscodeType.CONFIRM), passCodeViewModel.status.value)

        verify(exactly = 1) {
            preferencesProvider.putString(PREFERENCE_PASSCODE, any())
            preferencesProvider.putBoolean(PREFERENCE_SET_PASSCODE, true)
        }
    }

    @Test
    fun `process full passcode - create - error`() {
        every { contextProvider.getInt(R.integer.passcode_digits) } returns 0   //getNumberOfPassCodeDigits()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE, any()) } returns null  //getPassCode()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 1, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 2, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 3, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 4, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getInt(PREFERENCE_LOCK_ATTEMPTS, any()) } returns 0    //getNumberOfAttempts()
        every { preferencesProvider.getLong(PREFERENCE_LAST_UNLOCK_ATTEMPT_TIMESTAMP, any()) } returns 0   //getTimeToUnlockLeft()

        passCodeViewModel = PassCodeViewModel(preferencesProvider, contextProvider, PasscodeAction.CREATE)

        passCodeViewModel.onNumberClicked(1)
        passCodeViewModel.onNumberClicked(1)
        passCodeViewModel.onNumberClicked(1)
        passCodeViewModel.onNumberClicked(1)

        passCodeViewModel.onNumberClicked(2)
        passCodeViewModel.onNumberClicked(1)
        passCodeViewModel.onNumberClicked(1)
        passCodeViewModel.onNumberClicked(1)

        assertEquals(Status(PasscodeAction.CREATE, PasscodeType.ERROR), passCodeViewModel.status.value)
    }

    @Test
    fun `get passcode - ok`() {
        every { contextProvider.getInt(R.integer.passcode_digits) } returns OC_PASSCODE_4_DIGITS.length   //getNumberOfPassCodeDigits()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE, any()) } returns OC_PASSCODE_4_DIGITS  //getPassCode()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 1, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 2, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 3, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 4, null) } returns null  //loadPinFromOldFormatIfPossible()

        passCodeViewModel = PassCodeViewModel(preferencesProvider, contextProvider, PasscodeAction.CHECK)

        val getPassCode = passCodeViewModel.getPassCode()

        assertEquals(OC_PASSCODE_4_DIGITS, getPassCode)

        verify(exactly = 2) {
            preferencesProvider.getString(PREFERENCE_PASSCODE, any())
        }
    }

    @Test
    fun `check passcode is valid - ok`() {
        every { contextProvider.getInt(R.integer.passcode_digits) } returns OC_PASSCODE_4_DIGITS.length   //getNumberOfPassCodeDigits()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE, any()) } returns OC_PASSCODE_4_DIGITS  //getPassCode()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 1, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 2, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 3, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 4, null) } returns null  //loadPinFromOldFormatIfPossible()

        passCodeViewModel = PassCodeViewModel(preferencesProvider, contextProvider, PasscodeAction.CHECK)

        val passCode = "1111"

        val passCodeCheckResult = passCodeViewModel.checkPassCodeIsValid(passCode)

        assertTrue(passCodeCheckResult)

        verify(exactly = 2) {
            preferencesProvider.getString(PREFERENCE_PASSCODE, any())
        }
    }

    @Test
    fun `check passcode is valid - ko - saved passcode is null`() {
        every { contextProvider.getInt(R.integer.passcode_digits) } returns 0   //getNumberOfPassCodeDigits()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE, any()) } returns null  //getPassCode()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 1, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 2, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 3, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 4, null) } returns null  //loadPinFromOldFormatIfPossible()

        passCodeViewModel = PassCodeViewModel(preferencesProvider, contextProvider, PasscodeAction.CHECK)

        val passCode = "1111"

        val passCodeCheckResult = passCodeViewModel.checkPassCodeIsValid(passCode)

        assertFalse(passCodeCheckResult)

        verify(exactly = 2) {
            preferencesProvider.getString(PREFERENCE_PASSCODE, any())
        }
    }

    @Test
    fun `check passcode is valid - ko - saved passcode is empty`() {
        every { contextProvider.getInt(R.integer.passcode_digits) } returns "".length   //getNumberOfPassCodeDigits()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE, any()) } returns ""  //getPassCode()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 1, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 2, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 3, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 4, null) } returns null  //loadPinFromOldFormatIfPossible()

        passCodeViewModel = PassCodeViewModel(preferencesProvider, contextProvider, PasscodeAction.CHECK)

        val passCode = "1111"

        val passCodeCheckResult = passCodeViewModel.checkPassCodeIsValid(passCode)

        assertFalse(passCodeCheckResult)

        verify(exactly = 2) {
            preferencesProvider.getString(PREFERENCE_PASSCODE, any())
        }
    }

    @Test
    fun `check passcode is valid - ko - different digit`() {
        every { contextProvider.getInt(R.integer.passcode_digits) } returns OC_PASSCODE_4_DIGITS.length   //getNumberOfPassCodeDigits()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE, any()) } returns OC_PASSCODE_4_DIGITS  //getPassCode()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 1, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 2, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 3, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 4, null) } returns null  //loadPinFromOldFormatIfPossible()

        passCodeViewModel = PassCodeViewModel(preferencesProvider, contextProvider, PasscodeAction.CHECK)

        val passCode = "1211"

        val passCodeCheckResult = passCodeViewModel.checkPassCodeIsValid(passCode)

        assertFalse(passCodeCheckResult)

        verify(exactly = 2) {
            preferencesProvider.getString(PassCodeActivity.PREFERENCE_PASSCODE, any())
        }
    }

    @Test
    fun `check passcode is valid - ko - null digit`() {
        every { contextProvider.getInt(R.integer.passcode_digits) } returns OC_PASSCODE_4_DIGITS.length   //getNumberOfPassCodeDigits()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE, any()) } returns OC_PASSCODE_4_DIGITS  //getPassCode()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 1, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 2, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 3, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 4, null) } returns null  //loadPinFromOldFormatIfPossible()

        passCodeViewModel = PassCodeViewModel(preferencesProvider, contextProvider, PasscodeAction.CHECK)

        val nullDigit: String? = null
        val passCode: StringBuilder = StringBuilder()
        passCode.append("1")
        passCode.append("1")
        passCode.append(nullDigit)
        passCode.append("1")

        val passCodeCheckResult = passCodeViewModel.checkPassCodeIsValid(passCode.toString())

        assertFalse(passCodeCheckResult)

        verify(exactly = 2) {
            preferencesProvider.getString(PREFERENCE_PASSCODE, any())
        }
    }

    @Test
    fun `get number of passcode digits - ok - digits is equal or greater than 4`() {
        val numberDigits = 4

        every { contextProvider.getInt(R.integer.passcode_digits) } returns numberDigits   //getNumberOfPassCodeDigits()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE, any()) } returns OC_PASSCODE_4_DIGITS  //getPassCode()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 1, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 2, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 3, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 4, null) } returns null  //loadPinFromOldFormatIfPossible()

        passCodeViewModel = PassCodeViewModel(preferencesProvider, contextProvider, PasscodeAction.CHECK)

        val getNumberDigits = passCodeViewModel.getNumberOfPassCodeDigits()

        assertEquals(numberDigits, getNumberDigits)

        verify(exactly = 1) {
            contextProvider.getInt(R.integer.passcode_digits)
        }
    }

    @Test
    fun `get number of passcode digits - ok - digits is less than 4`() {
        val numberDigits = 3

        every { contextProvider.getInt(R.integer.passcode_digits) } returns numberDigits   //getNumberOfPassCodeDigits()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE, any()) } returns OC_PASSCODE_4_DIGITS  //getPassCode()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 1, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 2, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 3, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 4, null) } returns null  //loadPinFromOldFormatIfPossible()

        passCodeViewModel = PassCodeViewModel(preferencesProvider, contextProvider, PasscodeAction.CHECK)

        val getNumberDigits = passCodeViewModel.getNumberOfPassCodeDigits()

        assertNotEquals(numberDigits, getNumberDigits)
        assertEquals(4, getNumberDigits)

        verify(exactly = 1) {
            contextProvider.getInt(R.integer.passcode_digits)
        }
    }

    @Test
    fun `set migration required - ok`() {
        every { contextProvider.getInt(R.integer.passcode_digits) } returns OC_PASSCODE_4_DIGITS.length   //getNumberOfPassCodeDigits()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE, any()) } returns OC_PASSCODE_4_DIGITS  //getPassCode()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 1, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 2, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 3, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 4, null) } returns null  //loadPinFromOldFormatIfPossible()

        passCodeViewModel = PassCodeViewModel(preferencesProvider, contextProvider, PasscodeAction.CHECK)

        val required = true

        passCodeViewModel.setMigrationRequired(required)

        verify(exactly = 1) {
            preferencesProvider.putBoolean(PassCodeActivity.PREFERENCE_MIGRATION_REQUIRED, required)
        }
    }

    @Test
    fun `set last unlock timestamp - ok`() {
        every { contextProvider.getInt(R.integer.passcode_digits) } returns OC_PASSCODE_4_DIGITS.length   //getNumberOfPassCodeDigits()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE, any()) } returns OC_PASSCODE_4_DIGITS  //getPassCode()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 1, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 2, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 3, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 4, null) } returns null  //loadPinFromOldFormatIfPossible()

        passCodeViewModel = PassCodeViewModel(preferencesProvider, contextProvider, PasscodeAction.CHECK)

        passCodeViewModel.setLastUnlockTimestamp()

        verify(exactly = 1) {
            preferencesProvider.putLong(PREFERENCE_LAST_UNLOCK_TIMESTAMP, SystemClock.elapsedRealtime())
        }
    }

    @Test
    fun `get number of attempts - ok`() {
        every { contextProvider.getInt(R.integer.passcode_digits) } returns OC_PASSCODE_4_DIGITS.length   //getNumberOfPassCodeDigits()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE, any()) } returns OC_PASSCODE_4_DIGITS  //getPassCode()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 1, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 2, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 3, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 4, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getInt(PREFERENCE_LOCK_ATTEMPTS, any()) } returns 3    //getNumberOfAttempts()

        passCodeViewModel = PassCodeViewModel(preferencesProvider, contextProvider, PasscodeAction.CHECK)

        val numberOfAttempts = passCodeViewModel.getNumberOfAttempts()

        assertEquals(3, numberOfAttempts)

        verify(exactly = 1) {
            preferencesProvider.getInt(PREFERENCE_LOCK_ATTEMPTS, any())
        }
    }

    @Test
    fun `increase number of attempts - ok`() {
        every { contextProvider.getInt(R.integer.passcode_digits) } returns OC_PASSCODE_4_DIGITS.length   //getNumberOfPassCodeDigits()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE, any()) } returns OC_PASSCODE_4_DIGITS  //getPassCode()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 1, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 2, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 3, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 4, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getInt(PREFERENCE_LOCK_ATTEMPTS, any()) } returns 3    //getNumberOfAttempts()

        passCodeViewModel = PassCodeViewModel(preferencesProvider, contextProvider, PasscodeAction.CHECK)

        passCodeViewModel.increaseNumberOfAttempts()

        verify(exactly = 1) {
            preferencesProvider.putInt(PREFERENCE_LOCK_ATTEMPTS, any())
            preferencesProvider.putLong(PREFERENCE_LAST_UNLOCK_ATTEMPT_TIMESTAMP, SystemClock.elapsedRealtime())
        }
    }

    @Test
    fun `reset number of attempts - ok`() {
        every { contextProvider.getInt(R.integer.passcode_digits) } returns OC_PASSCODE_4_DIGITS.length   //getNumberOfPassCodeDigits()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE, any()) } returns OC_PASSCODE_4_DIGITS  //getPassCode()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 1, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 2, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 3, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 4, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getInt(PREFERENCE_LOCK_ATTEMPTS, any()) } returns 3    //getNumberOfAttempts()

        passCodeViewModel = PassCodeViewModel(preferencesProvider, contextProvider, PasscodeAction.CHECK)

        passCodeViewModel.resetNumberOfAttempts()

        verify(exactly = 1) {
            preferencesProvider.putInt(PREFERENCE_LOCK_ATTEMPTS, 0)
        }
    }

    @Test
    fun `get time to unlock left - ok`() {
        every { contextProvider.getInt(R.integer.passcode_digits) } returns OC_PASSCODE_4_DIGITS.length   //getNumberOfPassCodeDigits()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE, any()) } returns OC_PASSCODE_4_DIGITS  //getPassCode()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 1, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 2, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 3, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getString(PREFERENCE_PASSCODE_D + 4, null) } returns null  //loadPinFromOldFormatIfPossible()
        every { preferencesProvider.getInt(PREFERENCE_LOCK_ATTEMPTS, any()) } returns 3    //getNumberOfAttempts()
        every { preferencesProvider.getLong(PREFERENCE_LAST_UNLOCK_ATTEMPT_TIMESTAMP, any()) } returns 0   //getTimeToUnlockLeft()

        passCodeViewModel = PassCodeViewModel(preferencesProvider, contextProvider, PasscodeAction.CHECK)

        val timeToUnlockLeft = passCodeViewModel.getTimeToUnlockLeft()

        assertEquals(3000, timeToUnlockLeft)
    }

    private fun setPasscodeOk() {
        for (i in 0 until passCodeViewModel.getNumberOfPassCodeDigits()) {
            passCodeViewModel.onNumberClicked(1)
        }
    }

    private fun setPasscodeWrong() {
        for (i in 0 until passCodeViewModel.getNumberOfPassCodeDigits()) {
            passCodeViewModel.onNumberClicked(2)
        }
    }
}
