package br.com.pismo.produto

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.Router
import br.com.pismo.produto.api.InventarioVerticle
import br.com.pismo.produto.api.ProdutoVerticle
import br.com.pismo.produto.repository.InventarioRepositoryInMemory
import br.com.pismo.produto.repository.ProdutoRepositoryInMemory
import br.com.pismo.produto.service.InventarioService
import br.com.pismo.produto.service.ProdutoService

public class AppServer extends AbstractVerticle {	

	@Override
	public void start(Future<Void> fut) {
		
		def router = Router.router(vertx)
		
		def ProdutoVerticle produtoVerticle =			
			new ProdutoVerticle(
				router,
				new ProdutoService(
					new ProdutoRepositoryInMemory()))
		
		def InventarioVerticle inventarioVerticle =
			new InventarioVerticle(
				router,
				new InventarioService(
					new InventarioRepositoryInMemory()))
		
		produtoVerticle.registerInRouter(router);
		inventarioVerticle.registerInRouter(router);
		
		defineResponseForBaseURL(router)
		createServerInstace(router, fut)
		
	}

	private defineResponseForBaseURL(router) {
		router.route('/').handler( {routingContext ->
			HttpServerResponse response = routingContext.response()
			response
					.putHeader("content-type", "text/html")
					.end("<h1>Produto API</h1>")
		})
	}

	private createServerInstace(Router router, Future fut) {
		vertx
			.createHttpServer()
			.requestHandler(router.&accept)
			.listen(				
				config().getInteger("http.port", 8080),
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