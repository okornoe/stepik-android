package org.stepik.android.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import org.stepik.android.model.util.readDate
import java.util.Date

class Certificate(
        val id: Long,
        val user: Long,
        val course: Long,

        @SerializedName("issue_date")
        val issueDate: Date? = null,
        @SerializedName("update_date")
        val updateDate: Date? = null,

        val grade: String? = null,
        val type: Type? = null,
        val url: String? = null
) : Parcelable {

    /*
    Add new in the end, because serialization depends on order.
    */
    enum class Type {
        @SerializedName("regular")
        REGULAR,
        @SerializedName("distinction")
        DISTINCTION
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(this.id)
        dest.writeString(this.grade)
    }

    override fun describeContents(): Int = 0
    override fun toString(): String {
        return "Certificate(id=$id, user=$user, course=$course, issueDate=$issueDate, updateDate=$updateDate, grade=$grade, type=$type, url=$url)"
    }

    companion object CREATOR : Parcelable.Creator<Certificate> {
        override fun createFromParcel(source: Parcel): Certificate =
            Certificate(
                source.readLong(),
                source.readLong(),
                source.readLong(),
                source.readDate(),
                source.readDate(),
                source.readString(),
                getCertificateTypeByParcel(source),
                source.readString()
            )

        override fun newArray(size: Int): Array<Certificate?> = arrayOfNulls(size)

        private fun getCertificateTypeByParcel(input: Parcel): Certificate.Type? {
            val temp = input.readInt()
            val localValues = Certificate.Type.values()
            return if (temp >= 0 && temp < localValues.size) {
                localValues[temp]
            } else {
                null
            }
        }
    }


}