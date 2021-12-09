/*
 * ownCloud Android client application
 *
 * Copyright (C) 2021 ownCloud GmbH.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.owncloud.android.domain.files.usecases

import com.owncloud.android.domain.BaseUseCaseWithResult
import com.owncloud.android.domain.files.FileRepository
import com.owncloud.android.domain.files.model.OCFile

class GetFilesAvailableOfflineUseCase(
    private val fileRepository: FileRepository
) : BaseUseCaseWithResult<List<OCFile>, GetFilesAvailableOfflineUseCase.Params>() {

    override fun run(params: Params): List<OCFile> = fileRepository.getFilesAvailableOffline(params.owner)

    data class Params(
        val owner: String
    )
}
