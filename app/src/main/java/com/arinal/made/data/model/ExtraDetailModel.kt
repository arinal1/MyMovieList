package com.arinal.made.data.model

import android.os.Parcel
import android.os.Parcelable

data class ExtraDetailModel(val categoryId: Int, val dataId: Int) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        parcel?.writeInt(categoryId)
        parcel?.writeInt(dataId)
    }

    override fun describeContents(): Int = Parcelable.CONTENTS_FILE_DESCRIPTOR

    companion object CREATOR : Parcelable.Creator<ExtraDetailModel> {
        override fun createFromParcel(parcel: Parcel): ExtraDetailModel {
            return ExtraDetailModel(parcel)
        }

        override fun newArray(size: Int): Array<ExtraDetailModel?> {
            return arrayOfNulls(size)
        }
    }

}