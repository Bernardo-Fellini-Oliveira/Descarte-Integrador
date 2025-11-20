package com.example.descarteintegrador

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityInstrumentedTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testCsvDataLoading() {
        activityRule.scenario.onActivity { activity ->
            // Verifica se a lista não está vazia
            assertNotNull(activity.locaisColetaList)
            assertTrue(activity.locaisColetaList.isNotEmpty())

            // Assumindo que seu CSV tem pelo menos 300 linhas de dados (após o cabeçalho)
            // Ajuste este número de acordo com o tamanho real do seu CSV
            // Deixarei um valor de 300, caso não tenha esta quantidade, altere manualmente. Caso seja maior, altere também.
            assertEquals(300, activity.locaisColetaList.size)

            // Exemplo de verificação de um item específico (o primeiro item na lista)
            // Ajuste estes valores de acordo com a primeira linha de dados REAL do seu CSV
            val firstItem = activity.locaisColetaList[0]
            assertEquals("UDC ZONA NORTE - JARDIM LEOPOLDINA", firstItem.nome)
            assertEquals("Rua Orlando Feuerschutte, 1000", firstItem.endereco)
            assertEquals(-30.0076214, firstItem.lat, 0.0001) // Usar delta para Doubles
            assertEquals(-51.0968953, firstItem.lng, 0.0001)
            assertEquals("ecoponto", firstItem.tipo)
        }
    }
}