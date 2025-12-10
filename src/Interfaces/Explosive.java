package Interfaces;

import objects.GameObject;

import java.util.List;

public interface Explosive {

    //Faz a explosão (animações, blood, remover objetos, som, etc.)
    void explode(GameObject b);

    //Condição que decide quando deve explodir (colisão, queda, etc.)
    void explosionTrigger();

    //Cria o padrão de Blood que queremos
    List<GameObject> createBloodPattern(GameObject obj);
}
