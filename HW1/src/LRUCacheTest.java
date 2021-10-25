import org.junit.Assert;
import org.junit.Test;

public class LRUCacheTest {
    @Test
    public void constructor() {
        final int maxSize = 10;
        LRUCache<Integer, String> cache = new LRUCache<>(maxSize);

        Assert.assertEquals(cache.size(), 0);
        Assert.assertEquals(cache.maxSize(), maxSize);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWithNegativeMaxSize() {
        LRUCache<Integer, String> cache = new LRUCache<>(-1);
    }

    final Integer key1 = 1;
    final Integer key2 = 2;
    final Integer key3 = 3;
    final String val1 = "val1";
    final String val2 = "val2";
    final String val3 = "val3";

    @Test
    public void putGetContains() {
        LRUCache<Integer, String> cache = new LRUCache<>(5);
        String val = cache.put(key1, val1);

        Assert.assertNull(val);
        Assert.assertTrue(cache.contains(key1));
        Assert.assertEquals(cache.get(key1), val1);

        String prevVal = cache.put(key1, val2);
        Assert.assertEquals(prevVal, val1);
        Assert.assertTrue(cache.contains(key1));
        Assert.assertEquals(cache.get(key1), val2);
    }

    @Test
    public void exceedMaxSize() {
        final int maxSize = 2;
        LRUCache<Integer, String> cache = new LRUCache<>(maxSize);

        cache.put(key1, val1);
        cache.put(key2, val2);
        cache.get(key1);
        cache.put(key3, val3);

        Assert.assertEquals(cache.size(), maxSize);
        Assert.assertTrue(cache.contains(key1)
                && cache.contains(key3)
                && !cache.contains(key2));
    }

    @Test
    public void remove() {
        final int maxSize = 5;
        LRUCache<Integer, String> cache = new LRUCache<>(maxSize);
        cache.put(key1, val1);
        cache.put(key2, val2);
        cache.put(key3, val3);
        String prevVal = cache.remove(key2);

        Assert.assertEquals(prevVal, val2);
        Assert.assertEquals(cache.size(),2);
        Assert.assertTrue(!cache.contains(key2)
                && cache.contains(key1)
                && cache.contains(key3));
    }
}
