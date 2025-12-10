package com.example.descarteintegrador.ui.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.widget.Toast

object IntentUtils {

    private const val GOOGLE_MAPS_PACKAGE_NAME = "com.google.android.apps.maps"

    //Verifica se o dispositivo tem uma conexão ativa com a internet
    private fun networkEstaDisponivel(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    //Verifica se o aplicativo do Google Maps está instalado no dispositivo
    private fun googleMapsEstaInstalado(context: Context): Boolean {
        return try {
            context.packageManager.getPackageInfo(GOOGLE_MAPS_PACKAGE_NAME, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    /*
      Inicia uma pesquisa no Google Maps com o endereço fornecido.
      Abre o aplicativo do Google Maps se estiver instalado, caso contrário, abre o navegador.
      Exibe um Toast se não houver conexão com a internet.
     */
    fun openMap(context: Context, address: String) {
        if (!networkEstaDisponivel(context)) {
            Toast.makeText(context, "Sem conexão com a internet", Toast.LENGTH_SHORT).show()
            return
        }

        val mapUri = Uri.parse("http://maps.google.com/maps?q=${Uri.encode(address)}")
        val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)

        if (googleMapsEstaInstalado(context)) {
            mapIntent.setPackage(GOOGLE_MAPS_PACKAGE_NAME)
        }

        // Garante que há um aplicativo para lidar com o Intent antes de iniciá-lo
        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            Toast.makeText(context, "Nenhum aplicativo de mapa encontrado", Toast.LENGTH_SHORT).show()
        }
    }
}
