package app.comboomPunkTsucht.CBPSEngine.ecs.component;

import com.artemis.Component;

/**
 * Renderer component for visual representation.
 * Holds references to mesh and material data.
 */
public class Renderer extends Component {
    public String meshName = "cube"; // Default cube
    public String materialName = "default";
    public boolean visible = true;
    public int renderLayer = 0; // For depth sorting

    /**
     * Set mesh to render.
     */
    public Renderer setMesh(String name) {
        this.meshName = name;
        return this;
    }

    /**
     * Set material to use.
     */
    public Renderer setMaterial(String name) {
        this.materialName = name;
        return this;
    }

    /**
     * Set visibility.
     */
    public Renderer setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    /**
     * Set render layer for sorting.
     */
    public Renderer setRenderLayer(int layer) {
        this.renderLayer = layer;
        return this;
    }
}
