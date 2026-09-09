import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class FoodQueueSystem {

    static class Order {
        String id;
        String foodType;
        int preparationTime;
        int priority;
        int arrivalOrder;

        Order(String id, String foodType, int preparationTime, int priority, int arrivalOrder) {
            this.id = id;
            this.foodType = foodType;
            this.preparationTime = preparationTime;
            this.priority = priority;
            this.arrivalOrder = arrivalOrder;
        }

        @Override
        public String toString() {
            return id + "(" + foodType + ", " + preparationTime + " min, P" + priority + ")";
        }
    }

    static int priorityOf(String foodType) {
        if (foodType.equalsIgnoreCase("Express")) return 1;
        if (foodType.equalsIgnoreCase("Normal")) return 2;
        return 3; // Large
    }

    static void runFIFO(Order[] orders) {
        Queue<Order> q = new ArrayDeque<>();
        System.out.println("=== Algorithm A: FIFO Queue ===");
        for (Order o : orders) q.offer(o);
        int time = 0;
        while (!q.isEmpty()) {
            Order o = q.poll();
            System.out.println("Prepare " + o + " | start=" + time + " | wait=" + time + " min");
            time += o.preparationTime;
        }
    }

    static void runPriorityQueue(Order[] orders) {
        PriorityQueue<Order> pq = new PriorityQueue<>(
            Comparator.comparingInt((Order o) -> o.priority)
                      .thenComparingInt(o -> o.arrivalOrder)
        );
        System.out.println("=== Algorithm B: Priority Queue ===");
        for (Order o : orders) pq.offer(o);
        int time = 0;
        while (!pq.isEmpty()) {
            Order o = pq.poll();
            System.out.println("Prepare " + o + " | start=" + time + " | wait=" + time + " min");
            time += o.preparationTime;
        }
    }

    public static void main(String[] args) {
        Order[] orders = {
            new Order("O1", "Normal", 15, priorityOf("Normal"), 1),
            new Order("O2", "Express", 5, priorityOf("Express"), 2),
            new Order("O3", "Normal", 10, priorityOf("Normal"), 3),
            new Order("O4", "Express", 8, priorityOf("Express"), 4),
            new Order("O5", "Large", 25, priorityOf("Large"), 5)
        };

        runFIFO(orders);
        System.out.println();
        runPriorityQueue(orders);
    }
}
