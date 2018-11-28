package org.stepik.android.data.last_step.repository

import io.reactivex.Maybe
import org.stepic.droid.util.doOnSuccess
import org.stepik.android.data.last_step.source.LastStepCacheDataSource
import org.stepik.android.data.last_step.source.LastStepRemoteDataSource
import org.stepik.android.domain.last_step.model.LastStep
import org.stepik.android.domain.last_step.repository.LastStepRepository
import javax.inject.Inject

class LastStepRepositoryImpl
@Inject
constructor(
    private val lastStepRemoteDataSource: LastStepRemoteDataSource,
    private val lastStepCacheDataSource: LastStepCacheDataSource
) : LastStepRepository {
    override fun getLastStep(id: String): Maybe<LastStep> =
            lastStepRemoteDataSource
                .getLastStep(id)
                .doOnSuccess(lastStepCacheDataSource::saveLastStep)
                .switchIfEmpty(lastStepCacheDataSource.getLastStep(id))
}