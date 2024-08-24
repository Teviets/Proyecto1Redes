package com.chatredes.di

import com.chatredes.data.XMPP.XMPPClient
import com.chatredes.data.constantes.Constantes
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    fun provideXMPPClient(): XMPPClient {
        return XMPPClient.getInstance(Constantes.server)
    }
}
