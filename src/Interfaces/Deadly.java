package Interfaces;

import objects.GameCharacter;
import objects.GameObject;

//Interface que vai ser aplicada aos objetos que matam os peixes
public interface Deadly {
    boolean killCondition(GameObject obj);
}
