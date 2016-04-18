package io.vertx.blog.first 

import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.unit.Async
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import br.com.pismo.compra.AppServer
import br.com.pismo.compra.entity.Compra

@RunWith(VertxUnitRunner.class)
public class CompraVerticleAPITest {

	private Vertx vertx
	private int port

	@Before
	public void setUp(TestContext context) throws IOException {
		vertx = Vertx.vertx()		
	}

	@After
	public void tearDown(TestContext context) {
		vertx.close(context.asyncAssertSuccess())
	}

	@Test
	public void shouldBeAbleToBuyAProduct(TestContext context) {
		
	}
}