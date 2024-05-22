package edu.uoc.rng2.repositories

import edu.uoc.rng2.repositories.datasources.IMusicInDiskDataSource
import edu.uoc.rng2.repositories.datasources.MusicInDiskDataSource
import javax.inject.Inject

class MusicLibraryRepository @Inject constructor(
    private val musicInDiskDataSource: MusicInDiskDataSource,
) : IMusicInDiskDataSource by musicInDiskDataSource