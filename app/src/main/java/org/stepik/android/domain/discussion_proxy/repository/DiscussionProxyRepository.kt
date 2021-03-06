package org.stepik.android.domain.discussion_proxy.repository

import io.reactivex.Maybe
import io.reactivex.Single
import ru.nobird.android.domain.rx.maybeFirst
import org.stepik.android.model.comments.DiscussionProxy

interface DiscussionProxyRepository {
    fun getDiscussionProxy(discussionProxyId: String): Maybe<DiscussionProxy> =
        getDiscussionProxies(discussionProxyId)
            .maybeFirst()

    fun getDiscussionProxies(vararg discussionProxyIds: String): Single<List<DiscussionProxy>>
}