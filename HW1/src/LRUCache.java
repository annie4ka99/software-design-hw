import java.util.HashMap;
import java.util.Map;

public class LRUCache<K, V> {
    private class Node {
        K key;
        V value;
        Node prev;
        Node next;

        public Node(K key, V value, Node prev, Node next) {
            this.key = key;
            this.value = value;
            this.prev = prev;
            this.next = next;
        }
    }

    private int maxSize;
    private int curSize;
    private Map<K, Node> cache ;
    private Node linkedListHead;
    private Node linkedListTail;

    private void removeNode(Node node) {
        Node prevNode = node.prev;
        Node nextNode = node.next;
        prevNode.next = nextNode;
        nextNode.prev = prevNode;
    }

    private Node addFirst(K key, V value) {
        Node headNext = linkedListHead.next;
        Node newNode = new Node(key, value, linkedListHead, headNext);
        linkedListHead.next = newNode;
        headNext.prev = newNode;
        return newNode;
    }

    private Node pollLastNode() {
        Node tailPrev = linkedListTail.prev;
        removeNode(tailPrev);
        return tailPrev;
    }

    public LRUCache(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maximum size must be positive");
        }

        this.maxSize = maxSize;
        this.curSize = 0;
        this.cache = new HashMap<>();
        this.linkedListHead = new Node(null, null, null, null);
        this.linkedListTail = new Node(null, null, linkedListHead, null);
        this.linkedListHead.next = linkedListTail;
    }

    public int maxSize() {
        return maxSize;
    }

    public int size() {
        assert curSize >= 0 && curSize <= maxSize;

        return curSize;
    }

    public V get(K key) {
        final int initSize = size();

        Node node = cache.get(key);
        if (node != null) {
            removeNode(node);
            Node newNode = addFirst(key, node.value);
            cache.put(key, newNode);
        }

        assert size() == initSize;

        return node == null ? null : node.value;
    }

    public boolean contains(K key) {
        return get(key) != null;
    }

    public V put(K key, V value) {
        final int initSize = size();
        final boolean containedKey = contains(key);

        Node prevNode = cache.get(key);
        if (prevNode == null) {
            if (curSize == maxSize) {
                Node nodeToDelete = pollLastNode();
                cache.remove(nodeToDelete.key);
            } else {
                curSize += 1;
            }
        } else {
            removeNode(prevNode);
        }
        Node newNode = addFirst(key, value);
        cache.put(key, newNode);

        assert (initSize == maxSize() || containedKey) && size() == initSize ||
                initSize < maxSize() && !containedKey && size() == initSize + 1;
        assert get(key) == value;

        return prevNode == null ? null : prevNode.value;
    }

    public V remove(K key) {
        final int initSize = size();
        final boolean containedKey = contains(key);

        Node nodeToRemove = cache.get(key);
        if (nodeToRemove != null) {
            removeNode(nodeToRemove);
            cache.remove(key);
            curSize -= 1;
        }

        assert containedKey && size() == initSize - 1 ||
                containedKey && size() == initSize;
        assert !contains(key);

        return nodeToRemove == null ? null : nodeToRemove.value;
    }

}
