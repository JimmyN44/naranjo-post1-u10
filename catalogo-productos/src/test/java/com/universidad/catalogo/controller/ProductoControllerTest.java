package com.universidad.catalogo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.universidad.catalogo.model.Producto;
import com.universidad.catalogo.service.ProductoService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ProductoService service;

    @Test
    void get_productoExiste_retorna200() throws Exception {
        Producto p = new Producto();
        p.setId(1L);
        p.setNombre("Laptop");
        p.setPrecio(1500.0);
        when(service.buscarPorId(1L)).thenReturn(p);

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Laptop"))
                .andExpect(jsonPath("$.precio").value(1500.0));
    }

    @Test
    void get_noExiste_retorna404() throws Exception {
        when(service.buscarPorId(99L)).thenThrow(new EntityNotFoundException("no encontrado"));

        mockMvc.perform(get("/api/productos/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("no encontrado"));
    }

    @Test
    void post_conDatosValidos_retorna201() throws Exception {
        Producto entrada = new Producto();
        entrada.setNombre("Mouse");
        entrada.setPrecio(50.0);
        Producto guardado = new Producto();
        guardado.setId(1L);
        guardado.setNombre("Mouse");
        guardado.setPrecio(50.0);
        when(service.crear(any())).thenReturn(guardado);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Mouse"));
    }

    @Test
    void post_conPrecioInvalido_retorna400() throws Exception {
        Producto entrada = new Producto();
        entrada.setNombre("Auricular");
        entrada.setPrecio(-5.0);
        when(service.crear(any())).thenThrow(new IllegalArgumentException("El precio debe ser mayor a cero"));

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El precio debe ser mayor a cero"));
    }
}
