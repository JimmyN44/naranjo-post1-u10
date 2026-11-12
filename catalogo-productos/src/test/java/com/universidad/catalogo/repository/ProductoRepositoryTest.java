package com.universidad.catalogo.repository;

import com.universidad.catalogo.model.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductoRepositoryTest {

    @Autowired
    ProductoRepository repo;

    @Autowired
    TestEntityManager em;

    @BeforeEach
    void setUp() {
        em.persistAndFlush(crear("Laptop", 1500.0, "ELECTRONICA"));
        em.persistAndFlush(crear("Cuaderno", 5.0, "PAPELERIA"));
    }

    @Test
    void findByCategoria_electronica_retornaUnProducto() {
        assertThat(repo.findByCategoria("ELECTRONICA")).hasSize(1)
                .extracting("nombre").containsExactly("Laptop");
    }

    @Test
    void findByCategoria_sinCoincidencias_retornaListaVacia() {
        assertThat(repo.findByCategoria("JUGUETERIA")).isEmpty();
    }

    @Test
    void findAll_trasSetUp_contieneSoloLosDosProductosDelTest() {
        assertThat(repo.findAll()).hasSize(2)
                .extracting("nombre").containsExactlyInAnyOrder("Laptop", "Cuaderno");
    }

    private Producto crear(String nombre, double precio, String categoria) {
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setPrecio(precio);
        p.setCategoria(categoria);
        return p;
    }
}
