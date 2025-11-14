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
public class Envio extends Base <Long> {
 
    private String tracking;
    private EmpresaDeEnvio empresa;
    private TipoDeEnvio tipo;
    private double costo;
    private LocalDate fechaDespacho;
    private LocalDate fechaEstimada;
    private EstadoDeEnvio estado;

    //Constructores
    public Envio() {
    }
    
    public Envio(Long id ,String tracking, EmpresaDeEnvio empresa, TipoDeEnvio tipo, double costo, LocalDate fechaDespacho, LocalDate fechaEstimada, EstadoDeEnvio estado) {
        super(id);
        this.tracking = tracking;
        this.empresa = empresa;
        this.tipo = tipo;
        this.costo = costo;
        this.fechaDespacho = fechaDespacho;
        this.fechaEstimada = fechaEstimada;
        this.estado = estado;
    }

    //Getters & Setters
    public String getTracking() {
        return tracking;
    }

    public void setTracking(String tracking) {
        this.tracking = tracking;
    }

    public EmpresaDeEnvio getEmpresa() {
        return empresa;
    }

    public void setEmpresa(EmpresaDeEnvio empresa) {
        this.empresa = empresa;
    }

    public TipoDeEnvio getTipo() {
        return tipo;
    }

    public void setTipo(TipoDeEnvio tipo) {
        this.tipo = tipo;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public LocalDate getFechaDespacho() {
        return fechaDespacho;
    }

    public void setFechaDespacho(LocalDate fechaDespacho) {
        this.fechaDespacho = fechaDespacho;
    }

    public LocalDate getFechaEstimada() {
        return fechaEstimada;
    }

    public void setFechaEstimada(LocalDate fechaEstimada) {
        this.fechaEstimada = fechaEstimada;
    }

    public EstadoDeEnvio getEstado() {
        return estado;
    }

    public void setEstado(EstadoDeEnvio estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
         return  "----- ENVÍO -----\n" +
            "ID:              " + id + "\n" +
            "Tracking:        " + tracking + "\n" +
            "Empresa:         " + empresa + "\n" +
            "Tipo:            " + tipo + "\n" +
            "Costo:           " + costo + "\n" +
            "Fecha despacho:  " + fechaDespacho + "\n" +
            "Fecha estimada:  " + fechaEstimada + "\n" +
            "Estado:          " + estado + "\n" +
            "Eliminado:       " + eliminado + "\n";
    }

    
}
