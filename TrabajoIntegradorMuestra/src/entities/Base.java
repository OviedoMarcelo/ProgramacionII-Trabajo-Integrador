/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entities;

/**
 *
 * @author gonza
 */
public abstract class Base <T> {
    protected T id;
    protected boolean eliminado;

    public Base() {
        this.eliminado = false; // por defecto NO eliminado
    }

    public Base(T id) {
        this.id = id;
        this.eliminado = false;
    }

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }
    
    
    
    
}
