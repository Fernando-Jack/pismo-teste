package br.com.pismo.compra

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.Router
import br.com.pismo.compra.api.CompraAPI
import br.com.pismo.compra.integration.InventarioRestIntegration
import br.com.pismo.compra.repository.CompraRepositoryInMemory
import br.com.pismo.compra.service.CompraService

public class AppServer extends AbstractVerticle {

	@Override
	public void start(Future<Void> fut) {

		def router = Router.router(vertx)
		def eventBus = vertx.eventBus()

		def CompraAPI compraAPI = new CompraAPI(router, eventBus)
		compraAPI.registerInRouter(router)

		defineResponseForBaseURL(router)
		createServerInstace(router, fut)
	}

	private defineResponseForBaseURL(router) {
		router.route('/').handler( {routingContext ->
			HttpServerResponse response = routingContext.response()
			response
					.putHeader("content-type", "text/html")
					.end("<h1>Compras API</h1>")
		})
	}

	private createServerInstace(Router router, Future fut) {
		vertx
				.createHttpServer()
				.requestHandler(router.&accept)
				.listen(
				//config().getInteger("http.port", 8081),
					System.getenv('PORT') as int, '0.0.0.0' ,
				{result ->
					if (result.succeeded()) {
						fut.complete()
					} else {
						fut.fail(result.cause())
					}
				}
				)
	}
}