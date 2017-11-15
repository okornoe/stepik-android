package org.stepic.droid.di.course_list

import dagger.Subcomponent
import org.stepic.droid.base.CoursesDatabaseFragmentBase
import org.stepic.droid.ui.fragments.*

@CourseListScope
@Subcomponent(modules = arrayOf(CourseListModule::class))
interface CourseListComponent {

    @Subcomponent.Builder
    interface Builder {
        fun build(): CourseListComponent
    }


    fun inject(fragment: CoursesDatabaseFragmentBase)

    fun inject(fragment: CourseListFragmentBase)

    fun inject(fragment: CourseSearchFragment)

    fun inject(fragment: CoursesCarouselFragment)

    fun inject(fragment: FastContinueFragment)
}
