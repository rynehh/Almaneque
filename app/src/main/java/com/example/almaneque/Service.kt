package com.example.almaneque

// Esta clase actua como singletone para poder acceder a la api
// desde cualquier parte del proyecto
// Usa la parte de API SERVICE del proyecto junto con la parte de REST ENGINE para
// crear una sola instancia de la api
// Punto sobre patron de diseño
class Service {
    companion object {
        fun getApiService(): ApiService {
            return RestEngine.getRestEngine().create(ApiService::class.java)
        }
    }
}