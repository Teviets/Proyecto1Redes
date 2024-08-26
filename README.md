# Proyecto 1 Redes

## OverView

This project is an Android Application designed for managing task, communication, and colabaration within a specific doman. This application is built using kotlin and leverages mordern Android development tools such as hilt for dependency injections, coroutins for asynchronous programing and XMPP for real-time communication


## Features

<ol>
    <li><b>Real-Time Messaging:</b> Cmmunicate with other users via XMPP</li>
    <li><b>COntact Management:</b> Add contacts witin the app</li>
    <li><b>Custom UI: </b> Using Material Design 3 syling with custom themes</li>
    <li><b>Dependency Injection: </b>Managed using Hilt for efficient and scalable architecture</li>
    <li><b>Coroutines and Flow: </b>Allows to get and shor more eseally the getting and sending methods</li>
</ol>


## Technologies Used

<ol>
    <li><b>Lenguage:</b> Kotlin</li>
    <li><b>Architecture:</b> MVVM (Model-View-ViewModel)</li>
    <li><b>Dependency Injection: </b>Hilt</li>
    <li><b>Networking and Messaging: </b>XMPP (smack dependency)</li>
    <li><b>Asynchronous Programming: </b> Kotlin Coroutines and Flow</li>
    <li><b>UI: </b>Material Design 3</li>
    <li><b>Build system: </b>Gradle</li>
</ol>

## Project Structure

This project is organized into the following major modules

<ol>
    <li><b>Data:</b>
        <ul>
            <li><b>Constants:</b> Here is the constant values of the project </li>
            <li><b>XMPP:</b> Here we have the client similar to a repository to connect to XMPP using sigleton </li>
        </ul>
    </li>
    <li><b>Di (Dependency Injections):</b>
        <ul>
            <li><b>Modules:</b> Here we have the module to allow the different classes to inject into others </li>
        </ul>
    </li>
    <li><b>Domain:</b>
        <ul>
            <li><b>Models:</b> Here we have al the data classes to made the main structures and manage better the UI connecting to the backend </li>
            <li><b>UseCases:</b> Here we have the main connection between the XMPPClient and the viewModels  </li>
        </ul>
    </li>
    <li><b>UI:</b>
        <ul>
            <li><b>Activities:</b> Here we have the main activity that contains al the navigation between the fragments</li>
            <li><b>Adapter:</b> Here we have the adapter to fill the items into recyclerview whe we have the data </li>
            <li><b>Dialogs:</b> We made two custom dialogs and here is the logic behind the custom dialogs</li>
            <li><b>Fragments(Pantallas):</b> Here we have the logic of each one of the fragments </li>
            <li><b>ViewModels:</b> Here is allocated the viewModels to get the information obtain on the differents usecases </li>
        </ul>
    </li>
    <li><b>Res:</b>
        <ul>
            <li><b>drawable:</b> Here we have all the pictures or icons used on the app</li>
            <li><b>Layout:</b> Here we have all the desings for items of the recyclerview, fragments or dialogs</li>
            <li><b>Menu:</b> Here we have the differents menus for the toolbars that we use on the app</li>
            <li><b>Navigation:</b> Here we have map to navigate into the app</li>
            <li><b>Values:</b> Here we have al de constans to use into de XML structure </li>
        </ul>
    </li>
    <li><b>Gradle:</b>
        <ul>
            <li><b>Gradle App:</b> Here is where we stablish all de dependencies that the project needs to work at the level of the app</li>
            <li><b>Gradle project:</b> Here is where we configure mani dependencies at level of the project</li>
        </ul>
    </li>
</ol>

## Installation

### Prerequisites

- **Android Studio:** Make sure you have the latest version of Android Studio installed.
- **Gradle:** Ensure you are using the correct version specified in the project.

## Clone repository

```bash
git clone https://github.com/Teviets/Proyecto1Redes
```

## Setting Up the project

1. Open the project in Android Studio.
2. Sync the project with Gradle files.
3. Create a file local.properties and specify the path to your Android SDK.
```properties
sdk.dir=/path/to/android/sdk
```
4. Configure your XMPP server details in the  'constantes.kt'

## Run the project

- Connect yout device or prepare the emulator
- Run the app on Android Studio

- Also you can install the app with the executable APK file

## Usage

### Messaging

1. Navigate with the card of the contact
2. You can write to the other person

### Adding Contacts

1. Navigate to the screen with the contact list
2. Click the add button
3. Fill the information
4. Restart the app to update


## Code Examples

### Send function
```Kotlin
fun sendMessage(message: Message) {
    val stanza = connection?.stanzaFactory
        ?.buildMessageStanza()
        ?.to(message.receiver)
        ?.from(message.sender)
        ?.setBody(message.message)
        ?.build()

    stanza?.let {
        connection?.sendStanza(it)
        receivedMessages.add(message)
        notifyMessageSent(message)
        Log.d("XMPPClient", "Message sent: $message")
    }
}
```

### Observing contacts

```Kotlin
fun getContacts() {
    _status.value = StatusApp.Loading
    viewModelScope.launch {
        try {
            val contacts = contactsUseCase.getContacts()
            if (contacts.isNotEmpty()){
                _contacts.value = contacts
                _status.value = StatusApp.Default
            }else{
                _status.value = StatusApp.Error("No hay contactos")
            }
        }catch (e: Exception){
            e.printStackTrace()
            Log.e("Error de get contacts", e.toString())
            _status.value = StatusApp.Error("Error al obtener los contactos")
        }
    }
}
```

```Kotlin
private fun setUpRecyclerView() {
    if(!this::adapter.isInitialized){
        adapter = ContactAdapter(contacts, this)
        binding.apply {
            recyclerChat.layoutManager = LinearLayoutManager(requireContext())
            recyclerChat.setHasFixedSize(true)
            recyclerChat.adapter = adapter
        }
    }else{
        adapter.notifyDataSetChanged()
    }
}
```

## Dependency Injection (Hilt)

All dependencies are managed by Hilt. Ensure you annotate your Application class with @HiltAndroidApp and use @Inject where necessary and create the modules necessary.

In the case of this project i made 3 modules to be injected:

1. AppModule
2. ContextModule
3. NetworkModule


## Errors

- Dint get the messages that other people send to the user
- If the user fill the login form with wrong information the user needs to restart the app
- To visualze the new contacts the user needs to restart the app 

## Contact

- Name: Sebastian Estrada Tuch
- Email: est21405@uvg.edu.gt
- Carnet: 21405
