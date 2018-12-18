package org.stepik.android.view.course_content.ui.adapter.delegates.control_bar

import org.stepik.android.domain.personal_deadlines.model.DeadlinesWrapper
import org.stepic.droid.web.storage.model.StorageRecord

interface CourseContentControlBarClickListener {

    fun onCreateScheduleClicked()
    fun onChangeScheduleClicked(record: StorageRecord<DeadlinesWrapper>)
    fun onRemoveScheduleClicked(record: StorageRecord<DeadlinesWrapper>)

}