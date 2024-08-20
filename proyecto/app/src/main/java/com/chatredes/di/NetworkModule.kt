package com.chatredes.di

import com.chatredes.data.XMPP.XMPPClient
import com.chatredes.data.constantes.Constantes
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn

@Module
@InstallIn()
class NetworkModule {

    @Provides
    fun provideXMPPClient(): XMPPClient {
        return XMPPClient(Constantes.server)
    }
}