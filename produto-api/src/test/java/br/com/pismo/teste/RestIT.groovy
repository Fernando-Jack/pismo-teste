package br.com.pismo.teste;

import static com.jayway.restassured.RestAssured.delete
import static com.jayway.restassured.RestAssured.get
import static com.jayway.restassured.RestAssured.given
import static org.assertj.core.api.Assertions.assertThat
import static org.hamcrest.Matchers.equalTo

import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test

import br.com.pismo.produto.entity.Produto

import com.jayway.restassured.RestAssured
import com.jayway.restassured.http.ContentType

public class RestIT {

  @BeforeClass
  public static void configureRestAssured() {
    RestAssured.baseURI = "http://localhost"
    RestAssured.port = Integer.getInteger("http.port", 8080)
  }

  @AfterClass
  public static void unconfigureRestAssured() {
    RestAssured.reset();
  }  
  
  @Test
  public void shouldBeAbleToAddAndEditAProduct() {
	  
	def productDetails = [name:'Wii', price:500, category:'wii']

	Produto produto = given()
		.contentType(ContentType.JSON)		
		.body(productDetails)
		.request()
		.post("/api/v1/produto")
		.thenReturn().as(Produto.class)
	assertThat(produto.getName()).isEqualToIgnoringCase("Wii")
	assertThat(produto.getPrice()).isEqualTo(500)
	assertThat(produto.getId()).isNotZero()
	
	get("/api/v1/produto/" + produto.getId()).then()
		.assertThat()
		.statusCode(200)		
		.body("name", equalTo("Wii"))
		.body("price", equalTo(500))
		.body("id", equalTo(produto.getId()))
	
	productDetails = [id:produto.getId(), name:'Wiiii', price:550, category:'wii']
		
	given()
		.contentType(ContentType.JSON)		
		.body(productDetails)
		.put("/api/v1/produto/" + produto.getId())
		
	get("/api/v1/produto/" + produto.getId()).then()
		.assertThat()
		.statusCode(200)
		.contentType(ContentType.JSON)
		.body("name", equalTo("Wiiii"))
		.body("price", equalTo(550))
		.body("id", equalTo(produto.getId()))
  }
  
  @Test
  public void shouldBeAbleToAddAndDeleteAProduct() {
	  
	def productDetails = [name:'Wii', price:500, category:'wii']

    Produto produto = given()
        .contentType(ContentType.JSON)
		.body(productDetails)
		.request()
		.post("/api/v1/produto")
		.thenReturn().as(Produto.class)
    assertThat(produto.getName()).isEqualToIgnoringCase("Wii")
    assertThat(produto.getPrice()).isEqualTo(500)
    assertThat(produto.getId()).isNotZero()	
	
    get("/api/v1/produto/" + produto.getId()).then()
        .assertThat()
        .statusCode(200)		
        .body("name", equalTo("Wii"))
        .body("price", equalTo(500))
        .body("id", equalTo(produto.getId()))    
	
    delete("/api/v1/produto/" + produto.getId()).then().assertThat().statusCode(204)    

    get("/api/v1/produto/" + produto.getId()).then()
        .assertThat()
        .statusCode(404)
  }

}