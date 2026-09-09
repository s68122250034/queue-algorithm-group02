import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

public class ExperimentalComparison {

    // =========================
    // Order
    // =========================
    static class Order {
        String id;
        String foodType;
        int preparationTime;
        int priority;
        int arrivalOrder;

        Order(String id, String foodType, int preparationTime,
              int priority, int arrivalOrder) {
            this.id = id;
            this.foodType = foodType;
            this.preparationTime = preparationTime;
            this.priority = priority;
            this.arrivalOrder = arrivalOrder;
        }

        @Override
        public String toString() {
            return id + "(" + foodType + ", "
                    + preparationTime + " min, P" + priority + ")";
        }
    }

    // =========================
    // Priority
    // Express > Normal > Large
    // =========================
    static int priorityOf(String foodType) {
        if (foodType.equalsIgnoreCase("Express")) return 1;
        if (foodType.equalsIgnoreCase("Normal")) return 2;
        return 3; // Large
    }

    // =========================
    // Algorithm A: FIFO Queue
    // =========================
    static void runFIFO(Order[] orders) {

        Queue<Order> q = new ArrayDeque<>();

        System.out.println("=== Algorithm A: FIFO Queue ===");

        for (Order o : orders) {
            q.offer(o);
        }

        int time = 0;

        while (!q.isEmpty()) {
            Order o = q.poll();

            System.out.println(
                    "Prepare " + o
                    + " | start=" + time
                    + " | wait=" + time + " min"
            );

            time += o.preparationTime;
        }
    }

    // =========================
    // Algorithm B: Priority Queue
    // Express > Normal > Large
    // Priority เท่ากันใช้ arrivalOrder
    // =========================
    static void runPriorityQueue(Order[] orders) {

        PriorityQueue<Order> pq = new PriorityQueue<>(
                Comparator.comparingInt((Order o) -> o.priority)
                        .thenComparingInt(o -> o.arrivalOrder)
        );

        System.out.println("=== Algorithm B: Priority Queue ===");

        for (Order o : orders) {
            pq.offer(o);
        }

        int time = 0;

        while (!pq.isEmpty()) {
            Order o = pq.poll();

            System.out.println(
                    "Prepare " + o
                    + " | start=" + time
                    + " | wait=" + time + " min"
            );

            time += o.preparationTime;
        }
    }

    // ============================================================
    // สร้างข้อมูลสำหรับทดลอง
    // ใช้ Seed คงที่ เพื่อให้ข้อมูลเหมือนกันทุกครั้ง
    // ============================================================
    static Order[] generateOrders(int n, long seed) {

        Random random = new Random(seed);

        Order[] orders = new Order[n];

        String[] foodTypes = {
                "Express",
                "Normal",
                "Large"
        };

        for (int i = 0; i < n; i++) {

            String foodType = foodTypes[random.nextInt(foodTypes.length)];

            int preparationTime;

            if (foodType.equals("Express")) {
                preparationTime = 5 + random.nextInt(6);   // 5-10
            } else if (foodType.equals("Normal")) {
                preparationTime = 10 + random.nextInt(11); // 10-20
            } else {
                preparationTime = 20 + random.nextInt(11); // 20-30
            }

            orders[i] = new Order(
                    "O" + (i + 1),
                    foodType,
                    preparationTime,
                    priorityOf(foodType),
                    i + 1
            );
        }

        return orders;
    }

    // ============================================================
    // วัดเวลา Algorithm A: FIFO Queue
    // ============================================================
    static long measureFIFO(Order[] orders) {

        Queue<Order> q = new ArrayDeque<>();

        long start = System.nanoTime();

        for (Order o : orders) {
            q.offer(o);
        }

        while (!q.isEmpty()) {
            q.poll();
        }

        long end = System.nanoTime();

        return end - start;
    }

    // ============================================================
    // วัดเวลา Algorithm B: Priority Queue
    // ============================================================
    static long measurePriorityQueue(Order[] orders) {

        PriorityQueue<Order> pq = new PriorityQueue<>(
                Comparator.comparingInt((Order o) -> o.priority)
                        .thenComparingInt(o -> o.arrivalOrder)
        );

        long start = System.nanoTime();

        for (Order o : orders) {
            pq.offer(o);
        }

        while (!pq.isEmpty()) {
            pq.poll();
        }

        long end = System.nanoTime();

        return end - start;
    }

    // ============================================================
    // Algorithm Experiment
    //
    // n = 100, 1,000, 10,000, 50,000
    // Warm-up
    // Fixed Seed
    // 5 Trials
    // Average Time
    // ============================================================
    static void runExperiment() {

        int[] sizes = {
                100,
                1_000,
                10_000,
                50_000
        };

        int warmUpRounds = 3;
        int trials = 5;

        long seed = 12345L;

        System.out.println();
        System.out.println("==============================================");
        System.out.println("       ALGORITHM EXPERIMENT");
        System.out.println("==============================================");
        System.out.println("Warm-up rounds : " + warmUpRounds);
        System.out.println("Trials         : " + trials);
        System.out.println("Fixed Seed     : " + seed);
        System.out.println();

        System.out.printf(
                "%-10s %-20s %-20s%n",
                "n",
                "FIFO Average (ns)",
                "Priority Average (ns)"
        );

        System.out.println(
                "--------------------------------------------------------------"
        );

        for (int n : sizes) {

            // ------------------------------------------
            // สร้างข้อมูลด้วย Seed เดิม
            // ------------------------------------------
            Order[] orders = generateOrders(n, seed);

            // ------------------------------------------
            // Warm-up
            // ------------------------------------------
            for (int i = 0; i < warmUpRounds; i++) {
                measureFIFO(orders);
                measurePriorityQueue(orders);
            }

            // ------------------------------------------
            // วัดจริง 5 รอบ
            // ------------------------------------------
            long totalFIFO = 0;
            long totalPriority = 0;

            for (int i = 0; i < trials; i++) {

                totalFIFO += measureFIFO(orders);
                totalPriority += measurePriorityQueue(orders);
            }

            // ------------------------------------------
            // Average
            // ------------------------------------------
            double averageFIFO = (double) totalFIFO / trials;
            double averagePriority = (double) totalPriority / trials;

            System.out.printf(
                    "%-10d %-20.2f %-20.2f%n",
                    n,
                    averageFIFO,
                    averagePriority
            );
        }

        System.out.println(
                "--------------------------------------------------------------"
        );

        System.out.println(
                "หมายเหตุ: ค่าเวลาอาจแตกต่างกันในแต่ละครั้งตาม JVM และสภาพแวดล้อมของเครื่อง"
        );
    }

    // =========================
    // Main
    // =========================
    public static void main(String[] args) {

        // =========================================
        // Scenario
        // =========================================
        Order[] orders = {

                new Order(
                        "O1",
                        "Normal",
                        15,
                        priorityOf("Normal"),
                        1
                ),

                new Order(
                        "O2",
                        "Express",
                        5,
                        priorityOf("Express"),
                        2
                ),

                new Order(
                        "O3",
                        "Normal",
                        10,
                        priorityOf("Normal"),
                        3
                ),

                new Order(
                        "O4",
                        "Express",
                        8,
                        priorityOf("Express"),
                        4
                ),

                new Order(
                        "O5",
                        "Large",
                        25,
                        priorityOf("Large"),
                        5
                )
        };

        // =========================================
        // แสดงผล Algorithm A
        // =========================================
        runFIFO(orders);

        System.out.println();

        // =========================================
        // แสดงผล Algorithm B
        // =========================================
        runPriorityQueue(orders);

        // =========================================
        // Experiment
        // =========================================
        runExperiment();
    }
}