package yio.tro.shmatoosto.stuff.containers.tree;


public interface NodeAction<KeyType, ValueType> {


    void action(NodeYio<KeyType, ValueType> node);
}
