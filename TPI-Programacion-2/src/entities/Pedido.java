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
public class Pedido extends Base<Long> {

    private String numero;
    private LocalDate fecha;
    private String clienteNombre;
    private EstadoDePedido estado;
    private Envio envio;
    private double total;

    public Pedido(Long id, String numero, LocalDate fecha, String clienteNombre, EstadoDePedido estado, Envio envio, double total) {
        super(id);
        this.numero = numero;
        this.fecha = fecha;
        this.clienteNombre = clienteNombre;
        this.estado = estado;
        this.envio = envio;
        this.total = total;
    }

    public Pedido() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
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

    public void setTotal(double total) {
        this.total = total;
    }

    public double getTotal() {
        return total;
    }

}
