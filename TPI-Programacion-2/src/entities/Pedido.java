/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

import java.time.LocalDate;

/**
 *
 * @author gonza
 */
public class Pedido extends Base<Integer> {

    private String numero;
    private LocalDate fecha;
    private String clienteNombre;
    private double total;
    private EstadoDePedido estado;
    private Envio envio;

    public Pedido(int id, String numero, LocalDate fecha, String clienteNombre, double total, EstadoDePedido estado, Envio envio) {
        super(id);
        this.numero = numero;
        this.fecha = fecha;
        this.clienteNombre = clienteNombre;
        this.total = total;
        this.estado = estado;
        this.envio = envio;
    }

    public Pedido(String numero, LocalDate fecha, String clienteNombre, double total, EstadoDePedido estado, Envio envio) {
        this.numero = numero;
        this.fecha = fecha;
        this.clienteNombre = clienteNombre;
        this.total = total;
        this.estado = estado;
        this.envio = envio;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    //Getters & Setters
    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public EstadoDePedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoDePedido estado) {
        this.estado = estado;
    }

    public Envio getEnvio() {
        return envio;
    }

    public void setEnvio(Envio envio) {
        this.envio = envio;
    }

    //Metodos
    @Override
    public String toString() {
        return "----- PEDIDO -----\n"
                + "ID:              " + id + "\n"
                + "Eliminado:       " + eliminado + "\n"
                + "Número:          " + numero + "\n"
                + "Fecha:           " + fecha + "\n"
                + "Cliente:         " + clienteNombre + "\n"
                + "Estado:          " + estado + "\n"
                + "Envio:           " + envio + "\n";

    }

}
