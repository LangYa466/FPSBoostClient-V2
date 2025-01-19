package net.minecraft.client.renderer.vertex;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VertexFormat {
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<VertexFormatElement> elements;
    private int[] offsets;  // 改为原生数组提高访问效率
    private int nextOffset;
    private int colorElementOffset;
    private final List<Integer> uvOffsetsById;
    private int normalElementOffset;

    public VertexFormat(VertexFormat vertexFormatIn) {
        this();

        for (int i = 0; i < vertexFormatIn.getElementCount(); ++i) {
            this.addElement(vertexFormatIn.getElement(i));
        }

        this.nextOffset = vertexFormatIn.getNextOffset();
    }

    public VertexFormat() {
        this.elements = Lists.newArrayList();
        this.offsets = new int[16];  // 初始容量设置为16，避免频繁扩展
        this.nextOffset = 0;
        this.colorElementOffset = -1;
        this.uvOffsetsById = Lists.newArrayList();
        this.normalElementOffset = -1;
    }

    public void clear() {
        this.elements.clear();
        this.colorElementOffset = -1;
        this.uvOffsetsById.clear();
        this.normalElementOffset = -1;
        this.nextOffset = 0;
    }

    public VertexFormat addElement(VertexFormatElement element) {
        if (element.isPositionElement() && this.hasPosition()) {
            LOGGER.warn("VertexFormat error: Trying to add a position VertexFormatElement when one already exists, ignoring.");
            return this;
        }

        this.elements.add(element);
        ensureOffsetsCapacity(this.elements.size());  // 确保数组容量足够
        this.offsets[this.elements.size() - 1] = this.nextOffset;

        switch (element.getUsage()) {
            case NORMAL:
                this.normalElementOffset = this.nextOffset;
                break;

            case COLOR:
                this.colorElementOffset = this.nextOffset;
                break;

            case UV:
                this.uvOffsetsById.add(element.getIndex(), this.nextOffset);
                break;
        }

        this.nextOffset += element.getSize();
        return this;
    }

    private void ensureOffsetsCapacity(int size) {
        if (size > this.offsets.length) {
            int newCapacity = this.offsets.length * 2;
            this.offsets = Arrays.copyOf(this.offsets, newCapacity);
        }
    }

    public boolean hasNormal() {
        return this.normalElementOffset >= 0;
    }

    public int getNormalOffset() {
        return this.normalElementOffset;
    }

    public boolean hasColor() {
        return this.colorElementOffset >= 0;
    }

    public int getColorOffset() {
        return this.colorElementOffset;
    }

    public boolean hasUvOffset(int id) {
        return this.uvOffsetsById.size() - 1 >= id;
    }

    public int getUvOffsetById(int id) {
        return this.uvOffsetsById.get(id);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("format: ");
        sb.append(this.elements.size()).append(" elements: ");

        for (int i = 0; i < this.elements.size(); ++i) {
            sb.append(this.elements.get(i).toString());
            if (i != this.elements.size() - 1) {
                sb.append(" ");
            }
        }

        return sb.toString();
    }

    private boolean hasPosition() {
        for (VertexFormatElement element : this.elements) {
            if (element.isPositionElement()) {
                return true;
            }
        }
        return false;
    }

    public int getIntegerSize() {
        return this.getNextOffset() / 4;
    }

    public int getNextOffset() {
        return this.nextOffset;
    }

    public List<VertexFormatElement> getElements() {
        return this.elements;
    }

    public int getElementCount() {
        return this.elements.size();
    }

    public VertexFormatElement getElement(int index) {
        return this.elements.get(index);
    }

    public int getOffset(int index) {
        return this.offsets[index];
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof VertexFormat) {
            VertexFormat other = (VertexFormat) obj;
            return this.nextOffset == other.nextOffset &&
                    this.elements.equals(other.elements) &&
                    Arrays.equals(this.offsets, other.offsets);
        }
        return false;
    }

    public int hashCode() {
        int result = this.elements.hashCode();
        result = 31 * result + Arrays.hashCode(this.offsets);
        result = 31 * result + this.nextOffset;
        return result;
    }
}
