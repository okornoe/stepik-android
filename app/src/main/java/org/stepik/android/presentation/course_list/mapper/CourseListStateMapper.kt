package org.stepik.android.presentation.course_list.mapper

import org.stepic.droid.util.PagedList
import org.stepic.droid.util.mutate
import org.stepic.droid.util.plus
import org.stepik.android.domain.course_list.model.CourseListItem
import org.stepik.android.domain.user_courses.model.UserCourse
import org.stepik.android.model.Course
import org.stepik.android.presentation.course_list.CourseListUserView
import org.stepik.android.presentation.course_list.CourseListView
import org.stepik.android.presentation.course_list.model.CourseListUserType
import javax.inject.Inject

class CourseListStateMapper
@Inject
constructor() {
    fun mapToLoadMoreState(courseListState: CourseListView.State.Content): CourseListView.State =
        CourseListView.State.Content(
            courseListDataItems = courseListState.courseListDataItems,
            courseListItems = courseListState.courseListItems + CourseListItem.PlaceHolder()
        )

    fun mapFromLoadMoreToSuccess(state: CourseListView.State, items: PagedList<CourseListItem.Data>): CourseListView.State {
        if (state !is CourseListView.State.Content) {
            return state
        }

        return CourseListView.State.Content(
            courseListDataItems = state.courseListDataItems + items,
            courseListItems = state.courseListItems.dropLastWhile { it is CourseListItem.PlaceHolder } + items
        )
    }

    fun mapFromLoadMoreToError(state: CourseListView.State): CourseListView.State {
        if (state !is CourseListView.State.Content) {
            return state
        }
        return CourseListView.State.Content(
            courseListDataItems = state.courseListDataItems,
            courseListItems = state.courseListItems.dropLastWhile { it is CourseListItem.PlaceHolder }
        )
    }

    fun mapToEnrollmentUpdateState(state: CourseListView.State, enrolledCourse: Course): CourseListView.State {
        if (state !is CourseListView.State.Content) {
            return state
        }

        val courseListItems = state.courseListDataItems.map {
            if (it.course.id == enrolledCourse.id) it.copy(
                course = enrolledCourse
            ) else it
        }

        return CourseListView.State.Content(
            courseListDataItems = PagedList(
                state.courseListDataItems.map {
                    if (it.course.id == enrolledCourse.id) it.copy(course = enrolledCourse) else it
                },
                state.courseListDataItems.page,
                state.courseListDataItems.hasNext,
                state.courseListDataItems.hasPrev
            ),
            courseListItems = if (state.courseListItems.last() is CourseListItem.PlaceHolder) {
                courseListItems + CourseListItem.PlaceHolder()
            } else {
                courseListItems
            }
        )
    }

    fun mapToContinueCourseUpdateState(state: CourseListView.State, continuedCourse: Course): CourseListView.State {
        if (state !is CourseListView.State.Content) {
            return state
        }

        val indexOf = state.courseListDataItems.indexOfFirst { it.id == continuedCourse.id }

        val courseListDataItems = state.courseListDataItems.mutate {
            val courseListItem = removeAt(indexOf)
            add(0, courseListItem)
        }

        val courseListItems = if (state.courseListItems.last() is CourseListItem.PlaceHolder) {
            courseListDataItems + CourseListItem.PlaceHolder()
        } else {
            courseListDataItems
        }

        return CourseListView.State.Content(
            courseListDataItems = PagedList(
                courseListDataItems,
                state.courseListDataItems.page,
                state.courseListDataItems.hasNext,
                state.courseListDataItems.hasPrev
            ),
            courseListItems = courseListItems
        )
    }

    fun mapUserCourseRemoveState(oldState: CourseListUserView.State.Data, oldCourseListState: CourseListView.State.Content, courseId: Long): CourseListUserView.State.Data {
        val userCoursesUpdate = oldState.userCourses.mapNotNull {
            if (it.course == courseId) {
                null
            } else {
                it
            }
        }
        val index = oldCourseListState.courseListDataItems
            .indexOfFirst { it.course.id == courseId }

        val newItems = oldCourseListState.courseListDataItems.mutate { removeAt(index) }

        val courseListViewState = if (newItems.isNotEmpty()) {
            oldCourseListState.copy(
                courseListDataItems = newItems,
                courseListItems = newItems
            )
        } else {
            CourseListView.State.Empty
        }

        return oldState.copy(
            userCourses = userCoursesUpdate,
            courseListViewState = courseListViewState
        )
    }

    fun mapRemoveCourseListItemState(state: CourseListView.State, courseId: Long): CourseListView.State {
        if (state !is CourseListView.State.Content) {
            return state
        }
        val newDataItems = state.courseListDataItems.filterNot { it.course.id == courseId }
        val newItems = state.courseListItems.filterNot { it is CourseListItem.Data && it.course.id == courseId }

        return if (newDataItems.isEmpty()) {
            CourseListView.State.Empty
        } else {
            state.copy(
                courseListDataItems = PagedList(newDataItems),
                courseListItems = newItems
            )
        }
    }

    fun mapEnrolledCourseListItemState(insertionIndex: Int, state: CourseListView.State, courseListItemEnrolled: CourseListItem.Data): CourseListView.State =
        when (state) {
            is CourseListView.State.Empty -> {
                CourseListView.State.Content(
                    courseListDataItems = PagedList(listOf(courseListItemEnrolled)),
                    courseListItems = listOf(courseListItemEnrolled)
                )
            }
            is CourseListView.State.Content -> {
                CourseListView.State.Content(
                    courseListDataItems = PagedList(
                        state.courseListDataItems.mutate { add(insertionIndex, courseListItemEnrolled) },
                        state.courseListDataItems.page,
                        state.courseListDataItems.hasNext,
                        state.courseListDataItems.hasPrev
                    ),
                    courseListItems = state.courseListItems.mutate {
                        if (this[insertionIndex] is CourseListItem.PlaceHolder) {
                            set(insertionIndex, courseListItemEnrolled)
                        } else {
                            add(insertionIndex, courseListItemEnrolled)
                        }
                    }
                )
            }
            else ->
                state
        }

    fun mapUserCourseOperationToState(userCourse: UserCourse, oldState: CourseListUserView.State.Data): CourseListUserView.State.Data {
        val isUserCourseFromThisList =
            oldState.courseListUserType == CourseListUserType.ALL && !userCourse.isArchived ||
                    oldState.courseListUserType == CourseListUserType.FAVORITE && userCourse.isFavorite ||
                    oldState.courseListUserType == CourseListUserType.ARCHIVED && userCourse.isArchived

        val comparator = Comparator<UserCourse> { s1, s2 ->
            val result = (s1.lastViewed?.time ?: 0).compareTo(s2.lastViewed?.time ?: 0)
            val value = if (result == 0) {
                s1.id.compareTo(s2.id)
            } else {
                result
            }
            -value
        }

        val index = oldState.userCourses.binarySearch(userCourse, comparator)

        return if (index > 0) {
            if (isUserCourseFromThisList) {
                val userCourses = oldState.userCourses.mutate { set(index, userCourse) }
                oldState.copy(
                    userCourses = userCourses
                )
            } else {
                val userCourses = oldState.userCourses.mutate { removeAt(index) }
                val courseListViewState = mapRemoveCourseListItemState(oldState.courseListViewState, userCourse.course)
                oldState.copy(
                    userCourses = userCourses,
                    courseListViewState = courseListViewState
                )
            }
        } else {
            val userCourses = oldState.userCourses.mutate { add(-(index + 1), userCourse) }
            if (isUserCourseFromThisList) {
                val courseListViewState = insertCourseListItemPlaceHolder(-(index + 1), userCourse.course, oldState.courseListViewState)
                oldState.copy(
                    userCourses = userCourses,
                    courseListViewState = courseListViewState
                )
            } else {
                oldState.copy(
                    userCourses = userCourses
                )
            }
        }
    }

    private fun insertCourseListItemPlaceHolder(insertionIndex: Int, courseId: Long, courseListViewState: CourseListView.State): CourseListView.State {
        if (courseListViewState is CourseListView.State.Empty) {
            CourseListView.State.Content(
                courseListDataItems = PagedList(emptyList()),
                courseListItems = listOf(CourseListItem.PlaceHolder(courseId = courseId))
            )
        }
        if (courseListViewState !is CourseListView.State.Content) {
            return courseListViewState
        }
        return courseListViewState.copy(
            courseListItems = courseListViewState.courseListItems.mutate { add(insertionIndex, CourseListItem.PlaceHolder(courseId)) }
        )
    }
}