package graphvisualisation.graphics.objects;

public interface HoverAction<T> {
    void handle(T hoveredObject, boolean isHovering);
}
