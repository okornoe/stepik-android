package org.stepik.android.data.search.repository

import io.reactivex.Single
import org.stepic.droid.model.SearchQuery
import org.stepik.android.data.search.source.SearchRemoteDataSource
import org.stepik.android.domain.search.repository.SearchRepository
import javax.inject.Inject

class SearchRepositoryImpl
@Inject
constructor(
    private val searchRemoteDataSource: SearchRemoteDataSource
) : SearchRepository {
    override fun getSearchQueries(query: String): Single<List<SearchQuery>> =
        searchRemoteDataSource.getSearchQueries(query)
}