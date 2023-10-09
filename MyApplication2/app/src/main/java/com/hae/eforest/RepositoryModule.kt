package com.hae.eforest

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton



@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideRemoteRepository(
        remoteDataSource: RemoteDataSourceImpl
    ): RemoteRepository {
        return RemoteRepositoryImpl(
            remoteDataSource
        )
    }

    @Provides
    @Singleton
    fun provideSumSubRepository(
        sumSubDataSource: SumSubDataSourceImpl
    ): SumSubRepository {
        return SumSubRepositoryImpl(
            sumSubDataSource
        )
    }
}