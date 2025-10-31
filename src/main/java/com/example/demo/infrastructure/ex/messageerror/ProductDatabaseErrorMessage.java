package com.example.demo.infrastructure.ex.messageerror;

public final class ProductDatabaseErrorMessage {
    private ProductDatabaseErrorMessage() {}

    public static final String CREATE_ERROR = "Error al crear el producto";
    public static final String  FIND_ALL_ERROR= "Error al obtener todos los productos";
    public static final String FIND_BY_ID_ERROR = "Error al obtener el producto por ID";
    public static final String UPDATE_ERROR = "Error al actualizar el producto";
    public static final String DELETE_ERROR = "Error al eliminar el producto";
    public static final String ADD_STOCK_ERROR = "Error al agregar stock al producto";
    public static final String REMOVE_STOCK_ERROR = "Error al remover stock del producto";
}
