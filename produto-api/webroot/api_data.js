define({ "api": [
  {
    "type": "post",
    "url": "/produto/login",
    "title": "",
    "group": "Autentica__o",
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "String",
            "optional": false,
            "field": "status",
            "description": "<p>Retorna um token de acesso</p>"
          }
        ]
      }
    },
    "version": "0.0.0",
    "filename": "src/main/java/br/com/pismo/produto/AppServer.groovy",
    "groupTitle": "Autentica__o",
    "name": "PostProdutoLogin"
  },
  {
    "type": "get",
    "url": "/produto/:produto-id/inventario",
    "title": "",
    "group": "Inventario",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "Number",
            "optional": false,
            "field": "produto",
            "description": "<p>unique ID.</p>"
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "String",
            "optional": false,
            "field": "status",
            "description": "<p>Todo o inventario disponível retornado</p>"
          }
        ]
      }
    },
    "version": "0.0.0",
    "filename": "src/main/java/br/com/pismo/produto/api/InventarioAPI.groovy",
    "groupTitle": "Inventario",
    "name": "GetProdutoProdutoIdInventario"
  },
  {
    "type": "post",
    "url": "/produto/:produto-id/inventario",
    "title": "",
    "group": "Inventario",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "Number",
            "optional": false,
            "field": "produto",
            "description": "<p>unique ID.</p>"
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "String",
            "optional": false,
            "field": "status",
            "description": "<p>Adiciona um item ao inventario do produto</p>"
          }
        ]
      }
    },
    "version": "0.0.0",
    "filename": "src/main/java/br/com/pismo/produto/api/InventarioAPI.groovy",
    "groupTitle": "Inventario",
    "name": "PostProdutoProdutoIdInventario"
  },
  {
    "type": "put",
    "url": "/produto/:produto-id/inventario",
    "title": "",
    "group": "Inventario",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "Number",
            "optional": false,
            "field": "produto",
            "description": "<p>unique ID.</p>"
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "String",
            "optional": false,
            "field": "status",
            "description": "<p>Consome um item do inventario do produto</p>"
          }
        ]
      }
    },
    "version": "0.0.0",
    "filename": "src/main/java/br/com/pismo/produto/api/InventarioAPI.groovy",
    "groupTitle": "Inventario",
    "name": "PutProdutoProdutoIdInventario"
  },
  {
    "type": "delete",
    "url": "/produto/:id",
    "title": "",
    "group": "Produto",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "Number",
            "optional": false,
            "field": "produto",
            "description": "<p>unique ID.</p>"
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "String",
            "optional": false,
            "field": "status",
            "description": "<p>Remove o produto especificado</p>"
          }
        ]
      }
    },
    "version": "0.0.0",
    "filename": "src/main/java/br/com/pismo/produto/api/ProdutoAPI.groovy",
    "groupTitle": "Produto",
    "name": "DeleteProdutoId"
  },
  {
    "type": "get",
    "url": "/produto/",
    "title": "",
    "group": "Produto",
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "String",
            "optional": false,
            "field": "status",
            "description": "<p>Retorna todos os produtos disponiveis</p>"
          }
        ]
      }
    },
    "version": "0.0.0",
    "filename": "src/main/java/br/com/pismo/produto/api/ProdutoAPI.groovy",
    "groupTitle": "Produto",
    "name": "GetProduto"
  },
  {
    "type": "get",
    "url": "/produto/:id",
    "title": "",
    "group": "Produto",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "Number",
            "optional": false,
            "field": "produto",
            "description": "<p>unique ID.</p>"
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "String",
            "optional": false,
            "field": "status",
            "description": "<p>Retorna o produto especificado</p>"
          }
        ]
      }
    },
    "version": "0.0.0",
    "filename": "src/main/java/br/com/pismo/produto/api/ProdutoAPI.groovy",
    "groupTitle": "Produto",
    "name": "GetProdutoId"
  },
  {
    "type": "post",
    "url": "/produto/",
    "title": "",
    "group": "Produto",
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "String",
            "optional": false,
            "field": "status",
            "description": "<p>Adiciona um novo produto</p>"
          }
        ]
      }
    },
    "version": "0.0.0",
    "filename": "src/main/java/br/com/pismo/produto/api/ProdutoAPI.groovy",
    "groupTitle": "Produto",
    "name": "PostProduto"
  },
  {
    "type": "put",
    "url": "/produto/:id",
    "title": "",
    "group": "Produto",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "Number",
            "optional": false,
            "field": "produto",
            "description": "<p>unique ID.</p>"
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "String",
            "optional": false,
            "field": "status",
            "description": "<p>Atualiza o produto especificado</p>"
          }
        ]
      }
    },
    "version": "0.0.0",
    "filename": "src/main/java/br/com/pismo/produto/api/ProdutoAPI.groovy",
    "groupTitle": "Produto",
    "name": "PutProdutoId"
  }
] });
