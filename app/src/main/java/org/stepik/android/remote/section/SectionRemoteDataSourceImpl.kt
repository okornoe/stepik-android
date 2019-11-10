package org.stepik.android.remote.section

import io.reactivex.Single
import io.reactivex.functions.Function
import org.stepik.android.data.section.source.SectionRemoteDataSource
import org.stepik.android.model.Section
import org.stepik.android.remote.base.chunkedSingleMap
import org.stepik.android.remote.section.model.SectionResponse
import org.stepik.android.remote.section.service.SectionService
import retrofit2.Call
import javax.inject.Inject

class SectionRemoteDataSourceImpl
@Inject
constructor(
    private val sectionService: SectionService
) : SectionRemoteDataSource {
    private val sectionResponseMapper = Function(SectionResponse::sections)

    override fun getSectionsRx(vararg sectionIds: Long): Single<List<Section>> =
        sectionIds
            .chunkedSingleMap { ids ->
                sectionService.getSectionsRx(ids)
                    .map(sectionResponseMapper)
            }

    override fun getSections(vararg sectionIds: Long): Call<SectionResponse> =
        sectionService.getSections(sectionIds)
}