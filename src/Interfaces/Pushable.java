package Interfaces;

import objects.GameObject;
import pt.iscte.poo.utils.Vector2D;

public interface Pushable {
    boolean applyPush(Vector2D dir, GameObject obj);
}
