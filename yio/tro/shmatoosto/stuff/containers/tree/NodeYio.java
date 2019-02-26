package yio.tro.shmatoosto.stuff.containers.tree;

import yio.tro.shmatoosto.Yio;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;


public class NodeYio<KeyType, ValueType> {

    KeyType key;
    ValueType value;
    NodeYio<KeyType, ValueType> parent;
    ArrayList<NodeYio<KeyType, ValueType>> children;
    public int id;


    public NodeYio(KeyType key, ValueType value, NodeYio<KeyType, ValueType> parent) {
        this.key = key;
        this.value = value;
        this.parent = parent;
        children = new ArrayList<>();
    }


    public NodeYio<KeyType, ValueType> getParent() {
        return parent;
    }


    public NodeYio<KeyType, ValueType> getChild(KeyType key) {
        for (NodeYio<KeyType, ValueType> child : children) {
            if (child.key == null && !child.value.equals(key)) continue;
            if (child.key != null && !child.key.equals(key)) continue;
            return child;
        }

        System.out.println("Node get child returning null");
        return new NodeYio<KeyType, ValueType>(null, null, null);
    }


    public NodeYio<KeyType, ValueType> addChild(ValueType value) {
        return addChild(null, value);
    }


    public NodeYio<KeyType, ValueType> addChild(KeyType key, Object value) {
        NodeYio<KeyType, ValueType> node = new NodeYio<KeyType, ValueType>(key, (ValueType) value, this);
        Yio.addToEndByIterator(children, node);
        return node;
    }


    public String getPathString() {
        Stack<ValueType> stack = new Stack<ValueType>();
        NodeYio<KeyType, ValueType> currentNode = this;

        while (currentNode != null) {
            stack.push(currentNode.value);
            currentNode = currentNode.getParent();
        }

        StringBuilder stringBuilder = new StringBuilder();
        while (!stack.isEmpty()) {
            ValueType value = stack.pop();
            stringBuilder.append(value);
            if (!stack.isEmpty()) {
                stringBuilder.append(".");
            }
        }

        return stringBuilder.toString();
    }


    int countDepth() {
        int depth = 0;
        NodeYio<KeyType, ValueType> currentNode = this;

        while (currentNode.parent != null) {
            depth++;
            currentNode = currentNode.parent;
        }

        return depth;
    }


    public void showVerticalStructureInConsole() {
        NodeYio<KeyType, ValueType> currentNode = this;
        boolean canGoDown = true;

        while (true) {
            if (canGoDown) {
                Yio.safeSay(getSpaces(currentNode.countDepth()) + currentNode);
            }

            if (canGoDown && currentNode.hasChildren()) {
                currentNode = currentNode.children.get(0);
                continue;
            }

            if (currentNode == this) break; // exit loop when reached root again

            NodeYio<KeyType, ValueType> rightNode = currentNode.getRightNode();
            if (rightNode == null) { // can't go right
                currentNode = currentNode.getParent();
                canGoDown = false;
            } else {
                currentNode = rightNode;
                canGoDown = true;
            }
        }
    }


    NodeYio<KeyType, ValueType> getRightNode() {
        if (parent == null) return null;
        ArrayList<NodeYio<KeyType, ValueType>> parentChildren = parent.children;
        int index = parentChildren.indexOf(this);
        if (index >= parentChildren.size() - 1) return null;
        return parentChildren.get(index + 1);
    }


    boolean hasChildren() {
        return children.size() > 0;
    }


    String getSpaces(int numberOfSpaces) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < numberOfSpaces; i++) {
            stringBuilder.append("|  ");
        }

        return stringBuilder.toString();
    }


    public void showHorizontalStructureInConsole() {
        horizontalLoop(new NodeAction<KeyType, ValueType>() {
            @Override
            public void action(NodeYio<KeyType, ValueType> node) {
                System.out.println(node.getPathString());
            }
        });
    }


    public void horizontalLoop(NodeAction<KeyType, ValueType> nodeAction) {
        Queue<NodeYio<KeyType, ValueType>> queue = new LinkedList<>();
        queue.add(this);

        while (!queue.isEmpty()) {
            NodeYio<KeyType, ValueType> node = queue.poll();
            nodeAction.action(node);
            for (NodeYio<KeyType, ValueType> child : node.children) {
                queue.add(child);
            }
        }
    }


    @Override
    public String toString() {
        if (key == null) return "" + value;
        return "[" + key + ": " + value + "]";
    }


    public KeyType getKey() {
        return key;
    }


    public ValueType getValue() {
        return value;
    }


    public int getIntValue() {
        if (value == null) return 0;
        return Integer.parseInt(String.valueOf(value));
    }


    public long getLongValue() {
        if (value == null) return 0;
        return Long.parseLong(String.valueOf(value));
    }


    public float getFloatValue() {
        if (value == null) return 0;
        return Float.parseFloat(String.valueOf(value));
    }


    public double getDoubleValue() {
        if (value == null) return 0;
        return Double.parseDouble(String.valueOf(value));
    }


    public boolean getBooleanValue() {
        if (value == null) return false;
        if (value.equals("true")) return true;
        if (value.equals("false")) return false;
        return getIntValue() == 1;
    }


    public void setParent(NodeYio<KeyType, ValueType> parent) {
        this.parent = parent;
    }


    public ArrayList<NodeYio<KeyType, ValueType>> getListOfChildren() {
        return children;
    }
}
