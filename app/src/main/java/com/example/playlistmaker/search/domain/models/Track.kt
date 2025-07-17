package com.example.playlistmaker.search.domain.models

import android.os.Parcel
import android.os.Parcelable

data class Track(
    val trackId: Long,
    var isFavorite: Boolean = false,
    val trackName: String?,
    val artistName: String,
    val trackTime: String?,
    val trackTimeMillis: Long,
    val artworkUrl100: String?,
    val collectionName: String?,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readBoolean(),
        parcel.readString(),
        parcel.readString()!!,
        parcel.readString(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(trackId)
        dest.writeBoolean(isFavorite)
        dest.writeString(trackName)
        dest.writeString(artistName)
        dest.writeString(trackTime)
        dest.writeLong(trackTimeMillis)
        dest.writeString(artworkUrl100)
        dest.writeString(collectionName)
        dest.writeString(releaseDate)
        dest.writeString(primaryGenreName)
        dest.writeString(country)
        dest.writeString(previewUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Track> {
        override fun createFromParcel(parcel: Parcel): Track {
            return Track(parcel)
        }

        override fun newArray(size: Int): Array<Track?> {
            return arrayOfNulls(size)
        }
    }
}
