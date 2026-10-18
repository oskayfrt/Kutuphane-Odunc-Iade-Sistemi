package tr.edu.kutuphane.datastructures;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class QueueCustomTest {
    @Test
    void fifoTest(){
        QueueCustom<String> queue=new QueueCustom<>();

        queue.enqueue("Ali");
        queue.enqueue("Ayse");
        queue.enqueue("Mehmet");

        assertEquals("Ali", queue.peek());
        assertEquals("Ali", queue.dequeue());
        assertEquals("Ayse", queue.dequeue());
        assertEquals(1, queue.size());
    }

    @Test
    void circularAndGrowTest(){
        QueueCustom<Integer> queue=new QueueCustom<>();
        for(int i=0;i<8;i++){
            queue.enqueue(i);
        }
        for(int i=0;i<5;i++){
            assertEquals(i, queue.dequeue());
        }
        for(int i=8;i<17;i++){
            queue.enqueue(i);
        }
        for (int i = 5; i < 17; i++) {
            assertEquals(i, queue.dequeue());
        }
        assertTrue(queue.isEmpty());
        assertThrows(java.util.NoSuchElementException.class, queue::dequeue);
    }
}
