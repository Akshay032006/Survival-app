package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ai.AiOrchestrator
import com.example.ai.AiResponseResult
import com.example.data.repository.SurvivalRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Survival", appName)
  }

  @Test
  fun `orchestrator handles multi request query`() = runBlocking {
    val repository = SurvivalRepository()
    val orchestrator = AiOrchestrator(repository)
    val result = orchestrator.processQuery("I need a cheap gym, supermarket, good food and a place to stay near me.")
    assertTrue(result is AiResponseResult.MultiRequestPlan)
    val plan = (result as AiResponseResult.MultiRequestPlan).plan
    assertEquals(4, plan.steps.size)
  }

  @Test
  fun `orchestrator handles single category query`() = runBlocking {
    val repository = SurvivalRepository()
    val orchestrator = AiOrchestrator(repository)
    val result = orchestrator.processQuery("cheap food near me")
    assertTrue(result is AiResponseResult.SingleCategoryResult)
    val single = result as AiResponseResult.SingleCategoryResult
    assertNotNull(single.bestMatch)
  }
}
